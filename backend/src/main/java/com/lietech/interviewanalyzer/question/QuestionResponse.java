package com.lietech.interviewanalyzer.question;

public record QuestionResponse(
        Long id,
        String prompt,
        String category,
        int position
) {

    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getPrompt(),
                question.getCategory(),
                question.getPosition()
        );
    }
}