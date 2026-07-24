package com.interviewpilot.backend.dto;

public record ConversationTurn(
        int questionNumber,
        String questionText,
        String answerText,
        int fillerCount,
        int starScore,
        String lengthFlag
) {
}