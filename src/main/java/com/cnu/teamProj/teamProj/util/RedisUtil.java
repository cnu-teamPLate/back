package com.cnu.teamProj.teamProj.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 만료 시간이 되면 자동으로 삭제되는 데이터 생성
     * @param key 토큰
     * @param value 토큰의 목적
     * @param timeout 만료시간
     */
    public void setValuesWithTimeout(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 키 삭제 위한 함수
     * @param key 삭제하고자 하는 키 값
     */
    public void deleteValue(String key) {redisTemplate.delete(key);}

    /**
     * value 값을 얻기 위한 함수
     * @param key value 값의 키
     * @return value 값
     */
    public Object getValues(String key) {return redisTemplate.opsForValue().get(key);}
}
