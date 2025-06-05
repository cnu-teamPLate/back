package com.cnu.teamProj.teamProj.security.jwt;

import com.cnu.teamProj.teamProj.security.service.SecurityConstants;
import com.cnu.teamProj.teamProj.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Slf4j
public class JWTGenerator {

    private final RedisUtil redisUtil;

    public JWTGenerator(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

        log.info("expireDate: {}", expireDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SecurityConstants.SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }
    public String getUserNameFromJWT(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SecurityConstants.SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();//
    }

    /**
     * 토큰의 유효성 확인
     * @param token 클라이언트로부터 받은 토큰
     * @return
     */
    public boolean validateToken(String token){
        if(redisUtil.getValues(token) != null) {
            log.error("무효화된 토큰입니다");
            return false;
        }
        try{
            Jwts.parserBuilder().setSigningKey(SecurityConstants.SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch(Exception ex){
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        }
    }

    public long getRemainingTime(String jwt) {
        Claims claim = Jwts.parserBuilder().setSigningKey(SecurityConstants.SECRET_KEY).build().parseClaimsJws(jwt).getBody();
        Date expiration = claim.getExpiration();
        Date now = new Date();

        return expiration.getTime() - now.getTime();
    }
}
