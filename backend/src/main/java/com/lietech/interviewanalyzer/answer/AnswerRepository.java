package com.lietech.interviewanalyzer.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lietech.interviewanalyzer.interview.Interview;

import java.util.List;

public interface AnswerRepository
        extends JpaRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a WHERE a.interview.id = :interviewId")
    List<Answer> findByInterviewId(@Param("interviewId") Long interviewId);

    List<Answer> findByQuestionId(Long questionId);

    List<Answer> findByInterview(Interview interview);
}