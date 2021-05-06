package com.brwallet.config;

import java.nio.charset.Charset;
import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
	
	@Bean
	public RestTemplate restTemlplate() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

		factory.setReadTimeout(5000);
		factory.setConnectTimeout(5000);

		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.getMessageConverters()
			.add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		
		return restTemplate;

	}
}
