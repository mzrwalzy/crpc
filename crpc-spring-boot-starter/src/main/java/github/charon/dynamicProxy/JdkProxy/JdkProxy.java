package github.charon.dynamicProxy.JdkProxy;

public class JdkProxy {
    public static void main(String[] args) {
        SmsService instance = (SmsService) ProxyFactory.getInstance(new SmsServiceImpl());
        instance.send();
    }
}
