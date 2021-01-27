package com.xuanyuan.bus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Parcelable
import java.io.Serializable

/**
 * @author LUOFAXIN
 * LiveData数据的广播接收者
 */
class LiveDataReceiver : BroadcastReceiver() {
    fun getIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(IConst.ACTION)
        return intentFilter
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (IConst.ACTION == intent.action) {
            val key = intent.getStringExtra(IConst.KEY) ?: return
            val valueType = intent.getIntExtra(IConst.VALUE_TYPE, -1)
            if (valueType < 0) {
                //没有合适的数据
                return
            }
            when (DataType.values()[valueType]) {
                DataType.STRING -> {
                    val value = intent.getStringExtra(IConst.VALUE)
                    value?.let { LiveEventBus.get().with<String>(key).post(it) }
                }
                DataType.INTEGER -> {
                    val value = intent.getIntExtra(IConst.VALUE, -1)
                    value.let { LiveEventBus.get().with<Int>(key).post(it) }
                }
                DataType.BOOLEAN -> {
                    val value = intent.getBooleanExtra(IConst.VALUE, false)
                    value.let { LiveEventBus.get().with<Boolean>(key).post(it) }
                }
                DataType.LONG -> {
                    val value = intent.getLongExtra(IConst.VALUE, -1)
                    value.let { LiveEventBus.get().with<Long>(key).post(it) }
                }
                DataType.FLOAT -> {
                    val value = intent.getFloatExtra(IConst.VALUE, -1f)
                    value.let { LiveEventBus.get().with<Float>(key).post(it) }
                }
                DataType.DOUBLE -> {
                    val value = intent.getDoubleExtra(IConst.VALUE, -1.0)
                    value.let { LiveEventBus.get().with<Double>(key).post(it) }
                }
                DataType.PARCELABLE -> {
                    val value: Parcelable? = intent.getParcelableExtra(IConst.VALUE)
                    value?.let { LiveEventBus.get().with<Parcelable>(key).post(it) }
                }
                DataType.SERIALIZABLE -> {
                    val value: Serializable? = intent.getSerializableExtra(IConst.VALUE)
                    value?.let { LiveEventBus.get().with<Serializable>(key).post(it) }
                }
                DataType.UNKNOWN -> throw UnknownError("不支持的数据类型!")
                else -> throw UnknownError("不支持的数据类型!")
            }
        }
    }
}