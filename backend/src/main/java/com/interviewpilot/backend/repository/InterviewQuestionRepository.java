package com.interviewpilot.backend.repository;

import com.interviewpilot.backend.model.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findBySessionIdOrderByQuestionNumberAsc(Long sessionId);
}