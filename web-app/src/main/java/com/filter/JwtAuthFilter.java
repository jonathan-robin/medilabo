package com.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String JWT_COOKIE_NAME = "JWT";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	
    	

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/login") || requestURI.startsWith("/public") || requestURI.startsWith("/css") || requestURI.startsWith("/js") || requestURI.startsWith("/images")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
        	
            for (Cookie cookie : cookies) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    jwt = cookie.getValue();;
                    log.info("Filter JWT web-app : {}", jwt);
                    break;
                }
            }
        }
        else { 
        	log.info("No cookie in filter web-app");
        }

        if (jwt == null) {
        	log.info("jwt is null in filter web-app : redirect");
            response.sendRedirect("/login");
            return;
        }

        // Si le JWT est présent, authentifier l'utilisateur
        // Si le JWT est présent et que l'utilisateur n'est pas encore authentifié
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Remplace par un utilisateur réel (par exemple, tu pourrais extraire l'utilisateur via un service)
            UserDetails userDetails = User.withUsername("user") // Remplacer par l'utilisateur réel
                    .password("") // Pas besoin de mot de passe ici
                    .roles("USER") // Définit les rôles selon les besoins
                    .build();

            // Crée un objet d'authentification
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            // Ajoute les détails de l'authentification (détails de la requête)
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Définit l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        else { 
        	log.info("Security context get authenticatoin is not null ");

        }
        

        log.info("request  in filter{} with path ", request.getHeader("Authorization"), request.getRequestURI());

        log.info("response in filter{}, {}", response.getHeader("Authorization"), response.getStatus());

        // Continuer la requête si tout est ok
        filterChain.doFilter(request, response);
    }
}
