package com.interviewpilot.backend.repository;

import com.interviewpilot.backend.model.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, Long> {
    Optional<InterviewAnswer> findByQuestionId(Long questionId);
}