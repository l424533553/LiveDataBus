package com.xuanyuan.bus.live

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.google.gson.Gson
import com.xuanyuan.bus.config.DataType
import com.xuanyuan.bus.config.IConst
import java.io.Serializable

/**
 * @author LUOFAXIN
 * 值编码类
 */
class ValueCoder {
    private var gson: Gson? = null
    @Throws(EncodeException::class)
    fun encode(intent: Intent, value: Any) {
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
            is Bundle -> {
                intent.putExtra(IConst.VALUE_TYPE, DataType.BUNDLE.ordinal)
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
            else -> {
                try {
                    if (gson == null) {
                        gson = Gson()
                    }
                    val json = gson!!.toJson(value)
                    intent.putExtra(IConst.VALUE_TYPE, DataType.JSON.ordinal)
                    intent.putExtra(IConst.VALUE, json)
                    intent.putExtra(IConst.CLASS_NAME, value.javaClass.canonicalName)
                } catch (e: Exception) {
                    throw EncodeException(e)
                }
            }
        }
    }
}

/**
 * @author LUOFAXIN
 * 编码异常类
 */
class EncodeException : Exception {
    constructor() {}
    constructor(message: String) : super(message) {}
    constructor(message: String, cause: Throwable) : super(message, cause) {}
    constructor(cause: Throwable) : super(cause) {}
}


/**
 * @author LUOFAXIN
 */
class ValueDecoder {
    private var gson: Gson? = null
    @Throws(DecodeException::class)
    fun decode(intent: Intent): Any? {
        val valueTypeIndex = intent.getIntExtra(IConst.VALUE_TYPE, -1)
        if (valueTypeIndex < 0) {
            throw DecodeException("Index Error")
        }
        return when (DataType.values()[valueTypeIndex]) {
            DataType.STRING -> intent.getStringExtra(IConst.VALUE)
            DataType.INTEGER -> intent.getIntExtra(IConst.VALUE, -1)
            DataType.BOOLEAN -> intent.getBooleanExtra(IConst.VALUE, false)
            DataType.LONG -> intent.getLongExtra(IConst.VALUE, -1)
            DataType.FLOAT -> intent.getFloatExtra(IConst.VALUE, -1f)
            DataType.DOUBLE -> intent.getDoubleExtra(IConst.VALUE, -1.0)
            DataType.PARCELABLE -> intent.getParcelableExtra(IConst.VALUE)
            DataType.SERIALIZABLE -> intent.getSerializableExtra(IConst.VALUE)
            DataType.BUNDLE -> intent.getBundleExtra(IConst.VALUE)
            DataType.JSON -> {
                return try {
                    if (gson == null) {
                        gson = Gson()
                    }
                    val json = intent.getStringExtra(IConst.VALUE)
                    val className = intent.getStringExtra(IConst.CLASS_NAME)
                    if (className != null) {
                        gson?.fromJson(json, Class.forName(className))
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    throw DecodeException(e)
                }
            }
            DataType.UNKNOWN -> throw DecodeException()
        }
    }
}

/**
 * @author LUOFAXIN
 * 解码异常类
 */
class DecodeException : Exception {
    constructor() {}
    constructor(message: String) : super(message) {}
    constructor(message: String, cause: Throwable) : super(message, cause) {}
    constructor(cause: Throwable) : super(cause) {}
}