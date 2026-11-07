package com.yunshu.oa.common;

import lombok.Data;
import java.io.Serializable;

/** 统一响应体：所有接口返回 {code,msg,data,timestamp} */
@Data
public class R<T> implements Serializable {
    private int code;
    private String msg;
    private T data;
    private long timestamp = System.currentTimeMillis();

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>(); r.code = 200; r.msg = "success"; r.data = data; return r;
    }
    public static <T> R<T> ok() { return ok(null); }
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>(); r.code = code; r.msg = msg; return r;
    }
    public static <T> R<T> fail(String msg) { return fail(400, msg); }
}
