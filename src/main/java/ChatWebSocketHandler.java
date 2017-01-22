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
    private String sender, msg;
    private MessageHandler messageHandler;
    Chatbot chatbot;

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Main.userUsernameMap.get(user);
        Main.userUsernameMap.remove(user);
        Main.userChannelMap.remove(user);
        Main.brodcastMessage(sender = "Server", msg = (username + " opuścił czat. "));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        messageHandler.check(user, message);
    }
}
