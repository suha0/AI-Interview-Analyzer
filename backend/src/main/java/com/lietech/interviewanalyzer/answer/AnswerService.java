package com.lietech.interviewanalyzer.answer;

import com.lietech.interviewanalyzer.emotion.EmotionResult;
import com.lietech.interviewanalyzer.emotion.EmotionService;
import com.lietech.interviewanalyzer.interview.Interview;
import com.lietech.interviewanalyzer.interview.InterviewRepository;
import com.lietech.interviewanalyzer.question.Question;
import com.lietech.interviewanalyzer.question.QuestionRepository;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final InterviewRepository interviewRepository;
    private final QuestionRepository questionRepository;
    private final WebClient aiServiceWebClient;
    private final EmotionService emotionService;

    public AnswerService(
            AnswerRepository answerRepository,
            InterviewRepository interviewRepository,
            QuestionRepository questionRepository,
            WebClient aiServiceWebClient,
            EmotionService emotionService
    ) {
        this.answerRepository = answerRepository;
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.aiServiceWebClient = aiServiceWebClient;
        this.emotionService = emotionService;
    }

    public AnswerResponse submitAnswer(AnswerRequest request) {

        // =====================
        // FETCH INTERVIEW
        // =====================
        Interview interview = interviewRepository.findById(
                request.getInterviewId()
        ).orElseThrow(() ->
                new RuntimeException("Interview not found"));

        // =====================
        // FETCH QUESTION
        // =====================
        Question question = questionRepository.findById(
                request.getQuestionId()
        ).orElseThrow(() ->
                new RuntimeException("Question not found"));

        // =====================
        // AI ANALYSIS
        // =====================
        Map<String, Object> aiResult =
                aiServiceWebClient.post()
                        .uri("/api/answers/analyze")
                        .bodyValue(
                                Map.of(
                                        "question", question.getPrompt(),
                                        "answer", request.getAnswerText(),
                                        "targetRole", interview.getTargetRole()
                                )
                        )
                        .retrieve()
                        .bodyToMono(
                                new ParameterizedTypeReference<Map<String, Object>>() {}
                        )
                        .block();

        int aiScore = 0;
        String feedback = "";
        String followUpQuestion = null;

        if (aiResult != null) {

            Object scoreObj = aiResult.get("score");

            if (scoreObj instanceof Number number) {
                aiScore = number.intValue();
            }

            feedback = String.valueOf(
                    aiResult.getOrDefault("feedback", "")
            );

            Object followUpObj =
                    aiResult.get("followUpQuestion");

            if (followUpObj != null) {
                followUpQuestion =
                        followUpObj.toString();
            }
        }

        // =====================
        // EMOTION ANALYSIS
        // =====================
        EmotionResult emotion =
                emotionService.analyze(
                        request.getAnswerText()
                );

        // =====================
        // FINAL SCORE
        // =====================
        int finalScore = (int) Math.round(
                (aiScore * 0.75)
                        + (emotion.getConfidenceScore() * 25)
        );

        // =====================
        // SAVE ANSWER
        // =====================
        Answer answer = new Answer();

        answer.setInterview(interview);
        answer.setQuestion(question);
        answer.setAnswerText(
                request.getAnswerText()
        );

        answer.setScore(finalScore);
        answer.setFeedback(feedback);

        answer.setEmotion(
                emotion.getEmotion().name()
        );

        answer.setEmotionScore(
                emotion.getConfidenceScore()
        );

        answer.setEmotionExplanation(
                emotion.getExplanation()
        );

        Answer saved =
                answerRepository.save(answer);

        // =====================
        // RETURN RESPONSE
        // =====================
        return new AnswerResponse(
                saved.getId(),
                saved.getScore(),
                saved.getFeedback(),
                saved.getEmotion(),
                saved.getEmotionScore(),
                saved.getEmotionExplanation(),
                followUpQuestion
        );
    }
}