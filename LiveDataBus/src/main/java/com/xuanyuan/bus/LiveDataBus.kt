package com.xuanyuan.bus.external;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import androidx.lifecycle.LiveEventBus;


/**
 * @author 罗发新
 * 时间：2019/6/11 0011    星期二
 * 邮件：424533553@qq.com
 */
public class LiveDataBus {

    @NonNull
    public static String LIVE_EVENT_BUS_COMMON_LIVE_BEAN = "LIVE_EVENT_BUS_COMMON_LIVE_BEAN";
    /**
     * 只接受接单的String
     */
    @NonNull
    public static String LIVE_EVENT_BUS_COMMON_LIVE_STRING = "LIVE_EVENT_BUS_COMMON_LIVE_STRING";


    public static <T> void sendLiveBean(@NonNull String eventType, @NonNull T data) {
        if (TextUtils.isEmpty(eventType)) {
            return;
        }
        LiveEventBean<T> liveEventBean = new LiveEventBean<>();
        liveEventBean.setEventType(eventType);
        liveEventBean.setT(data);
        LiveDataBus.post(LiveDataBus.LIVE_EVENT_BUS_COMMON_LIVE_BEAN, LiveEventBean.class, liveEventBean);
    }

    public static void sendLiveString(@NonNull String eventType) {
        LiveDataBus.post(LiveDataBus.LIVE_EVENT_BUS_COMMON_LIVE_STRING, String.class, eventType);
    }

    public static void sendLiveString(@NonNull String eventType, long delay) {
        LiveDataBus.postDelay(LiveDataBus.LIVE_EVENT_BUS_COMMON_LIVE_STRING, String.class, eventType, delay);
    }

    public static <T> void post(@NonNull String key, @NonNull Class<T> type, @NonNull T value) {
        LiveEventBus.get().with(key, type).post(value);
    }

    public static void liveDataBusInit(@NonNull Context context) {
        LiveEventBus.get().supportBroadcast(context);
    }

    /**
     * @param isAlwaysActive 是否一直处于活动状态，即使activity处于后台也能接受到消息
     */
    public static void liveDataBusInit(boolean isAlwaysActive) {
        LiveEventBus.get().lifecycleObserverAlwaysActive(isAlwaysActive);
    }

    /**
     *
     */
    public static <T> void postDelay(@NonNull String key, @NonNull Class<T> type, @NonNull T value, long delay) {
        LiveEventBus.get().with(key, type).postDelay(value, delay);
    }

    public static <T> void observe(@NonNull String key, @NonNull Class<T> type, @NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        LiveEventBus.get().with(key, type).observe(owner, observer);
    }

    public static <T> void observeForever(@NonNull String key, @NonNull Class<T> type, @NonNull Observer<T> observer) {
        LiveEventBus.get().with(key, type).observeForever(observer);
    }

    public static <T> void removeObserver(@NonNull String key, @NonNull Class<T> type, @NonNull Observer<T> observer) {
        LiveEventBus.get().with(key, type).removeObserver(observer);

    }

    public static void supportBroadcast(@NonNull Context context) {
        LiveEventBus.get().supportBroadcast(context);
    }

    /**
     * @param active true：总是进行监听，与生命周期无关 ; false：在onResume状态可以接收消息
     */
    public static void lifecycleObserverAlwaysActive(boolean active) {
        LiveEventBus.get().lifecycleObserverAlwaysActive(active);
    }

}
