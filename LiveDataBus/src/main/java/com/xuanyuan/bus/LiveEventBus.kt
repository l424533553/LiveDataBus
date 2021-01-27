package com.xuanyuan.bus

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.io.Serializable
import java.util.*

/**
 * @author LUOFAXIN
 */
class LiveEventBus internal constructor() {
    /**
     * LiveEvent 集合Map
     */
    val bus: MutableMap<String, LiveEvent<*>>
    private var appContext: Context? = null

    /**
     * 单例模式，线程安全类型
     */
    private object SingletonHolder {
        val DEFAULT_BUS = LiveEventBus()
    }

    companion object {
        fun get(): LiveEventBus {
            return SingletonHolder.DEFAULT_BUS
        }
    }

    init {
        bus = HashMap()
    }

//    @Synchronized
//    fun <T> with(key: String, type: Class<T>): LiveEvent<T> {
//        var liveEvent = bus[key]
//        return if (liveEvent == null) {
//            liveEvent = LiveEvent<T>(key)
//            bus[key] = liveEvent
//            liveEvent
//
//        } else {
//            liveEvent as LiveEvent<T>
//        }
//    }

    @Synchronized
    fun <T> with(key: String): LiveEvent<T> {
        var liveEvent = bus[key]
        return if (liveEvent == null) {
            liveEvent = LiveEvent<T>(key)
            bus[key] = liveEvent
            liveEvent

        } else {
            liveEvent as LiveEvent<T>
        }
    }

//    private var broadCastHelper: BroadCastHelper? = null

    /**
     * 设置框架是否支持 BroadCast方式,设置该方法后,将通过广播的方式支持信息的跨进程通信
     * 建议在Application.onCreate中调用
     */
    fun supportBroadcast(@NonNull context: Context) {
        appContext = context.applicationContext
    }

    /**
     * MutableLiveData 包装类，事件操作类
     */
    inner class LiveEvent<T> internal constructor(private val key: String) {
        private val liveData: MutableLiveData<T> = MutableLiveData()
        private val mainHandler = Handler(Looper.getMainLooper())
        private val isMainThread = Looper.myLooper() == Looper.getMainLooper()

        /**
         * 发送一个消息，支持前台线程、后台线程发送
         */
        fun post(value: T) {
            if (isMainThread) {
                liveData.setValue(value)
            } else {
                mainHandler.post { liveData.setValue(value) }
            }
        }

        /**
         * 延迟发送一个消息，支持前台线程、后台线程发送
         *
         * @param delay 延迟毫秒数
         */
        fun postDelay(value: T, delay: Long) {
            mainHandler.postDelayed({ liveData.setValue(value) }, delay)
        }

        /**
         * 发送一个消息，支持前台线程、后台线程发送
         * 需要跨进程、跨APP发送消息的时候调用该方法
         */
        fun broadcast(value: T) {
            if (isMainThread) {
                sendBroad(key, value)
            } else {
                mainHandler.post { sendBroad(key, value) }
            }
        }

        @SuppressLint("SyntheticAccessor")
        private fun <T> sendBroad(key: String, value: T) {
            appContext?.let {
                val intent = Intent(IConst.ACTION)
                intent.putExtra(IConst.KEY, key)
                encode(intent, value)
                it.sendBroadcast(intent)
            }
        }


        /**
         * 注册一个Observer，生命周期感知，自动取消订阅
         */
        fun observe(owner: LifecycleOwner, observer: Observer<T>) {
            if (isMainThread) {
                liveData.observe(owner, observer)
            } else {
                mainHandler.post { liveData.observe(owner, observer) }
            }
        }

        /**
         * 注册一个Observer
         */
        fun observeForever(observer: Observer<T>) {
            if (isMainThread) {
                liveData.observeForever(observer)

            } else {
                mainHandler.post { liveData.observeForever(observer) }
            }
        }

        /**
         * 通过observeForever的，需要手动调用该方法取消订阅
         */
        fun removeObserver(observer: Observer<T>) {
            if (isMainThread) {
                removeObserverInternal(observer)
            } else {
                mainHandler.post { removeObserverInternal(observer) }
            }
        }

        @MainThread
        private fun removeObserverInternal(observer: Observer<T>) {
            liveData.removeObserver(observer)
            if (!liveData.hasObservers()) {
                get().bus.remove(key)
            }
        }

        private fun <T> encode(intent: Intent, value: T) {
            when (value) {
                is String -> {
                    intent.putExtra(IConst.VALUE_TYPE, DataType.STRING.ordinal)
                    intent.putExtra(IConst.VALUE, value)
                }
                is Int -> {
                    intent.putExtra(IConst.VALUE_TYPE, DataType.INTEGER.ordinal)
                    intent.putExtra(IConst.VALUE, value)
                }
                is Boolean -> {
                    intent.putExtra(IConst.VALUE_TYPE, DataType.BOOLEAN.ordinal)
                    intent.putExtra(IConst.VALUE, value)
                }
                is Long -> {
                    intent.putExtra(IConst.VALUE_TYPE, DataType.LONG.ordinal)
                    intent.putExtra(IConst.VALUE, value)
                }
                is Float -> {
                    intent.putExtra(IConst.VALUE_TYPE, DataType.FLOAT.ordinal)
                    intent.putExtra(IConst.VALUE, value)
                }
                is Double -> {
                    intent.putExtra(IConst.VALUE_TYPE, DataType.DOUBLE.ordinal)
                    intent.putExtra(IConst.VALUE, value)
                }
                is Parcelable -> {
                    intent.putExtra(IConst.VALUE_TYPE, DataType.PARCELABLE.ordinal)
                    intent.putExtra(IConst.VALUE, value)
                }
                is Serializable -> {
                    intent.putExtra(IConst.VALUE_TYPE, DataType.SERIALIZABLE.ordinal)
                    intent.putExtra(IConst.VALUE, value)
                }
            }
        }
    }
}

