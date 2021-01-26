package androidx.lifecycle;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.xuanyuan.bus.live.BroadCastHelper;
import com.xuanyuan.bus.live.LiveEventObservable;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LUOFAXIN
 */
public class LiveEventBus {
    final Map<String, LiveEvent<?>> bus;
    boolean lifecycleObserverAlwaysActive = true;
    LiveEventBus() {
        bus = new HashMap<>();
    }

    /**
     * @param active true：总是进行监听，与生命周期无关 ; false：在onResume状态可以接收消息
     */
    public void lifecycleObserverAlwaysActive(boolean active) {
        lifecycleObserverAlwaysActive = active;
    }

    /**
     * 单例模式，线程安全类型
     */
    private static class SingletonHolder {
        static final LiveEventBus DEFAULT_BUS = new LiveEventBus();
    }

    @NonNull
    public static LiveEventBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    @NonNull
    public synchronized <T> LiveEventObservable<T> with(@NonNull String key, @NonNull Class<T> type) {
        LiveEvent<T> liveEvent;
        if (!bus.containsKey(key)) {
            liveEvent = new LiveEvent<>(key);
            bus.put(key, liveEvent);
        } else {
            liveEvent = (LiveEvent<T>) bus.get(key);
        }
        assert liveEvent != null;
        return liveEvent;
    }

    BroadCastHelper broadCastHelper;

    /**
     * 设置框架是否支持 BroadCast方式,设置该方法后,将通过广播的方式支持信息的跨进程通信
     * 建议在Application.onCreate中调用
     */
    public void supportBroadcast(@NonNull Context context) {
        broadCastHelper = new BroadCastHelper(context);
    }

    private class LiveEvent<T> implements LiveEventObservable<T> {
        @NonNull
        private final String key;
        private final ExternalLiveData<T> liveData;
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

        LiveEvent(@NonNull String key) {
            this.key = key;
            this.liveData = new ExternalLiveData<>(lifecycleObserverAlwaysActive);
        }

        public boolean isMainThread() {
            return Looper.myLooper() == Looper.getMainLooper();
        }

        @Override
        public void post(@NotNull T value) {
            if (isMainThread()) {
                postInternal(value);
            } else {
                mainHandler.post(new PostValueTask(value));
            }
        }

        @Override
        public void broadcast(@NotNull final T value) {
            if (broadCastHelper != null) {
                if (isMainThread()) {
                    broadcastInternal(value);
                } else {
                    mainHandler.post(() -> broadcastInternal(value));
                }
            } else {
                post(value);
            }
        }

        /**
         * 发送广播信息，需要框架设置支持广播发送
         */
        @MainThread
        private void broadcastInternal(T value) {
            if (broadCastHelper != null) {
                broadCastHelper.sendBroad(key, value);
            }
        }

        @Override
        public void postDelay(@NotNull T value, long delay) {
            mainHandler.postDelayed(new PostValueTask(value), delay);
        }

        @Override
        public void observe(@NonNull final LifecycleOwner owner, @NonNull final Observer<T> observer) {
            if (isMainThread()) {
                liveData.observeInternal(owner, observer);
            } else {
                mainHandler.post(() -> liveData.observeInternal(owner, observer));
            }
        }

        @Override
        public void observeSticky(@NonNull final LifecycleOwner owner, @NonNull final Observer<T> observer) {
            if (isMainThread()) {
                liveData.observeStickyInternal(owner, observer);
            } else {
                mainHandler.post(() -> liveData.observeStickyInternal(owner, observer));
            }
        }

        @Override
        public void observeForever(@NonNull final Observer<T> observer) {
            if (isMainThread()) {
                liveData.observeForeverInternal(observer);
            } else {
                mainHandler.post(() -> liveData.observeForeverInternal(observer));
            }
        }

        @Override
        public void observeStickyForever(@NonNull final Observer<T> observer) {
            if (isMainThread()) {
                liveData.observeStickyForeverInternal(observer);
            } else {
                mainHandler.post(() -> liveData.observeStickyForeverInternal(observer));
            }
        }

        @Override
        public void removeObserver(@NonNull final Observer<T> observer) {
            if (isMainThread()) {
                removeObserverInternal(observer);
            } else {
                mainHandler.post(() -> removeObserverInternal(observer));
            }
        }

        @MainThread
        void postInternal(T value) {
            liveData.setValue(value);
        }

        @MainThread
        private void removeObserverInternal(@NonNull Observer<T> observer) {
            liveData.removeObserverInternal(observer);
            if (!liveData.hasObservers()) {
                LiveEventBus.get().bus.remove(key);
            }
        }

        private class PostValueTask implements Runnable {
            private final T newValue;

            public PostValueTask(@NonNull T newValue) {
                this.newValue = newValue;
            }

            @Override
            public void run() {
                postInternal(newValue);
            }
        }
    }

}
