package com.lietech.interviewanalyzer.interview;

import java.time.Instant;

public record InterviewResultResponse(
        Long interviewId,
        String title,
        String targetRole,
        Integer totalScore,
        InterviewStatus status,
        Instant completedAt
) {
}