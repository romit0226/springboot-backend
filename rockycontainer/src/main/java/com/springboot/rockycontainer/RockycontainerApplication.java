package com.springboot.rockycontainer;

import org.hibernate.annotations.processing.Exclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.core.SpringSecurityCoreVersion;

@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class
        })
public class RockycontainerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RockycontainerApplication.class, args);
	}

}
