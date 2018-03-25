package com.armandorvila.poc.accounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;

@EnableWebFlux
@Configuration
public class WebFluxConfiguration {

	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}
}
