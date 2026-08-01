package com.upl.tutorial.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upl.tutorial.dto.TutorialManageRequest;
import com.upl.tutorial.dto.TutorialRequest;
import com.upl.tutorial.dto.TutorialResponse;
import com.upl.tutorial.service.TutorialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tutorials")
@RequiredArgsConstructor
public class TutorialController {

    
    private final TutorialService service;

    @GetMapping
    public ResponseEntity<List<TutorialResponse>> tutorials(@RequestParam int courseId){

        List<TutorialResponse> tutorials = service.fetchTutorials(courseId);
        if (tutorials.isEmpty()) {
         return ResponseEntity.noContent().build();
      } 
        return ResponseEntity.ok(tutorials);
    }

    @PostMapping("/add")
    public ResponseEntity<Integer> create(@Valid @RequestBody TutorialRequest request){

        int tutorialId = service.create(request);
        return ResponseEntity.ok(tutorialId);

    }

    @PutMapping
    public ResponseEntity<String> updateTutorial(@Valid @RequestBody TutorialManageRequest request){

        service.updateTutorial(request);
         return ResponseEntity.ok("Sucessfully updated the tutorial");
    }

       
}
