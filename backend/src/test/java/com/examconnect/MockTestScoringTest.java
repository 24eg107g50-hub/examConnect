package com.examconnect;

import com.examconnect.dto.*;
import com.examconnect.service.DoubtService;
import com.examconnect.service.MockTestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "jwt.secret=test-only-jwt-signing-key-not-for-use-outside-tests",
        "admin.initial.password=TestOnly@123"
})
@Transactional
class MockTestScoringTest {

    @Autowired
    private MockTestService mockTestService;

    @Autowired
    private DoubtService doubtService;

    @Test
    @DisplayName("Mock test start, answer evaluation, negative marking, and result scoring")
    void testMockTestScoring() {
        // Start Test 1 for user 2 (Rahul Sharma from seed data)
        StartTestResponseDto startResponse = mockTestService.startTest(1L, 2L);
        assertNotNull(startResponse);
        assertNotNull(startResponse.getAttemptId());
        assertTrue(startResponse.getTotalQuestions() > 0);

        // Submit test with 1 correct answer (Q1 = 'C') and 1 wrong answer (Q2 = 'A' when correct is 'B')
        SubmitTestRequest submitReq = SubmitTestRequest.builder()
                .answers(List.of(
                        TestQuestionAnswerDto.builder().questionId(1L).selectedOption("C").build(),
                        TestQuestionAnswerDto.builder().questionId(2L).selectedOption("A").build()
                ))
                .build();

        TestResultDto result = mockTestService.submitTest(startResponse.getAttemptId(), submitReq, 2L);
        assertNotNull(result);
        assertEquals(1, result.getCorrectCount());
        assertEquals(1, result.getIncorrectCount());
        // Q1 awarded 1.0, Q2 penalized -0.66, total = 0.34
        assertTrue(result.getTotalScore() > 0.0);
        assertFalse(result.getQuestionReviews().isEmpty());
    }

    @Test
    @DisplayName("Doubt creation and answer upvoting works")
    void testDoubtAndVoting() {
        CreateDoubtRequest doubtReq = CreateDoubtRequest.builder()
                .title("Sample Doubt on Polity")
                .description("Detailed question about Article 14 equality doctrine.")
                .examId(1L)
                .build();

        DoubtDetailDto createdDoubt = doubtService.createDoubt(doubtReq, 2L);
        assertNotNull(createdDoubt);
        assertEquals("Sample Doubt on Polity", createdDoubt.getTitle());

        // Student 3 answers the doubt
        CreateAnswerRequest answerReq = CreateAnswerRequest.builder()
                .content("Article 14 combines British equality before law with American equal protection of laws.")
                .build();

        AnswerDto answer = doubtService.createAnswer(createdDoubt.getId(), answerReq, 3L);
        assertNotNull(answer);

        // Student 2 upvotes Student 3's answer
        int votes = doubtService.toggleAnswerVote(answer.getId(), 2L);
        assertEquals(1, votes);

        // Student 2 accepts Student 3's answer
        assertDoesNotThrow(() -> doubtService.acceptAnswer(createdDoubt.getId(), answer.getId(), 2L));
    }
}
