package github.charon.dynamicProxy.CglibProxy;

public class CglibProxy {
    public static void main(String[] args) {
        AliSmsService instance = (AliSmsService) CglibProxyFactory.getInstance(AliSmsService.class);
        instance.send();
    }
}
