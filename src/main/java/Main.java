import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;
import static spark.Spark.*;

/**
 * Created by mblaszkiewicz on 14.01.2017.
 */

public class Main {
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static Map<Session, String> userChannelMap = new ConcurrentHashMap<>();
    static Set<String> channels = new HashSet<>();

    public static void main(String[] args) {
        BasicConfigurator.configure();
        staticFileLocation("/public");
        webSocket("/chat", ChatWebSocketHandler.class);
        init();

        channels.add("chatbot");
    }

    public static void changeChannel(Session user, String channel) {
        userChannelMap.put(user, channel);
        channels.add(channel);
    }

    public static void brodcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach( session -> {
            try {
                String channel = userChannelMap.get(session);
                List<Session> usersOnChannel = userChannelMap.entrySet().stream()
                        .filter(e -> e.getValue().equals(channel))
                        .map(Map.Entry::getKey).collect(Collectors.toList());
                List<String> usernamesOnChannel = userUsernameMap.entrySet().stream()
                        .filter(e->usersOnChannel.contains(e.getKey()))
                        .map(Map.Entry::getValue).collect(Collectors.toList());
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(sender, message))
                        .put("userlist", usernamesOnChannel)
                        .put("channelname", channel)
                        .put("channellist", channels)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void brodcastMessage(Session sender, String message) {
        System.out.println("Panika 5");
        if(userChannelMap.get(sender) != null) {
            System.out.println("Panika 10");
            userChannelMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
                try {
                    String channel = userChannelMap.get(sender);
                    List<Session> usersOnChannel = userChannelMap.entrySet().stream()
                            .filter(e -> e.getValue().equals(channel))
                            .map(Map.Entry::getKey).collect(Collectors.toList());
                    List<String> usernamesOnChannel = userUsernameMap.entrySet().stream()
                            .filter(e -> usersOnChannel.contains(e.getKey()))
                            .map(Map.Entry::getValue).collect(Collectors.toList());
                    if (userChannelMap.get(session).equals(channel)) {
                        session.getRemote().sendString(String.valueOf(new JSONObject()
                                .put("userMessage", createHtmlMessageFromSender(userUsernameMap.get(sender), message))
                                .put("userlist", usernamesOnChannel)
                                .put("channelname", channel)
                                .put("channellist", channels)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void brodcastChatbot(Session sender, String message, String author) {
        if(userChannelMap.get(sender) != null)
                try {
                    String channel = userChannelMap.get(sender);
                    List<Session> usersOnChannel = userChannelMap.entrySet().stream()
                            .filter(e -> e.getValue().equals(channel))
                            .map(Map.Entry::getKey).collect(Collectors.toList());
                    List<String> usernamesOnChannel = userUsernameMap.entrySet().stream()
                            .filter(e->usersOnChannel.contains(e.getKey()))
                            .map(Map.Entry::getValue).collect(Collectors.toList());
                    sender.getRemote().sendString(String.valueOf(new JSONObject()
                            .put("userMessage", createHtmlMessageFromSender(author, message))
                            .put("userlist", usernamesOnChannel)
                            .put("channelname", channel)
                            .put("channellist", channels)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }

    public static String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " mówi:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }
}
