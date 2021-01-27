package com.xuanyuan.demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;

import com.xuanyuan.bus.LiveDataBus;
import com.xuanyuan.bus.R;

/**
 * @author LUOFAXIN
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnSendOne).setOnClickListener(v ->
                // 2.数据更改后，数据发布
                LiveDataBus.sendLiveString("UI线程中发布数据更新")
        );

        findViewById(R.id.btnMultiData).setOnClickListener(v -> {
                    new Thread(() -> {
                        int count = 0;
                        while (count < 100) {
                            LiveDataBus.sendLiveString("非UI线程中发布数据更新");
                            try {
                                Thread.sleep(400);
                                count++;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
        );

//      //1. 注册监听 observe(owner, observer)方式注册
//      LiveDataBus.observe(LiveDataBus.LIVE_EVENT_BUS_COMMON_LIVE_STRING, this, observer);
        //1.注册监听 observeForever(observer)方式注册
        LiveDataBus.observeForever("LIVE_EVENT_BUS_COMMON_LIVE_STRING", observer);
    }

    private final Observer<String> observer = s ->
            // 3. 数据处理
            Log.i("111111", s);
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //4.注销观察
        LiveDataBus.removeObserver("LIVE_EVENT_BUS_COMMON_LIVE_STRING", observer);
    }
}

