import org.eclipse.jetty.websocket.api.Session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mblaszkiewicz on 14.01.2017.
 */
public class MessageHandler {
    private static Chatbot chatbot;

    public static void check(Session user, String message) {
        if(message.startsWith("-username: "))
        {
            Pattern P = Pattern.compile("\"([^\"]*)\"");
            Matcher m = P.matcher(message);
            if(m.find()) {
                Main.userUsernameMap.put(user, m.group(1));
                Main.changeChannel(user, "General");
                Main.brodcastMessage("Serwer", (m.group(1) + " dołączył do czatu."));
            }
        }
        else if(message.startsWith("-newchannel: "))
        {
            Pattern P = Pattern.compile("\"([^\"]*)\"");
            Matcher m = P.matcher(message);
            if(m.find()) {
                Main.changeChannel(user, m.group(1));
                Main.brodcastMessage("Server", (Main.userUsernameMap.get(user) + " zmienił kanał na: " + m.group(1)));
            }
        }
        else if(message.startsWith("-leavechannel")) {
            Main.userChannelMap.remove(user);
            Main.brodcastMessage("Server", (Main.userUsernameMap.get(user) + " opuścił kanał"));
        }
        else if(Main.userChannelMap.get(user) != null && Main.userChannelMap.get(user).equals("chatbot")) {
            Main.brodcastChatbot(user, message, Main.userUsernameMap.get(user));
            Main.brodcastChatbot(user, chatbot.questionCheck(message), "Chatbot");
        }
        else
            Main.brodcastMessage(user, message);
    }

}
