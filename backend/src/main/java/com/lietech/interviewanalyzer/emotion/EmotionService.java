package com.lietech.interviewanalyzer.emotion;

import org.springframework.stereotype.Service;

@Service
public class EmotionService {

    public EmotionResult analyze(String answerText) {

        EmotionResult result = new EmotionResult();

        if (answerText == null || answerText.trim().isEmpty()) {

            result.setEmotion(EmotionType.NEGATIVE);
            result.setConfidenceScore(0.0);
            result.setExplanation("No answer provided");

            return result;
        }

        String text = answerText.toLowerCase();

        int confidentScore = 0;
        int nervousScore = 0;
        int negativeScore = 0;

        String[] confidentWords = {
                "definitely", "certainly", "successfully",
                "implemented", "developed", "designed",
                "achieved", "led", "managed", "optimized",
                "improved", "built"
        };

        String[] nervousWords = {
                "maybe",
                "perhaps",
                "i think",
                "not sure",
                "possibly"
        };

        String[] negativeWords = {
                "can't",
                "unable",
                "failed",
                "struggle",
                "difficult",
                "problem"
        };

        for (String word : confidentWords) {
            if (text.contains(word)) confidentScore++;
        }

        for (String word : nervousWords) {
            if (text.contains(word)) nervousScore++;
        }

        for (String word : negativeWords) {
            if (text.contains(word)) negativeScore++;
        }

        if (confidentScore > nervousScore && confidentScore > negativeScore) {

            result.setEmotion(EmotionType.CONFIDENT);
            result.setConfidenceScore(
                    Math.min(0.95, 0.60 + confidentScore * 0.05)
            );
            result.setExplanation(
                    "Confident language and achievement-oriented wording detected"
            );
        }
        else if (nervousScore > confidentScore && nervousScore > negativeScore) {

            result.setEmotion(EmotionType.NERVOUS);
            result.setConfidenceScore(
                    Math.min(0.90, 0.50 + nervousScore * 0.05)
            );
            result.setExplanation(
                    "Uncertain or hesitant language detected"
            );
        }
        else if (negativeScore > 0) {

            result.setEmotion(EmotionType.NEGATIVE);
            result.setConfidenceScore(
                    Math.min(0.90, 0.50 + negativeScore * 0.05)
            );
            result.setExplanation(
                    "Negative wording or struggle indicators detected"
            );
        }
        else {

            result.setEmotion(EmotionType.NEUTRAL);
            result.setConfidenceScore(0.50);
            result.setExplanation(
                    "No strong emotional indicators detected"
            );
        }

        return result;
    }
}