package com.examconnect.service;

import com.examconnect.dto.ExamDto;
import com.examconnect.dto.SubjectDto;
import com.examconnect.dto.TopicDto;
import com.examconnect.entity.Exam;
import com.examconnect.entity.Subject;
import com.examconnect.entity.Topic;
import com.examconnect.exception.BadRequestException;
import com.examconnect.exception.ResourceNotFoundException;
import com.examconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final PracticeQuestionRepository practiceQuestionRepository;
    private final MockTestRepository mockTestRepository;

    @Transactional(readOnly = true)
    public List<ExamDto> getAllExams(String category) {
        List<Exam> exams;
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) {
            exams = examRepository.findByCategoryIgnoreCaseAndActiveTrue(category.trim());
        } else {
            exams = examRepository.findByActiveTrue();
        }

        return exams.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExamDto getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with ID: " + id));
        return mapToDto(exam);
    }

    @Transactional(readOnly = true)
    public List<SubjectDto> getSubjectsByExamId(Long examId) {
        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("Exam not found with ID: " + examId);
        }
        return subjectRepository.findByExamIdAndActiveTrue(examId).stream()
                .map(this::mapSubjectToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopicDto> getTopicsBySubjectId(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
        return topicRepository.findBySubjectIdAndActiveTrue(subjectId).stream()
                .map(this::mapTopicToDto)
                .collect(Collectors.toList());
    }

    // Admin methods
    @Transactional
    public ExamDto createExam(ExamDto dto) {
        if (examRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("An exam with code '" + dto.getCode() + "' already exists");
        }

        Exam exam = Exam.builder()
                .code(dto.getCode().toUpperCase().trim())
                .name(dto.getName().trim())
                .description(dto.getDescription())
                .category(dto.getCategory().trim())
                .active(dto.isActive())
                .build();

        return mapToDto(examRepository.save(exam));
    }

    @Transactional
    public ExamDto updateExam(Long id, ExamDto dto) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with ID: " + id));

        exam.setName(dto.getName().trim());
        exam.setDescription(dto.getDescription());
        exam.setCategory(dto.getCategory().trim());
        exam.setActive(dto.isActive());

        return mapToDto(examRepository.save(exam));
    }

    @Transactional
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exam not found with ID: " + id);
        }
        examRepository.deleteById(id);
    }

    @Transactional
    public SubjectDto createSubject(SubjectDto dto) {
        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with ID: " + dto.getExamId()));

        Subject subject = Subject.builder()
                .exam(exam)
                .name(dto.getName().trim())
                .code(dto.getCode() != null ? dto.getCode().trim() : null)
                .description(dto.getDescription())
                .active(dto.isActive())
                .build();

        return mapSubjectToDto(subjectRepository.save(subject));
    }

    @Transactional
    public TopicDto createTopic(TopicDto dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + dto.getSubjectId()));

        Topic topic = Topic.builder()
                .subject(subject)
                .name(dto.getName().trim())
                .description(dto.getDescription())
                .active(dto.isActive())
                .build();

        return mapTopicToDto(topicRepository.save(topic));
    }

    private ExamDto mapToDto(Exam exam) {
        long subjectsCount = subjectRepository.findByExamIdAndActiveTrue(exam.getId()).size();
        long materialsCount = studyMaterialRepository.searchMaterials(exam.getId(), null, null, null, null).getTotalElements();
        long questionsCount = practiceQuestionRepository.countByExamId(exam.getId());
        long mockTestsCount = mockTestRepository.findByExamIdAndActiveTrue(exam.getId()).size();

        return ExamDto.builder()
                .id(exam.getId())
                .code(exam.getCode())
                .name(exam.getName())
                .description(exam.getDescription())
                .category(exam.getCategory())
                .active(exam.isActive())
                .subjectsCount(subjectsCount)
                .materialsCount(materialsCount)
                .questionsCount(questionsCount)
                .mockTestsCount(mockTestsCount)
                .build();
    }

    private SubjectDto mapSubjectToDto(Subject subject) {
        long topicsCount = topicRepository.findBySubjectIdAndActiveTrue(subject.getId()).size();
        return SubjectDto.builder()
                .id(subject.getId())
                .examId(subject.getExam().getId())
                .examName(subject.getExam().getName())
                .name(subject.getName())
                .code(subject.getCode())
                .description(subject.getDescription())
                .active(subject.isActive())
                .topicsCount(topicsCount)
                .build();
    }

    private TopicDto mapTopicToDto(Topic topic) {
        return TopicDto.builder()
                .id(topic.getId())
                .subjectId(topic.getSubject().getId())
                .subjectName(topic.getSubject().getName())
                .name(topic.getName())
                .description(topic.getDescription())
                .active(topic.isActive())
                .build();
    }
}
