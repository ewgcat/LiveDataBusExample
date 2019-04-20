
<a href="https://996.icu"><img src="https://img.shields.io/badge/link-996.icu-red.svg" alt="996.icu" /></a>
# LiveDataBus_hook
hook技术应用

1. 利用系统内部提供的接口，通过实现该接口，然后注入进系统（特定场景下使用）

2.动态代理（使用所有场景）

二、Hook 技术实现的步骤

Hook 技术实现的步骤也分为两步

1.找到 hook 点（Java 层），该 hook 点必须满足以下的条件：需要 hook 的方法，所属的对象必须是静态的，因为我们是通过反射来获取对象的，我们获取的是系统的对象，所以不能够 new 一个新的对象，必须用系统创建的那个对象，所以只有静态的才能保证和系统的对象一致。

2.将 hook 方法放到系统之外执行（放入我们自己的逻辑）

