package com.st00mp.agentindexbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global OpenAPI information displayed at the top of Swagger UI.
 * Endpoint-level documentation lives on the controllers; common error
 * behavior (400/404) is handled by the GlobalExceptionHandler.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI agentIndexOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agent Index API")
                        .version("v1")
                        .description("""
                                REST API to manage agent templates and their instances. \
                                Templates hold reusable instructions with {{placeholders}}; \
                                instances bind values to those placeholders and assemble the final output.""")
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT"))
                        .contact(new Contact()
                                .name("Agent Index")
                                .url("https://github.com/st00mp/agent-index-backend")));
    }
}
