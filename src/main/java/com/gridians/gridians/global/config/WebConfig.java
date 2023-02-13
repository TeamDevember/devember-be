package com.gridians.gridians.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


	@Value("${custom.oracle.address}")
	private String cloudAddress;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				.addMapping("/**")
				.allowedOriginPatterns("*")
				.allowedOrigins("http://localhost:8080", "http://" + cloudAddress + ":8080",
						"http://localhost:3000",
						"https://localhost:3000",
						"https://127.0.0.1:3000"
				)
				.allowedMethods("GET", "POST", "DELETE", "PUT", "UPDATE")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600)
				;
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new UserStatusConverter());
	}
}

