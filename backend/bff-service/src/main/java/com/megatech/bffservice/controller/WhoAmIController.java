package com.megatech.bffservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bff")
public class WhoAmIController {

    @GetMapping("/whoami")
    public Map<String, Object> whoami(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sub", jwt.getSubject());
        response.put("aud", jwt.getAudience());
        response.put("iss", jwt.getIssuer() != null ? jwt.getIssuer().toString() : null);
        response.put("roles", jwt.getClaimAsStringList("roles"));
        response.put("scopes", scopesAsList(jwt.getClaimAsString("scp")));
        response.put("issuedAt", jwt.getIssuedAt());
        response.put("expiresAt", jwt.getExpiresAt());
        response.put("claims", jwt.getClaims());
        return response;
    }

    private List<String> scopesAsList(String scp) {
        if (scp == null || scp.isBlank()) {
            return List.of();
        }
        return Arrays.asList(scp.split(" "));
    }
}
