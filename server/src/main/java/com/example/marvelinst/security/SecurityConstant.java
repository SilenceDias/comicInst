package com.example.marvelinst.security;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.security.Key;

public class SecurityConstant {

    public static final String SIGN_UO_URLS = "/api/auth/**";

    public static final String SECRET = "SecretKeyGenJWT";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER_STRING = "Authorization";

    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 600_000; //10 min

    public static final SecretKey SECRET_KEY = Jwts.SIG.HS512.key().build();
}
