import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mblaszkiewicz on 14.01.2017.
 */
@WebSocket
public class ChatWebSocketHandler {
    private MessageHandler messageHandler;
    private Messenger messenger;

    public ChatWebSocketHandler(Messenger messenger) {
        this.messenger = messenger;
        messageHandler = new MessageHandler(messenger);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        messenger.removeUser(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        messageHandler.check(user, message);
    }
}
