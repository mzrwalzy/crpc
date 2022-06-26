package github.charon.dynamicProxy.CglibProxy;

import net.sf.cglib.proxy.Enhancer;

public class CglibProxyFactory {
    public static Object getInstance(Class<?> klass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(klass.getClassLoader());
        enhancer.setSuperclass(klass);
        enhancer.setCallback(new MyMethodInterceptor());
        return enhancer.create();
    }
}
