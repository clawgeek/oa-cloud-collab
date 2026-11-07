package com.yunshu.oa.security;

import com.yunshu.oa.jsoncodec.JsonCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/** 轻量 HS256 JWT：Header.Payload.Signature，JDK 原生 Mac 实现 */
@Component
public class JwtUtil {
    @Value("${oa.jwt.secret}")
    private String secret;
    @Value("${oa.jwt.expire-hours:12}")
    private long expireHours;

    public String issue(Long userId, String username, String realName, Long deptId) {
        long exp = System.currentTimeMillis() / 1000 + expireHours * 3600;
        String header = b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = b64(("{\"uid\":" + userId + ",\"username\":\"" + username
                + "\",\"realName\":\"" + realName + "\",\"deptId\":" + deptId
                + ",\"exp\":" + exp + "}").getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + "." + sign(header + "." + payload);
    }

    public Map<String, Object> verify(String token) {
        try {
            String[] p = token.split("\\.");
            if (p.length != 3 || !sign(p[0] + "." + p[1]).equals(p[2])) return null;
            Map<String, Object> claims = JsonCodec.parse(new String(Base64.getUrlDecoder().decode(p[1]), StandardCharsets.UTF_8));
            Object exp = claims.get("exp");
            if (exp != null && ((Number) exp).longValue() < System.currentTimeMillis() / 1000) return null;
            return claims;
        } catch (Exception e) { return null; }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return b64(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
    private static String b64(byte[] b) { return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
}
