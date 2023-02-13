package com.gridians.gridians.global.config.aws;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.context.annotation.Configuration;

//EC2 사용하지 않을 때 해당 Configuration 사용

@Configuration
@EnableAutoConfiguration(exclude = ContextInstanceDataAutoConfiguration.class)
class EC2Configuration{

}