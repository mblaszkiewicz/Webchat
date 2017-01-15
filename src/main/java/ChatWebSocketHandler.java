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
    Chatbot chatbot;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Main.userUsernameMap.get(user);
        Main.userUsernameMap.remove(user);
        Main.brodcastMessage(sender = "Server", msg = (username + "left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if(message.startsWith("-username: "))
        {
            Pattern P = Pattern.compile("\"([^\"]*)\"");
            Matcher m = P.matcher(message);
            if(m.find()) {
                Main.userUsernameMap.put(user, m.group(1));
                Main.changeChannel(user, "General");
                Main.brodcastMessage(sender = "Server", msg = (m.group(1) + " joined the chat"));
            }
        }
        else if(message.startsWith("newchannel: "))
        {
            Pattern P = Pattern.compile("\"([^\"]*)\"");
            Matcher m = P.matcher(message);
            if(m.find()) {
                Main.changeChannel(user, m.group(1));
                Main.brodcastMessage(sender = "Server", msg = (Main.userUsernameMap.get(user) + " left the channel for " + m.group(1)));
            }
        }
        else if(message.startsWith("leavechannel")) {
            Main.userChannelMap.remove(user);
        }
        else if(Main.userChannelMap.get(user).equals("chatbot")) {
            Main.brodcastMessage(user, msg = chatbot.questionCheck(message));
        }
        else
            Main.brodcastMessage(user, msg = message);
    }
}
