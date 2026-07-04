package com.lietech.interviewanalyzer.interview;

import com.lietech.interviewanalyzer.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService service;

    public InterviewController(InterviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<Interview> listMine(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return service.getMyInterviews(principal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Interview create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateInterviewRequest request
    ) {
        return service.createInterview(
                principal,
                request
        );
    }

    @PostMapping("/{interviewId}/start")
    public Interview start(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId
    ) {
        return service.startInterview(
                principal,
                interviewId
        );
    }

    @PostMapping("/{interviewId}/complete")
    public Interview complete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId
    ) {
        return service.completeInterview(
                principal,
                interviewId
        );
    }

    @GetMapping("/{interviewId}")
    public Interview getInterview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId
    ) {
        return service.getInterview(
                principal,
                interviewId
        );
    }

    @GetMapping("/{interviewId}/result")
    public InterviewResultResponse getResult(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId
    ) {
        return service.getResult(
                principal,
                interviewId
        );
    }

    @GetMapping("/{interviewId}/feedback")
    public String getFeedback(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId
    ) {
        return service.generateFinalFeedback(
                principal,
                interviewId
        );
    }

    @GetMapping("/{interviewId}/report")
    public ResponseEntity<byte[]> exportPdfReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long interviewId
    ) {

        byte[] pdf = service.generatePdfReport(
                principal,
                interviewId
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=interview-report-" +
                                interviewId +
                                ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}