package com.upl.tutorial.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.upl.tutorial.dto.AdminDashboardAnalyticsResponse;
import com.upl.tutorial.dto.CourseManageRequest;
import com.upl.tutorial.dto.CoursePageRequest;
import com.upl.tutorial.dto.CoursePageResponse;
import com.upl.tutorial.dto.CourseRequest;
import com.upl.tutorial.dto.InstructorMetricsDto;
import com.upl.tutorial.dto.TopInstructorDto;
import com.upl.tutorial.exception.EntityNotFoundException;
import com.upl.tutorial.exception.InstructorNotActiveException;
import com.upl.tutorial.model.Course;
import com.upl.tutorial.model.CourseHistory;
import com.upl.tutorial.model.CourseStatus;
import com.upl.tutorial.model.UserStatus;
import com.upl.tutorial.model.Users;
import com.upl.tutorial.repository.CourseHistoryRepo;
import com.upl.tutorial.repository.CourseRepo;
import com.upl.tutorial.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    
    @Mock
    private CourseRepo courseRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private CourseHistoryRepo courseHistoryRepo;

    @InjectMocks
    private CourseService courseService;
    
    @BeforeEach
    void setUp() {
        // Clear security context before each test to ensure a clean state
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Always clean up after test execution
        SecurityContextHolder.clearContext();
    }
   
    // ==========================================
    // 1. Tests for createCourse()
    // ==========================================

    @Test
    void createCourse_Success() {
        // Given
        CourseRequest request = new CourseRequest();
        request.setTitle("Java Basics");
        request.setDescription("Learn Java from scratch");
        mockSecurityContext("instructor@test.com");
        Users instructor = new Users();
        instructor.setuserId(1);
        instructor.setStatus(UserStatus.Active);

        when(userRepo.findByEmail("instructor@test.com")).thenReturn(Optional.of(instructor));

        when(courseRepo.save(any(Course.class))).thenAnswer(invocation -> {
            Course c = invocation.getArgument(0);
            c.setcourseId(101); // Simulate generated ID from database
            return c;
        });

        // When
        int courseId = courseService.createCourse(request);

        // Then
        assertEquals(101, courseId);
        verify(userRepo).findByEmail("instructor@test.com");
        verify(courseRepo).save(any(Course.class));
    }

    @Test
    void createCourse_InstructorNotActive_ThrowsInstructorNotActiveException() {
        // Given
        CourseRequest request = new CourseRequest();
        Users instructor = new Users();
        instructor.setuserId(1);
        instructor.setStatus(UserStatus.Pending); // Not active
        mockSecurityContext("inactive@test.com");
        when(userRepo.findByEmail("inactive@test.com")).thenReturn(Optional.of(instructor));

        // When & Then
        InstructorNotActiveException exception = assertThrows(
            InstructorNotActiveException.class,
            () -> courseService.createCourse(request)
        );

        assertEquals("Instructor not in active status for id :1", exception.getMessage());
    }

    // ==========================================
    // 2. Tests for getActiveCourses()
    // ==========================================

    @Test
    void getActiveCourses_Success() {
        // Given
        CoursePageRequest request = new CoursePageRequest();
        request.setPage(1);
        request.setSize(5);
        request.setSortBy("title");
        request.setSortDir("asc");

        Users instructor = new Users();
        instructor.setName("John Doe");
        instructor.setEmail("john@test.com");
        instructor.setStatus(UserStatus.Active);

        Course course = new Course();
        course.setcourseId(10);
        course.setTitle("Spring Boot");
        course.setDescription("REST APIs");
        course.setStatus(CourseStatus.Active);
        course.setInstructor(instructor);

        Page<Course> coursePage = new PageImpl<>(List.of(course));
        when(courseRepo.findAllByStatus(eq(CourseStatus.Active), any(Pageable.class))).thenReturn(coursePage);

        // When
        Page<CoursePageResponse> result = courseService.getActiveCourses(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        CoursePageResponse response = result.getContent().get(0);
        assertEquals("Spring Boot", response.getTitle());
        assertEquals("Active", response.getStatus());
        assertEquals("John Doe", response.getInstructor().getName());
    }

    // ==========================================
    // 3. Tests for updateCourse()
    // ==========================================

    @Test
    void updateCourse_Success() {
        // Given
        CourseManageRequest request = new CourseManageRequest();
        request.setCourseId(1);
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setChanges("Updated content fields");

        Course existingCourse = new Course();
        existingCourse.setcourseId(1);
        existingCourse.setTitle("Old Title");
        existingCourse.setDescription("Old Description");

        when(courseRepo.findById(1)).thenReturn(Optional.of(existingCourse));

        // When
        courseService.updateCourse(request);

        // Then
        assertEquals("Updated Title", existingCourse.getTitle());
        assertEquals("Updated Description", existingCourse.getDescription());
        verify(courseHistoryRepo).save(any(CourseHistory.class));
    }

    // ==========================================
    // 4. Tests for deleteCourse()
    // ==========================================

    @Test
    void deleteCourse_Success() {
        // Given
        CourseManageRequest request = new CourseManageRequest();
        request.setCourseId(1);
        request.setChanges("Course soft deleted");

        Course existingCourse = new Course();
        existingCourse.setcourseId(1);
        existingCourse.setStatus(CourseStatus.Active);

        when(courseRepo.findById(1)).thenReturn(Optional.of(existingCourse));

        // When
        courseService.deleteCourse(request);

        // Then
        assertEquals(CourseStatus.Inactive, existingCourse.getStatus());
        verify(courseHistoryRepo).save(any(CourseHistory.class));
    }

    @Test
    void deleteCourse_NotFound_ThrowsEntityNotFoundException() {
        // Given
        CourseManageRequest request = new CourseManageRequest();
        request.setCourseId(99);

        when(courseRepo.findById(99)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> courseService.deleteCourse(request)
        );

        assertEquals("Course is not present for course id:99", exception.getMessage());
    }

    // ==========================================
    // 5. Tests for fetchAdminAnalytics()
    // ==========================================

    @Test
    void fetchAdminAnalytics_Success() {
        // Given
        when(courseRepo.countByStatus(CourseStatus.Active)).thenReturn(15L);

        TopInstructorDto topInstructor = mock(TopInstructorDto.class);
        when(courseRepo.findTopInstructors(PageRequest.of(0, 5))).thenReturn(List.of(topInstructor));

        InstructorMetricsDto metrics = mock(InstructorMetricsDto.class);
        when(userRepo.getInstructorMetrics()).thenReturn(metrics);

        // When
        AdminDashboardAnalyticsResponse response = courseService.fetchAdminAnalytics();

        // Then
        assertNotNull(response);
        assertEquals(15L, response.getTotalPublishedCourses());
        assertEquals(1, response.getTopInstructors().size());
        assertEquals(metrics, response.getInstructorMetrics());
    }

    /**
     * Reusable helper to mock SecurityContextHolder logic
     */
    private void mockSecurityContext(String expectedEmail) {
        // 1. Mock UserDetails
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(expectedEmail);

        // 2. Mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 3. Mock SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // 4. Set the mocked context in SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }

   
}
