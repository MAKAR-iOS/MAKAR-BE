package makar.dev.common.security.config;

import lombok.RequiredArgsConstructor;
import makar.dev.common.security.filter.JwtAccessDeniedHandler;
import makar.dev.common.security.filter.JwtAuthenticationEntryPoint;
import makar.dev.common.security.filter.JwtFilter;
import makar.dev.common.security.jwt.JwtVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtVerifier jwtVerifier;

    @Bean
    public BCryptPasswordEncoder customPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtVerifier);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http
                .cors(cors -> {});
        // H2 DB 헤더 옵션
        http
                .headers(headers ->
                        headers.addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)
                        )
                );
        http.exceptionHandling(
                exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
        );
        http.sessionManagement(
                sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers(request -> request.getRequestURI().startsWith("/swagger-ui")).permitAll()
                        .requestMatchers(request -> request.getRequestURI().startsWith("/v3/api-docs")).permitAll()
                        .requestMatchers(request -> request.getRequestURI().startsWith("/api/v1/auth/sign-up")).permitAll()
                        .requestMatchers(request -> request.getRequestURI().startsWith("/api/v1/auth/sign-in")).permitAll()
                        .anyRequest().authenticated()
        );
        http.addFilterBefore(
                jwtFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
