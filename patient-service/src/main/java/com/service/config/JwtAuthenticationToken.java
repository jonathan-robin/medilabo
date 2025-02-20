//package com.service.config;
//
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.util.Collection;
//import java.util.Collections;
//
//public class JwtAuthenticationToken extends AbstractAuthenticationToken {
//
//    private final String jwt;  // Le JWT est stocké ici
//    private String username;   // Le nom d'utilisateur extrait du JWT
//
//    // Le constructeur avec le JWT
//    public JwtAuthenticationToken(String jwt) {
//        super(Collections.emptyList());  // Initialement, il n'y a pas d'autorités
//        this.jwt = jwt;
//        setAuthenticated(false);  // L'authentification n'est pas encore vérifiée
//    }
//
//    // Le constructeur avec les autorités et le nom d'utilisateur
//    public JwtAuthenticationToken(String jwt, String username, Collection<GrantedAuthority> authorities) {
//        super(authorities);  // Passer les autorités ici
//        this.jwt = jwt;
//        this.username = username;
//        setAuthenticated(true);  // On marque l'authentification comme validée
//    }
//
//    @Override
//    public Object getCredentials() {
//        return this.jwt;  // Retourne le JWT
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return this.username;  // Retourne le nom d'utilisateur
//    }
//
//    @Override
//    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
//        super.setAuthenticated(authenticated);
//    }
//
//    public String getJwt() {
//        return this.jwt;  // Getter pour obtenir le JWT
//    }
//
//    public String getUsername() {
//        return this.username;  // Getter pour obtenir le nom d'utilisateur
//    }
//}
