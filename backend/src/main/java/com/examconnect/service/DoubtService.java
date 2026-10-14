package com.examconnect.service;

import com.examconnect.dto.*;
import com.examconnect.entity.*;
import com.examconnect.exception.BadRequestException;
import com.examconnect.exception.ForbiddenException;
import com.examconnect.exception.ResourceNotFoundException;
import com.examconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoubtService {

    private final DoubtRepository doubtRepository;
    private final AnswerRepository answerRepository;
    private final AnswerVoteRepository answerVoteRepository;
    private final ContentReportRepository contentReportRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;

    @Transactional(readOnly = true)
    public PagedResponse<DoubtSummaryDto> getDoubts(
            Long examId, Long subjectId, Long topicId, Boolean unansweredOnly,
            String search, int page, int size, String sortBy) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("popular".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "viewsCount");
        } else if ("answers".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "answersCount");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        boolean filterUnanswered = Boolean.TRUE.equals(unansweredOnly);

        Page<Doubt> doubtPage = doubtRepository.searchDoubts(examId, subjectId, topicId, filterUnanswered, search, pageable);

        Page<DoubtSummaryDto> dtoPage = doubtPage.map(this::mapToSummaryDto);

        return PagedResponse.<DoubtSummaryDto>builder()
                .content(dtoPage.getContent())
                .page(dtoPage.getNumber())
                .size(dtoPage.getSize())
                .totalElements(dtoPage.getTotalElements())
                .totalPages(dtoPage.getTotalPages())
                .last(dtoPage.isLast())
                .build();
    }

    @Transactional
    public DoubtDetailDto getDoubtById(Long id, Long currentUserId) {
        Doubt doubt = doubtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doubt not found with ID: " + id));

        // Increment views count
        doubt.setViewsCount(doubt.getViewsCount() + 1);
        doubtRepository.save(doubt);

        List<Answer> answers = answerRepository.findByDoubtIdOrderByIsAcceptedDescUpvotesCountDescCreatedAtDesc(id);

        List<AnswerDto> answerDtos = answers.stream()
                .map(a -> mapAnswerToDto(a, currentUserId))
                .collect(Collectors.toList());

        boolean isOwner = currentUserId != null && doubt.getUser().getId().equals(currentUserId);

        return DoubtDetailDto.builder()
                .id(doubt.getId())
                .title(doubt.getTitle())
                .description(doubt.getDescription())
                .userId(doubt.getUser().getId())
                .userName(doubt.getUser().getFullName())
                .examId(doubt.getExam() != null ? doubt.getExam().getId() : null)
                .examName(doubt.getExam() != null ? doubt.getExam().getName() : null)
                .subjectId(doubt.getSubject() != null ? doubt.getSubject().getId() : null)
                .subjectName(doubt.getSubject() != null ? doubt.getSubject().getName() : null)
                .topicId(doubt.getTopic() != null ? doubt.getTopic().getId() : null)
                .topicName(doubt.getTopic() != null ? doubt.getTopic().getName() : null)
                .attachmentUrl(doubt.getAttachmentUrl())
                .hasAcceptedAnswer(doubt.isHasAcceptedAnswer())
                .acceptedAnswerId(doubt.getAcceptedAnswerId())
                .answersCount(doubt.getAnswersCount())
                .viewsCount(doubt.getViewsCount())
                .isOwner(isOwner)
                .createdAt(doubt.getCreatedAt())
                .answers(answerDtos)
                .build();
    }

    @Transactional
    public DoubtDetailDto createDoubt(CreateDoubtRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Exam exam = null;
        if (request.getExamId() != null) {
            exam = examRepository.findById(request.getExamId()).orElse(null);
        }
        Subject subject = null;
        if (request.getSubjectId() != null) {
            subject = subjectRepository.findById(request.getSubjectId()).orElse(null);
        }
        Topic topic = null;
        if (request.getTopicId() != null) {
            topic = topicRepository.findById(request.getTopicId()).orElse(null);
        }

        Doubt doubt = Doubt.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .user(user)
                .exam(exam)
                .subject(subject)
                .topic(topic)
                .attachmentUrl(request.getAttachmentUrl())
                .answersCount(0)
                .viewsCount(0)
                .build();

        Doubt saved = doubtRepository.save(doubt);
        return getDoubtById(saved.getId(), userId);
    }

    @Transactional
    public DoubtDetailDto updateDoubt(Long id, CreateDoubtRequest request, Long userId) {
        Doubt doubt = doubtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doubt not found with ID: " + id));

        if (!doubt.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You cannot edit a doubt posted by another user");
        }

        doubt.setTitle(request.getTitle().trim());
        doubt.setDescription(request.getDescription().trim());
        if (request.getExamId() != null) {
            doubt.setExam(examRepository.findById(request.getExamId()).orElse(null));
        }
        if (request.getSubjectId() != null) {
            doubt.setSubject(subjectRepository.findById(request.getSubjectId()).orElse(null));
        }
        if (request.getTopicId() != null) {
            doubt.setTopic(topicRepository.findById(request.getTopicId()).orElse(null));
        }
        doubt.setAttachmentUrl(request.getAttachmentUrl());

        doubtRepository.save(doubt);
        return getDoubtById(id, userId);
    }

    @Transactional
    public void deleteDoubt(Long id, Long userId, boolean isAdmin) {
        Doubt doubt = doubtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doubt not found with ID: " + id));

        if (!isAdmin && !doubt.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You cannot delete a doubt posted by another user");
        }

        doubtRepository.deleteById(id);
    }

    // Answers
    @Transactional
    public AnswerDto createAnswer(Long doubtId, CreateAnswerRequest request, Long userId) {
        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new ResourceNotFoundException("Doubt not found with ID: " + doubtId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Answer answer = Answer.builder()
                .doubt(doubt)
                .user(user)
                .content(request.getContent().trim())
                .isAccepted(false)
                .upvotesCount(0)
                .build();

        Answer saved = answerRepository.save(answer);

        doubt.setAnswersCount(doubt.getAnswersCount() + 1);
        doubtRepository.save(doubt);

        return mapAnswerToDto(saved, userId);
    }

    @Transactional
    public AnswerDto updateAnswer(Long answerId, CreateAnswerRequest request, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));

        if (!answer.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You cannot edit an answer written by another user");
        }

        answer.setContent(request.getContent().trim());
        Answer saved = answerRepository.save(answer);

        return mapAnswerToDto(saved, userId);
    }

    @Transactional
    public void deleteAnswer(Long answerId, Long userId, boolean isAdmin) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));

        if (!isAdmin && !answer.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You cannot delete an answer written by another user");
        }

        Doubt doubt = answer.getDoubt();
        if (answer.isAccepted()) {
            doubt.setHasAcceptedAnswer(false);
            doubt.setAcceptedAnswerId(null);
        }
        doubt.setAnswersCount(Math.max(0, doubt.getAnswersCount() - 1));
        doubtRepository.save(doubt);

        answerRepository.deleteById(answerId);
    }

    // Voting
    @Transactional
    public int toggleAnswerVote(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // User cannot vote for their own answer
        if (answer.getUser().getId().equals(userId)) {
            throw new BadRequestException("You cannot upvote your own answer");
        }

        if (answerVoteRepository.existsByAnswerIdAndUserId(answerId, userId)) {
            answerVoteRepository.deleteByAnswerIdAndUserId(answerId, userId);
            answer.setUpvotesCount(Math.max(0, answer.getUpvotesCount() - 1));
        } else {
            AnswerVote vote = AnswerVote.builder().answer(answer).user(user).build();
            answerVoteRepository.save(vote);
            answer.setUpvotesCount(answer.getUpvotesCount() + 1);
        }

        Answer saved = answerRepository.save(answer);
        return saved.getUpvotesCount();
    }

    // Accepted Answer
    @Transactional
    public void acceptAnswer(Long doubtId, Long answerId, Long userId) {
        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new ResourceNotFoundException("Doubt not found with ID: " + doubtId));

        // Rule 1: Only the owner of a question can accept an answer
        if (!doubt.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Only the author of this doubt can accept an answer");
        }

        Answer targetAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with ID: " + answerId));

        // Rule 2: The accepted answer must belong to the same question
        if (!targetAnswer.getDoubt().getId().equals(doubtId)) {
            throw new BadRequestException("This answer does not belong to the specified doubt");
        }

        // Reset any previously accepted answer for this doubt
        List<Answer> doubtAnswers = answerRepository.findByDoubtIdOrderByIsAcceptedDescUpvotesCountDescCreatedAtDesc(doubtId);
        for (Answer a : doubtAnswers) {
            if (a.isAccepted() && !a.getId().equals(answerId)) {
                a.setAccepted(false);
                answerRepository.save(a);
            }
        }

        // Toggle state
        boolean newAcceptedState = !targetAnswer.isAccepted();
        targetAnswer.setAccepted(newAcceptedState);
        answerRepository.save(targetAnswer);

        if (newAcceptedState) {
            doubt.setHasAcceptedAnswer(true);
            doubt.setAcceptedAnswerId(answerId);
        } else {
            doubt.setHasAcceptedAnswer(false);
            doubt.setAcceptedAnswerId(null);
        }

        doubtRepository.save(doubt);
    }

    // Content Reporting
    @Transactional
    public ContentReportDto reportContent(ContentReportRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ContentReport report = ContentReport.builder()
                .reporter(user)
                .contentType(request.getContentType().toUpperCase().trim())
                .contentId(request.getContentId())
                .reason(request.getReason().trim())
                .details(request.getDetails())
                .status("PENDING")
                .build();

        ContentReport saved = contentReportRepository.save(report);
        return mapReportToDto(saved);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ContentReportDto> getReports(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ContentReport> reportPage;
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            reportPage = contentReportRepository.findByStatus(status.trim().toUpperCase(), pageable);
        } else {
            reportPage = contentReportRepository.findAll(pageable);
        }

        Page<ContentReportDto> dtoPage = reportPage.map(this::mapReportToDto);
        return PagedResponse.<ContentReportDto>builder()
                .content(dtoPage.getContent())
                .page(dtoPage.getNumber())
                .size(dtoPage.getSize())
                .totalElements(dtoPage.getTotalElements())
                .totalPages(dtoPage.getTotalPages())
                .last(dtoPage.isLast())
                .build();
    }

    @Transactional
    public void resolveReport(Long reportId, String newStatus) {
        ContentReport report = contentReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with ID: " + reportId));
        report.setStatus(newStatus.toUpperCase().trim());
        report.setResolvedAt(LocalDateTime.now());
        contentReportRepository.save(report);
    }

    private DoubtSummaryDto mapToSummaryDto(Doubt d) {
        return DoubtSummaryDto.builder()
                .id(d.getId())
                .title(d.getTitle())
                .description(d.getDescription().length() > 200 ? d.getDescription().substring(0, 200) + "..." : d.getDescription())
                .userId(d.getUser().getId())
                .userName(d.getUser().getFullName())
                .examId(d.getExam() != null ? d.getExam().getId() : null)
                .examName(d.getExam() != null ? d.getExam().getName() : null)
                .subjectId(d.getSubject() != null ? d.getSubject().getId() : null)
                .subjectName(d.getSubject() != null ? d.getSubject().getName() : null)
                .topicId(d.getTopic() != null ? d.getTopic().getId() : null)
                .topicName(d.getTopic() != null ? d.getTopic().getName() : null)
                .hasAcceptedAnswer(d.isHasAcceptedAnswer())
                .answersCount(d.getAnswersCount())
                .viewsCount(d.getViewsCount())
                .createdAt(d.getCreatedAt())
                .build();
    }

    private AnswerDto mapAnswerToDto(Answer a, Long currentUserId) {
        boolean hasVoted = false;
        boolean isOwner = false;
        if (currentUserId != null) {
            hasVoted = answerVoteRepository.existsByAnswerIdAndUserId(a.getId(), currentUserId);
            isOwner = a.getUser().getId().equals(currentUserId);
        }

        return AnswerDto.builder()
                .id(a.getId())
                .doubtId(a.getDoubt().getId())
                .userId(a.getUser().getId())
                .userName(a.getUser().getFullName())
                .content(a.getContent())
                .isAccepted(a.isAccepted())
                .upvotesCount(a.getUpvotesCount())
                .hasVoted(hasVoted)
                .isOwner(isOwner)
                .createdAt(a.getCreatedAt())
                .build();
    }

    private ContentReportDto mapReportToDto(ContentReport r) {
        return ContentReportDto.builder()
                .id(r.getId())
                .reporterId(r.getReporter().getId())
                .reporterName(r.getReporter().getFullName())
                .contentType(r.getContentType())
                .contentId(r.getContentId())
                .reason(r.getReason())
                .details(r.getDetails())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .resolvedAt(r.getResolvedAt())
                .build();
    }
}
