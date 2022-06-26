package github.charon.config;

import github.charon.common.utils.concurrent.threadpool.ThreadPoolFactoryUtil;
import github.charon.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Slf4j
public class CustomShutdownHook {

    private Integer port;

    private String zkHost;

    private Integer zkPort;

    public CustomShutdownHook(int p, String zkHost, int zkPort) {
        this.port = p;
        this.zkHost = zkHost;
        this.zkPort = zkPort;
    }

//    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();
//
//    public static CustomShutdownHook getCustomShutdownHook() {
//        return CUSTOM_SHUTDOWN_HOOK;
//    }

    public void clearAll() {
        log.info("add ShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), this.port);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(this.zkHost + ":" + this.zkPort), address);
            } catch (UnknownHostException ignored) {
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }

    public int getPort() {
        return this.port;
    }
}
