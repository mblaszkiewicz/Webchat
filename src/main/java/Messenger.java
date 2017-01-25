import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;
import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;

/**
 * Created by mblaszkiewicz on 25.01.2017.
 */
public class Messenger
{
    private Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    private Map<Session, String> userChannelMap = new ConcurrentHashMap<>();
    private Set<String> channels = new HashSet<>();

    public Messenger() {
        staticFileLocation("/public");
        ChatWebSocketHandler webSocketHandler = new ChatWebSocketHandler(this);
        webSocket("/chat", webSocketHandler);
        init();
        channels.add("chatbot");
    }

    public void changeChannel(Session user, String channel) {
        userChannelMap.put(user, channel);
        channels.add(channel);
    }

    public void removeUser(Session user) {
        String username = userUsernameMap.get(user);
        userUsernameMap.remove(user);
        brodcastMessage(user, "Serwer", username + " opuścił czat. ");
        userChannelMap.remove(user);
    }

    public void brodcastMessage(Session sender, String author, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach( session -> {
            try {
                String channel = userChannelMap.get(sender);
                if(userChannelMap.get(session).equals(channel)) {
                    List<Session> usersOnChannel = userChannelMap.entrySet().stream()
                            .filter(e -> e.getValue().equals(channel))
                            .map(Map.Entry::getKey).collect(Collectors.toList());
                    List<String> usernamesOnChannel = userUsernameMap.entrySet().stream()
                            .filter(e -> usersOnChannel.contains(e.getKey()))
                            .map(Map.Entry::getValue).collect(Collectors.toList());
                    session.getRemote().sendString(String.valueOf(new JSONObject()
                            .put("userMessage", createHtmlMessageFromSender(author, message))
                            .put("userlist", usernamesOnChannel)
                            .put("channelname", channel)
                            .put("channellist", channels)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " mówi:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }

    public void putOnUserUsernameMap(Session user, String username) {
        userUsernameMap.put(user, username);
    }

    public String getUserUsernameMap(Session user) {
        return userUsernameMap.get(user);
    }

    public String getUserChannelMap(Session user) {
        return userChannelMap.get(user);
    }

    public void removeUserChannelMap(Session user) {
        brodcastMessage(user, "Serwer", (getUserUsernameMap(user) + " opuścił kanał"));
        userChannelMap.remove(user);
    }
}
