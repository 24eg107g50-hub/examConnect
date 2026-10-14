package com.examconnect.service;

import com.examconnect.dto.*;
import com.examconnect.entity.*;
import com.examconnect.exception.BadRequestException;
import com.examconnect.exception.ForbiddenException;
import com.examconnect.exception.ResourceNotFoundException;
import com.examconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockTestService {

    private final MockTestRepository mockTestRepository;
    private final MockTestQuestionRepository testQuestionRepository;
    private final PracticeQuestionRepository questionRepository;
    private final TestAttemptRepository attemptRepository;
    private final TestResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final PracticeQuestionService practiceQuestionService;

    @Transactional(readOnly = true)
    public List<MockTestSummaryDto> getAllMockTests(Long examId) {
        List<MockTest> tests;
        if (examId != null) {
            tests = mockTestRepository.findByExamIdAndActiveTrue(examId);
        } else {
            tests = mockTestRepository.findAll().stream().filter(MockTest::isActive).collect(Collectors.toList());
        }
        return tests.stream().map(this::mapToSummaryDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MockTestSummaryDto getMockTestById(Long id) {
        MockTest test = mockTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found with ID: " + id));
        return mapToSummaryDto(test);
    }

    @Transactional
    public StartTestResponseDto startTest(Long mockTestId, Long userId) {
        MockTest test = mockTestRepository.findById(mockTestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mock test not found with ID: " + mockTestId));

        if (!test.isActive()) {
            throw new BadRequestException("This mock test is currently inactive");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if there is already an active in-progress attempt within valid time
        LocalDateTime now = LocalDateTime.now();
        List<TestAttempt> existingAttempts = attemptRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (TestAttempt att : existingAttempts) {
            if (att.getMockTest().getId().equals(mockTestId) && "IN_PROGRESS".equals(att.getStatus())) {
                LocalDateTime deadline = att.getStartTime().plusMinutes(att.getMockTest().getDurationMinutes());
                if (now.isBefore(deadline.plusMinutes(2))) {
                    // Resume existing attempt
                    return buildStartResponse(att, test);
                } else {
                    // Mark expired
                    att.setStatus("TIMED_OUT");
                    att.setSubmitTime(deadline);
                    attemptRepository.save(att);
                }
            }
        }

        // Create new attempt
        TestAttempt attempt = TestAttempt.builder()
                .user(user)
                .mockTest(test)
                .status("IN_PROGRESS")
                .startTime(now)
                .maxScore(test.getTotalMarks())
                .build();

        TestAttempt savedAttempt = attemptRepository.save(attempt);

        return buildStartResponse(savedAttempt, test);
    }

    @Transactional
    public TestResultDto submitTest(Long attemptId, SubmitTestRequest request, Long userId) {
        TestAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Test attempt not found with ID: " + attemptId));

        if (!attempt.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You cannot submit an attempt that belongs to another student");
        }

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new BadRequestException("This test attempt has already been submitted or completed");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = attempt.getStartTime().plusMinutes(attempt.getMockTest().getDurationMinutes());
        // Allow 3 minutes network grace period
        boolean isTimedOut = now.isAfter(deadline.plusMinutes(3));

        MockTest mockTest = attempt.getMockTest();
        List<MockTestQuestion> testQuestions = testQuestionRepository.findByMockTestIdOrderByOrderIndexAsc(mockTest.getId());

        Map<Long, String> userAnswers = new HashMap<>();
        Map<Long, Boolean> userReviewFlags = new HashMap<>();
        if (request != null && request.getAnswers() != null) {
            for (TestQuestionAnswerDto a : request.getAnswers()) {
                if (a.getQuestionId() != null) {
                    userAnswers.put(a.getQuestionId(), a.getSelectedOption());
                    userReviewFlags.put(a.getQuestionId(), a.isMarkedForReview());
                }
            }
        }

        int correctCount = 0;
        int incorrectCount = 0;
        int unansweredCount = 0;
        double totalScore = 0.0;

        List<TestResponse> responses = new ArrayList<>();

        for (MockTestQuestion tq : testQuestions) {
            PracticeQuestion q = tq.getQuestion();
            String selected = userAnswers.get(q.getId());
            boolean markedReview = Boolean.TRUE.equals(userReviewFlags.get(q.getId()));

            Boolean isCorrect = null;
            double marksAwarded = 0.0;

            if (selected != null && !selected.trim().isEmpty()) {
                selected = selected.trim().toUpperCase();
                if (selected.equals(q.getCorrectOption())) {
                    isCorrect = true;
                    marksAwarded = q.getMarks();
                    correctCount++;
                } else {
                    isCorrect = false;
                    marksAwarded = -mockTest.getNegativeMarkingPerQuestion();
                    incorrectCount++;
                }
            } else {
                unansweredCount++;
                selected = null;
            }

            totalScore += marksAwarded;

            TestResponse response = TestResponse.builder()
                    .attempt(attempt)
                    .question(q)
                    .selectedOption(selected)
                    .isCorrect(isCorrect)
                    .marksAwarded(marksAwarded)
                    .markedForReview(markedReview)
                    .build();

            responses.add(response);
        }

        responseRepository.saveAll(responses);

        long secondsTaken = Duration.between(attempt.getStartTime(), now).getSeconds();
        int timeTaken = (int) Math.min(secondsTaken, mockTest.getDurationMinutes() * 60L);

        // Ensure score does not dip below 0
        double finalScore = Math.max(0.0, Math.round(totalScore * 100.0) / 100.0);
        double percentage = (finalScore / mockTest.getTotalMarks()) * 100.0;
        percentage = Math.round(percentage * 10.0) / 10.0;

        int totalAnswered = correctCount + incorrectCount;
        double accuracy = totalAnswered > 0 ? ((double) correctCount / totalAnswered) * 100.0 : 0.0;
        accuracy = Math.round(accuracy * 10.0) / 10.0;

        boolean passed = finalScore >= mockTest.getPassingMarks();

        attempt.setStatus(isTimedOut ? "TIMED_OUT" : "COMPLETED");
        attempt.setSubmitTime(now);
        attempt.setTimeTakenSeconds(timeTaken);
        attempt.setTotalScore(finalScore);
        attempt.setPercentage(percentage);
        attempt.setCorrectCount(correctCount);
        attempt.setIncorrectCount(incorrectCount);
        attempt.setUnansweredCount(unansweredCount);
        attempt.setAccuracy(accuracy);
        attempt.setPassed(passed);

        TestAttempt savedAttempt = attemptRepository.save(attempt);
        return buildResultDto(savedAttempt, responses);
    }

    @Transactional(readOnly = true)
    public TestResultDto getResult(Long attemptId, Long userId) {
        TestAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Test attempt not found with ID: " + attemptId));

        if (!attempt.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You cannot view results of another student's test attempt");
        }

        List<TestResponse> responses = responseRepository.findByAttemptId(attemptId);
        return buildResultDto(attempt, responses);
    }

    @Transactional(readOnly = true)
    public List<AttemptHistoryDto> getUserHistory(Long userId) {
        return attemptRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
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
    }

    // Admin methods
    @Transactional
    public MockTestSummaryDto createMockTest(MockTestCreateRequest request) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        List<PracticeQuestion> questions = questionRepository.findAllById(request.getQuestionIds());
        if (questions.isEmpty()) {
            throw new BadRequestException("At least one valid question must be provided");
        }

        double totalMarks = questions.stream().mapToDouble(PracticeQuestion::getMarks).sum();
        double passingMarks = request.getPassingMarks() != null ? request.getPassingMarks() : (totalMarks * 0.4);

        MockTest mockTest = MockTest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .exam(exam)
                .durationMinutes(request.getDurationMinutes())
                .totalQuestions(questions.size())
                .totalMarks(totalMarks)
                .passingMarks(passingMarks)
                .negativeMarkingPerQuestion(request.getNegativeMarkingPerQuestion() != null ? request.getNegativeMarkingPerQuestion() : 0.25)
                .active(true)
                .testQuestions(new ArrayList<>())
                .build();

        MockTest saved = mockTestRepository.save(mockTest);

        int order = 1;
        for (PracticeQuestion q : questions) {
            MockTestQuestion tq = MockTestQuestion.builder()
                    .mockTest(saved)
                    .question(q)
                    .orderIndex(order++)
                    .build();
            saved.getTestQuestions().add(tq);
        }

        return mapToSummaryDto(mockTestRepository.save(saved));
    }

    @Transactional
    public void deleteMockTest(Long id) {
        if (!mockTestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mock test not found with ID: " + id);
        }
        mockTestRepository.deleteById(id);
    }

    private StartTestResponseDto buildStartResponse(TestAttempt attempt, MockTest test) {
        LocalDateTime startTime = attempt.getStartTime();
        LocalDateTime deadline = startTime.plusMinutes(test.getDurationMinutes());

        List<MockTestQuestion> testQuestions = testQuestionRepository.findByMockTestIdOrderByOrderIndexAsc(test.getId());
        List<PracticeQuestionDto> questions = testQuestions.stream()
                .map(tq -> practiceQuestionService.mapToStudentDto(tq.getQuestion()))
                .collect(Collectors.toList());

        return StartTestResponseDto.builder()
                .attemptId(attempt.getId())
                .mockTestId(test.getId())
                .testTitle(test.getTitle())
                .examName(test.getExam().getName())
                .durationMinutes(test.getDurationMinutes())
                .startTime(startTime)
                .deadline(deadline)
                .totalQuestions(test.getTotalQuestions())
                .totalMarks(test.getTotalMarks())
                .negativeMarkingPerQuestion(test.getNegativeMarkingPerQuestion())
                .questions(questions)
                .build();
    }

    private TestResultDto buildResultDto(TestAttempt attempt, List<TestResponse> responses) {
        Map<String, int[]> subjectStats = new HashMap<>(); // [total, correct, incorrect]
        Map<String, int[]> topicStats = new HashMap<>();

        List<QuestionReviewDto> reviews = new ArrayList<>();

        for (TestResponse r : responses) {
            PracticeQuestion q = r.getQuestion();
            String subjectName = q.getSubject().getName();
            String topicName = q.getTopic() != null ? q.getTopic().getName() : "General";

            subjectStats.putIfAbsent(subjectName, new int[3]);
            topicStats.putIfAbsent(topicName, new int[3]);

            if (r.getSelectedOption() != null) {
                subjectStats.get(subjectName)[0]++;
                topicStats.get(topicName)[0]++;
                if (Boolean.TRUE.equals(r.getIsCorrect())) {
                    subjectStats.get(subjectName)[1]++;
                    topicStats.get(topicName)[1]++;
                } else {
                    subjectStats.get(subjectName)[2]++;
                    topicStats.get(topicName)[2]++;
                }
            }

            List<QuestionOptionDto> options = q.getOptions().stream()
                    .map(o -> QuestionOptionDto.builder().optionKey(o.getOptionKey()).optionText(o.getOptionText()).build())
                    .collect(Collectors.toList());

            reviews.add(QuestionReviewDto.builder()
                    .questionId(q.getId())
                    .questionText(q.getQuestionText())
                    .subjectName(subjectName)
                    .topicName(topicName)
                    .options(options)
                    .selectedOption(r.getSelectedOption())
                    .correctOption(q.getCorrectOption())
                    .isCorrect(r.getIsCorrect())
                    .marksAwarded(r.getMarksAwarded())
                    .explanation(q.getExplanation())
                    .markedForReview(Boolean.TRUE.equals(r.getMarkedForReview()))
                    .build());
        }

        Map<String, SubjectPerformanceDto> subjectPerformance = new HashMap<>();
        subjectStats.forEach((name, counts) -> {
            int total = counts[0];
            int correct = counts[1];
            int incorrect = counts[2];
            double acc = total > 0 ? ((double) correct / total) * 100.0 : 0.0;
            subjectPerformance.put(name, SubjectPerformanceDto.builder()
                    .subjectName(name)
                    .totalAttempted(total)
                    .correctCount(correct)
                    .incorrectCount(incorrect)
                    .accuracy(Math.round(acc * 10.0) / 10.0)
                    .build());
        });

        Map<String, TopicPerformanceDto> topicPerformance = new HashMap<>();
        topicStats.forEach((name, counts) -> {
            int total = counts[0];
            int correct = counts[1];
            int incorrect = counts[2];
            double acc = total > 0 ? ((double) correct / total) * 100.0 : 0.0;
            topicPerformance.put(name, TopicPerformanceDto.builder()
                    .topicName(name)
                    .totalAttempted(total)
                    .correctCount(correct)
                    .incorrectCount(incorrect)
                    .accuracy(Math.round(acc * 10.0) / 10.0)
                    .isWeakTopic(acc < 50.0)
                    .build());
        });

        return TestResultDto.builder()
                .attemptId(attempt.getId())
                .mockTestId(attempt.getMockTest().getId())
                .testTitle(attempt.getMockTest().getTitle())
                .examName(attempt.getMockTest().getExam().getName())
                .totalScore(attempt.getTotalScore())
                .maxScore(attempt.getMaxScore())
                .percentage(attempt.getPercentage())
                .correctCount(attempt.getCorrectCount())
                .incorrectCount(attempt.getIncorrectCount())
                .unansweredCount(attempt.getUnansweredCount())
                .totalQuestions(attempt.getMockTest().getTotalQuestions())
                .accuracy(attempt.getAccuracy())
                .timeTakenSeconds(attempt.getTimeTakenSeconds())
                .passed(attempt.getPassed())
                .submitTime(attempt.getSubmitTime())
                .subjectWisePerformance(subjectPerformance)
                .topicWisePerformance(topicPerformance)
                .questionReviews(reviews)
                .build();
    }

    private MockTestSummaryDto mapToSummaryDto(MockTest test) {
        return MockTestSummaryDto.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .examId(test.getExam().getId())
                .examName(test.getExam().getName())
                .durationMinutes(test.getDurationMinutes())
                .totalQuestions(test.getTotalQuestions())
                .totalMarks(test.getTotalMarks())
                .passingMarks(test.getPassingMarks())
                .negativeMarkingPerQuestion(test.getNegativeMarkingPerQuestion())
                .active(test.isActive())
                .build();
    }
}
