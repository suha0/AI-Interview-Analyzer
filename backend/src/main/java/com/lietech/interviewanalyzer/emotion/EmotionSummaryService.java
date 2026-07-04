package com.lietech.interviewanalyzer.emotion;

import com.lietech.interviewanalyzer.answer.Answer;
import com.lietech.interviewanalyzer.answer.AnswerRepository;
import com.lietech.interviewanalyzer.interview.Interview;
import com.lietech.interviewanalyzer.interview.InterviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class EmotionSummaryService {

    private final AnswerRepository answerRepository;
    private final InterviewRepository interviewRepository;

    public EmotionSummaryService(AnswerRepository answerRepository,
                                 InterviewRepository interviewRepository) {
        this.answerRepository = answerRepository;
        this.interviewRepository = interviewRepository;
    }

    // ==============================
    // GET SUMMARY (API endpoint use)
    // ==============================
    @Transactional(readOnly = true)
    public Map<String, Object> getSummary(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        List<Answer> answers = answerRepository.findByInterviewId(interviewId);

        long total = answers.size();

        long confident = answers.stream()
                .filter(a -> "CONFIDENT".equals(a.getEmotion()))
                .count();

        long nervous = answers.stream()
                .filter(a -> "NERVOUS".equals(a.getEmotion()))
                .count();

        long negative = answers.stream()
                .filter(a -> "NEGATIVE".equals(a.getEmotion()))
                .count();

        double avgScore = answers.stream()
                .mapToInt(Answer::getScore)
                .average()
                .orElse(0.0);

        return Map.of(
                "interviewId", interviewId,
                "totalAnswers", total,
                "confidentCount", confident,
                "nervousCount", nervous,
                "negativeCount", negative,
                "averageScore", avgScore
        );
    }

    // ======================================
    // UPDATE SUMMARY (called after each answer)
    // ======================================
    @Transactional
    public void updateSummary(Interview interview, List<EmotionResult> emotions) {

        if (interview == null || emotions == null || emotions.isEmpty()) {
            return;
        }

        long total = emotions.size();

        long confident = emotions.stream()
                .filter(e -> e.getEmotion() == EmotionType.CONFIDENT)
                .count();

        long nervous = emotions.stream()
                .filter(e -> e.getEmotion() == EmotionType.NERVOUS)
                .count();

        long negative = emotions.stream()
                .filter(e -> e.getEmotion() == EmotionType.NEGATIVE)
                .count();

        double avgConfidence = emotions.stream()
                .mapToDouble(EmotionResult::getConfidenceScore)
                .average()
                .orElse(0.0);

        System.out.println("===== EMOTION SUMMARY UPDATED =====");
        System.out.println("Interview ID: " + interview.getId());
        System.out.println("Total Answers: " + total);
        System.out.println("CONFIDENT: " + confident);
        System.out.println("NERVOUS: " + nervous);
        System.out.println("NEGATIVE: " + negative);
        System.out.println("Avg Confidence: " + avgConfidence);
    }
}