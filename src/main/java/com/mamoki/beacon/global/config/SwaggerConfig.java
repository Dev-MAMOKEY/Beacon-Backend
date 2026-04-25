package com.mamoki.beacon.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition( // API 문서 전체 정보 정의
        info = @Info(
                title = "비콘 API",
                version = "v1",
                description = "비콘 API 문서입니다."
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 서버"),
        },
        security = { // 모든 API에 기본적으로 JWT 인증 적용
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth", // SecurityRequirement에서 참조하는 이름
        type = SecuritySchemeType.HTTP, // HTTP 인증 방식
        scheme = "bearer", // Bearer 토큰 방식
        bearerFormat = "JWT", // 토큰 형식 설명
        description = "Authrorization 헤더에 Bearer {accessToken} 형식으로 JWT 토큰을 전달하여 인증을 수행합니다." // Swagger UI에서 인증 토큰 입력 시 참고할 설명
)
public class SwaggerConfig {
}
