package com.example.integration.common;

import org.springframework.boot.test.context.SpringBootTest;

import com.example.green.GreenApplication;
import com.example.integration.config.MySqlTestContainerConfig;
import com.example.integration.config.S3TestContainerConfig;
import com.example.integration.file.FileTestUtil;

@SpringBootTest(classes = {
	GreenApplication.class,
	FileTestUtil.class,
	MySqlTestContainerConfig.class,
	S3TestContainerConfig.class
})
public class ServiceIntegrationTest {
}
