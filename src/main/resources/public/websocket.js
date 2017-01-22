var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onclose= function () { alert("Połączenie WebSocket zamknięte"); };

window.onload = function () {

    document.getElementById("send").addEventListener("click", function () {
        sendMessage(document.getElementById("message").value);
    });

    document.getElementById("message").addEventListener("keypress", function (e) {
        if (e.keyCode === 13) { sendMessage(e.target.value); }
    });

    document.getElementById("sendusername").addEventListener("click", function () {
        document.cookie = "username="+document.getElementById("username").value;
        sendMessage("-username: \""+document.getElementById("username").value+"\"");
        document.getElementById("namedialog").style.display = "none";
    });

    document.getElementById("username").addEventListener("keypress", function (e) {
        if(e.keyCode === 13) {
            document.cookie = "username="+e.target.value;
            sendMessage("-username: \""+e.target.value+"\"");
            document.getElementById("namedialog").style.display = "none";
        }
    });

    document.getElementById("sendnewchannel").addEventListener("click", function (e) {
        sendMessage("-newchannel: \""+document.getElementById("newchannel").value+"\"");
        document.getElementById("newchannel").value = "";
        document.getElementById("message").disabled = false;
        document.getElementById("send").disabled = false;
    });

    document.getElementById("newchannel").addEventListener("keypress", function (e) {
        if (e.keyCode === 13) {

            sendMessage("-newchannel: \""+e.target.value+"\"");
            document.getElementById("newchannel").value = "";
            document.getElementById("message").disabled = false;
            document.getElementById("send").disabled = false;
        }
    });

    document.getElementById("leavechannel").addEventListener("click", function (e) {
        sendMessage("-leavechannel");
        document.getElementById("message").disabled = true;
        document.getElementById("send").disabled = true;
    });

    document.getElementById("namedialog").style.display = "block";

}

function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        document.getElementById("message").value = "";
    }
}

function changeChannel(newchannel) {
    sendMessage("-newchannel: \""+newchannel+"\"");
    document.getElementById("message").disabled = false;
    document.getElementById("send").disabled = false;
}

function updateChat(msg) {
    var data = JSON.parse(msg.data);
    document.getElementById("chat").insertAdjacentHTML("afterbegin", data.userMessage);
    document.getElementById("userlist").innerHTML = "";
    document.getElementById("channelname").innerHTML = "<p> Kanał: " + data.channelname + "</p>";

    document.getElementById("channellist").innerHTML = "";
    data.userlist.forEach(function (user) {
        document.getElementById("userlist").insertAdjacentHTML("afterbegin", "<li>" + user + "</li>");
    });
    data.channellist.forEach(function (channel) {
        document.getElementById("channellist").insertAdjacentHTML("afterbegin", "<li onclick='changeChannel(\""+channel+"\")'>" + channel + "</li>");
    });
}