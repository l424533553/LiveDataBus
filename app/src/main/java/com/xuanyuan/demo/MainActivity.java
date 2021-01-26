package com.xuanyuan.demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;

import com.xuanyuan.bus.external.LiveDataBus;
import com.xuanyuan.bus.R;

/**
 * @author LUOFAXIN
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LifecycleOwner ffsd;
        findViewById(R.id.btnSendOne).setOnClickListener(v ->
                LiveDataBus.sendLiveString("单次点击")
        );

        findViewById(R.id.btnMultiData).setOnClickListener(v -> {
                    new Thread(() -> {
                        int count = 0;
                        while (count < 100) {
                            LiveDataBus.sendLiveString("复数点击");
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

        //纯String类型数据通信
        LiveDataBus.observe(LiveDataBus.LIVE_EVENT_BUS_COMMON_LIVE_STRING, String.class, this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.i("111111", s);
            }
        });
    }
}