package com.upl.tutorial.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upl.tutorial.dto.AdminDashboardAnalyticsResponse;
import com.upl.tutorial.dto.InstructorApproveRequest;
import com.upl.tutorial.dto.PendingUserResponse;
import com.upl.tutorial.model.ApprovalStatus;
import com.upl.tutorial.model.UserStatus;
import com.upl.tutorial.service.CourseService;
import com.upl.tutorial.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    
    private final UserService service;  
    private final CourseService courseService;  

    @PostMapping("/approve")
    public ResponseEntity<String> approveInstructor( @Valid @RequestBody InstructorApproveRequest request) {
          
        request.setStatus(ApprovalStatus.Approved); 
        service.updateInstructorStatus(request, UserStatus.Active);        

        return ResponseEntity.ok("Instructor Approved");
    }

    @PostMapping("/reject")
    public ResponseEntity<String> rejectInstructor( @Valid @RequestBody InstructorApproveRequest request) {
        request.setStatus(ApprovalStatus.Rejected);
        service.updateInstructorStatus(request, UserStatus.Rejected);
        return ResponseEntity.ok("Instructor Rejected");
    }

    @GetMapping("/analytics")
    public ResponseEntity<AdminDashboardAnalyticsResponse> fetchAdminAnalytics() {
        return ResponseEntity.ok(courseService.fetchAdminAnalytics());
    }

    @GetMapping("/instructors/pending")
    public ResponseEntity<List<PendingUserResponse>> getPendingInstructors() {
        return ResponseEntity.ok(service.getPendingInstructors());
    }

}
