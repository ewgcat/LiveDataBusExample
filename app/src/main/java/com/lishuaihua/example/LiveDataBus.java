package com.lishuaihua.example;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LiveDataBus {
    /**
     * 创建一个map来装载订阅者
     */
    private Map<String, BusMutableLiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final LiveDataBus DEFAULT_BUS = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }


    /**
     * 外部调用 （提供给订阅者的方法）
     */
    public synchronized <T> BusMutableLiveData<T> with(String key, Class<T> type) {
        if (!bus.containsKey(key)) {
            bus.put(key, new BusMutableLiveData<Object>());
        }
        return (BusMutableLiveData<T>) bus.get(key);
    }


    /**
     * 重写MutableLiveData 在observe方法中进行Hook
     */
    public static class BusMutableLiveData<T> extends MutableLiveData<T>{

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);
            try {
                hook(observer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * hook方法
         */
        private void hook(Observer<? super T> observer) throws Exception {
            //获取到LiveData类的class对象
            Class<LiveData> liveDataClass = LiveData.class;
            //通过反射区获取LiveData里面的observer
            Field mObservers = liveDataClass.getDeclaredField("mObservers");
            //设置成员变量可以被访问
            mObservers.setAccessible(true);
            //获取这个成员变量的值  它的值是一个Map
            Object objectObservers = mObservers.get(this);
            //获取objectObservers的class对象
            Class<?> observerClass = objectObservers.getClass();
            //获取到observerClass里面的get方法
            Method observerGet = observerClass.getDeclaredMethod("get", Object.class);
            //设置这个observerGet这个对象
            observerGet.setAccessible(true);
            //执行该方法 传入一个方法执行在哪个对象中的这个对象 传入执行这个方法所需要的参数
            Object invokeEntry = observerGet.invoke(objectObservers, observer);
            //定义一个空的对象
            Object objectWrapper = null;
            if (invokeEntry instanceof Map.Entry) {
                objectWrapper = ((Map.Entry) invokeEntry).getValue();
            }
            if (objectWrapper == null) {
                throw new NullPointerException("Wrapper can not be bull!");
            }
            Class<?> superclass = objectWrapper.getClass().getSuperclass();
            //通过superclass获取mlastVersion
            Field mLastVersion = superclass.getDeclaredField("mLastVersion");
            mLastVersion.setAccessible(true);
            Field mVersion = liveDataClass.getDeclaredField("mVersion");
            mVersion.setAccessible(true);
            Object object = mVersion.get(this);
            //把mVersion的值赋值给mLastVersion成员变量达人对象
            mLastVersion.set(objectWrapper, object);
        }
    }
}
