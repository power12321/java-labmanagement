package org.example.core.util;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.core.domain.SysUser;

import java.util.Date;

/**
 * JWT工具类
 */
public class JwtUtil {

    /**
     * 给指定用户生成jwt令牌
     * @param user 用户信息对象
     * @param secret 密钥
     * @return 生成的令牌
     */
    public static String createToken(JSONObject user, String secret){
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512,secret)
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))//1天有效期
                .claim("userId", user.getLong("userId"))
                .claim("roleId", user.getLong("roleId"))
                .claim("account", user.getString("account"))
                .claim("name", user.getString("name"))
                .compact();

    }

    /**
     * 校验token是否有效
     * @param token 要校验的令牌
     * @return true表示令牌合法；false表示令牌不合法
     */
    public static boolean isValidToken(String token, String secret){
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            // 检查Redis黑名单
            return !SpringUtils.getBean(RedisUtil.class).isInBlacklist(token);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解析token，得到其中的用户信息
     * @param token 要解析的令牌
     * @return 解析得到的User对象
     */
    public static SysUser parseToken(String token, String secret) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        SysUser user = new SysUser();
        user.setUserId(Long.parseLong(claims.get("userId").toString()));
        user.setName(claims.get("name").toString());
        user.setRoleId(Long.parseLong(claims.get("roleId").toString()));
        user.setAccount(claims.get("account").toString());
        return user;
    }

    /**
     * 注销token
     * @param token
     */
    public static void invalidateToken(String token) {
        String secret = "0xx6znv3ozvh9fum";
        // 获取令牌剩余有效期
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();
        // 将令牌加入Redis黑名单
        if (expiration > 0) {
            SpringUtils.getBean(RedisUtil.class).addToBlacklist(token, expiration);
        }
    }

 
}
