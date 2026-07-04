package com.lietech.interviewanalyzer.interview;

import java.time.Instant;

public record InterviewResponse(
        Long id,
        String title,
        String targetRole,
        InterviewStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt
) {
    public static InterviewResponse from(Interview interview) {
        return new InterviewResponse(
                interview.getId(),
                interview.getTitle(),
                interview.getTargetRole(),
                interview.getStatus(),
                interview.getCreatedAt(),
                interview.getStartedAt(),
                interview.getCompletedAt()
        );
    }
}
