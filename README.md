
<a href="https://996.icu"><img src="https://img.shields.io/badge/link-996.icu-red.svg" alt="996.icu" /></a>


 ### LiveDataBus消息总线例子

从LiveData谈起

LiveData是Android Architecture Components提出的框架。LiveData是一个可以被观察的数据持有类，它可以感知并遵循Activity、Fragment或Service等组件的生命周期。正是由于LiveData对组件生命周期可感知特点，因此可以做到仅在组件处于生命周期的激活状态时才更新UI数据。

LiveData需要一个观察者对象，一般是Observer类的具体实现。当观察者的生命周期处于STARTED或RESUMED状态时，LiveData会通知观察者数据变化；在观察者处于其他状态时，即使LiveData的数据变化了，也不会通知。

LiveData的优点

UI和实时数据保持一致，因为LiveData采用的是观察者模式，这样一来就可以在数据发生改变时获得通知，更新UI。

避免内存泄漏，观察者被绑定到组件的生命周期上，当被绑定的组件销毁（destroy）时，观察者会立刻自动清理自身的数据。

不会再产生由于Activity处于stop状态而引起的崩溃，例如：当Activity处于后台状态时，是不会收到LiveData的任何事件的。

不需要再解决生命周期带来的问题，LiveData可以感知被绑定的组件的生命周期，只有在活跃状态才会通知数据变化。

实时数据刷新，当组件处于活跃状态或者从不活跃状态到活跃状态时总是能收到最新的数据。

解决Configuration Change问题，在屏幕发生旋转或者被回收再次启动，立刻就能收到最新的数据。

谈一谈Android Architecture Components

Android Architecture Components的核心是Lifecycle、LiveData、ViewModel 以及 Room，通过它可以非常优雅的让数据与界面进行交互，并做一些持久化的操作，高度解耦，自动管理生命周期，而且不用担心内存泄漏的问题。

Room 
一个强大的SQLite对象映射库。

ViewModel
一类对象，它用于为UI组件提供数据，在设备配置发生变更时依旧可以存活。

LiveData 一个可感知生命周期、可被观察的数据容器，它可以存储数据，还会在数据发生改变时进行提醒。

Lifecycle
包含LifeCycleOwer和LifecycleObserver，分别是生命周期所有者和生命周期感知者。

Android Architecture Components的特点

数据驱动型编程
变化的永远是数据，界面无需更改。

感知生命周期，防止内存泄漏

高度解耦
数据，界面高度分离。

数据持久化
数据、ViewModel不与 UI的生命周期挂钩，不会因为界面的重建而销毁。

重点：为什么使用LiveData构建数据通信总线LiveDataBus

使用LiveData的理由

LiveData具有的这种可观察性和生命周期感知的能力，使其非常适合作为Android通信总线的基础构件。

使用者不用显示调用反注册方法。
由于LiveData具有生命周期感知能力，所以LiveDataBus只需要调用注册回调方法，而不需要显示的调用反注册方法。这样带来的好处不仅可以编写更少的代码，而且可以完全杜绝其他通信总线类框架（如EventBus、RxBus）忘记调用反注册所带来的内存泄漏的风险。

为什么要用LiveDataBus替代EventBus和RxBus

LiveDataBus的实现及其简单，相对EventBus复杂的实现，LiveDataBus只需要一个类就可以实现。

LiveDataBus可以减小APK包的大小，由于LiveDataBus只依赖Android官方Android Architecture Components组件的LiveData，没有其他依赖，本身实现只有一个类。作为比较，EventBus JAR包大小为57kb，RxBus依赖RxJava和RxAndroid，其中RxJava2包大小2.2MB，RxJava1包大小1.1MB，RxAndroid包大小9kb。使用LiveDataBus可以大大减小APK包的大小。

LiveDataBus依赖方支持更好，LiveDataBus只依赖Android官方Android Architecture Components组件的LiveData，相比RxBus依赖的RxJava和RxAndroid，依赖方支持更好。

LiveDataBus具有生命周期感知，LiveDataBus具有生命周期感知，在Android系统中使用调用者不需要调用反注册，相比EventBus和RxBus使用更为方便，并且没有内存泄漏风险。

LiveDataBus的设计和架构

LiveDataBus的组成

### hook技术应用


1. 利用系统内部提供的接口，通过实现该接口，然后注入进系统（特定场景下使用）

2.动态代理（使用所有场景）

二、Hook 技术实现的步骤

Hook 技术实现的步骤也分为两步

1.找到 hook 点（Java 层），该 hook 点必须满足以下的条件：需要 hook 的方法，所属的对象必须是静态的，因为我们是通过反射来获取对象的，我们获取的是系统的对象，所以不能够 new 一个新的对象，必须用系统创建的那个对象，所以只有静态的才能保证和系统的对象一致。

2.将 hook 方法放到系统之外执行（放入我们自己的逻辑）

