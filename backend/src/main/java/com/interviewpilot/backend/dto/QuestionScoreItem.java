package com.interviewpilot.backend.dto;

import java.util.List;

public record QuestionScoreItem(
        int questionNumber,
        double score,
        List<String> strengths,
        List<String> weaknesses,
        String interviewerImpression
) {
}