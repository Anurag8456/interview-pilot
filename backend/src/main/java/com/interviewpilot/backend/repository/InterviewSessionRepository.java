package com.interviewpilot.backend.repository;

import com.interviewpilot.backend.model.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    List<InterviewSession> findByCandidateLabelIgnoreCaseOrderByCreatedAtDesc(String candidateLabel);
}