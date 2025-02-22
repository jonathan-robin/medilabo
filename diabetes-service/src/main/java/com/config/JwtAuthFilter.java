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
    private static final String ALLOWED_ORIGIN = "localhost:8080";
    private static final String USER_AGENT = "ReactorNetty/1.1.0";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String forwardedHost = request.getHeader("x-forwarded-host");
        String userAgent = request.getHeader("user-agent");

        if (forwardedHost == null || !forwardedHost.equals(ALLOWED_ORIGIN)) {
            if (userAgent == null || !userAgent.equals(USER_AGENT)) {
            	log.info("forwardedHost : {}", forwardedHost);
            	log.info("userAgent : {}", userAgent);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid Origin");
                response.sendRedirect("/error");
                return;
            }
        }
        
        /* else if its first callback from auth-service and the jwt is correct save user in memory */ 
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

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = User.withUsername("user") 
                    .password("") 
                    .roles("USER")
                    .build();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        else { 
        	log.info("Security context get authenticatoin is not null ");
        }
        

        log.info("request  in filter{} with path ", request.getHeader("Authorization"), request.getRequestURI());
        log.info("response in filter{}, {}", response.getHeader("Authorization"), response.getStatus());

        filterChain.doFilter(request, response);
    }
}
