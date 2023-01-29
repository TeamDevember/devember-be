package com.gridians.girdians.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				.addMapping("/**")
				.allowedOriginPatterns("*")
				.allowedOrigins("http://localhost:3000")
				.allowedMethods("GET", "POST", "DELETE", "UPDATE")
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

