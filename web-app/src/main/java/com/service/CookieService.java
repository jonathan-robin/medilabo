package com.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class CookieService {

	public String getCookie(HttpServletRequest request) {
		
		
		String jwtFromHeader = null;
        String jwtFromCookie = null;
        String jwt = null;

        if (request.getHeader("Authorization") != null)
            jwtFromHeader = request.getHeader("Authorization").replace("Bearer ", "");

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JWT")) {
                jwtFromCookie = cookie.getValue();
            }
        }
        
        jwt = (jwtFromCookie != null) ? jwtFromCookie : jwtFromHeader;
        return jwt;
        

   
	}
	
	
	
	
	
}
