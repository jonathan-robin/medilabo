package auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler {

    private final AuthService authService;

    @Autowired
    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(Credentials.class) 
                .flatMap(credentials -> authService.login(credentials.getUsername(), credentials.getPassword()))
                .flatMap(jwt -> ServerResponse.ok().bodyValue(jwt)) 
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }
}
