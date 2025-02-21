package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Autowired
 	private JwtAuthFilter jwtAuthFilter;

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     	
         http.csrf().disable()
 	        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) 
 	        .sessionManagement()
 	            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) 
 	        .and()
 	        .formLogin().disable(); 
 	    return http.build();
 	    
     }
     
 	@Bean
 	public SecurityContextRepository securityContextRepository() {
 	    return new HttpSessionSecurityContextRepository();
 	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
