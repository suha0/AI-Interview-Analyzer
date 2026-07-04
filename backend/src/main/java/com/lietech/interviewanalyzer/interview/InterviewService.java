package com.lietech.interviewanalyzer.interview;

import com.lietech.interviewanalyzer.answer.Answer;
import com.lietech.interviewanalyzer.answer.AnswerRepository;
import com.lietech.interviewanalyzer.report.PdfService;
import com.lietech.interviewanalyzer.security.UserPrincipal;
import com.lietech.interviewanalyzer.service.AIQuestionService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class InterviewService {

    private final InterviewRepository repository;
    private final AIQuestionService aiQuestionService;
    private final AnswerRepository answerRepository;
    private final PdfService pdfService;

    public InterviewService(
            InterviewRepository repository,
            AIQuestionService aiQuestionService,
            AnswerRepository answerRepository,
            PdfService pdfService
    ) {
        this.repository = repository;
        this.aiQuestionService = aiQuestionService;
        this.answerRepository = answerRepository;
        this.pdfService = pdfService;
    }

    public List<Interview> getMyInterviews(UserPrincipal user) {
        return repository.findByCandidateIdOrderByCreatedAtDesc(user.id());
    }

    public Interview createInterview(
            UserPrincipal user,
            CreateInterviewRequest request
    ) {

        Interview interview = new Interview();

        interview.setCandidateId(user.id());
        interview.setTitle(request.title());
        interview.setTargetRole(request.targetRole());

        interview.setStatus(InterviewStatus.CREATED);
        interview.setCreatedAt(Instant.now());

        Interview savedInterview = repository.save(interview);

        aiQuestionService.generateQuestions(
                request,
                savedInterview
        );

        return savedInterview;
    }

    public Interview startInterview(
            UserPrincipal principal,
            Long id
    ) {

        Interview interview = getOwnedInterview(
                principal,
                id
        );

        interview.setStatus(InterviewStatus.IN_PROGRESS);
        interview.setStartedAt(Instant.now());

        return repository.save(interview);
    }

    public Interview completeInterview(
            UserPrincipal principal,
            Long id
    ) {

        Interview interview = getOwnedInterview(
                principal,
                id
        );

        List<Answer> answers =
                answerRepository.findByInterviewId(id);

        int averageScore = (int) answers.stream()
                .filter(answer -> answer.getScore() != null)
                .mapToInt(Answer::getScore)
                .average()
                .orElse(0);

        interview.setTotalScore(averageScore);
        interview.setStatus(InterviewStatus.COMPLETED);
        interview.setCompletedAt(Instant.now());

        return repository.save(interview);
    }

    public Interview getInterview(
            UserPrincipal principal,
            Long id
    ) {
        return getOwnedInterview(
                principal,
                id
        );
    }

    public InterviewResultResponse getResult(
            UserPrincipal principal,
            Long id
    ) {

        Interview interview = getOwnedInterview(
                principal,
                id
        );

        return new InterviewResultResponse(
                interview.getId(),
                interview.getTitle(),
                interview.getTargetRole(),
                interview.getTotalScore(),
                interview.getStatus(),
                interview.getCompletedAt()
        );
    }

    public String generateFinalFeedback(
            UserPrincipal principal,
            Long interviewId
    ) {

        Interview interview = getOwnedInterview(
                principal,
                interviewId
        );

        Integer totalScore = interview.getTotalScore();

        if (totalScore == null) {
            return """
                    Interview is not completed yet.
                    Complete the interview to receive AI feedback.
                    """;
        }

        if (totalScore >= 90) {
            return """
                    Outstanding Performance

                    • Excellent technical knowledge
                    • Strong communication skills
                    • Clear and confident responses
                    • Industry-ready interview performance

                    Recommendation:
                    Continue practicing advanced scenarios and system design questions.
                    """;
        }

        if (totalScore >= 75) {
            return """
                    Very Good Performance

                    • Strong understanding of core concepts
                    • Good communication abilities
                    • Consistent answer quality

                    Recommendation:
                    Improve answer depth and provide more real-world examples.
                    """;
        }

        if (totalScore >= 60) {
            return """
                    Good Performance

                    • Fundamental concepts are understood
                    • Some answers need more clarity
                    • Communication can be improved

                    Recommendation:
                    Focus on explaining solutions step-by-step and strengthen technical depth.
                    """;
        }

        if (totalScore >= 40) {
            return """
                    Average Performance

                    • Basic understanding is present
                    • Technical gaps were identified
                    • Confidence and communication need improvement

                    Recommendation:
                    Review fundamentals and practice mock interviews regularly.
                    """;
        }

        return """
                Needs Significant Improvement

                • Important technical concepts are missing
                • Communication requires improvement
                • More interview practice is recommended

                Recommendation:
                Revisit core topics, solve practice problems, and attend mock interview sessions.
                """;
    }

    private Interview getOwnedInterview(
            UserPrincipal principal,
            Long interviewId
    ) {

        Interview interview = repository.findById(interviewId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Interview not found"
                        )
                );

        if (!interview.getCandidateId().equals(principal.id())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }

        return interview;
    }
    public byte[] generatePdfReport(
        UserPrincipal principal,
        Long interviewId
) {

    Interview interview =
            getOwnedInterview(
                    principal,
                    interviewId
            );

    String feedback =
            generateFinalFeedback(
                    principal,
                    interviewId
            );

    return pdfService.generateInterviewReport(
            interview,
            feedback
    );
}
}