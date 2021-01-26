package com.xuanyuan.bus.external;

import androidx.annotation.NonNull;

/**
 * date： 2018/10/17 0017.
 * email:424533553@qq.com
 * describe:事件总线
 * @author 罗发新
 */
public class LiveEventBean<T> {
    private String eventType;
    private T t;
    private String msg;

    @NonNull
    public String getEventType() {
        return eventType;
    }

    public void setEventType(@NonNull String eventType) {
        this.eventType = eventType;
    }

    @NonNull
    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    @NonNull
    public String getMsg() {
        return msg;
    }

    public void setMsg(@NonNull String msg) {
        this.msg = msg;
    }
}
