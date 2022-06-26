package github.charon.dynamicProxy.JdkProxy;

public class SmsServiceImpl implements SmsService{
    @Override
    public void send() {
        System.out.println("java");
    }
}
