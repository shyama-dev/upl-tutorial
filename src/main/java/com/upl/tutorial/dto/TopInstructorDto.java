package com.upl.tutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopInstructorDto {
    private int instructorId;
    private String instructorName;
    private long totalCourses;
}
