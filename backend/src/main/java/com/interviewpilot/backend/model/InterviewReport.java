package com.interviewpilot.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "interview_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private Double overallScore;

    @Column(length = 2000)
    private String modelAnswerForWeakest;

    private Integer weakestQuestionNumber;

    @Column(length = 2000)
    private String interviewerImpression;
}