package com.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import com.filter.HeaderFilter;
import com.filter.HeaderFilter.Config;
import com.model.Credentials;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RouteConfig {

        @Bean
        public RouteLocator gatewayRoutes(RouteLocatorBuilder builder, HeaderFilter authorizationHeaderFilter) {
  	
    		return builder.routes()
    				.route("auth", r -> r.path("/login")
						.and()
						.method(HttpMethod.POST)
						.uri("http://localhost:8084/login"))
                    .route("patient-service", r -> r.path("/patients", "/patients/**")
                        .filters(f -> f.filter(authorizationHeaderFilter.apply(new Config()), 1))
                        .uri("http://localhost:8081/patients")) 
                    .route("note-route", r -> r.path("/notes", "/notes/**")
	            		  .filters(f -> f.filter(authorizationHeaderFilter.apply(new Config()), 1))
	            		  .uri("http://localhost:8083/notes"))	  
			        .route("diabetes", r -> r.path("/diabetes", "/diabetes/**")
		            	  .filters(f -> f.filter(authorizationHeaderFilter.apply(new Config()), 1))
		          		  .uri("http://localhost:8085/diabetes"))	  
			        .build();
            
                
        }


}