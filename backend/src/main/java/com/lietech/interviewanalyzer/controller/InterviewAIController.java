package com.lietech.interviewanalyzer.controller;

import com.lietech.interviewanalyzer.interview.CreateInterviewRequest;
import com.lietech.interviewanalyzer.interview.Interview;
import com.lietech.interviewanalyzer.interview.InterviewService;
import com.lietech.interviewanalyzer.question.QuestionResponse;
import com.lietech.interviewanalyzer.security.UserPrincipal;
import com.lietech.interviewanalyzer.service.AIQuestionService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class InterviewAIController {

    private final AIQuestionService aiQuestionService;
    private final InterviewService interviewService;

    public InterviewAIController(
            AIQuestionService aiQuestionService,
            InterviewService interviewService
    ) {
        this.aiQuestionService = aiQuestionService;
        this.interviewService = interviewService;
    }

    @PostMapping("/generate-questions")
    public List<QuestionResponse> generateQuestions(
            @RequestBody CreateInterviewRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {

        // Create interview first
        Interview interview =
            interviewService.createInterview(user, request);

        // Generate + save questions
        return aiQuestionService.generateQuestions(
                request,
                interview
        );
    }
}