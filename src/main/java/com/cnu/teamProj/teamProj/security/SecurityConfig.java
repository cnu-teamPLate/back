package com.cnu.teamProj.teamProj.security;
import static com.cnu.teamProj.teamProj.security.SecurityConstants.*;

import com.cnu.teamProj.teamProj.security.handler.CustomLogoutHandler;
import com.cnu.teamProj.teamProj.security.handler.CustomLogoutSuccessHandler;
import com.cnu.teamProj.teamProj.security.jwt.JWTAuthenticationFilter;
import com.cnu.teamProj.teamProj.security.jwt.JWTGenerator;
import com.cnu.teamProj.teamProj.security.jwt.JwtAuthEntryPoint;
import com.cnu.teamProj.teamProj.security.service.CustomUserDetailsService;
import com.cnu.teamProj.teamProj.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@Configurable
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;

    private final CustomUserDetailsService userDetailsService;

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private final JWTGenerator jwtGenerator;
    private final RedisUtil redisUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((exception)->exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers(PUBLIC_ENDPOINTS.toArray(new String[0])).permitAll()
                            .requestMatchers(USER_ENDPOINTS.toArray(new String[0])).hasRole("USER")
                            .requestMatchers(MANAGER_ENDPOINTS.toArray(new String[0])).hasRole("MANAGER")
                            .anyRequest().authenticated();
                }) //,"/v3/api-docs/**","/swagger-resources/**", "/webjars/**", "/api/logistics"
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(logout ->
                        logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                                .addLogoutHandler(new CustomLogoutHandler(jwtGenerator, redisUtil))
                                .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                                .deleteCookies("User-Token", "JSESSIONID")
                        );
        ;
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000", "https://teamplate-front.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.HEAD.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));
        configuration.setMaxAge(6000L);
        configuration.setAllowedHeaders(List.of("*", "Origin"));//test
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public JWTAuthenticationFilter jwtAuthenticationFilter(){
//        return new JWTAuthenticationFilter();
//    }
}
