package com.yunshu.oa.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** 口令散列：PBKDF2WithHmacSHA256 / 120000 轮 / 256bit，JDK 原生实现，无第三方依赖 */
public final class PasswordUtil {
    private static final byte[] SALT = "yunshuOA2026salt".getBytes(StandardCharsets.UTF_8);
    private static final int ITER = 120000;
    private static final int LEN = 256;
    private PasswordUtil() {}

    public static String hash(String raw) {
        try {
            PBEKeySpec spec = new PBEKeySpec(raw.toCharArray(), SALT, ITER, LEN);
            byte[] dk = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return "pbkdf2$" + hex(dk);
        } catch (Exception e) { throw new IllegalStateException("口令散列失败", e); }
    }

    public static boolean matches(String raw, String stored) {
        if (stored == null || raw == null) return false;
        return MessageDigest.isEqual(hash(raw).getBytes(StandardCharsets.UTF_8),
                                    stored.getBytes(StandardCharsets.UTF_8));
    }

    private static String hex(byte[] b) {
        StringBuilder s = new StringBuilder(b.length * 2);
        for (byte x : b) s.append(String.format("%02x", x));
        return s.toString();
    }
}
