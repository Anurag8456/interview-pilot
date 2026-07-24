package com.interviewpilot.backend.service;

import org.springframework.stereotype.Service;

@Service
public class AnswerLengthAnalyzer {

    public String analyzeLength(String answerText, boolean isBehavioral) {
        if (answerText == null || answerText.isBlank()) {
            return "TOO_SHORT";
        }
        int wordCount = answerText.trim().split("\\s+").length;

        if (isBehavioral) {
            if (wordCount < 40) return "TOO_SHORT";
            if (wordCount > 250) return "VERBOSE";
            return "APPROPRIATE";
        } else {
            if (wordCount < 10) return "TOO_SHORT";
            if (wordCount > 150) return "VERBOSE";
            return "APPROPRIATE";
        }
    }
}