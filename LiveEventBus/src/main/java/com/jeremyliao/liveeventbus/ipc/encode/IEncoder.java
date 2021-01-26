package com.jeremyliao.liveeventbus.ipc.encode;

import android.content.Intent;

/**
 * Created by liaohailiang on 2019/3/25.
 */
public interface IEncoder {

    /**
     *编码
     *
     * @param intent intent
     * @param value  编码源值
     * @throws EncodeException  编码异常
     */
    void encode(Intent intent, Object value) throws EncodeException;
}
