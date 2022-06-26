package github.charon.spring;

import github.charon.annotation.RpcServer;
import github.charon.remote.transport.netty.server.NettyRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ServerRegisteredEventListener {

    @Autowired
    private NettyRpcServer nettyRpcServer;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event)  {
        ApplicationContext applicationContext = event.getApplicationContext();
        if(applicationContext.getParent() == null
                && applicationContext.getBeanNamesForAnnotation(RpcServer.class).length > 0)//root application context 没有parent，他就是老大.
        {
            nettyRpcServer.start();
        }

    }

}
