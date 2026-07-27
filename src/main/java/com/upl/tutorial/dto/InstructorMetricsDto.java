package com.upl.tutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorMetricsDto {
    private long totalInstructors;
    private long activeCount;
    private long pendingCount;
    private long rejectedCount;
    
}
