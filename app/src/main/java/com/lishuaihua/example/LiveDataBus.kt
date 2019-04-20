package com.lishuaihua.example

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.HashMap
import java.util.Map

class LiveDataBus private constructor() {
    /**
     * 创建一个map来装载订阅者
     */
    private val bus: MutableMap<String, BusMutableLiveData<Any>>

    init {
        bus = HashMap()
    }

    private object SingletonHolder {
        public val DEFAULT_BUS = LiveDataBus()
    }


    /**
     * 外部调用 （提供给订阅者的方法）
     */
    @Synchronized
    fun <T> with(key: String, type: Class<T>): BusMutableLiveData<T>? {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData()
        }
        return bus[key] as BusMutableLiveData<T>?
    }


    /**
     * 重写MutableLiveData 在observe方法中进行Hook
     */
    class BusMutableLiveData<T> : MutableLiveData<T>() {

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            try {
                hook(observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * hook方法
         */
        @Throws(Exception::class)
        private fun hook(observer: Observer<in T>) {
            //获取到LiveData类的class对象
            val liveDataClass = LiveData::class.java
            //通过反射区获取LiveData里面的observer
            val mObservers = liveDataClass!!.getDeclaredField("mObservers")
            //设置成员变量可以被访问
            mObservers.setAccessible(true)
            //获取这个成员变量的值  它的值是一个Map
            val objectObservers = mObservers.get(this)
            //获取objectObservers的class对象
            val observerClass = objectObservers.javaClass
            //获取到observerClass里面的get方法
            val observerGet = observerClass.getDeclaredMethod("get", Any::class.java!!)
            //设置这个observerGet这个对象
            observerGet.setAccessible(true)
            //执行该方法 传入一个方法执行在哪个对象中的这个对象 传入执行这个方法所需要的参数
            val invokeEntry = observerGet.invoke(objectObservers, observer)
            //定义一个空的对象
            var objectWrapper: Any? = null
            if (invokeEntry is Map.Entry<*, *>) {
                objectWrapper = (invokeEntry as Map.Entry<*, *>).value
            }
            if (objectWrapper == null) {
                throw NullPointerException("Wrapper can not be bull!")
            }
            val superclass = objectWrapper.javaClass.getSuperclass()
            //通过superclass获取mlastVersion
            val mLastVersion = superclass!!.getDeclaredField("mLastVersion")
            mLastVersion.setAccessible(true)
            val mVersion = liveDataClass!!.getDeclaredField("mVersion")
            mVersion.setAccessible(true)
            val `object` = mVersion.get(this)
            //把mVersion的值赋值给mLastVersion成员变量达人对象
            mLastVersion.set(objectWrapper, `object`)
        }
    }

    companion object {

        fun get(): LiveDataBus {
            return SingletonHolder.DEFAULT_BUS
        }
    }
}
