package org.example;

import org.example.properties.SpaceCenterServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(SpaceCenterServiceProperties.class)
public class MissionSchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MissionSchedulerApplication.class, args);
    }
}