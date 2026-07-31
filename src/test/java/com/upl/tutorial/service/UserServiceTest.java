package com.upl.tutorial.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.upl.tutorial.dto.InstructorApproveRequest;
import com.upl.tutorial.dto.UserRequest;
import com.upl.tutorial.dto.UserResponse;
import com.upl.tutorial.exception.EntityNotFoundException;
import com.upl.tutorial.model.ApprovalStatus;
import com.upl.tutorial.model.InstructorApproval;
import com.upl.tutorial.model.UserRole;
import com.upl.tutorial.model.UserStatus;
import com.upl.tutorial.model.Users;
import com.upl.tutorial.repository.InstructorApprovalRepo;
import com.upl.tutorial.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private InstructorApprovalRepo repo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;
    
    // Standard test data
    private UserRequest userRequest;
    private InstructorApproveRequest approveRequest;
    private Users instructorUser;
    private Users adminUser;

    @BeforeEach
    void setUp() {
        // Setup UserRequest
        userRequest = new UserRequest();
        userRequest.setName("Test User");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("rawPassword123");

        // Setup InstructorApproveRequest
        approveRequest = new InstructorApproveRequest();
        approveRequest.setUserId(1);
        approveRequest.setAdminId(2);
        approveRequest.setRemarks("Approved for teaching");

        // Setup mocked Instructor entity
        instructorUser = new Users();
        instructorUser.setuserId(1);
        instructorUser.setRole(UserRole.INSTRUCTOR);
        instructorUser.setStatus(UserStatus.Pending);

        // Setup mocked Admin entity
        adminUser = new Users();
        adminUser.setuserId(2);
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.Active);
    }

    // ==========================================
    // 1. TESTS FOR register()
    // ==========================================
    @Nested
    @DisplayName("register() Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should successfully register a new instructor")
        void register_Success() {
            // when
            when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword123");

            Users savedUser = new Users();
            savedUser.setuserId(100);
            when(userRepo.save(any(Users.class))).thenReturn(savedUser);

            // When
            UserResponse response = userService.register(userRequest);

            // Then - Standard JUnit 5 assertions
            assertNotNull(response, "UserResponse should not be null");
            assertEquals(100, response.getUserId(), "User ID should match saved user ID");

            // Verify captured properties saved to repository
            ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
            verify(userRepo).save(userCaptor.capture());

            Users capturedUser = userCaptor.getValue();
            assertEquals("Test User", capturedUser.getName());
            assertEquals("test@example.com", capturedUser.getEmail());
            assertEquals("encodedPassword123", capturedUser.getPassword());
            assertEquals(UserRole.INSTRUCTOR, capturedUser.getRole());
            assertEquals(UserStatus.Pending, capturedUser.getStatus());
            assertNotNull(capturedUser.getcreatedAt());
        }
    }

    // ==========================================
    // 2. TESTS FOR updateInstructorStatus()
    // ==========================================
    @Nested
    @DisplayName("updateInstructorStatus() Tests")
    class updateInstructorStatusTests {

        //@Test
        @ParameterizedTest
        @EnumSource(value = UserStatus.class, names = {"Active", "Rejected"})
        @DisplayName("Should successfully approve instructor when both instructor and admin exist")
        void updateInstructorStatus_Success(UserStatus expectedStatus) {
            // when
            List<Integer> userIds = List.of(1, 2);
            when(userRepo.findAllById(userIds)).thenReturn(List.of(instructorUser, adminUser));
            ApprovalStatus statusToSet = (expectedStatus == UserStatus.Active) ? ApprovalStatus.Approved : ApprovalStatus.Rejected;
            approveRequest.setStatus(statusToSet);
            // When
            userService.updateInstructorStatus(approveRequest, expectedStatus);

            // Then - Verify instructor status updated
            assertEquals(expectedStatus, instructorUser.getStatus());
            
            // Verify InstructorApproval object saved to repo
            ArgumentCaptor<InstructorApproval> approvalCaptor = ArgumentCaptor.forClass(InstructorApproval.class);
            verify(repo).save(approvalCaptor.capture());

            InstructorApproval savedApproval = approvalCaptor.getValue();
            assertEquals(instructorUser, savedApproval.getInstructor());
            assertEquals(adminUser, savedApproval.getAdmin());
            assertEquals("Approved for teaching", savedApproval.getRemarks());
           // assertEquals(expectedStatus.name(), savedApproval.getStatus().name());
            assertNotNull(savedApproval.getTimestamp());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when instructor is missing")
        void updateInstructorStatus_InstructorMissing_ThrowsException() {
            // when: Only Admin returned from DB
            List<Integer> userIds = List.of(1, 2);
            when(userRepo.findAllById(userIds)).thenReturn(List.of(adminUser));

            // When & Then
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.updateInstructorStatus(approveRequest, UserStatus.Active),
                    "Expected EntityNotFoundException when instructor is missing"
            );

            assertEquals("Instructor Not found for id :1", exception.getMessage());
            verify(repo, never()).save(any(InstructorApproval.class));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when admin is missing")
        void updateInstructorStatus_AdminMissing_ThrowsException() {
            // when: Only Instructor returned from DB
            List<Integer> userIds = List.of(1, 2);
            when(userRepo.findAllById(userIds)).thenReturn(List.of(instructorUser));

            // When & Then
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.updateInstructorStatus(approveRequest, UserStatus.Active),
                    "Expected EntityNotFoundException when admin is missing"
            );

            assertEquals("Admin Not found for id :2", exception.getMessage());
            verify(repo, never()).save(any(InstructorApproval.class));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when no users are found")
        void updateInstructorStatus_NoUsersFound_ThrowsException() {
            // when: Empty list returned from DB
            List<Integer> userIds = List.of(1, 2);
            when(userRepo.findAllById(userIds)).thenReturn(Collections.emptyList());

            // When & Then
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> userService.updateInstructorStatus(approveRequest, UserStatus.Active),
                    "Expected EntityNotFoundException when no users exist"
            );

            assertEquals("Instructor Not found for id :1", exception.getMessage());
            verify(repo, never()).save(any(InstructorApproval.class));
        }
    }
}
