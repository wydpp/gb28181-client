package com.wydpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GB28181ClientApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(GB28181ClientApplication.class);

    public static void main(String[] args) {
        LOGGER.info("this is GB28181ClientApplication!");
        SpringApplication.run(GB28181ClientApplication.class);
    }
}
