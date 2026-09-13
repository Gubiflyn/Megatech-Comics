package com.megatech.bffservice.security;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Resuelve el identificador de usuario a partir del JWT, sin importar
 * si viene de Azure AD (claim "oid", staff) o de un token local de
 * cliente propio (claim "sub", clienteUuid).
 */
public final class JwtUsuarioIdResolver {

    private JwtUsuarioIdResolver() {
    }

    public static String resolver(Jwt jwt) {
        String usuarioId = jwt.getClaimAsString("oid");

        if (usuarioId == null) {
            usuarioId = jwt.getSubject();
        }

        return usuarioId;
    }
}
