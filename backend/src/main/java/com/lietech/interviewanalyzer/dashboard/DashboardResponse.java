package com.lietech.interviewanalyzer.dashboard;

import java.util.List;

public record DashboardResponse(
        long totalInterviews,
        long completedInterviews,
        long inProgressInterviews,
        long createdInterviews,
        double averageScore,
        int highestScore,
        int lowestScore,
        List<RecentInterviewResponse> recentInterviews
) {
}