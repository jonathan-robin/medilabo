package auth.service;


import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;
@Service
public class AuthService {
	
	 @Value("${token.secret.key}")
	 
	 
	 private static String secretKey = "7DA2220C0016000C0047BB08F1F84BCD208F369A45AE16D5CC27E464FCE388A0";


	private static long expirationMs = 3600000;

    public String login(String username, String password) throws RuntimeException {
        if (isValidUser(username, password)) {
            return generateJwt(username); 
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    private boolean isValidUser(String username, String password) {
        return "user".equals(username) && "pass".equals(password);  
    }

    public static String generateJwt(String username) {
        byte[] secretKeyBytes = Base64.getEncoder().encode(secretKey.getBytes());
        SecretKey key = Keys.hmacShaKeyFor(secretKeyBytes);

        Date expirationDate = new Date(System.currentTimeMillis() + expirationMs);

        return Jwts.builder()
                .setSubject(username)  
                .setIssuedAt(new Date())  
                .setExpiration(expirationDate)
                .claim("role", "USER")  
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact();
    }
}
