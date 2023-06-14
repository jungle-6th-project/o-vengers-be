package jungle.ovengers.config.security;

import jungle.ovengers.config.security.filter.DelegatedAccessDeniedHandler;
import jungle.ovengers.config.security.filter.DelegatedAuthenticationEntryPoint;
import jungle.ovengers.config.security.filter.token.AuthProvider;
import jungle.ovengers.config.security.filter.token.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthProvider authProvider;
    private final DelegatedAccessDeniedHandler accessDeniedHandler;
    private final DelegatedAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf()
            .disable();
        http.formLogin()
            .disable();
        http.httpBasic()
            .disable();
        http.cors();
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET, "/error", "/api/v1/members/kakao", "/swagger-resources/**", "/swagger-ui/*", "/v2/api-docs", "/bbodok-websocket")
            .permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/members/**")
            .permitAll()
            .antMatchers("/api/v1/**", "/app/**", "/queue/**", "/topic/**")
            .authenticated()
            .anyRequest()
            .denyAll();

        http.addFilterAt(generateAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);

        http.exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler);

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    public AuthenticationFilter generateAuthenticationFilter() {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(new ProviderManager(authProvider));
        return authenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
