package com.xuanyuan.bus.config

/**
 * @author LUOFAXIN
 */
interface IConst {
    companion object {
        const val ACTION = "intent.action.ACTION_LEB_IPC"
        const val KEY = "key"
        const val VALUE_TYPE = "value_type"
        const val VALUE = "value"
        const val CLASS_NAME = "class_name"
    }
}

enum class DataType {
    /**
     * 数据类型
     */
    STRING, INTEGER, BOOLEAN, LONG, FLOAT, DOUBLE, PARCELABLE, SERIALIZABLE, BUNDLE, JSON, UNKNOWN
}