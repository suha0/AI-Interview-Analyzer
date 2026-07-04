package com.lietech.interviewanalyzer.analysis;

import com.lietech.interviewanalyzer.security.UserPrincipal;

import jakarta.validation.Valid;

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
@RequestMapping("/api/interviews/{interviewId}/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService service;

    @PostMapping("/metrics")
    @ResponseStatus(HttpStatus.CREATED)
    public MetricResponse recordMetric(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId,
            @Valid @RequestBody MetricRequest request
    ) {

        return service.record(principal, interviewId, request);
    }

    @GetMapping("/report")
    public InterviewReport report(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId
    ) {

        return service.report(principal, interviewId);
    }
}