package com.chorewebpage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class NtfyConfig {

    @Bean
    public RestClient ntfyRestClient(NtfyProperties ntfyProperties) {
        RestClient.Builder builder = RestClient.builder();
        if (StringUtils.hasText(ntfyProperties.getBaseUrl())) {
            builder.baseUrl(ntfyProperties.getBaseUrl());
        }
        return builder.build();
    }
}
