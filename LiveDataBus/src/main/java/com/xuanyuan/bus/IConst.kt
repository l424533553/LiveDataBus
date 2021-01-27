package com.xuanyuan.bus

/**
 * @author LUOFAXIN
 */
interface IConst {
    companion object {
        const val ACTION = "intent.action.ACTION_LIVE_DATA_BUS"
        const val KEY = "key"
        const val VALUE_TYPE = "value_type"
        const val VALUE = "value"
    }
}

enum class DataType {
    /**
     * 数据枚举类
     */
    STRING, INTEGER, BOOLEAN, LONG, FLOAT, DOUBLE, PARCELABLE, SERIALIZABLE, JSON, UNKNOWN
}