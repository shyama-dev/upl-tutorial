package com.upl.tutorial.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upl.tutorial.dto.TutorialManageRequest;
import com.upl.tutorial.dto.TutorialRequest;
import com.upl.tutorial.dto.TutorialResponse;
import com.upl.tutorial.service.TutorialService;

@ExtendWith(MockitoExtension.class)
public class TutorialControllerTest {
    
    private MockMvc mockMvc;

    @Mock
    private TutorialService tutorialService;

    @InjectMocks
    private TutorialController tutorialController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tutorialController).build();
    }

    // --- 1. GET /tutorials Tests ---

    @Test
    void fetchTutorials_WhenTutorialsExist_ShouldReturnOkAndList() throws Exception {
        int courseId = 1;
        List<TutorialResponse> responseList = List.of(new TutorialResponse());
        when(tutorialService.fetchTutorials(courseId)).thenReturn(responseList);

        mockMvc.perform(get("/tutorials")
                .param("courseId", String.valueOf(courseId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseList)));

        verify(tutorialService).fetchTutorials(eq(courseId));
    }

    @Test
    void fetchTutorials_WhenNoTutorialsExist_ShouldReturnNoContent() throws Exception {
        int courseId = 1;
        when(tutorialService.fetchTutorials(courseId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tutorials")
                .param("courseId", String.valueOf(courseId)))
                .andExpect(status().isNoContent());

        verify(tutorialService).fetchTutorials(eq(courseId));
    }

    // --- 2. POST /tutorials/add Test ---

    @Test
    void create_ShouldReturnOkAndTutorialId() throws Exception {
        TutorialRequest request = new TutorialRequest();
        request.setCourseId(1);
        request.setTitle("Sample Title");
        request.setContent("Sample Content");
        int generatedTutorialId = 10;

        when(tutorialService.create(any(TutorialRequest.class))).thenReturn(generatedTutorialId);

        mockMvc.perform(post("/tutorials/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(generatedTutorialId)));

        verify(tutorialService).create(any(TutorialRequest.class));
    }

    // --- 3. PUT /tutorials Test ---

    @Test
    void updateTutorial_ShouldReturnOkAndSuccessMessage() throws Exception {
        TutorialManageRequest request = new TutorialManageRequest();
        request.setTutorialId(1);

        mockMvc.perform(put("/tutorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sucessfully updated the tutorial"));

        verify(tutorialService).updateTutorial(any(TutorialManageRequest.class));
    }
}
