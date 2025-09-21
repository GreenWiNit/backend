package com.example.integration.common;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.green.GreenApplication;
import com.example.integration.config.S3TestContainerConfig;
import com.example.integration.config.TestContainerConfig;

/*
 * 해당 클래스는 복잡하게 설정됐습니다!
 * 각자 BaseIntegrationTest 로 바꿔주세요!
 * 해당 클래스가 상속하는 곳이 없을 경우 제거하도록 하겠습니다!
 * */
@Deprecated
@SpringBootTest(classes = {
	GreenApplication.class,
	TestContainerConfig.class,
	S3TestContainerConfig.class,
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ServiceIntegrationTest {
}
