package com.upl.tutorial.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.upl.tutorial.dto.TopInstructorDto;
import com.upl.tutorial.model.Course;
import com.upl.tutorial.model.CourseStatus;

@Repository
public interface CourseRepo extends JpaRepository<Course,Integer> {

 
List<Course> findByInstructor_UserIdAndStatus( int instructor_id,CourseStatus status);  

Page<Course> findAllByStatus(CourseStatus status, Pageable pageable);

@Query("""
    SELECT new com.upl.tutorial.dto.TopInstructorDto(
        c.instructor.userId, 
        c.instructor.name, 
        COUNT(c)
    )
    FROM Course c
    WHERE c.status = 'Active'
    GROUP BY c.instructor.userId, c.instructor.name
    ORDER BY COUNT(c) DESC
""")
List<TopInstructorDto> findTopInstructors(Pageable pageable);

long countByStatus(CourseStatus active);



}
