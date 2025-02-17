package com.service.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

/* chargé de récupérer le token JWT dans les headers */
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Récupérer le token JWT de l'en-tête Authorization
        String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // Vérifier que l'en-tête contient le mot-clé "Bearer"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extraire le token sans "Bearer "
            token = authorizationHeader.substring(7);
            try {
                // Extraire les informations (claims) du token
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.error(e);
            }
        }

        // Si un username est extrait et que l'utilisateur n'est pas encore authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Valider le token et créer un contexte d'authentification
            if (jwtUtil.validateToken(token, username)) {
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Passer la requête au filtre suivant
        filterChain.doFilter(request, response);
    }



    @Override
    public void destroy() {
        // Méthode vide car il n'y a pas de ressources à nettoyer
    }


}
