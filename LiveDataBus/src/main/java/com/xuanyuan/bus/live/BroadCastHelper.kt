package com.xuanyuan.bus.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveEventBus
import com.xuanyuan.bus.config.IConst

/**
 * 广播发送支持类
 */
class BroadCastHelper(context: Context) {

    private var appContext: Context? = null

    init {
        appContext = context.applicationContext
        appContext?.let {
            dataReceiver = LiveDataReceiver()
            coder = ValueCoder()
            val intentFilter = IntentFilter()
            intentFilter.addAction(IConst.ACTION)
            it.registerReceiver(dataReceiver, intentFilter)
        }
    }

    private lateinit var coder: ValueCoder
    private var dataReceiver: LiveDataReceiver? = null

    fun sendBroad(key: String, value: Any) {
        val intent = Intent(IConst.ACTION)
        intent.putExtra(IConst.KEY, key)
        try {
            coder.encode(intent, value)
            appContext?.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * @author LUOFAXIN
 * LiveData数据的广播接收者
 */
class LiveDataReceiver : BroadcastReceiver() {
    private var decoder: ValueDecoder? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (IConst.ACTION == intent.action) {
            try {
                val key = intent.getStringExtra(IConst.KEY)
                if (key != null) {
                    if (decoder == null) {
                        decoder = ValueDecoder()
                    }
                    val value = decoder?.decode(intent)
                    value?.let { LiveEventBus.get().with(key, Any::class.java).post(it) }
                }
            } catch (e: DecodeException) {
                e.printStackTrace()
            }
        }
    }
}