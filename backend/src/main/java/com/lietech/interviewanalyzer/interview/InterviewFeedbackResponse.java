package com.lietech.interviewanalyzer.interview;

import java.util.List;

public record InterviewFeedbackResponse(
        Integer totalScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> recommendations
) {
}