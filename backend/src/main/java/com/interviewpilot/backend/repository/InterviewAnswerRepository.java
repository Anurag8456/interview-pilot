package com.interviewpilot.backend.repository;

import com.interviewpilot.backend.model.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, Long> {
}