package com.interviewpilot.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewpilot.backend.dto.ConversationTurn;
import com.interviewpilot.backend.dto.FinalReportResult;
import com.interviewpilot.backend.dto.NextTurnResult;
import com.interviewpilot.backend.model.InterviewRole;
import com.interviewpilot.backend.rubric.RoleRubrics;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewPromptService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InterviewPromptService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    private String rubricAsText(InterviewRole role) {
        return RoleRubrics.forRole(role).stream()
                .map(area -> "- " + area.name() + ": " + area.description())
                .collect(Collectors.joining("\n"));
    }

    public String generateFirstQuestion(InterviewRole role) throws Exception {
        String prompt = """
                You are a professional, encouraging technical interviewer conducting a mock interview
                for the role: %s

                The candidate will be evaluated on these focus areas:
                %s

                Ask ONE opening interview question appropriate for this role — behavioral or technical,
                your choice, but appropriate as a first question. Do not ask multiple questions.
                Do not include any preamble or greeting, just the question itself.

                Return ONLY valid JSON with this field:
                { "questionText": "..." }
                """.formatted(role, rubricAsText(role));

        String jsonResponse = geminiService.callGemini(prompt);
        return objectMapper.readTree(jsonResponse).path("questionText").asText();
    }

    public NextTurnResult generateNextTurn(InterviewRole role,
                                           List<ConversationTurn> historySoFar,
                                           int nextQuestionNumber) throws Exception {

        String historyText = historySoFar.stream()
                .map(t -> "Q" + t.questionNumber() + ": " + t.questionText()
                        + "\nCandidate's answer: " + t.answerText()
                        + "\n(signals: fillerWords=" + t.fillerCount() + ", starScore=" + t.starScore()
                        + "/4, lengthFlag=" + t.lengthFlag() + ")")
                .collect(Collectors.joining("\n\n"));

        boolean isCurveball = nextQuestionNumber == 5;

        String curveballInstruction = isCurveball
                ? "This is the FINAL question (question 5). Ask ONE unexpected, off-script curveball "
                  + "question that tests composure and honesty — for example about a mistake, a "
                  + "disagreement, or an ambiguous tradeoff. Do NOT ask a standard rehearsed question."
                : "Ask question " + nextQuestionNumber + " of 5, appropriate for this role.";

        String prompt = """
                You are a professional, encouraging technical interviewer conducting a mock interview
                for the role: %s

                Focus areas being evaluated:
                %s

                Conversation so far:
                %s

                Based on the candidate's most recent answer and its signals above, decide:
                1. A quick internal quality score for their LAST answer, from 0 to 10
                   (consider both the content and the filler/STAR/length signals given)
                2. An appropriate difficulty level for the NEXT question: EASY, MEDIUM, or HARD
                   — if their last answer was strong, increase difficulty; if weak, keep it steady or ease slightly.
                3. %s

                Return ONLY valid JSON with these fields:
                { "nextQuestionText": "...", "nextDifficultyLevel": "EASY|MEDIUM|HARD", "quickScore": 0-10 }
                """.formatted(role, rubricAsText(role), historyText, curveballInstruction);

        String jsonResponse = geminiService.callGemini(prompt);
        return objectMapper.readValue(jsonResponse, NextTurnResult.class);
    }

    public FinalReportResult generateFinalReport(InterviewRole role, List<ConversationTurn> allTurns) throws Exception {
        String historyText = allTurns.stream()
                .map(t -> "Q" + t.questionNumber() + ": " + t.questionText()
                        + "\nCandidate's answer: " + t.answerText()
                        + "\n(signals: fillerWords=" + t.fillerCount() + ", starScore=" + t.starScore()
                        + "/4, lengthFlag=" + t.lengthFlag() + ")")
                .collect(Collectors.joining("\n\n"));

        String prompt = """
                You are a professional, encouraging technical interviewer wrapping up a mock interview
                for the role: %s

                Focus areas being evaluated:
                %s

                Full conversation:
                %s

                For EACH of the 5 questions, provide: a score 0-10, exactly 2 strengths, exactly 2
                weaknesses, and a one-line "interviewerImpression" — what a real interviewer would
                mentally note after hearing this answer in 10 seconds (be honest, specific, not generic).

                Then identify the single weakest-scoring question and write one strong model answer for it.
                Also compute an overall score (0-10) as an average, weighted toward more recent questions.

                Return ONLY valid JSON matching this shape:
                {
                  "questionScores": [
                    { "questionNumber": 1, "score": 0-10, "strengths": ["...", "..."],
                      "weaknesses": ["...", "..."], "interviewerImpression": "..." }
                  ],
                  "overallScore": 0-10,
                  "modelAnswerForWeakest": "...",
                  "weakestQuestionNumber": 1-5
                }
                """.formatted(role, rubricAsText(role), historyText);

        String jsonResponse = geminiService.callGemini(prompt);
        return objectMapper.readValue(jsonResponse, FinalReportResult.class);
    }
}