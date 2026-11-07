package com.yunshu.oa.common;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** 从前端 JSON Map 中安全取值，避免到处写 instanceof 判断 */
public final class Params {
    private Params() {}
    public static String s(Map<String, Object> m, String k) {
        Object v = m == null ? null : m.get(k); return v == null ? "" : String.valueOf(v);
    }
    public static String s(Map<String, Object> m, String k, String dft) {
        String v = s(m, k); return v.isEmpty() ? dft : v;
    }
    public static Long l(Map<String, Object> m, String k) { return l(m, k, null); }
    public static Long l(Map<String, Object> m, String k, Long dft) {
        Object v = m == null ? null : m.get(k);
        if (v == null || String.valueOf(v).isEmpty()) return dft;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(String.valueOf(v));
    }
    public static int i(Map<String, Object> m, String k, int dft) {
        Object v = m == null ? null : m.get(k);
        if (v == null) return dft;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return dft; }
    }
    public static BigDecimal d(Map<String, Object> m, String k) {
        Object v = m == null ? null : m.get(k);
        if (v == null) return BigDecimal.ZERO;
        return new BigDecimal(String.valueOf(v));
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> map(Object o) {
        return o instanceof Map ? (Map<String, Object>) o : new LinkedHashMap<>();
    }
}
