package com.examconnect.service;

import com.examconnect.dto.*;
import com.examconnect.entity.*;
import com.examconnect.exception.ResourceNotFoundException;
import com.examconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PracticeQuestionService {

    private final PracticeQuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final UserPracticeAttemptRepository practiceAttemptRepository;

    @Transactional(readOnly = true)
    public PagedResponse<PracticeQuestionDto> getQuestionsForPractice(
            Long examId, Long subjectId, Long topicId, String difficulty, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PracticeQuestion> questionPage = questionRepository.findFilteredQuestions(examId, subjectId, topicId, difficulty, pageable);

        Page<PracticeQuestionDto> dtoPage = questionPage.map(this::mapToStudentDto);

        return PagedResponse.<PracticeQuestionDto>builder()
                .content(dtoPage.getContent())
                .page(dtoPage.getNumber())
                .size(dtoPage.getSize())
                .totalElements(dtoPage.getTotalElements())
                .totalPages(dtoPage.getTotalPages())
                .last(dtoPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PracticeQuestionDto getQuestionById(Long id) {
        PracticeQuestion q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + id));
        return mapToStudentDto(q);
    }

    @Transactional
    public PracticeSubmitResponse submitPracticeAnswer(PracticeSubmitRequest request, Long userId) {
        PracticeQuestion q = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + request.getQuestionId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isCorrect = q.getCorrectOption().equalsIgnoreCase(request.getSelectedOption().trim());

        // Save attempt
        UserPracticeAttempt attempt = UserPracticeAttempt.builder()
                .user(user)
                .question(q)
                .selectedOption(request.getSelectedOption().trim().toUpperCase())
                .isCorrect(isCorrect)
                .build();
        practiceAttemptRepository.save(attempt);

        long totalPracticed = practiceAttemptRepository.countByUserId(userId);
        long totalCorrect = practiceAttemptRepository.countByUserIdAndIsCorrectTrue(userId);
        double accuracy = totalPracticed > 0 ? ((double) totalCorrect / totalPracticed) * 100.0 : 0.0;

        return PracticeSubmitResponse.builder()
                .questionId(q.getId())
                .selectedOption(request.getSelectedOption().toUpperCase())
                .correctOption(q.getCorrectOption())
                .isCorrect(isCorrect)
                .explanation(q.getExplanation())
                .totalQuestionsPracticed(totalPracticed)
                .totalCorrect(totalCorrect)
                .overallAccuracy(Math.round(accuracy * 10.0) / 10.0)
                .build();
    }

    // Admin methods
    @Transactional
    public PracticeQuestionAdminDto createQuestion(PracticeQuestionAdminDto dto) {
        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Topic topic = null;
        if (dto.getTopicId() != null) {
            topic = topicRepository.findById(dto.getTopicId()).orElse(null);
        }

        PracticeQuestion question = PracticeQuestion.builder()
                .questionText(dto.getQuestionText())
                .exam(exam)
                .subject(subject)
                .topic(topic)
                .correctOption(dto.getCorrectOption().trim().toUpperCase())
                .explanation(dto.getExplanation())
                .difficulty(dto.getDifficulty())
                .marks(dto.getMarks() != null ? dto.getMarks() : 1.0)
                .negativeMarks(dto.getNegativeMarks() != null ? dto.getNegativeMarks() : 0.25)
                .options(new ArrayList<>())
                .build();

        PracticeQuestion saved = questionRepository.save(question);

        if (dto.getOptions() != null) {
            for (QuestionOptionDto optDto : dto.getOptions()) {
                QuestionOption opt = QuestionOption.builder()
                        .question(saved)
                        .optionKey(optDto.getOptionKey().trim().toUpperCase())
                        .optionText(optDto.getOptionText().trim())
                        .build();
                saved.getOptions().add(opt);
            }
            saved = questionRepository.save(saved);
        }

        return mapToAdminDto(saved);
    }

    @Transactional
    public PracticeQuestionAdminDto updateQuestion(Long id, PracticeQuestionAdminDto dto) {
        PracticeQuestion question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + id));

        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Topic topic = null;
        if (dto.getTopicId() != null) {
            topic = topicRepository.findById(dto.getTopicId()).orElse(null);
        }

        question.setQuestionText(dto.getQuestionText());
        question.setExam(exam);
        question.setSubject(subject);
        question.setTopic(topic);
        question.setCorrectOption(dto.getCorrectOption().trim().toUpperCase());
        question.setExplanation(dto.getExplanation());
        question.setDifficulty(dto.getDifficulty());
        if (dto.getMarks() != null) question.setMarks(dto.getMarks());
        if (dto.getNegativeMarks() != null) question.setNegativeMarks(dto.getNegativeMarks());

        // Update options
        question.getOptions().clear();
        if (dto.getOptions() != null) {
            for (QuestionOptionDto optDto : dto.getOptions()) {
                QuestionOption opt = QuestionOption.builder()
                        .question(question)
                        .optionKey(optDto.getOptionKey().trim().toUpperCase())
                        .optionText(optDto.getOptionText().trim())
                        .build();
                question.getOptions().add(opt);
            }
        }

        return mapToAdminDto(questionRepository.save(question));
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question not found with ID: " + id);
        }
        questionRepository.deleteById(id);
    }

    public PracticeQuestionDto mapToStudentDto(PracticeQuestion q) {
        List<QuestionOptionDto> options = q.getOptions().stream()
                .map(o -> QuestionOptionDto.builder().optionKey(o.getOptionKey()).optionText(o.getOptionText()).build())
                .collect(Collectors.toList());

        return PracticeQuestionDto.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .examId(q.getExam().getId())
                .examName(q.getExam().getName())
                .subjectId(q.getSubject().getId())
                .subjectName(q.getSubject().getName())
                .topicId(q.getTopic() != null ? q.getTopic().getId() : null)
                .topicName(q.getTopic() != null ? q.getTopic().getName() : null)
                .difficulty(q.getDifficulty())
                .marks(q.getMarks())
                .negativeMarks(q.getNegativeMarks())
                .options(options)
                .correctOption(null) // Hidden from students before answering!
                .explanation(null)
                .build();
    }

    public PracticeQuestionAdminDto mapToAdminDto(PracticeQuestion q) {
        List<QuestionOptionDto> options = q.getOptions().stream()
                .map(o -> QuestionOptionDto.builder().optionKey(o.getOptionKey()).optionText(o.getOptionText()).build())
                .collect(Collectors.toList());

        return PracticeQuestionAdminDto.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .examId(q.getExam().getId())
                .subjectId(q.getSubject().getId())
                .topicId(q.getTopic() != null ? q.getTopic().getId() : null)
                .correctOption(q.getCorrectOption())
                .explanation(q.getExplanation())
                .difficulty(q.getDifficulty())
                .marks(q.getMarks())
                .negativeMarks(q.getNegativeMarks())
                .options(options)
                .build();
    }
}
