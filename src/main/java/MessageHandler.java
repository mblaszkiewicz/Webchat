import org.eclipse.jetty.websocket.api.Session;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mblaszkiewicz on 14.01.2017.
 */
public class MessageHandler {
    private Chatbot chatbot;
    private Messenger messenger;

    public MessageHandler(Messenger messenger) {
        this.messenger = messenger;
        chatbot = new Chatbot();
    }

    public void check(Session user, String message) {
        if(message.startsWith("-username: "))
        {
            Pattern P = Pattern.compile("\"([^\"]*)\"");
            Matcher m = P.matcher(message);
            if(m.find()) {
                messenger.putOnUserUsernameMap(user, m.group(1));
                messenger.changeChannel(user, "General");
                messenger.brodcastMessage(user, "Serwer", (m.group(1) + " dołączył do czatu."));
            }
        }
        else if(message.startsWith("-newchannel: "))
        {
            Pattern P = Pattern.compile("\"([^\"]*)\"");
            Matcher m = P.matcher(message);
            if(m.find()) {
                messenger.changeChannel(user, m.group(1));
                messenger.brodcastMessage(user, "Serwer", (messenger.getUserUsernameMap(user) + " zmienił kanał na: " + m.group(1)));
            }
        }
        else if(message.startsWith("-leavechannel")) {
            messenger.removeUserChannelMap(user);
        }
        else if(messenger.getUserChannelMap(user) != null && messenger.getUserChannelMap(user).equals("chatbot")) {
            messenger.brodcastMessage(user, messenger.getUserChannelMap(user), message);
            messenger.brodcastMessage(user, "Chatbot", chatbot.questionCheck(message));
        }
        else
            messenger.brodcastMessage(user, messenger.getUserUsernameMap(user), message);
    }

}
