package com.lietech.interviewanalyzer.dashboard;

import com.lietech.interviewanalyzer.interview.*;
import com.lietech.interviewanalyzer.security.UserPrincipal;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final InterviewRepository repository;

    public DashboardService(
            InterviewRepository repository
    ) {
        this.repository = repository;
    }

    public DashboardResponse getDashboard(
            UserPrincipal principal
    ) {

        Long userId = principal.id();

        List<Interview> interviews =
                repository.findByCandidateIdOrderByCreatedAtDesc(
                        userId
                );

        long total =
                repository.countByCandidateId(userId);

        long completed =
                repository.countByCandidateIdAndStatus(
                        userId,
                        InterviewStatus.COMPLETED
                );

        long inProgress =
                repository.countByCandidateIdAndStatus(
                        userId,
                        InterviewStatus.IN_PROGRESS
                );

        long created =
                repository.countByCandidateIdAndStatus(
                        userId,
                        InterviewStatus.CREATED
                );

        double averageScore =
                interviews.stream()
                        .filter(i -> i.getTotalScore() != null)
                        .mapToInt(Interview::getTotalScore)
                        .average()
                        .orElse(0);

        int highestScore =
                interviews.stream()
                        .filter(i -> i.getTotalScore() != null)
                        .mapToInt(Interview::getTotalScore)
                        .max()
                        .orElse(0);

        int lowestScore =
                interviews.stream()
                        .filter(i -> i.getTotalScore() != null)
                        .mapToInt(Interview::getTotalScore)
                        .min()
                        .orElse(0);

        List<RecentInterviewResponse> recent =
                repository.findTop5ByCandidateIdOrderByCreatedAtDesc(
                                userId
                        )
                        .stream()
                        .map(i ->
                                new RecentInterviewResponse(
                                        i.getId(),
                                        i.getTitle(),
                                        i.getTotalScore(),
                                        i.getStatus()
                                )
                        )
                        .toList();

        return new DashboardResponse(
                total,
                completed,
                inProgress,
                created,
                averageScore,
                highestScore,
                lowestScore,
                recent
        );
    }

    public List<ProgressResponse> getProgress(
            UserPrincipal principal
    ) {

        return repository
                .findByCandidateIdAndStatusOrderByCompletedAtAsc(
                        principal.id(),
                        InterviewStatus.COMPLETED
                )
                .stream()
                .map(interview ->
                        new ProgressResponse(
                                interview.getId(),
                                interview.getTitle(),
                                interview.getTotalScore(),
                                interview.getCompletedAt()
                        )
                )
                .toList();
    }
}
