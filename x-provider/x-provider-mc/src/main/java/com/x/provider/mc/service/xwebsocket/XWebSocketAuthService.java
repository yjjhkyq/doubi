package com.x.provider.mc.service.xwebsocket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * @author: liushenyi
 * @date: 2022/07/22/18:08
 */
public class XWebSocketAuthService {

    private String tokenSecret;

    public XWebSocketAuthService(String tokenSecret){
        this.tokenSecret = tokenSecret;
    }

    public String authenticationToken(long expireAt, String subject) {
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        return JWT.create().withExpiresAt(new Date(expireAt))
                .withSubject(subject)
                .sign(algorithm)
                ;
    }

    public String getSubject(String token) {
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
