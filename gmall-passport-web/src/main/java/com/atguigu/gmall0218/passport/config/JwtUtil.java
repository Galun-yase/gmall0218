package com.atguigu.gmall0218.passport.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

/**
 * @author 任青成
 * @date 2020/8/15 19:43
 */
public class JwtUtil {

    /**
     * 生成TOKEN
     *
     * @param key   公共部分
     * @param param 私有部分
     * @param salt  签名部分
     * @return
     */
    public static String encode(String key, Map<String, Object> param, String salt) {
        if (salt != null) {
            key += salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
        // 将用户信息放入jwtBuilder
        jwtBuilder = jwtBuilder.setClaims(param);
        // 生成token
        String token = jwtBuilder.compact();
        return token;

    }

    /**
     * 解析token
     *
     * @param token 生成的字符串token
     * @param key   公共部分
     * @param salt  私有部分
     * @return
     */
    public static Map<String, Object> decode(String token, String key, String salt) {
        Claims claims = null;
        if (salt != null) {
            key += salt;
        }
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            return null;
        }
        return claims;
    }


}
