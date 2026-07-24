package com.interviewpilot.backend.repository;

import com.interviewpilot.backend.model.InterviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterviewReportRepository extends JpaRepository<InterviewReport, Long> {
}