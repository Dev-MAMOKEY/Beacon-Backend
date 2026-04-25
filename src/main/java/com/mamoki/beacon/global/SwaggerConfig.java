package com.mamoki.beacon.global;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "비콘 API",
                version = "v1",
                description = "비콘 API 문서입니다."
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 서버"),
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Authrorization 헤더에 Bearer {accessToken} 형식으로 JWT 토큰을 전달하여 인증을 수행합니다."
)
public class SwaggerConfig {
}
