package com.upl.tutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoursePageRequest {
    private int page = 1;          
    private int size = 10;         
    private String sortBy = "title";  
    private String sortDir = "asc"; 
}
