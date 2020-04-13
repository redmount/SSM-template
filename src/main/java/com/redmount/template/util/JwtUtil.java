package com.redmount.template.util;

import com.redmount.template.core.ProjectConstant;
import com.redmount.template.core.exception.AuthorizationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Token    生成/验证工具
 * author:  朱峰
 * date:    2019年1月28日
 */
@Component
public class JwtUtil {

    private static String key;

    @Value("${token.key}")
    public void setKey(String key) {
        JwtUtil.key = key;
    }

    private static String getKey() {
        return key;
    }

    private static Long expireTime;

    @Value("${token.expireTime}")
    public void setExpireTime(Long expireTime) {
        JwtUtil.expireTime = expireTime;
    }

    private static Long getExpireTime() {
        return expireTime;
    }

    /**
     * 用户登录成功后生成Jwt
     * 使用Hs256算法
     *
     * @return 加密后的JWT文本
     */
    public static String createJWT(Object user) {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<>();
        Object value;
        for (Field field : Objects.requireNonNull(ReflectUtil.getFieldList(user.getClass()))) {
            field.setAccessible(true);
            value = ReflectUtil.getFieldValue(user, field.getName());
            if (value != null) {
                claims.put(field.getName(), value);
            }
        }

        //生成签发人
        String subject = ProjectConstant.PROJECT_NAME;

        //下面就是在为payload添加各种标准声明和私有声明了
        //这里其实就是new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                //iat: jwt的签发时间
                .setIssuedAt(now)
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, getKey());
        if (getExpireTime() >= 0) {
            long expMillis = nowMillis + getExpireTime();
            Date exp = new Date(expMillis);
            //设置过期时间
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * Token的解密
     *
     * @param token 加密后的token
     * @return 解密后的用户信息
     */
    public static Claims parseJWT(String token) {
        //得到DefaultJwtParser
        return Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(getKey())
                //设置需要解析的jwt
                .parseClaimsJws(token).getBody();
    }


    /**
     * 校验token
     * 在这里可以使用官方的校验，我这里校验的是token中携带的密码于数据库一致的话就校验通过
     *
     * @param token token密文
     * @return 此token是否合法
     */
    public static Boolean isVerify(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        try {
            //得到DefaultJwtParser
            Claims claims = Jwts.parser()
                    //设置签名的秘钥
                    .setSigningKey(getKey())
                    //设置需要解析的jwt
                    .parseClaimsJws(token).getBody();
            claims.getSubject();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AuthorizationException();
        }
        return true;
    }

    public static Object getUserByToken(String token, Class userClass) {
        if (StringUtils.isBlank(token)) {
            throw new AuthorizationException();
        }
        Object user;
        try {
            user = userClass.newInstance();
            Claims claims = Jwts.parser()
                    //设置签名的秘钥
                    .setSigningKey(getKey())
                    //设置需要解析的jwt
                    .parseClaimsJws(token).getBody();
            for (Field field : ReflectUtil.getFieldList(userClass)) {
                field.setAccessible(true);
                if (claims.containsKey(field.getName())) {
                    ReflectUtil.setFieldValue(user, field.getName(), claims.get(field.getName()));
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new AuthorizationException();
        }
        return user;
    }
}
