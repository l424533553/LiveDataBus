package com.xuanyuan.demo;

import android.app.Application;

import com.xuanyuan.bus.external.LiveDataBus;

/**
 * @author 罗发新
 * 创建时间：2021/1/25  9:24
 * 邮件：424533553@qq.com
 * CSDN：https://blog.csdn.net/luo_boke
 * <p>
 * 更新时间：2021/1/25  9:24
 * <p>
 * 文件说明：
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        LiveDataBus.supportBroadcast(this);
        LiveDataBus.lifecycleObserverAlwaysActive(false);

    }


}
