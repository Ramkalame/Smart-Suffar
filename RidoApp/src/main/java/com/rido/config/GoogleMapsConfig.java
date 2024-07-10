package com.rido.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
//@ConfigurationProperties(prefix = "google.maps.api")
public class GoogleMapsConfig {

	private String key;

	   @Value("${google.maps.api.key}")
	    private String apiKey;

	    @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }
	
	

	    // You can add more configurations here if needed
	}


