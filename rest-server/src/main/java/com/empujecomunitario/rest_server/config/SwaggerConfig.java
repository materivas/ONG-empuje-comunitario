package com.empujecomunitario.rest_server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(title = "Donaciones REST API", version = "1.0", description = "Endpoints REST del módulo de reportes y filtros personalizados"))
@Configuration
public class SwaggerConfig {
}
