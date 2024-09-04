package com.rookie.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rookie.model.Token;

import java.util.Date;

public class JWTUtils {
    public static String tokenRelease(Token token, String secret, Long expire) {
        return JWT.create()
                .withClaim("id", token.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + expire))
                .sign(Algorithm.HMAC256(secret));
    }

    public static Token tokenParse(String token, String secret) {
        DecodedJWT decodedJWT = JWT
                .require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);

        Long id = decodedJWT.getClaim("id").asLong();
        return new Token(id);
    }
}
