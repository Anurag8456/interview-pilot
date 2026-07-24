package com.interviewpilot.backend.dto;

public record SubmitAnswerRequest(Long sessionId, int questionNumber, String answerText) {
}