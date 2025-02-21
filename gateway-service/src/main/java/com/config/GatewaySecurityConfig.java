//package com.config;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.security.web.session.SessionManagementFilter;
//
//import jakarta.servlet.Filter;
//import lombok.extern.slf4j.Slf4j;
//
//
//@Configuration
//@Slf4j
//@EnableWebSecurity(debug = true)
//public class GatewaySecurityConfig {
//
//   @Autowired
//	private SecurityFilter securityFilter;
//   BasicAuthenticationFilter basic;
//   
//
//
//
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    	
////        FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
////        List<SecurityFilterChain> list = filterChainProxy.getFilterChains();
////        list.stream()
////          .forEach(filter -> log.info("class: {}", filter.getClass()));
//    	
//        http.csrf().disable()
//        .authorizeRequests()
//            .anyRequest().permitAll()  // Permet tout l'accès pour toutes les requêtes
//        .and()
//        .addFilterBefore(securityFilter, SessionManagementFilter.class)
//        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
//        .addFilterBefore(securityFilter, BasicAuthenticationFilter.class);
////        .addFilterAfter(basic, securityFilter);
//        // Ajoute ton filtre personnalisé avant le filtre d'authentification standard de Spring Security
////        .formLogin().disable() ;// Désactive l'authentification par formulaire
////        .httpBasic().disable(); // Désactive l'authentification HTTP Basic
//
//    // On ajoute une sécurité personnalisée pour éviter l'exécution d'autres filtres de sécurité
////	    http.addFilterAfter(securityFilter, BasicAuthenticationFilter.class);
//	    return http.build();
//	    
//    }
//}
