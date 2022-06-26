package github.charon.remote.transport.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
@Data
public class Task implements Runnable{
    private Socket socket;

    private static final Logger logger = LoggerFactory.getLogger(HelloServer.class);

    @Override
    public void run() {
        logger.info("client connected");
        try (
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())
        ) {
            Message message = (Message) objectInputStream.readObject();
            logger.info("server receive " + Thread.currentThread().getName() + "message: " + message.getContent());
            message.setContent(Thread.currentThread().getName() + "github/charon");
            Thread.sleep(3000);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            logger.error("error: ", e);
        }
    }
}
