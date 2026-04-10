package org.example.config;


import lombok.RequiredArgsConstructor;
import org.example.properties.SpaceCenterServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final SpaceCenterServiceProperties properties;

    @Bean
    public RestClient spaceOperationRestClient() {
        return RestClient.builder()
                .baseUrl(properties.url())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}