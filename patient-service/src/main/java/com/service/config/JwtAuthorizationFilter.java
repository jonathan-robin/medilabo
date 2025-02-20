//package com.service.config;
//
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//
//@Slf4j
//public class JwtAuthorizationFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String authorizationHeader = request.getHeader("Authorization");
//
//        log.info("authorizationHeader: {}", authorizationHeader);
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            String jwt = authorizationHeader.substring(7);  // Extraction du token
//            log.info("jwt: {}", jwt);
//
//            // Ici, tu devrais ajouter la logique pour valider le JWT
//            // Si le JWT est valide, on crée un JwtAuthenticationToken
//
//            JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
//            // Définir l'authentification à true si le JWT est validé
//            authentication.setAuthenticated(true);
//            response.addHeader("Authorization", authorizationHeader);
//            
//            // Mettre l'authentification dans le contexte de sécurité
////            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else {
//        	log.info("Authorization header is missing or invalid.");
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid.");
//            return;
//        }
//        
//        
//
//        // Continuer avec la chaîne de filtres
//        filterChain.doFilter(request, response);
//    }
//}
