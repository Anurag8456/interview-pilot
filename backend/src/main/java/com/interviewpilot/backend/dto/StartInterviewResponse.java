package com.interviewpilot.backend.dto;

public record StartInterviewResponse(Long sessionId, int questionNumber, String questionText) {
}