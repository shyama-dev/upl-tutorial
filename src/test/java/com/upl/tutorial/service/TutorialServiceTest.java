package com.upl.tutorial.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.upl.tutorial.dto.TutorialManageRequest;
import com.upl.tutorial.dto.TutorialRequest;
import com.upl.tutorial.dto.TutorialResponse;
import com.upl.tutorial.exception.EntityNotFoundException;
import com.upl.tutorial.model.Course;
import com.upl.tutorial.model.Tutorial;
import com.upl.tutorial.model.TutorialHistory;
import com.upl.tutorial.model.Users;
import com.upl.tutorial.repository.CourseRepo;
import com.upl.tutorial.repository.TutorialHistoryRepo;
import com.upl.tutorial.repository.TutorialRepository;
import com.upl.tutorial.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TutorialServiceTest {

    @Mock
    private TutorialRepository tutorialRepo;

    @Mock
    private CourseRepo courseRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private TutorialHistoryRepo tutorialHistoryRepo;

    @InjectMocks
    private TutorialService tutorialService;

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
    // 1. Tests for create()
    // ==========================================

    @Test
    void create_Success() {
        // Given
        TutorialRequest request = new TutorialRequest();
        request.setCourseId(1);
        request.setTitle("Java Basics");
        request.setYoutubeLink("https://youtube.com/watch?v=123");
        request.setContent("Introduction to Java");

        Course course = new Course();

        Tutorial savedTutorial = new Tutorial();
        savedTutorial.setCourse(course);
        savedTutorial.setTitle(request.getTitle());

        when(courseRepo.findById(1)).thenReturn(Optional.of(course));
        when(tutorialRepo.save(any(Tutorial.class))).thenAnswer(invocation -> {
            Tutorial t = invocation.getArgument(0);
            t.settutorialId(101);
            return t;
        });

        int generatedId = tutorialService.create(request);

        // Then
        assertEquals(101, generatedId);
        verify(courseRepo).findById(1);
        verify(tutorialRepo).save(any(Tutorial.class));
    }

    @Test
    void create_CourseNotFound_ThrowsEntityNotFoundException() {
        // Given
        TutorialRequest request = new TutorialRequest();
        request.setCourseId(99);

        when(courseRepo.findById(99)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tutorialService.create(request));

        assertEquals("Course not found for id :99", exception.getMessage());
    }

    // ==========================================
    // 2. Tests for fetchTutorials()
    // ==========================================

    @Test
    void fetchTutorials_Success() {
        // Given
        int courseId = 10;

        Tutorial tutorial = new Tutorial();
        tutorial.setTitle("Spring Boot");
        tutorial.setContent("Spring Security");
        tutorial.setyoutubeLink("https://youtube.com/watch?v=abc");
        tutorial.setcreatedAt(LocalDateTime.now());

        when(tutorialRepo.findByCourse_CourseId(courseId)).thenReturn(List.of(tutorial));

        // When
        List<TutorialResponse> response = tutorialService.fetchTutorials(courseId);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Spring Boot", response.get(0).getTitle());
        verify(tutorialRepo).findByCourse_CourseId(courseId);
    }

    // ==========================================
    // 3. Tests for updateTutorial()
    // ==========================================

    @Test
    void updateTutorial_Success() {
        // Given
        TutorialManageRequest request = new TutorialManageRequest();
        request.setTutorialId(1);
        request.setTitle("Updated Title");
        request.setContent("Updated Content");
        request.setYoutubeLink("https://youtube.com/updated");
        request.setChanges("Updated content and title");
        mockSecurityContext("instructor@test.com");
        Tutorial existingTutorial = new Tutorial();
        existingTutorial.setTitle("Old Title");

        Users mockUser = new Users();

        when(tutorialRepo.findById(1)).thenReturn(Optional.of(existingTutorial));
        when(userRepo.findByEmail("instructor@test.com")).thenReturn(Optional.of(mockUser));

        // When
        tutorialService.updateTutorial(request);

        // Then
        assertEquals("Updated Title", existingTutorial.getTitle());
        assertEquals("Updated Content", existingTutorial.getContent());
        assertEquals("https://youtube.com/updated", existingTutorial.getyoutubeLink());

        verify(tutorialHistoryRepo).save(any(TutorialHistory.class));
    }

    @Test
    void updateTutorial_TutorialNotFound_ThrowsEntityNotFoundException() {
        // Given
        TutorialManageRequest request = new TutorialManageRequest();
        request.setTutorialId(999);

        when(tutorialRepo.findById(999)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> tutorialService.updateTutorial(request));

        assertEquals("Tutorial not found for id :999", exception.getMessage());
    }
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
