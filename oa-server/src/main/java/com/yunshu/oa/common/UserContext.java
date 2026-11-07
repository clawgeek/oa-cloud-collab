package com.yunshu.oa.common;

/** 请求级上下文 */
public final class UserContext {
    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();
    private UserContext() {}
    public static void set(LoginUser u) { HOLDER.set(u); }
    public static LoginUser get() { return HOLDER.get(); }
    public static Long userId() { LoginUser u = HOLDER.get(); return u == null ? null : u.getUserId(); }
    public static String name() { LoginUser u = HOLDER.get(); return u == null ? "system" : u.getRealName(); }
    public static void clear() { HOLDER.remove(); }
}
