package com.interviewpilot.backend.dto;

import com.interviewpilot.backend.model.InterviewRole;

public record StartInterviewRequest(InterviewRole role, String candidateLabel) {
}