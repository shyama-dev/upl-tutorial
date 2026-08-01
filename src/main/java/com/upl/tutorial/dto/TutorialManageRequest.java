package com.upl.tutorial.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorialManageRequest {
    
     @Min(value = 1, message = "Instructor ID must be at least 1")
    private int tutorialId;

    private String title;
    private String content;
    private String youtubeLink;
    private String changes;

}
