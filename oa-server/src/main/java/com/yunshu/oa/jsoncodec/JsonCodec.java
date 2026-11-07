package com.yunshu.oa.jsoncodec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 极简 JSON 编解码（只覆盖本系统用到的标量/对象/数组），避免引入额外依赖 */
public final class JsonCodec {
    private JsonCodec() {}

    public static String stringify(Object o) {
        StringBuilder sb = new StringBuilder(); write(o, sb); return sb.toString();
    }
    private static void write(Object o, StringBuilder sb) {
        if (o == null) { sb.append("null"); return; }
        if (o instanceof String s) { quote(s, sb); return; }
        if (o instanceof Number || o instanceof Boolean) { sb.append(o); return; }
        if (o instanceof Map<?, ?> m) {
            sb.append('{'); boolean first = true;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (!first) sb.append(','); first = false;
                quote(String.valueOf(e.getKey()), sb); sb.append(':'); write(e.getValue(), sb);
            }
            sb.append('}'); return;
        }
        if (o instanceof Iterable<?> it) {
            sb.append('['); boolean first = true;
            for (Object x : it) { if (!first) sb.append(','); first = false; write(x, sb); }
            sb.append(']'); return;
        }
        quote(o.toString(), sb);
    }
    private static void quote(String s, StringBuilder sb) {
        sb.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\""); case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n"); case '\r' -> sb.append("\\r"); case '\t' -> sb.append("\\t");
                default -> { if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c); }
            }
        }
        sb.append('"');
    }

    public static Map<String, Object> parse(String json) { return new Parser(json).parseObject(); }
    public static List<Object> parseArray(String json) { return new Parser(json).parseArray(); }

    private static final class Parser {
        private final String s; private int i;
        Parser(String s) { this.s = s; }
        Map<String, Object> parseObject() {
            Map<String, Object> m = new LinkedHashMap<>(); expect('{'); ws();
            if (peek() == '}') { i++; return m; }
            while (true) {
                ws(); String k = parseString(); ws(); expect(':'); ws();
                m.put(k, value()); ws();
                char c = s.charAt(i++); if (c == '}') return m; if (c != ',') throw err();
            }
        }
        List<Object> parseArray() {
            List<Object> a = new ArrayList<>(); expect('['); ws();
            if (peek() == ']') { i++; return a; }
            while (true) {
                ws(); a.add(value()); ws();
                char c = s.charAt(i++); if (c == ']') return a; if (c != ',') throw err();
            }
        }
        private Object value() {
            ws(); char c = peek();
            if (c == '"') return parseString();
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            int st = i;
            while (i < s.length() && ",}] \t\r\n".indexOf(s.charAt(i)) < 0) i++;
            String t = s.substring(st, i);
            if ("true".equals(t)) return true;
            if ("false".equals(t)) return false;
            if ("null".equals(t)) return null;
            if (t.contains(".")) return Double.parseDouble(t);
            return Long.parseLong(t);
        }
        private String parseString() {
            expect('"'); StringBuilder b = new StringBuilder();
            while (i < s.length()) {
                char c = s.charAt(i++);
                if (c == '"') return b.toString();
                if (c != '\\') { b.append(c); continue; }
                char e = s.charAt(i++);
                switch (e) {
                    case 'n' -> b.append('\n'); case 'r' -> b.append('\r'); case 't' -> b.append('\t');
                    case 'u' -> { b.append((char) Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; }
                    default -> b.append(e);
                }
            }
            throw err();
        }
        private void ws() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
        private char peek() { if (i >= s.length()) throw err(); return s.charAt(i); }
        private void expect(char c) { if (i >= s.length() || s.charAt(i++) != c) throw err(); }
        private IllegalArgumentException err() { return new IllegalArgumentException("非法 JSON @ " + i + ": " + s); }
    }
}
