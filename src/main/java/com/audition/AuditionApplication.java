package com.audition;

import com.audition.configuration.AuditionApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AuditionApiProperties.class)
public class AuditionApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AuditionApplication.class, args);
    }

}
