package com.upl.tutorial.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardAnalyticsResponse {
    private InstructorMetricsDto instructorMetrics;
    private long totalPublishedCourses;
    private List<TopInstructorDto> topInstructors;
}
