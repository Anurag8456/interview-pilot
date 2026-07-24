package com.interviewpilot.backend.dto;

import java.util.List;

public record FinalReportResult(
        List<QuestionScoreItem> questionScores,
        double overallScore,
        String modelAnswerForWeakest,
        int weakestQuestionNumber
) {
}