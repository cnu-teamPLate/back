package com.cnu.teamProj.teamProj.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
public class SecurityUtil {
    public static String getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        log.info("시큐리티 컨텍스트에서 찾은 유저 정보: {}", userId);
        return userId;
    }
}
