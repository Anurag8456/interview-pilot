package com.interviewpilot.backend.controller;

import com.interviewpilot.backend.model.InterviewRole;
import com.interviewpilot.backend.service.InterviewPromptService;
import org.springframework.beans.factory.annotation.Autowired;

import com.interviewpilot.backend.service.AnswerLengthAnalyzer;
import com.interviewpilot.backend.service.FillerWordAnalyzer;
import com.interviewpilot.backend.service.StarStructureDetector;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    private final FillerWordAnalyzer fillerWordAnalyzer;
    private final StarStructureDetector starStructureDetector;
    private final AnswerLengthAnalyzer answerLengthAnalyzer;

    public TestController(FillerWordAnalyzer fillerWordAnalyzer,
                          StarStructureDetector starStructureDetector,
                          AnswerLengthAnalyzer answerLengthAnalyzer) {
        this.fillerWordAnalyzer = fillerWordAnalyzer;
        this.starStructureDetector = starStructureDetector;
        this.answerLengthAnalyzer = answerLengthAnalyzer;
    }

    @PostMapping("/test-analyze")
    public Map<String, Object> testAnalyze(@RequestBody Map<String, String> body) {
        String answerText = body.get("answerText");
        boolean isBehavioral = Boolean.parseBoolean(body.getOrDefault("isBehavioral", "true"));

        int fillerCount = fillerWordAnalyzer.countFillerWords(answerText);
        int starScore = starStructureDetector.scoreStarStructure(answerText);
        String lengthFlag = answerLengthAnalyzer.analyzeLength(answerText, isBehavioral);

        return Map.of(
                "fillerCount", fillerCount,
                "fillerSeverity", fillerWordAnalyzer.severityLabel(fillerCount),
                "starScore", starScore,
                "lengthFlag", lengthFlag
        );
    }
    @Autowired
    private InterviewPromptService interviewPromptService;

    @GetMapping("/test-first-question")
    public String testFirstQuestion(@RequestParam InterviewRole role) throws Exception {
        return interviewPromptService.generateFirstQuestion(role);
    }
}

