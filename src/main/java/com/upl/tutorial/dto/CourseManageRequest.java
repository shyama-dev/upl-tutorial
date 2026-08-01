package com.upl.tutorial.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseManageRequest {
    @Min(value = 1, message = "Instructor ID must be at least 1")
    private int courseId;
    
     private String title;
    
    private String description;    

    private String changes;
}
