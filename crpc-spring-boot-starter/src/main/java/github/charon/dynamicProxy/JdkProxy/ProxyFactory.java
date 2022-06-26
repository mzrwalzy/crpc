package github.charon.dynamicProxy.JdkProxy;

import java.lang.reflect.Proxy;

public class ProxyFactory {
    public static Object getInstance(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new MyInvocationHandler(target));
    }
}
