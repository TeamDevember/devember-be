package com.gridians.gridians.global.config.aws;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration(exclude = ContextInstanceDataAutoConfiguration.class)
class EC2Configuration{

}