package com.upl.tutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoursePageResponse {

    private String title;
    private String description;
    private Integer courseId;
    private InstructorResponse instructor;
    private String status;
        
}
