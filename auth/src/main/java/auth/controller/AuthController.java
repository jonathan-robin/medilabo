package auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import auth.model.Credentials;
import auth.service.AuthService;import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class AuthController {
	
	
	private final AuthService auth; 
	
	public AuthController(AuthService auth) {
		this.auth = auth;
	}

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Credentials credentials, HttpServletRequest request, HttpServletResponse response) {
    	
        log.info("Intercept request POST /login : username={}, password={}", credentials.getUsername(), credentials.getPassword());
        if ("user".equals(credentials.getUsername()) && "pass".equals(credentials.getPassword())) {
        	String jwt = auth.login(credentials.getUsername(), credentials.getPassword());
        	response.setHeader("Authorization", "Bearer "+jwt);
        	response.addCookie(new Cookie("JWT", jwt.toString()));
        	return ResponseEntity.ok(jwt);
        }	
        else
        	throw new RuntimeException("Authentication failed");
        
        
    }

	
}
