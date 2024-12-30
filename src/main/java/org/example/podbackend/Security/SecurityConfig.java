package org.example.podbackend.Security;

import org.apache.catalina.filters.CorsFilter;
import org.example.podbackend.common.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailService customUserDetailService;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final PodAuthenticationExceptionEntryPoint podAuthenticationExceptionEntryPoint;
  private final TokenAuthenticationProvider tokenAuthenticationProvider;
  public SecurityConfig(
          CustomUserDetailService customUserDetailService,
          JwtAuthenticationFilter jwtAuthenticationFilter,
          PodAuthenticationExceptionEntryPoint podAuthenticationExceptionEntryPoint,
          TokenAuthenticationProvider tokenAuthenticationProvider
  ) {
    this.customUserDetailService = customUserDetailService;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.podAuthenticationExceptionEntryPoint = podAuthenticationExceptionEntryPoint;
    this.tokenAuthenticationProvider = tokenAuthenticationProvider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
      .authorizeHttpRequests(
      (authorizeRequests) ->
        authorizeRequests
                .requestMatchers("/api/auth/u/**").permitAll()
                .requestMatchers("/image/**").permitAll()
                .requestMatchers("/api/banks/u").permitAll()
                .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .exceptionHandling(exception -> exception.authenticationEntryPoint(podAuthenticationExceptionEntryPoint))
      .csrf(AbstractHttpConfigurer::disable)
      .cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return httpSecurity.build();
  }

  @Bean
  public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
    daoProvider.setUserDetailsService(customUserDetailService);
    daoProvider.setPasswordEncoder(bCryptPasswordEncoder());
    authenticationManagerBuilder.authenticationProvider(tokenAuthenticationProvider);
    authenticationManagerBuilder.authenticationProvider(daoProvider);
    return authenticationManagerBuilder.build();
  }

  BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.addAllowedOriginPattern("*");
    corsConfiguration.addAllowedMethod("*");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}
