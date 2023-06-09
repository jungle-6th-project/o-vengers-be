package jungle.ovengers.config.security;

import jungle.ovengers.config.security.filter.DelegatedAccessDeniedHandler;
import jungle.ovengers.config.security.filter.DelegatedAuthenticationEntryPoint;
import jungle.ovengers.config.security.filter.token.AuthProvider;
import jungle.ovengers.config.security.filter.token.AuthenticationFilter;
import jungle.ovengers.config.security.filter.token.TokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthProvider authProvider;
    private final DelegatedAccessDeniedHandler accessDeniedHandler;
    private final DelegatedAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    @Profile(value = {"default", "local"})
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/error", "/api/v1/members/kakao")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/members/**")
                .permitAll()
                .antMatchers("/api/v1/**")
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
}
