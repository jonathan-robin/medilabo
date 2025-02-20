package auth;


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
	 private static String secretKey;

	@Value("${token.expiration.time}")
	private static long expirationMs;

    public Mono<String> login(String username, String password) {
        if (isValidUser(username, password)) {
            return Mono.just(generateJwt(username)); 
        } else {
            return Mono.error(new RuntimeException("Invalid credentials"));
        }
    }

    private boolean isValidUser(String username, String password) {
        return "user".equals(username) && "password".equals(password);  
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
