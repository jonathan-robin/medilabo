
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

@Configuration
@EnableWebSecurity
public class WebAppSecurityConfig {

   @Autowired
	private JwtAuthFilter jwtAuthFilter;
//
//    public WebAppSecurityConfig(JwtAuthFilter jwtAuthFilter) {
//        this.jwtAuthFilter = jwtAuthFilter;
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	

        http
	        .csrf().disable()
	        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Ajoute le filtre avant Spring Security
	        .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // Crée une session à chaque fois qu'une authentification est réussie
	        .and()
	        .formLogin().disable(); 
	    return http.build();
//            .csrf().disable()
//            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Vérifie le JWT avant Spring Security
////            .securityContext(securityContext -> securityContext
////                    .securityContextRepository(new HttpSessionSecurityContextRepository()) // 🔥 Stocke dans la session
////                )
////            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////            .sessionManagement(session -> session
////                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 🔥 Active la session si besoin
////                )
////            .and()
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/login", "/unauthorized").permitAll()
////                .anyRequest().authenticated()
////                .and()
//            );
////            .formLogin().disable();
//
//        return http.build();
    }
    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        
//        http
//            .csrf(csrf -> csrf.disable())
//            .securityContext(securityContext -> securityContext
//                .securityContextRepository(new HttpSessionSecurityContextRepository()) // 🔥 Stocke dans la session
//            )
// 
//            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) 
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/login", "/unauthorized").permitAll()
//                .requestMatchers("/patients/**").authenticated()
//            )
//            .formLogin(form -> form.disable());
//
//        return http.build();
//    }

    
	@Bean
	public SecurityContextRepository securityContextRepository() {
	    return new HttpSessionSecurityContextRepository();
	}
}
