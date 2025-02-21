package com.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Credentials {
    private String username;
    private String password;
    
    public Credentials(String username, String password) { 
    	this.username = username; 
    	this.password = password;
    }
    
}