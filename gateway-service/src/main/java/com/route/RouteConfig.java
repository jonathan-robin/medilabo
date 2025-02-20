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
  	
        	log.info("***************************** Route config call **************************************");
        		return builder.routes()
                                .route("patient-service", r -> r.path("/patients")
	                                .filters(f -> f.filter(authorizationHeaderFilter.apply(new Config()), 1))
	                                .uri("http://localhost:8081/patients"))
                                
                                /* work well but auth is handle by gateway try to make a dedicated ms */
//                                .route("auth", r -> r.path("/login")
//                                        .and()
//                                        .method(HttpMethod.POST)
//                                        .uri("http://localhost:8080/login"))
                                
                              .route("auth", r -> r.path("/login")
                              .and()
                              .method(HttpMethod.POST)
                              .uri("http://localhost:8084/login"))
                               .build();
                
                
        }


}