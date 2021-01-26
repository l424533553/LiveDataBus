//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package androidx.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle.State;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ExternalLiveData<T> extends MutableLiveData<T> {
    public static final int START_VERSION = -1;

    public ExternalLiveData() {
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        if (owner.getLifecycle().getCurrentState() != State.DESTROYED) {
            try {
                ExternalLifecycleBoundObserver wrapper = new ExternalLifecycleBoundObserver(owner, observer);
                LiveData<T>.LifecycleBoundObserver existing = (LifecycleBoundObserver)this.callMethodPutIfAbsent(observer, wrapper);
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

    protected State observerActiveLevel() {
        return State.CREATED;
    }

    private Object getFieldObservers() throws Exception {
        Field fieldObservers = LiveData.class.getDeclaredField("mObservers");
        fieldObservers.setAccessible(true);
        return fieldObservers.get(this);
    }

    private Object callMethodPutIfAbsent(Object observer, Object wrapper) throws Exception {
        Object mObservers = this.getFieldObservers();
        Class<?> classOfSafeIterableMap = mObservers.getClass();
        Method putIfAbsent = classOfSafeIterableMap.getDeclaredMethod("putIfAbsent", Object.class, Object.class);
        putIfAbsent.setAccessible(true);
        return putIfAbsent.invoke(mObservers, observer, wrapper);
    }

    class ExternalLifecycleBoundObserver extends LiveData<T>.LifecycleBoundObserver {

        ExternalLifecycleBoundObserver(@NonNull LifecycleOwner owner,@NonNull Observer<? super T> observer) {
            super( owner, observer);
        }

        @Override
        boolean shouldBeActive() {
            return this.mOwner.getLifecycle().getCurrentState().isAtLeast(ExternalLiveData.this.observerActiveLevel());
        }
    }
}
