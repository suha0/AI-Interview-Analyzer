package com.lietech.interviewanalyzer.interview;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository
        extends JpaRepository<Interview, Long> {

    List<Interview> findByCandidateIdOrderByCreatedAtDesc(
            Long candidateId
    );

    long countByCandidateId(Long candidateId);

    long countByCandidateIdAndStatus(
            Long candidateId,
            InterviewStatus status
    );

    List<Interview> findTop5ByCandidateIdOrderByCreatedAtDesc(
            Long candidateId
    );

    List<Interview> findByCandidateIdAndStatusOrderByCompletedAtAsc(
            Long candidateId,
            InterviewStatus status
    );
}