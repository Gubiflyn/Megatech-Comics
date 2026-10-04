package com.megatech.bffservice.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Validates that the "aud" claim of an incoming JWT contains the audience
 * this resource server expects. Spring Security only validates issuer and
 * signature by default, so this closes the audience-confusion gap.
 */
public class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

    private static final String ERROR_CODE = "invalid_token";

    private final String expectedAudience;

    public JwtAudienceValidator(String expectedAudience) {
        this.expectedAudience = expectedAudience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getAudience() != null && jwt.getAudience().contains(expectedAudience)) {
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error = new OAuth2Error(
                ERROR_CODE,
                "The required audience '" + expectedAudience + "' is missing from the token",
                null
        );
        return OAuth2TokenValidatorResult.failure(error);
    }
}
