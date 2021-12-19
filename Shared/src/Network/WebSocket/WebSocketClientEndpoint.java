package Network.WebSocket;
import org.glassfish.grizzly.utils.StringDecoder;
import org.glassfish.grizzly.utils.StringEncoder;
import org.glassfish.tyrus.core.frame.BinaryFrame;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

class MessageHandler {
    void handleMessage(String message) {
        System.out.println("[webSocket] message handler: " + message);
    }

    void handleMessage(ByteBuffer message) {
        System.out.println("[webSocket] byte handler: " + message);
    }
}
@ClientEndpoint
public class WebSocketClientEndpoint {

    Session userSession = null;
    private MessageHandler messageHandler = new MessageHandler();

    public WebSocketClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }


    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }


    @OnMessage
    public void onMessage(ByteBuffer bytes) {
        System.out.println("Handle byte buffer" + StandardCharsets.UTF_8.decode(bytes));
    }


    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(int[] message) {
        if (this.userSession != null)
            this.userSession.getAsyncRemote().sendObject(message);
        else {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(this, new URI("ws://localhost:8080/socket"));

                this.sendMessage(message);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("null user session!");
        }
    }


    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        if (this.userSession != null)
            this.userSession.getAsyncRemote().sendBinary(StandardCharsets.UTF_8.encode(message));
        else {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(this, new URI("ws://localhost:8080/socket"));

                this.sendMessage(message);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("null user session!");
        }
    }

}