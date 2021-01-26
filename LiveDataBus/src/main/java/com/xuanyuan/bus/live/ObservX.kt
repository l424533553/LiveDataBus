package com.xuanyuan.bus.live

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * @param <T> 事件观察者
 * @author LUOFAXIN
 */
class LiveEventObserverWrapper<T>(private val observer: Observer<T>) : Observer<T> {
    /**
     * 确保每个事件只会响应一次
     */
    @JvmField
    var preventNextEvent = false
    override fun onChanged(t: T?) {
        if (preventNextEvent) {
            preventNextEvent = false
            return
        }
        try {
            observer.onChanged(t)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
}


/**
 * @author LUOFAXIN
 */
interface LiveEventObservable<T> {
    /**
     * 发送一个消息，支持前台线程、后台线程发送
     */
    fun post(value: T)

    /**
     * 发送一个消息，支持前台线程、后台线程发送
     * 需要跨进程、跨APP发送消息的时候调用该方法
     */
    fun broadcast(value: T)

    /**
     * 延迟发送一个消息，支持前台线程、后台线程发送
     *
     * @param delay 延迟毫秒数
     */
    fun postDelay(value: T, delay: Long)

    /**
     * 注册一个Observer，生命周期感知，自动取消订阅
     */
    fun observe(owner: LifecycleOwner, observer: Observer<T>)

    /**
     * 注册一个Observer，生命周期感知，自动取消订阅
     * 如果之前有消息发送，可以在注册时收到消息（消息同步）
     */
    fun observeSticky(owner: LifecycleOwner, observer: Observer<T>)

    /**
     * 注册一个Observer
     */
    fun observeForever(observer: Observer<T>)

    /**
     * 注册一个Observer
     * 如果之前有消息发送，可以在注册时收到消息（消息同步）
     */
    fun observeStickyForever(observer: Observer<T>)

    /**
     * 通过observeForever或observeStickyForever注册的，需要调用该方法取消订阅
     */
    fun removeObserver(observer: Observer<T>)
}