package github.charon.remote.transport.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HelloServer {
    private static final Logger logger = LoggerFactory.getLogger(HelloServer.class);

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>(1), Executors.defaultThreadFactory());

    public void start() {
        int port = 6666;
        try (ServerSocket server = new ServerSocket(port)) {
            Socket socket;
            while ((socket = server.accept()) != null) {
                Task t = new Task(socket);
                threadPool.execute(t);
            }
        } catch (IOException e) {
            logger.error("error: ", e);
        }
    }


    public static void main(String[] args) {
        HelloServer helloServer = new HelloServer();
        helloServer.start();
    }
}