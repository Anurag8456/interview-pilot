package com.interviewpilot.backend.service;

import org.springframework.stereotype.Service;

@Service
public class StarStructureDetector {

    private static final String[] SITUATION_SIGNALS = {"when i", "at my", "during", "while working", "in my previous"};
    private static final String[] TASK_SIGNALS = {"i needed to", "i was responsible", "my task was", "the goal was", "i had to"};
    private static final String[] ACTION_SIGNALS = {"i decided", "i implemented", "i built", "i created", "i approached this by", "so i"};
    private static final String[] RESULT_SIGNALS = {"as a result", "this led to", "the outcome was", "we achieved", "ultimately", "in the end"};

    public int scoreStarStructure(String answerText) {
        if (answerText == null || answerText.isBlank()) {
            return 0;
        }
        String lowerText = answerText.toLowerCase();
        int score = 0;
        if (containsAny(lowerText, SITUATION_SIGNALS)) score++;
        if (containsAny(lowerText, TASK_SIGNALS)) score++;
        if (containsAny(lowerText, ACTION_SIGNALS)) score++;
        if (containsAny(lowerText, RESULT_SIGNALS)) score++;
        return score;
    }

    private boolean containsAny(String text, String[] signals) {
        for (String signal : signals) {
            if (text.contains(signal)) {
                return true;
            }
        }
        return false;
    }
}