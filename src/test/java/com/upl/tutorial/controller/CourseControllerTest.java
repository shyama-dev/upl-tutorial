package com.upl.tutorial.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upl.tutorial.dto.CourseManageRequest;
import com.upl.tutorial.dto.CoursePageRequest;
import com.upl.tutorial.dto.CoursePageResponse;
import com.upl.tutorial.dto.CourseRequest;
import com.upl.tutorial.dto.InstructorResponse;
import com.upl.tutorial.service.CourseService;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

    }

    // --- 1. getActiveCourses Tests ---

    @Test
    void getActiveCourses_WhenCoursesExist_ShouldReturnOkAndPage() throws Exception {
        CoursePageResponse responseItem = new CoursePageResponse();
        InstructorResponse instructorResponse = new InstructorResponse();
        responseItem.setInstructor(instructorResponse);

        Page<CoursePageResponse> coursePage = new PageImpl<>(List.of(responseItem), PageRequest.of(0, 10), 1);

        when(courseService.getActiveCourses(any(CoursePageRequest.class))).thenReturn(coursePage);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk());

        verify(courseService).getActiveCourses(any(CoursePageRequest.class));
    }

    @Test
    void getActiveCourses_WhenNoCourses_ShouldReturnNoContent() throws Exception {
        Page<CoursePageResponse> emptyPage = new PageImpl<>(Collections.emptyList());

        when(courseService.getActiveCourses(any(CoursePageRequest.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isNoContent());

        verify(courseService).getActiveCourses(any(CoursePageRequest.class));
    }

    // --- 2. getCoursesByInstructor Tests ---

   @Test
void getCoursesByInstructor_WhenCoursesExist_ShouldReturnOkAndPage() throws Exception {
    Page<CoursePageResponse> coursePage = new PageImpl<>(List.of(new CoursePageResponse()), PageRequest.of(0, 10), 1);

    when(courseService.getCoursesByLoggedInInstructor(any(CoursePageRequest.class))).thenReturn(coursePage);

    mockMvc.perform(get("/courses/instructor"))
            .andExpect(status().isOk());

    verify(courseService).getCoursesByLoggedInInstructor(any(CoursePageRequest.class));
}

    @Test
    void getCoursesByInstructor_WhenNoCourses_ShouldReturnNoContent() throws Exception {
        Page<CoursePageResponse> emptyPage = Page.empty();

        when(courseService.getCoursesByLoggedInInstructor(any(CoursePageRequest.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/courses/instructor"))
                .andExpect(status().isNoContent());

        verify(courseService).getCoursesByLoggedInInstructor(any(CoursePageRequest.class));
    }

    // --- 3. createCourse Test ---

    @Test
    void createCourse_ShouldReturnCreatedAndCourseId() throws Exception {
        CourseRequest request = new CourseRequest();
        request.setTitle("Sample Course");
        request.setDescription("Sample Description");
        int generatedId = 101;

        when(courseService.createCourse(any(CourseRequest.class))).thenReturn(generatedId);

        mockMvc.perform(post("/courses/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("101"));

        verify(courseService).createCourse(any(CourseRequest.class));
    }

    // --- 4. updateCourse Test ---

    @Test
    void updateCourse_ShouldReturnOkAndSuccessMessage() throws Exception {
        CourseManageRequest request = new CourseManageRequest();
        request.setCourseId(101);

        mockMvc.perform(put("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(" Sucessfully Updated the Course"));

        verify(courseService).updateCourse(any(CourseManageRequest.class));
    }

    // --- 5. deleteCourse Test ---

    @Test
    void deleteCourse_ShouldReturnOkAndSuccessMessage() throws Exception {
        CourseManageRequest request = new CourseManageRequest();
        request.setCourseId(101);

        mockMvc.perform(delete("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucessfully Deleted the Course"));

        verify(courseService).deleteCourse(any(CourseManageRequest.class));
    }

}
