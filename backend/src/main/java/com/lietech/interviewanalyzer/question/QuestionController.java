package com.lietech.interviewanalyzer.question;

import com.lietech.interviewanalyzer.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interviews/{interviewId}/questions")
public class QuestionController {

    @Autowired
    private QuestionService service;

    @GetMapping
    public List<QuestionResponse> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId
    ) {
        return service.list(principal, interviewId);
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public List<QuestionResponse> generate(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId,
            @Valid @RequestBody GenerateQuestionsRequest request
    ) {
        return service.generate(principal, interviewId, request);
    }
}