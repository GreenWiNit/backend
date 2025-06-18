package com.example.green.global.security.annotation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class SecurityAnnotationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("@PublicApi가 적용된 엔드포인트는 인증 없이 접근 가능하다")
    void shouldAllowAccessToPublicApiWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("@AuthenticatedApi가 적용된 엔드포인트는 인증 없이 접근할 수 없다")
    void shouldDenyAccessToAuthenticatedApiWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content("{\"title\":\"Valid Title Here\",\"content\":\"This is a valid content that meets the minimum length requirement.\",\"challengeId\":1}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("@AuthenticatedApi가 적용된 엔드포인트는 인증된 사용자가 접근 가능하다")
    void shouldAllowAccessToAuthenticatedApiWithAuthentication() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content("{\"title\":\"Valid Title Here\",\"content\":\"This is a valid content that meets the minimum length requirement.\",\"challengeId\":1}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("@AdminApi가 적용된 엔드포인트는 일반 사용자가 접근할 수 없다")
    void shouldDenyAccessToAdminApiWithUserRole() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("@AdminApi가 적용된 엔드포인트는 관리자가 접근 가능하다")
    void shouldAllowAccessToAdminApiWithAdminRole() throws Exception {
        mockMvc.perform(delete("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }
} 