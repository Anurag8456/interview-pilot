package com.interviewpilot.backend.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FillerWordAnalyzer {

    private static final List<String> FILLER_PHRASES = List.of(
            "um", "uh", "like", "basically", "i think", "sort of", "kind of", "you know", "actually", "literally"
    );

    public int countFillerWords(String answerText) {
        if (answerText == null || answerText.isBlank()) {
            return 0;
        }
        String lowerText = answerText.toLowerCase();
        int count = 0;
        for (String phrase : FILLER_PHRASES) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(phrase) + "\\b");
            Matcher matcher = pattern.matcher(lowerText);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }

    public String severityLabel(int fillerCount) {
        if (fillerCount <= 2) return "LOW";
        if (fillerCount <= 5) return "MODERATE";
        return "HIGH";
    }
}