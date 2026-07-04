package com.lietech.interviewanalyzer.dashboard;

import com.lietech.interviewanalyzer.interview.InterviewStatus;

public record RecentInterviewResponse(
        Long id,
        String title,
        Integer score,
        InterviewStatus status
) {
}