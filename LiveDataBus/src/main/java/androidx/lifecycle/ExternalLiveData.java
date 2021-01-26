//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package androidx.lifecycle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle.State;

import com.xuanyuan.bus.live.LiveEventObserverWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LUOFAXIN
 */
public class ExternalLiveData<T> extends MutableLiveData<T> {

    /**
     *  active true：总是进行监听，与生命周期无关 ; false：在onResume状态可以接收消息
     */
    boolean lifecycleObserverAlwaysActive;
    public static final int START_VERSION = -1;

    public ExternalLiveData(boolean active) {
        lifecycleObserverAlwaysActive = active;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        if (owner.getLifecycle().getCurrentState() != State.DESTROYED) {
            try {
                ExternalLifecycleBoundObserver wrapper = new ExternalLifecycleBoundObserver(owner, observer);
                LifecycleBoundObserver existing = (LifecycleBoundObserver) this.callMethodPutIfAbsent(observer, wrapper);
                if (existing != null && !existing.isAttachedTo(owner)) {
                    throw new IllegalArgumentException("Cannot add the same observer with different lifecycles");
                }

                if (existing != null) {
                    return;
                }
                owner.getLifecycle().addObserver(wrapper);
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }
    }

    @Override
    public int getVersion() {
        return super.getVersion();
    }

    @NonNull
    protected State observerActiveLevel() {
        return lifecycleObserverAlwaysActive ? State.CREATED : State.STARTED;
    }

    private Object getFieldObservers() throws Exception {
        Field fieldObservers = LiveData.class.getDeclaredField("mObservers");
        fieldObservers.setAccessible(true);
        return fieldObservers.get(this);
    }

    /**通过反射的方式拿到LiveData中的mObservers
     *
     */
    private Object callMethodPutIfAbsent(Object observer, Object wrapper) throws Exception {
        Object mObservers = this.getFieldObservers();
        Class<?> classOfSafeIterableMap = mObservers.getClass();
        Method putIfAbsent = classOfSafeIterableMap.getDeclaredMethod("putIfAbsent", Object.class, Object.class);
        putIfAbsent.setAccessible(true);
        return putIfAbsent.invoke(mObservers, observer, wrapper);
    }

    class ExternalLifecycleBoundObserver extends LifecycleBoundObserver {
        ExternalLifecycleBoundObserver(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super(owner, observer);
        }

        @Override
        boolean shouldBeActive() {
            return this.mOwner.getLifecycle().getCurrentState().isAtLeast(observerActiveLevel());
        }
    }


    /**
     * 确保程序的运行是在主线程中执行的
     */
    @MainThread
    public void observeInternal(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        LiveEventObserverWrapper<T> liveEventObserverWrapper = new LiveEventObserverWrapper<>(observer);
        liveEventObserverWrapper.preventNextEvent = getVersion() > ExternalLiveData.START_VERSION;
        observe(owner, liveEventObserverWrapper);
    }

    /**
     * 黏性注册观察
     */
    @MainThread
    public void observeStickyInternal(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        LiveEventObserverWrapper<T> liveEventObserverWrapper = new LiveEventObserverWrapper<>(observer);
        observe(owner, liveEventObserverWrapper);
    }

    private final Map<Observer<T>, LiveEventObserverWrapper<T>> observerMap = new HashMap<>();
    /**
     * 非黏性永久注册
     */
    @MainThread
    public void observeForeverInternal(@NonNull Observer<T> observer) {
        LiveEventObserverWrapper<T> liveEventObserverWrapper = new LiveEventObserverWrapper<>(observer);
        liveEventObserverWrapper.preventNextEvent = getVersion() > ExternalLiveData.START_VERSION;
        observerMap.put(observer, liveEventObserverWrapper);
        observeForever(liveEventObserverWrapper);
    }


    @MainThread
    public void observeStickyForeverInternal(@NonNull Observer<T> observer) {
        LiveEventObserverWrapper<T> liveEventObserverWrapper = new LiveEventObserverWrapper<>(observer);
        observerMap.put(observer, liveEventObserverWrapper);
        observeForever(liveEventObserverWrapper);
    }


    @MainThread
    public void removeObserverInternal(@NonNull Observer<T> observer) {
        Observer<T> realObserver;
        if (observerMap.containsKey(observer)) {
            realObserver = observerMap.remove(observer);
        } else {
            realObserver = observer;
        }
        if (realObserver != null) {
           removeObserver(realObserver);
        }
    }


}
