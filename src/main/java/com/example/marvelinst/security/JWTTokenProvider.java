package com.example.marvelinst.security;

import com.example.marvelinst.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTTokenProvider {
    public static final Logger LOG = LoggerFactory.getLogger(JWTTokenProvider.class);

    public String generateToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expireDate = new Date(now.getTime() + SecurityConstant.EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", userId);
        claimsMap.put("username", user.getEmail());
        claimsMap.put("firstName", user.getName());
        claimsMap.put("lastname", user.getLastname());
        return Jwts.builder().subject(userId)
                .claims(claimsMap)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(SecurityConstant.SECRET_KEY).compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(SecurityConstant.SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex){
            LOG.error(ex.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser().verifyWith(SecurityConstant.SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String id = (String) claims.get("id");
        return Long.parseLong(id);
    }
}
