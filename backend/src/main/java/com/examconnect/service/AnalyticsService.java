package com.examconnect.service;

import com.examconnect.dto.*;
import com.examconnect.entity.*;
import com.examconnect.exception.ResourceNotFoundException;
import com.examconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final StudyMaterialRepository materialRepository;
    private final MaterialProgressRepository progressRepository;
    private final TestAttemptRepository attemptRepository;
    private final TestResponseRepository responseRepository;
    private final UserPracticeAttemptRepository practiceAttemptRepository;
    private final DoubtRepository doubtRepository;
    private final AnswerRepository answerRepository;
    private final StudyMaterialService studyMaterialService;

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        long availableExams = examRepository.count();
        long materialsAccessed = progressRepository.countByUserId(userId);
        long testsCompleted = attemptRepository.countByUserIdAndStatus(userId, "COMPLETED");
        Double avgScore = attemptRepository.getAverageScoreByUserId(userId);
        long questionsPosted = doubtRepository.countByUserId(userId);
        long answersReceived = answerRepository.countAnswersReceivedForUserQuestions(userId);

        // Recently accessed materials
        List<StudyMaterialDto> recentMaterials = progressRepository.findRecentlyAccessed(userId).stream()
                .limit(5)
                .map(mp -> studyMaterialService.mapToDto(mp.getMaterial(), userId))
                .collect(Collectors.toList());

        // Recent test results
        List<AttemptHistoryDto> recentTests = attemptRepository.findTop5ByUserIdAndStatusOrderByCreatedAtDesc(userId, "COMPLETED").stream()
                .map(a -> AttemptHistoryDto.builder()
                        .attemptId(a.getId())
                        .mockTestId(a.getMockTest().getId())
                        .testTitle(a.getMockTest().getTitle())
                        .examName(a.getMockTest().getExam().getName())
                        .score(a.getTotalScore())
                        .maxScore(a.getMaxScore())
                        .percentage(a.getPercentage())
                        .passed(a.getPassed())
                        .timeTakenSeconds(a.getTimeTakenSeconds())
                        .status(a.getStatus())
                        .date(a.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        // Subject Progress & Weak Topics calculation
        PerformanceAnalyticsDto analytics = getPerformanceAnalytics(userId);

        return DashboardStatsDto.builder()
                .studentName(user.getFullName())
                .availableExams(availableExams)
                .studyMaterialsAccessed(materialsAccessed)
                .mockTestsCompleted(testsCompleted)
                .averageScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0)
                .questionsPosted(questionsPosted)
                .answersReceived(answersReceived)
                .recentlyAccessedMaterials(recentMaterials)
                .recentTestResults(recentTests)
                .weakTopics(analytics.getWeakTopics())
                .subjectProgress(analytics.getSubjectPerformance())
                .recommendations(analytics.getRuleRecommendations())
                .build();
    }

    @Transactional(readOnly = true)
    public PerformanceAnalyticsDto getPerformanceAnalytics(Long userId) {
        long testsAttempted = attemptRepository.countByUserId(userId);
        long testsCompleted = attemptRepository.countByUserIdAndStatus(userId, "COMPLETED");
        Double avgScore = attemptRepository.getAverageScoreByUserId(userId);
        Double highestScore = attemptRepository.getHighestScoreByUserId(userId);
        long materialsAccessed = progressRepository.countByUserId(userId);
        long materialsCompleted = progressRepository.countByUserIdAndCompletedTrue(userId);

        List<TestAttempt> completedAttempts = attemptRepository.findCompletedAttemptsChronological(userId);

        List<ScoreTrendDto> scoreTrends = completedAttempts.stream()
                .map(a -> ScoreTrendDto.builder()
                        .testTitle(a.getMockTest().getTitle())
                        .date(a.getCreatedAt())
                        .score(a.getTotalScore())
                        .percentage(a.getPercentage())
                        .build())
                .collect(Collectors.toList());

        // Compute questions stats from attempts and practice
        long totalQuestionsAttempted = 0;
        long totalCorrect = 0;
        long totalIncorrect = 0;

        Map<String, int[]> subjectMap = new HashMap<>(); // [total, correct, incorrect]
        Map<String, int[]> topicMap = new HashMap<>();
        Map<String, String> topicToSubject = new HashMap<>();
        Map<String, Long[]> topicIds = new HashMap<>(); // [subjectId, topicId]

        for (TestAttempt att : completedAttempts) {
            List<TestResponse> responses = responseRepository.findByAttemptId(att.getId());
            for (TestResponse r : responses) {
                if (r.getSelectedOption() != null) {
                    totalQuestionsAttempted++;
                    String subj = r.getQuestion().getSubject().getName();
                    String top = r.getQuestion().getTopic() != null ? r.getQuestion().getTopic().getName() : "General";

                    subjectMap.putIfAbsent(subj, new int[3]);
                    topicMap.putIfAbsent(top, new int[3]);
                    topicToSubject.put(top, subj);

                    Long subjId = r.getQuestion().getSubject().getId();
                    Long topId = r.getQuestion().getTopic() != null ? r.getQuestion().getTopic().getId() : null;
                    topicIds.put(top, new Long[]{subjId, topId});

                    subjectMap.get(subj)[0]++;
                    topicMap.get(top)[0]++;

                    if (Boolean.TRUE.equals(r.getIsCorrect())) {
                        totalCorrect++;
                        subjectMap.get(subj)[1]++;
                        topicMap.get(top)[1]++;
                    } else {
                        totalIncorrect++;
                        subjectMap.get(subj)[2]++;
                        topicMap.get(top)[2]++;
                    }
                }
            }
        }

        // Also incorporate standalone practice attempts
        List<UserPracticeAttempt> practiceAttempts = practiceAttemptRepository.findAllByUserId(userId);
        for (UserPracticeAttempt pa : practiceAttempts) {
            totalQuestionsAttempted++;
            String subj = pa.getQuestion().getSubject().getName();
            String top = pa.getQuestion().getTopic() != null ? pa.getQuestion().getTopic().getName() : "General";

            subjectMap.putIfAbsent(subj, new int[3]);
            topicMap.putIfAbsent(top, new int[3]);
            topicToSubject.put(top, subj);

            Long subjId = pa.getQuestion().getSubject().getId();
            Long topId = pa.getQuestion().getTopic() != null ? pa.getQuestion().getTopic().getId() : null;
            topicIds.put(top, new Long[]{subjId, topId});

            subjectMap.get(subj)[0]++;
            topicMap.get(top)[0]++;

            if (pa.isCorrect()) {
                totalCorrect++;
                subjectMap.get(subj)[1]++;
                topicMap.get(top)[1]++;
            } else {
                totalIncorrect++;
                subjectMap.get(subj)[2]++;
                topicMap.get(top)[2]++;
            }
        }

        double overallAccuracy = totalQuestionsAttempted > 0 ?
                ((double) totalCorrect / totalQuestionsAttempted) * 100.0 : 0.0;

        List<SubjectPerformanceDto> subjectPerformance = new ArrayList<>();
        subjectMap.forEach((name, counts) -> {
            int tot = counts[0];
            int cor = counts[1];
            int incor = counts[2];
            double acc = tot > 0 ? ((double) cor / tot) * 100.0 : 0.0;
            subjectPerformance.add(SubjectPerformanceDto.builder()
                    .subjectName(name)
                    .totalAttempted(tot)
                    .correctCount(cor)
                    .incorrectCount(incor)
                    .accuracy(Math.round(acc * 10.0) / 10.0)
                    .build());
        });

        List<TopicPerformanceDto> allTopics = new ArrayList<>();
        topicMap.forEach((name, counts) -> {
            int tot = counts[0];
            int cor = counts[1];
            int incor = counts[2];
            double acc = tot > 0 ? ((double) cor / tot) * 100.0 : 0.0;
            allTopics.add(TopicPerformanceDto.builder()
                    .topicName(name)
                    .subjectName(topicToSubject.getOrDefault(name, "General"))
                    .totalAttempted(tot)
                    .correctCount(cor)
                    .incorrectCount(incor)
                    .accuracy(Math.round(acc * 10.0) / 10.0)
                    .isWeakTopic(acc < 50.0)
                    .build());
        });

        // Filter and sort weak topics by accuracy ascending
        List<TopicPerformanceDto> weakTopics = allTopics.stream()
                .filter(TopicPerformanceDto::isWeakTopic)
                .sorted(Comparator.comparingDouble(TopicPerformanceDto::getAccuracy))
                .collect(Collectors.toList());

        // Transparent rule-based recommendations:
        // Accuracy < 50%: HIGH priority revision needed
        // Repeated mistakes: recommend practice questions
        List<RuleRecommendationDto> recommendations = new ArrayList<>();
        for (TopicPerformanceDto wt : weakTopics) {
            Long[] ids = topicIds.get(wt.getTopicName());
            recommendations.add(RuleRecommendationDto.builder()
                    .topicName(wt.getTopicName())
                    .subjectName(wt.getSubjectName())
                    .severity("HIGH")
                    .recommendationText("Low accuracy (" + wt.getAccuracy() + "%). Revise theory notes and practice at least 10 targeted questions.")
                    .subjectId(ids != null ? ids[0] : null)
                    .topicId(ids != null ? ids[1] : null)
                    .build());
        }

        // Add moderate recommendation for topics between 50% and 75%
        allTopics.stream()
                .filter(t -> t.getAccuracy() >= 50.0 && t.getAccuracy() < 75.0)
                .limit(3)
                .forEach(t -> {
                    Long[] ids = topicIds.get(t.getTopicName());
                    recommendations.add(RuleRecommendationDto.builder()
                            .topicName(t.getTopicName())
                            .subjectName(t.getSubjectName())
                            .severity("MEDIUM")
                            .recommendationText("Moderate accuracy (" + t.getAccuracy() + "%). Take a quick mock quiz to reinforce mastery.")
                            .subjectId(ids != null ? ids[0] : null)
                            .topicId(ids != null ? ids[1] : null)
                            .build());
                });

        return PerformanceAnalyticsDto.builder()
                .totalTestsAttempted(testsAttempted)
                .totalTestsCompleted(testsCompleted)
                .averageScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0)
                .highestScore(highestScore != null ? Math.round(highestScore * 10.0) / 10.0 : 0.0)
                .overallAccuracy(Math.round(overallAccuracy * 10.0) / 10.0)
                .totalQuestionsAttempted(totalQuestionsAttempted)
                .totalCorrectQuestions(totalCorrect)
                .totalIncorrectQuestions(totalIncorrect)
                .totalMaterialsAccessed(materialsAccessed)
                .totalMaterialsCompleted(materialsCompleted)
                .scoreTrends(scoreTrends)
                .subjectPerformance(subjectPerformance)
                .weakTopics(weakTopics)
                .ruleRecommendations(recommendations)
                .build();
    }
}
