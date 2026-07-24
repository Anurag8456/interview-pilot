package com.interviewpilot.backend.dto;

public record NextTurnResult(
        String nextQuestionText,
        String nextDifficultyLevel,
        double quickScore
) {
}