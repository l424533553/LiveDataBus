package com.jeremyliao.liveeventbus.ipc.decode;

import android.content.Intent;

/**
 * Created by liaohailiang on 2019/3/25.
 */
public interface IDecoder {

    /**
     * 解码
     * @param intent    intent
     * @return  解码对象
     * @throws DecodeException  解码
     */
    Object decode(Intent intent) throws DecodeException;
}
