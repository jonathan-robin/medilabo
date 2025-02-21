package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

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
