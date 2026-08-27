package com.musicapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI description served at /v3/api-docs, browsable at /swagger-ui.html.
 *
 * The bearer scheme is declared globally so the "Authorize" button in Swagger UI
 * applies the JWT to every request; sign in through POST /api/auth/signin first
 * and paste the accessToken.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI musicApiOpenAPI() {
        final String bearer = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Music API")
                        .version("1.0.0")
                        .description("""
                                REST backend for the music streaming site: songs, albums, genres,
                                likes, comments, play history, artist studio and admin moderation.

                                Every endpoint answers with the same envelope:
                                `{ "success": boolean, "message": string, "data": object|null }`.
                                `success` is true only for 2xx responses, and `message` never
                                carries internal error text.

                                Authentication: POST /api/auth/signin, then press Authorize and
                                paste the returned `accessToken`.
                                """)
                        .contact(new Contact().name("Music API team"))
                        .license(new License().name("Proprietary")))
                // Relative on purpose: Swagger UI then calls whatever host it was
                // loaded from. An absolute URL here breaks the moment the app runs
                // anywhere other than the machine that wrote it.
                .servers(List.of(new Server()
                        .url("/")
                        .description("This server")))
                .addSecurityItem(new SecurityRequirement().addList(bearer))
                .components(new Components().addSecuritySchemes(bearer,
                        new SecurityScheme()
                                .name(bearer)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT returned by POST /api/auth/signin")));
    }
}
