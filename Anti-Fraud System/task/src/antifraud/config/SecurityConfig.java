package antifraud.config;

import antifraud.model.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf().disable()
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                )
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasAuthority(UserRole.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.SUPPORT.name())
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAuthority(UserRole.MERCHANT.name())
                        .requestMatchers("/api/antifraud/suspicious-ip/**").hasAuthority(UserRole.SUPPORT.name())
                        .requestMatchers("/api/antifraud/stolencard/**").hasAuthority(UserRole.SUPPORT.name())
                        .requestMatchers("/api/antifraud/history/**").hasAuthority(UserRole.SUPPORT.name())
                        .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAuthority(UserRole.SUPPORT.name())
                        .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasAuthority(UserRole.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasAuthority(UserRole.ADMINISTRATOR.name())
                        .requestMatchers("/actuator/shutdown").permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
