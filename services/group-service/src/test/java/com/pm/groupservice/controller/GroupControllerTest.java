package com.pm.groupservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.groupservice.dto.request.CreateGroupRequest;
import com.pm.groupservice.dto.request.JoinGroupRequest;
import com.pm.groupservice.dto.response.GroupDetailResponse;
import com.pm.groupservice.dto.response.GroupResponse;
import com.pm.groupservice.exception.GlobalExceptionHandler;
import com.pm.groupservice.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private UUID sampleUserId;
    private UUID sampleGroupId;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleGroupId = UUID.randomUUID();
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/groups/join should join group via invite code")
    void joinGroup_shouldReturn200() throws Exception {
        JoinGroupRequest request = JoinGroupRequest.builder()
                .inviteCode("SM-123456")
                .build();

        GroupResponse response = GroupResponse.builder()
                .id(sampleGroupId)
                .name("Summer Trip 2026")
                .inviteCode("SM-123456")
                .build();

        when(groupService.joinGroup(any(), any(JoinGroupRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/groups/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Summer Trip 2026"));
    }
}
