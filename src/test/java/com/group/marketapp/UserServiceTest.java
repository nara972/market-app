package com.group.marketapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.marketapp.common.jwt.JwtTokenProvider;
import com.group.marketapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Map;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testUserRegistrationAndLogin() throws Exception {
        // 1. 회원가입 요청
        String signupRequestJson = """
            {
                "loginId": "testUser",
                "password": "password123",
                "username": "Test User",
                "address": "123 Test Street"
            }
        """;

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson))
                .andExpect(status().isOk());

        // 2. 로그인 요청
        String loginRequestJson = """
            {
                "loginId": "testUser",
                "password": "password123"
            }
        """;

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        // 3. 로그인 결과 검증
        String loginResponse = loginResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> tokenResponse = objectMapper.readValue(loginResponse, new TypeReference<>() {});

        String accessToken = tokenResponse.get("accessToken");
        String refreshToken = tokenResponse.get("refreshToken");

        log.info("Access Token: {}", accessToken);
        log.info("Refresh Token: {}", refreshToken);

        // 4. Access Token 검증
        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();

        // 5. Redis에 Refresh Token 저장 여부 확인
        String storedRefreshToken = (String) redisTemplate.opsForValue().get("RT:testUser");
        assertThat(storedRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    void testLogout() throws Exception {
        // 1. 로그인 요청
        String loginRequestJson = """
            {
                "loginId": "testUser",
                "password": "password123"
            }
        """;

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> tokenResponse = objectMapper.readValue(loginResponse, new TypeReference<>() {});

        String accessToken = tokenResponse.get("accessToken");
        String refreshToken = tokenResponse.get("refreshToken");

        // 2. 로그아웃 요청
        String logoutRequestJson = """
            {
                "refreshToken": "%s"
            }
        """.formatted(refreshToken);

        mockMvc.perform(post("/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logoutRequestJson))
                .andExpect(status().isOk());

        // 3. Redis에 Access Token 및 Refresh Token 제거 확인
        String storedRefreshToken = (String) redisTemplate.opsForValue().get("RT:testUser");
        assertThat(storedRefreshToken).isNull();

        String isAccessTokenLoggedOut = (String) redisTemplate.opsForValue().get(accessToken);
        assertThat(isAccessTokenLoggedOut).isEqualTo("logout");
    }
}
