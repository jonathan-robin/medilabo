
package com.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.filter.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class WebAppSecurityConfig {

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
}
