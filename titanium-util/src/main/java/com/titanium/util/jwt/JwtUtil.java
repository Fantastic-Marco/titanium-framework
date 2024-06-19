package com.titanium.util.jwt;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.SneakyThrows;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class JwtUtil {
    public static final String SECRET_KEY = "MDkxZDg5ZjItMGExMi00YzBlLThiZTgtOGRkNTJhYzkyNWNk";

    public static String createJwt(String subject, Map<String, ?> claims, String secretKey) {
        byte[] bytes = Decoders.BASE64URL.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(bytes);
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public static String createJwt(String subject, Map<String, ?> claims, String secretKey, Long expireTime, TimeUnit timeUnit) {
        byte[] bytes = Decoders.BASE64URL.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(bytes);
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + timeUnit.toMillis(expireTime))) // 设置过期时间为一小时
                .signWith(key) // 使用HS256算法和密钥签名
                .compact();
    }

    /**
     * 解析jwt
     * @param jwt
     * @param secretKey
     * @return
     * @throws ExpiredJwtException   token过期
     * @throws SignatureException    解析异常
     * @throws MalformedJwtException token格式错误
     */
    public static Jws<Claims> decodeJwt(String jwt, String secretKey) {
        byte[] bytes = Decoders.BASE64URL.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(bytes);
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt);
    }

    public static String generateSalt() {
        return Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }

    @SneakyThrows
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("name", "Marco");
        map.put("age", 20);
        String token = createJwt("Marco", map, SECRET_KEY, 200L, TimeUnit.SECONDS);
        System.out.println("jwt encode: " + token);
        Jws<Claims> claimsJws = decodeJwt(token, SECRET_KEY);
        JwsHeader header = claimsJws.getHeader();
        header.entrySet().forEach(entry -> {
            System.out.println("jwt header:" + entry.getKey() + " : " + entry.getValue());
        });
        System.out.println("jwt claims: " + JSONUtil.toJsonStr(claimsJws));
        System.out.println("jwt decode: " + claimsJws.getPayload().getSubject());
    }
}
