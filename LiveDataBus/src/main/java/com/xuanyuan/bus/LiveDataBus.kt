package com.xuanyuan.bus

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.xuanyuan.bus.LiveEventBus.Companion.get


/**
 * @author 罗发新
 * 时间：2019/6/11 0011    星期二
 * 邮件：424533553@qq.com
 */
object LiveDataBus {
    var LIVE_EVENT_BUS_COMMON_LIVE_BEAN = "LIVE_EVENT_BUS_COMMON_LIVE_BEAN"

    /**
     * 只接受接单的String
     */
    @JvmField
    var LIVE_EVENT_BUS_COMMON_LIVE_STRING = "LIVE_EVENT_BUS_COMMON_LIVE_STRING"

    @JvmStatic
    fun <T> sendLiveBean(eventType: String, data: T) {
        if (TextUtils.isEmpty(eventType)) {
            return
        }
        val liveEventBean = LiveEventBean<T>()
        liveEventBean.eventType = eventType
        liveEventBean.t = data
        post(LIVE_EVENT_BUS_COMMON_LIVE_BEAN, liveEventBean)
    }

    @JvmStatic
    fun sendLiveString(eventType: String) {
        post(LIVE_EVENT_BUS_COMMON_LIVE_STRING, eventType)
    }

    @JvmStatic
    fun sendLiveString(eventType: String, delay: Long) {
        postDelay(LIVE_EVENT_BUS_COMMON_LIVE_STRING, eventType, delay)
    }

    @JvmStatic
    fun <T> post(key: String, value: T) {
        get().with<T>(key).post(value)
    }


    @JvmStatic
    fun <T> postDelay(key: String, value: T, delay: Long) {
        get().with<T>(key).postDelay(value, delay)
    }

    @JvmStatic
    fun <T> sendBroad(key: String, value: T) {
        get().with<T>(key).broadcast(value)
    }

    @JvmStatic
    fun <T> observe(key: String, owner: LifecycleOwner, observer: Observer<T>) {
        get().with<T>(key).observe(owner, observer)
    }

    @JvmStatic
    fun <T> observeForever(key: String, observer: Observer<T>) {
        get().with<T>(key).observeForever(observer)
    }

    @JvmStatic
    fun <T> removeObserver(key: String, observer: Observer<T>) {
        get().with<T>(key).removeObserver(observer)
    }

    @JvmStatic
    fun supportBroadcast(context: Context) {
        get().supportBroadcast(context)
    }
}

class LiveEventBean<T> {
    var eventType: String? = null
    var t: T? = null
    var msg: String? = null
}