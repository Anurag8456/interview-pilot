package com.interviewpilot.backend.dto;

public record SubmitAnswerResponse(
        boolean isComplete,
        Long sessionId,
        Integer nextQuestionNumber,
        String nextQuestionText,
        FinalReportResult report
) {
}