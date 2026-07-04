package com.lietech.interviewanalyzer.service;

import com.lietech.interviewanalyzer.interview.CreateInterviewRequest;
import com.lietech.interviewanalyzer.interview.Interview;
import com.lietech.interviewanalyzer.question.Question;
import com.lietech.interviewanalyzer.question.QuestionRepository;
import com.lietech.interviewanalyzer.question.QuestionResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIQuestionService {

    private final RestTemplate restTemplate;
    private final QuestionRepository questionRepository;

    @Value("${app.ai-service.base-url}")
    private String aiServiceUrl;

    public AIQuestionService(
            RestTemplate restTemplate,
            QuestionRepository questionRepository
    ) {
        this.restTemplate = restTemplate;
        this.questionRepository = questionRepository;
    }

    // =====================================
    // GENERATE INITIAL INTERVIEW QUESTIONS
    // =====================================

    public List<QuestionResponse> generateQuestions(
            CreateInterviewRequest request,
            Interview interview
    ) {

        String url = aiServiceUrl + "/generate-questions";

        Map<String, Object> body = new HashMap<>();

        body.put("targetRole", request.targetRole());
        body.put("difficulty", request.difficulty());
        body.put("count", request.count());

        Map response = restTemplate.postForObject(
                url,
                body,
                Map.class
        );

        List<String> questions =
                ((List<String>) response.get("questions"))
                        .stream()
                        .filter(q -> q != null)
                        .map(String::trim)
                        .filter(q -> q.length() > 15)
                        .toList();

        List<QuestionResponse> result = new ArrayList<>();

        int position = 1;

        for (String q : questions) {

            Question question = new Question();

            question.setInterview(interview);
            question.setPrompt(q);
            question.setCategory("AI");
            question.setPosition(position++);

            Question savedQuestion =
                    questionRepository.save(question);

            result.add(
                    QuestionResponse.from(savedQuestion)
            );
        }

        return result;
    }

    // =====================================
    // GENERATE FOLLOW-UP QUESTION
    // =====================================

    public QuestionResponse generateFollowUpQuestion(
            Interview interview,
            String originalQuestion,
            String candidateAnswer
    ) {

        String url = aiServiceUrl + "/generate-followup";

        Map<String, Object> body = new HashMap<>();

        body.put("question", originalQuestion);
        body.put("answer", candidateAnswer);

        Map response = restTemplate.postForObject(
                url,
                body,
                Map.class
        );

        String followUpQuestion =
                String.valueOf(
                        response.getOrDefault(
                                "question",
                                "Can you explain that in more detail?"
                        )
                );

        Integer nextPosition =
                questionRepository
                        .findByInterviewIdOrderByPositionAsc(interview.getId())
                        .size() + 1;

        Question question = new Question();

        question.setInterview(interview);
        question.setPrompt(followUpQuestion);
        question.setCategory("FOLLOW_UP");
        question.setPosition(nextPosition);

        Question savedQuestion =
                questionRepository.save(question);

        return QuestionResponse.from(savedQuestion);
    }
}