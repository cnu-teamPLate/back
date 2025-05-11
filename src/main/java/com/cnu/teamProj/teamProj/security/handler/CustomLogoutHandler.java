package com.cnu.teamProj.teamProj.security.handler;

import com.cnu.teamProj.teamProj.security.jwt.JWTGenerator;
import com.cnu.teamProj.teamProj.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final JWTGenerator jwtGenerator;
    private final RedisUtil redisUtil;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = request.getHeader("Authorization").substring(7);
        String userId = jwtGenerator.getUserNameFromJWT(token);
        log.info("토큰에서 꺼낸 userId = {}", userId);
        long expiration = jwtGenerator.getRemainingTime(token);
        //redis 에 저장
        redisUtil.setValuesWithTimeout(token, "logout", expiration);

        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }

    }
}
