package com.cnu.teamProj.teamProj.config;

import com.google.common.collect.Lists;
<<<<<<< HEAD
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
=======
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
>>>>>>> 8eab220069bed35ff4329f8543e77adb7145fa4c
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//@EnableSwagger2
@Configuration
@EnableWebMvc
public class SwaggerConfig {
//    @Bean
//    public Docket openApi() {
<<<<<<< HEAD
//        return new Docket(DocumentationType.OAS_30)
=======
//        return new Docket(DocumentationType.SWAGGER_2)
>>>>>>> 8eab220069bed35ff4329f8543e77adb7145fa4c
//                .securityContexts(Collections.singletonList(securityContext())) //시큐리티용
//                .securitySchemes(List.of(apiKey())) //시큐리티용
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.cnu.teamProj.teamProj.*.controller"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(apiInfo());
//    }
<<<<<<< HEAD
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("teamPlate 프로젝트 API Documentation")
//                .description("API 사용 설명서입니다.")
//                .version("1.0.0")
//                .termsOfServiceUrl("")
//                .license("LICENSE")
//                .licenseUrl("")
//                .build();
//    }
//
//    private ApiKey apiKey(){
//        return new ApiKey("JWT", "Authorization", "header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder().securityReferences(defaultAuth())
//                .operationSelector(operationContext -> true)
//                .build();
//    }
//
//    List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
//    }

    @Bean
    public OpenAPI openAPI(){
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
=======
    @Bean
    public OpenAPI openAPI(){
        Info info = new Info().version("v1.0.0").title("API-teamPlate").description("API Description");
        return new OpenAPI().info(info);
    }
>>>>>>> 8eab220069bed35ff4329f8543e77adb7145fa4c

    }
    private Info apiInfo(){
        return new Info()
                .title("TeamPlate API")
                .description("cnu 팀 teamPlate API입니다")
                .version("v1.0.0");
    }
}
