package com.interviewpilot.backend.rubric;

import com.interviewpilot.backend.model.InterviewRole;
import java.util.List;
import java.util.Map;

public class RoleRubrics {

    private static final List<RubricArea> SDE_INTERN_AREAS = List.of(
            new RubricArea("Problem-Solving Approach", "Breaks the problem down logically before jumping to a solution"),
            new RubricArea("Code Correctness Reasoning", "Explains why an approach works, not just what it does"),
            new RubricArea("Communication Clarity", "Explains technical ideas in a way a non-expert could follow"),
            new RubricArea("Edge-Case Awareness", "Proactively considers edge cases and failure conditions")
    );

    private static final List<RubricArea> DATA_ANALYST_AREAS = List.of(
            new RubricArea("Metrics-Driven Thinking", "Anchors answers in specific numbers, KPIs, or measurable outcomes"),
            new RubricArea("Business Framing", "Connects data work back to a real business question or decision"),
            new RubricArea("Tool Fluency", "Speaks concretely about tools/methods used, not just vague description"),
            new RubricArea("Assumption-Stating", "Explicitly states assumptions made when data is incomplete")
    );

    private static final List<RubricArea> FRONTEND_DEV_AREAS = List.of(
            new RubricArea("UI/UX Reasoning", "Justifies interface decisions based on user experience, not just preference"),
            new RubricArea("Performance Awareness", "Considers load time, rendering cost, or responsiveness"),
            new RubricArea("Component Design Thinking", "Thinks in reusable, well-structured components"),
            new RubricArea("Accessibility Awareness", "Considers accessibility (screen readers, keyboard nav, contrast)")
    );

    private static final Map<InterviewRole, List<RubricArea>> RUBRICS = Map.of(
            InterviewRole.SDE_INTERN, SDE_INTERN_AREAS,
            InterviewRole.DATA_ANALYST, DATA_ANALYST_AREAS,
            InterviewRole.FRONTEND_DEVELOPER, FRONTEND_DEV_AREAS
    );

    public static List<RubricArea> forRole(InterviewRole role) {
        return RUBRICS.get(role);
    }
}