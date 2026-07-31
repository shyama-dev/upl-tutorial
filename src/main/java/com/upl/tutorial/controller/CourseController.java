package com.upl.tutorial.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upl.tutorial.dto.CourseManageRequest;
import com.upl.tutorial.dto.CoursePageRequest;
import com.upl.tutorial.dto.CoursePageResponse;
import com.upl.tutorial.dto.CourseRequest;
import com.upl.tutorial.dto.CourseResponse;
import com.upl.tutorial.service.CourseService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

   private final CourseService service;

   @SecurityRequirements(value = {})
   @GetMapping
   public ResponseEntity<Page<CoursePageResponse>> getActiveCourses
               (@ParameterObject CoursePageRequest request) {

      Page<CoursePageResponse> courses = service.getActiveCourses(request);
      if (courses.isEmpty()) {
         return ResponseEntity.noContent().build();
      }

      return ResponseEntity.status(HttpStatus.OK).body(courses);

   }

   @GetMapping("/instructor")
   public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(@RequestParam int instructorId) {
      List<CourseResponse> courses = service.getCoursesByInstructor(instructorId);
      if (courses.isEmpty()) {
         return ResponseEntity.noContent().build();
      }

      return ResponseEntity.status(HttpStatus.OK).body(courses);

   }

   @PostMapping("/create")
   public ResponseEntity<Integer> createCourse(@Valid @RequestBody CourseRequest request) {

      int courseId = service.createCourse(request);

      return ResponseEntity.status(HttpStatus.CREATED).body(Integer.valueOf(courseId));
   }

   @PutMapping
   public ResponseEntity<String> updateCourse(@Valid @RequestBody CourseManageRequest request) {

      service.updateCourse(request);

      return ResponseEntity.ok(" Sucessfully Updated the Course");
   }

   @DeleteMapping
   public ResponseEntity<String> deleteCourse(@Valid @RequestBody CourseManageRequest request) {

      service.deleteCourse(request);

      return ResponseEntity.ok("Sucessfully Deleted the Course");
   }

}
