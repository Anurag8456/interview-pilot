package com.interviewpilot.backend.repository;

import com.interviewpilot.backend.model.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
}