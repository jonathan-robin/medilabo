package auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class AuthSecurityConfig {

    private final AuthenticationManager authenticationManager;

    public AuthSecurityConfig(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {
        return new CustomAuthenticationFilter(authenticationManager);
    }

    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .requestMatchers("/login").permitAll()
            .anyRequest().authenticated();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }
}
