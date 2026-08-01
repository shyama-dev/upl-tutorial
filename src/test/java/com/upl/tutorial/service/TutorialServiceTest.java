package com.upl.tutorial.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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

    private Course course;
    private Tutorial tutorial;
    private Users instructor;
/*
    @BeforeEach
    void setUp() {
        course = new Course();
        // Assuming setters or constructor exist; set basic fields if needed

        tutorial = new Tutorial();
        tutorial.setTitle("Original Title");
        tutorial.setContent("Original Content");
        tutorial.setyoutubeLink("https://youtube.com/original");
        tutorial.setcreatedAt(LocalDateTime.now());

        instructor = new Users();
    }

    // ==========================================
    // Tests for create()
    // ==========================================
    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("Should create tutorial successfully when course exists")
        void create_Success() {
            // Arrange
            TutorialRequest request = new TutorialRequest();
            request.setCourseId(1);
            request.setTitle("Java Basics");
            request.setContent("Introductory Java content");
            request.setYoutubeLink("https://youtube.com/watch?v=123");

            Tutorial savedTutorial = new Tutorial();
            // Simulating assigned ID on save (assumes gettutorialId returns 100)
            savedTutorial.settutorialId(100);
            // Adjust mock return value matching your entity's getter implementation

            when(courseRepo.findById(1)).thenReturn(Optional.of(course));
            when(tutorialRepo.save(any(Tutorial.class))).thenReturn(savedTutorial);

            // Act
            int tutorialId = tutorialService.create(request);

            // Assert
            assertEquals(100, tutorialId, "Tutorial ID should match saved tutorial ID");
            verify(courseRepo, times(1)).findById(1);

            ArgumentCaptor<Tutorial> captor = ArgumentCaptor.forClass(Tutorial.class);
            verify(tutorialRepo, times(1)).save(captor.capture());

            Tutorial capturedTutorial = captor.getValue();

            assertEquals(course, capturedTutorial.getCourse());
            assertEquals("Java Basics", capturedTutorial.getTitle());
            assertEquals("Introductory Java content", capturedTutorial.getContent());
            assertEquals("https://youtube.com/watch?v=123", capturedTutorial.getyoutubeLink());
            assertNotNull(capturedTutorial.getcreatedAt());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when course does not exist")
   void create_CourseNotFound() {
    // Arrange
    TutorialRequest request = new TutorialRequest();
    request.setCourseId(99);

    when(courseRepo.findById(99)).thenReturn(Optional.empty());

    // Act & Assert
    EntityNotFoundException exception = assertThrows(
        EntityNotFoundException.class, 
        () -> tutorialService.create(request)
    );

    assertEquals("Course not found for id :99", exception.getMessage());
    verify(tutorialRepo, never()).save(any());
    }
    }

    // ==========================================
    // Tests for fetchTutorials()
    // ==========================================
    @Nested
    @DisplayName("fetchTutorials()")
    class FetchTutorialsTests {

        @Test
        @DisplayName("Should return list of TutorialResponse when tutorials exist")
        void fetchTutorials_Success() {
            // Arrange
            int courseId = 1;
            when(tutorialRepo.findByCourse_CourseId(courseId)).thenReturn(List.of(tutorial));

            // Act
            List<TutorialResponse> responses = tutorialService.fetchTutorials(courseId);

            // Assert
            assertThat(responses).hasSize(1);
            TutorialResponse response = responses.get(0);
            assertThat(response.getTitle()).isEqualTo("Original Title");
            assertThat(response.getContent()).isEqualTo("Original Content");
            assertThat(response.getYoutubeLink()).isEqualTo("https://youtube.com/original");

            verify(tutorialRepo, times(1)).findByCourse_CourseId(courseId);
        }

        @Test
        @DisplayName("Should return empty list when no tutorials exist for course")
        void fetchTutorials_Empty() {
            // Arrange
            int courseId = 1;
            when(tutorialRepo.findByCourse_CourseId(courseId)).thenReturn(Collections.emptyList());

            // Act
            List<TutorialResponse> responses = tutorialService.fetchTutorials(courseId);

            // Assert
            assertThat(responses).isEmpty();
            verify(tutorialRepo, times(1)).findByCourse_CourseId(courseId);
        }
    }

    // ==========================================
    // Tests for updateTutorial()
    // ==========================================
    @Nested
    @DisplayName("updateTutorial()")
    class UpdateTutorialTests {

        @Test
        @DisplayName("Should update tutorial fields and record history when input is valid")
        void updateTutorial_Success() {
            // Arrange
            TutorialManageRequest request = new TutorialManageRequest();
            request.setTutorialId(10);
            request.setInstructorId(5);
            request.setTitle("Updated Title");
            request.setContent("Updated Content");
            request.setYoutubeLink("https://youtube.com/updated");
            request.setChanges("Updated title, content, and video link");

            when(tutorialRepo.findById(10)).thenReturn(Optional.of(tutorial));
            when(userRepo.findById(5)).thenReturn(Optional.of(instructor));

            // Act
            tutorialService.updateTutorial(request);

            // Assert
            assertThat(tutorial.getTitle()).isEqualTo("Updated Title");
            assertThat(tutorial.getContent()).isEqualTo("Updated Content");
            assertThat(tutorial.getyoutubeLink()).isEqualTo("https://youtube.com/updated");

            ArgumentCaptor<TutorialHistory> historyCaptor = ArgumentCaptor.forClass(TutorialHistory.class);
            verify(tutorialHistoryRepo, times(1)).save(historyCaptor.capture());

            TutorialHistory savedHistory = historyCaptor.getValue();
            assertThat(savedHistory.getTutorial()).isEqualTo(tutorial);
            assertThat(savedHistory.getInstructor()).isEqualTo(instructor);
            assertThat(savedHistory.getChanges()).isEqualTo("Updated title, content, and video link");
            assertThat(savedHistory.getmodifiedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should ignore updates for null or blank fields")
        void updateTutorial_PartialUpdate_IgnoreBlankOrNull() {
            // Arrange
            TutorialManageRequest request = new TutorialManageRequest();
            request.setTutorialId(10);
            request.setInstructorId(5);
            request.setTitle(" "); // blank -> should not update
            request.setContent(null); // null -> should not update
            request.setYoutubeLink(""); // empty -> should not update
            request.setChanges("No real field changes");

            when(tutorialRepo.findById(10)).thenReturn(Optional.of(tutorial));
            when(userRepo.findById(5)).thenReturn(Optional.of(instructor));

            // Act
            tutorialService.updateTutorial(request);

            // Assert — original values should remain untouched
            assertThat(tutorial.getTitle()).isEqualTo("Original Title");
            assertThat(tutorial.getContent()).isEqualTo("Original Content");
            assertThat(tutorial.getyoutubeLink()).isEqualTo("https://youtube.com/original");

            verify(tutorialHistoryRepo, times(1)).save(any(TutorialHistory.class));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when tutorial not found")
        void updateTutorial_TutorialNotFound() {
            // Arrange
            TutorialManageRequest request = new TutorialManageRequest();
            request.setTutorialId(99);

            when(tutorialRepo.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> tutorialService.updateTutorial(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Tutorial not found for id :99");

            verify(userRepo, never()).findById(any());
            verify(tutorialHistoryRepo, never()).save(any());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when instructor (user) not found")
        void updateTutorial_InstructorNotFound() {
            // Arrange
            TutorialManageRequest request = new TutorialManageRequest();
            request.setTutorialId(10);
            request.setInstructorId(99);

            when(tutorialRepo.findById(10)).thenReturn(Optional.of(tutorial));
            when(userRepo.findById(99)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> tutorialService.updateTutorial(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Instructor not found for id :99");

            verify(tutorialHistoryRepo, never()).save(any());
        }
    } */
}
