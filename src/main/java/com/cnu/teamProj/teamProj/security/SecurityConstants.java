package com.cnu.teamProj.teamProj.security;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 70000;//
    public static final String JWT_SECRET = "ajfkdjadjhjahue45313518435dfasjfhelasifhslae4865313515E212313131515161123156156486512312315648615313131548645645";
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes());;
}