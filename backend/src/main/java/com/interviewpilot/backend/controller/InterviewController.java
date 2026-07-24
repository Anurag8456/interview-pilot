package com.interviewpilot.backend.controller;

import com.interviewpilot.backend.dto.*;
import com.interviewpilot.backend.model.InterviewSession;
import com.interviewpilot.backend.repository.InterviewSessionRepository;
import com.interviewpilot.backend.service.InterviewSessionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewSessionService interviewSessionService;
    private final InterviewSessionRepository interviewSessionRepository;

    public InterviewController(InterviewSessionService interviewSessionService,
                               InterviewSessionRepository interviewSessionRepository) {
        this.interviewSessionService = interviewSessionService;
        this.interviewSessionRepository = interviewSessionRepository;
    }

    @PostMapping("/start")
    public StartInterviewResponse start(@RequestBody StartInterviewRequest request) throws Exception {
        return interviewSessionService.startInterview(request);
    }

    @PostMapping("/answer")
    public SubmitAnswerResponse answer(@RequestBody SubmitAnswerRequest request) throws Exception {
        return interviewSessionService.submitAnswer(request);
    }

    @GetMapping("/history/{candidateLabel}")
    public List<InterviewSession> history(@PathVariable String candidateLabel) {
        return interviewSessionRepository.findByCandidateLabelIgnoreCaseOrderByCreatedAtDesc(candidateLabel);
    }
}