package com.lietech.interviewanalyzer.answer;

public record FollowUpRequest(
        String question,
        String answer,
        String targetRole
) {
}