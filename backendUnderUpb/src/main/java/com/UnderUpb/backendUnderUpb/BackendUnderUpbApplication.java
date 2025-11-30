package com.UnderUpb.backendUnderUpb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableJpaRepositories("com.UnderUpb.backendUnderUpb.repository")
@EntityScan("com.UnderUpb.backendUnderUpb.entity")
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
public class BackendUnderUpbApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendUnderUpbApplication.class, args);
	}

}
