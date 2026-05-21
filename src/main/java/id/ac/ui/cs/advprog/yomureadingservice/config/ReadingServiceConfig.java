package id.ac.ui.cs.advprog.yomureadingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configures a {@link RestClient} instance pre-wired with the Reading Service base URL.
 * The base URL is resolved from {@code reading.service.base-url} in application.properties
 * and can be overridden via the {@code READING_SERVICE_BASE_URL} environment variable.
 */
@Configuration
public class ReadingServiceConfig {

    @Value("${reading.service.base-url}")
    private String readingServiceBaseUrl;

    @Bean
    public RestClient readingServiceRestClient() {
        return RestClient.builder()
                .baseUrl(readingServiceBaseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
