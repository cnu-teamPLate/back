package com.cnu.teamProj.teamProj.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Configuration
@EnableWebMvc
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));

        //https 접근 가능 설정
        Server server = new Server();
        server.setUrl("https://www.teamplate-api.site");

        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .servers(List.of(server))
                .addSecurityItem(securityRequirement)
                .components(components);

    }
    private Info apiInfo(){
        return new Info()
                .title("TeamPlate API")
                .description("cnu 팀 teamPlate API입니다")
                .version("v1.0.0");
    }
}
