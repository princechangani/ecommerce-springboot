package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String message;
    private boolean success;

    public AuthenticationResponse(String accessToken,Long id, String username, Collection<? extends GrantedAuthority> authorities) {
    }
}
