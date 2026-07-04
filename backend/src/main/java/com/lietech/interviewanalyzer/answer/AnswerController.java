package com.lietech.interviewanalyzer.answer;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService service;

    public AnswerController(AnswerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerResponse submitAnswer(
            @Valid @RequestBody AnswerRequest request
    ) {
        return service.submitAnswer(request);
    }
}