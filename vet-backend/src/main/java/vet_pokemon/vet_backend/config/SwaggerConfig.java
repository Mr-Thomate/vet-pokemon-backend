package vet_pokemon.vet_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("API Veterinaria Huellitas")
                        .version("0.1.0")
                        .description("Backend REST de la veterinaria Pokemon (proyecto Fullstack 2)")
        );
    }
}
