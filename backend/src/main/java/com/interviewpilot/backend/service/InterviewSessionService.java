package com.interviewpilot.backend.service;

import com.interviewpilot.backend.dto.*;
import com.interviewpilot.backend.model.*;
import com.interviewpilot.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewSessionService {

    private final InterviewSessionRepository sessionRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final InterviewReportRepository reportRepository;
    private final InterviewPromptService interviewPromptService;
    private final FillerWordAnalyzer fillerWordAnalyzer;
    private final StarStructureDetector starStructureDetector;
    private final AnswerLengthAnalyzer answerLengthAnalyzer;

    public InterviewSessionService(InterviewSessionRepository sessionRepository,
                                   InterviewQuestionRepository questionRepository,
                                   InterviewAnswerRepository answerRepository,
                                   InterviewReportRepository reportRepository,
                                   InterviewPromptService interviewPromptService,
                                   FillerWordAnalyzer fillerWordAnalyzer,
                                   StarStructureDetector starStructureDetector,
                                   AnswerLengthAnalyzer answerLengthAnalyzer) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.reportRepository = reportRepository;
        this.interviewPromptService = interviewPromptService;
        this.fillerWordAnalyzer = fillerWordAnalyzer;
        this.starStructureDetector = starStructureDetector;
        this.answerLengthAnalyzer = answerLengthAnalyzer;
    }

    public StartInterviewResponse startInterview(StartInterviewRequest request) throws Exception {
        InterviewSession session = new InterviewSession();
        session.setRole(request.role());
        session.setCandidateLabel(request.candidateLabel());
        session = sessionRepository.save(session);

        String questionText = interviewPromptService.generateFirstQuestion(request.role());

        InterviewQuestion question = new InterviewQuestion();
        question.setSessionId(session.getId());
        question.setQuestionNumber(1);
        question.setQuestionText(questionText);
        question.setDifficultyLevel("MEDIUM");
        question.setIsCurveball(false);
        questionRepository.save(question);

        return new StartInterviewResponse(session.getId(), 1, questionText);
    }

    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request) throws Exception {
        InterviewSession session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        InterviewQuestion currentQuestion = questionRepository
                .findBySessionIdAndQuestionNumber(request.sessionId(), request.questionNumber())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        int fillerCount = fillerWordAnalyzer.countFillerWords(request.answerText());
        int starScore = starStructureDetector.scoreStarStructure(request.answerText());
        String lengthFlag = answerLengthAnalyzer.analyzeLength(request.answerText(), true);

        InterviewAnswer answer = new InterviewAnswer();
        answer.setQuestionId(currentQuestion.getId());
        answer.setAnswerText(request.answerText());
        answer.setFillerWordCount(fillerCount);
        answer.setStarScore(starScore);
        answer.setLengthFlag(lengthFlag);
        answerRepository.save(answer);

        if (request.questionNumber() < 5) {
            List<ConversationTurn> history = buildHistory(request.sessionId());
            NextTurnResult nextTurn = interviewPromptService.generateNextTurn(
                    session.getRole(), history, request.questionNumber() + 1);

            InterviewQuestion nextQuestion = new InterviewQuestion();
            nextQuestion.setSessionId(session.getId());
            nextQuestion.setQuestionNumber(request.questionNumber() + 1);
            nextQuestion.setQuestionText(nextTurn.nextQuestionText());
            nextQuestion.setDifficultyLevel(nextTurn.nextDifficultyLevel());
            nextQuestion.setIsCurveball(request.questionNumber() + 1 == 5);
            questionRepository.save(nextQuestion);

            session.setCurrentQuestionNumber(request.questionNumber() + 1);
            sessionRepository.save(session);

            return new SubmitAnswerResponse(false, session.getId(),
                    request.questionNumber() + 1, nextTurn.nextQuestionText(), null);

        } else {
            List<ConversationTurn> fullHistory = buildHistory(request.sessionId());
            FinalReportResult report = interviewPromptService.generateFinalReport(session.getRole(), fullHistory);

            for (QuestionScoreItem item : report.questionScores()) {
                InterviewQuestion q = questionRepository
                        .findBySessionIdAndQuestionNumber(request.sessionId(), item.questionNumber())
                        .orElseThrow();
                InterviewAnswer a = answerRepository.findByQuestionId(q.getId()).orElseThrow();
                a.setLlmScore(item.score());
                a.setStrengths(item.strengths());
                a.setWeaknesses(item.weaknesses());
                a.setInterviewerImpression(item.interviewerImpression());
                answerRepository.save(a);
            }

            InterviewReport reportEntity = new InterviewReport();
            reportEntity.setSessionId(session.getId());
            reportEntity.setOverallScore(report.overallScore());
            reportEntity.setModelAnswerForWeakest(report.modelAnswerForWeakest());
            reportEntity.setWeakestQuestionNumber(report.weakestQuestionNumber());
            reportRepository.save(reportEntity);

            session.setStatus(SessionStatus.COMPLETE);
            sessionRepository.save(session);

            return new SubmitAnswerResponse(true, session.getId(), null, null, report);
        }
    }

    private List<ConversationTurn> buildHistory(Long sessionId) {
        List<InterviewQuestion> questions = questionRepository
                .findBySessionIdOrderByQuestionNumberAsc(sessionId);

        return questions.stream()
                .map(q -> {
                    InterviewAnswer a = answerRepository.findByQuestionId(q.getId()).orElse(null);
                    if (a == null) {
                        return new ConversationTurn(q.getQuestionNumber(), q.getQuestionText(), "", 0, 0, "N/A");
                    }
                    return new ConversationTurn(q.getQuestionNumber(), q.getQuestionText(),
                            a.getAnswerText(), a.getFillerWordCount(), a.getStarScore(), a.getLengthFlag());
                })
                .collect(Collectors.toList());
    }
}