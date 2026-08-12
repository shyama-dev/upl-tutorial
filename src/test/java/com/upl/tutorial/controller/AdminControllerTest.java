package com.upl.tutorial.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import com.upl.tutorial.dto.AdminDashboardAnalyticsResponse;
import com.upl.tutorial.dto.InstructorApproveRequest;
import com.upl.tutorial.dto.PendingUserResponse;
import com.upl.tutorial.model.ApprovalStatus;
import com.upl.tutorial.model.UserStatus;
import com.upl.tutorial.service.CourseService;
import com.upl.tutorial.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

   private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private AdminController adminController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void approveInstructor_ShouldReturnOk_AndCallUserService() throws Exception {
        InstructorApproveRequest request = createValidRequest();
        request.setStatus(ApprovalStatus.Approved); 

        mockMvc.perform(post("/admin/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Instructor Approved"));

        verify(userService).updateInstructorStatus(any(InstructorApproveRequest.class), eq(UserStatus.Active));
        assertEquals(ApprovalStatus.Approved, request.getStatus());
    }

    @Test
    void rejectInstructor_ShouldReturnOk_AndCallUserService() throws Exception {
        InstructorApproveRequest request = createValidRequest();
        request.setStatus(ApprovalStatus.Rejected);

        mockMvc.perform(post("/admin/reject")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Instructor Rejected"));

        verify(userService).updateInstructorStatus(any(InstructorApproveRequest.class), eq(UserStatus.Rejected));
        assertEquals(ApprovalStatus.Rejected, request.getStatus());
    }

    @Test
    void fetchAdminAnalytics_ShouldReturnAnalyticsResponse() throws Exception {
        AdminDashboardAnalyticsResponse response = new AdminDashboardAnalyticsResponse();
        when(courseService.fetchAdminAnalytics()).thenReturn(response);

        mockMvc.perform(get("/admin/analytics"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(courseService).fetchAdminAnalytics();
    }

    
    @Test
    void getPendingInstructors_ShouldReturnList() throws Exception {
        List<PendingUserResponse> pendingList = new ArrayList<>();
        pendingList.add(new PendingUserResponse());
        when(userService.getPendingInstructors()).thenReturn(pendingList);

        mockMvc.perform(get("/admin/instructors/pending"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(pendingList)));

        verify(userService).getPendingInstructors();
    }

    // Helper method to build a request with required fields populated
    private InstructorApproveRequest createValidRequest() {
        InstructorApproveRequest request = new InstructorApproveRequest();
        request.setUserId(101);
        request.setAdminId(1);
        request.setRemarks("Approved by admin");
        return request;
    }
    
}
