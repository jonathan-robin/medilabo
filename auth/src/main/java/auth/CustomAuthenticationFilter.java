package auth;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.naming.AuthenticationException;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.apache.commons.codec.digest.DigestUtils;



import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CustomAuthenticationFilter that implements UsernamePasswordAuthenticationFilter used by
 * formLogin. Why this custom authentication filter just to override method
 * successfullAuthentication to add a jwtoken in response when authenticate request of login is
 * sucessfull.
 */
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


	    private final AuthenticationManager authenticationManager;

	    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
	        this.authenticationManager = authenticationManager;
	    }

	    @Override
	    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
	        String username = request.getParameter("username");
	        String password = request.getParameter("password");

	        System.out.println("Tentative de connexion pour l'utilisateur: " + username);

	        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
	        }

	        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
	        return authenticationManager.authenticate(authenticationToken);
	    }

	    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException failed) throws IOException, ServletException {
	        System.out.println("Échec de l'authentification pour l'utilisateur: " + request.getParameter("username"));
	        super.unsuccessfulAuthentication(request, response, failed);
	    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = ((User) authResult.getPrincipal()).getUsername();
        String tokenSecret ="7DA2220C0016000C0047BB08F1F84BCD208F369A45AE16D5CC27E464FCE388A0";
        byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        // Utilisation de DigestUtils pour générer un MD5 de username ou MessageDigest en Java natif
        String usernameHash = DigestUtils.md5Hex(username); // ou remplacer par une autre méthode MD5

        // Construction du token JWT
        String jwToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")  // Définir l'en-tête avec un paramètre typ
                .setSubject(username)           // Définir le sujet (username)
                .setId(usernameHash)           // Utiliser le hash du username
                .setExpiration(Date.from(now.plusMillis((86400000L))))
                .setIssuedAt(Date.from(now))
                .signWith(secretKey, SignatureAlgorithm.HS256)  // Utilisation de HS256 comme algorithme
                .compact();

        // Ajouter le JWT dans les en-têtes de la réponse
        response.addHeader("Authorization", "Bearer " + jwToken);

    }

}