package com.ivanolmo.kanbantaskmanager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
  @Value("${security.cors.allowed-origins}")
  private String allowedOrigins;

  private static final String[] AUTH_WHITELIST = {"/api/v1/auth/**", "/api/v1/actuator/**",
      "/actuator/**", "/api/v1/v3/api-docs/**", "/api/v1/swagger-ui/**", "/h2-console/**"};
  private final AuthenticationProvider authenticationProvider;
  private final JwtAuthFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> {
          for (String endpoint : AUTH_WHITELIST) {
            auth.requestMatchers(antMatcher(endpoint)).permitAll();
          }
          auth.anyRequest().authenticated();
        })
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.addAllowedOrigin(allowedOrigins);
    config.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(),
        HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
    config.setAllowedHeaders(Arrays.asList(HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION,
        HttpHeaders.CONTENT_TYPE, HttpHeaders.ORIGIN, HttpHeaders.USER_AGENT, HttpHeaders.REFERER,
        HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    config.setMaxAge(3600L);
    source.registerCorsConfiguration("/**", config);

    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(-102);

    return bean;
  }
}
