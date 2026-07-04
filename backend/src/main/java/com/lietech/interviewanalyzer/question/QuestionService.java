package com.lietech.interviewanalyzer.question;

import com.lietech.interviewanalyzer.interview.Interview;
import com.lietech.interviewanalyzer.interview.InterviewRepository;
import com.lietech.interviewanalyzer.security.UserPrincipal;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuestionService {

    private final InterviewRepository interviews;
    private final QuestionRepository questions;
    private final WebClient aiServiceWebClient;

    public QuestionService(
            InterviewRepository interviews,
            QuestionRepository questions,
            WebClient aiServiceWebClient
    ) {
        this.interviews = interviews;
        this.questions = questions;
        this.aiServiceWebClient = aiServiceWebClient;
    }

    @Transactional
    public List<QuestionResponse> generate(
            UserPrincipal principal,
            Long interviewId,
            GenerateQuestionsRequest request
    ) {

        Interview interview = ownedInterview(principal, interviewId);

        // AI Request
        AiQuestionRequest aiRequest = new AiQuestionRequest(
                interview.getTargetRole(),
                request.difficulty(),
                request.count()
        );

        // Call AI service
        AiQuestionResponse aiResponse = aiServiceWebClient.post()
                .uri("/generate-questions")
                .bodyValue(aiRequest)
                .retrieve()
                .bodyToMono(AiQuestionResponse.class)
                .block();

        if (aiResponse == null || aiResponse.questions() == null || aiResponse.questions().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to generate questions from AI service"
            );
        }

        int index = 1;

        for (String prompt : aiResponse.questions()) {

            Question question = new Question();

            question.setInterview(interview);
            question.setPrompt(prompt);
            question.setCategory(request.difficulty());
            question.setPosition(index++);

            questions.save(question);
        }

        return list(principal, interviewId);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> list(
            UserPrincipal principal,
            Long interviewId
    ) {

        ownedInterview(principal, interviewId);

        return questions.findByInterviewIdOrderByPositionAsc(interviewId)
                .stream()
                .map(QuestionResponse::from)
                .toList();
    }

    private Interview ownedInterview(
            UserPrincipal principal,
            Long interviewId
    ) {

        Interview interview = interviews.findById(interviewId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Interview not found"
                        )
                );

        if (!interview.getCandidateId().equals(principal.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return interview;
    }

    // AI DTOs
    private record AiQuestionRequest(
            String targetRole,
            String difficulty,
            int count
    ) {}

    private record AiQuestionResponse(
            List<String> questions
    ) {}
}