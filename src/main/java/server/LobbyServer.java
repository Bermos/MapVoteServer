package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lobby.Handler;
import lobby.Lobby;
import lobby.User;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import server.WsPackage.Action;

import java.net.InetSocketAddress;
import java.util.Map;

public class LobbyServer extends WebSocketServer {

    private Handler lobbyHandler;
    private JsonParser jsp = new JsonParser();

    public LobbyServer(int port, Handler lobbyHandler) {
        super(new InetSocketAddress(port));
        this.lobbyHandler = lobbyHandler;
    }

    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        Map<String, String> args = ServerHelper.getUrlParams(clientHandshake.getResourceDescriptor());
        String lobbyid = args.get("lobbyid");
        String username = args.get("username");

        System.out.println("[Connect] Username: " + username + " - lobbyid: " + lobbyid);
        WsPackage wsp = WsPackage.create();

        // rule out lobbyid param not set in url
        if (lobbyid == null) {
            wsp.action(Action.ERROR)
                    .addData("error", "Bad Request")
                    .addData("message", "Url contains no lobbyid param, closing connection.")
                    .send(webSocket);

            webSocket.close();

            return;
        }

        // rule out lobbyid referencing non-existent lobby
        Lobby lobby = lobbyHandler.getLobbyById(args.get("lobbyid"));
        if (lobby == null) {
            wsp.action(Action.ERROR)
                    .addData("error", "Not Found")
                    .addData("message", "No lobby with the requested id found. Closing connection.")
                    .send(webSocket);

            webSocket.close();

            return;
        }

        wsp.action(Action.SUCCESS)
                .addData("message", "Lobby found, joining lobby.")
                .send(webSocket);

        User user = new User(username, lobby);
        webSocket.setAttachment(user);
        lobby.join(webSocket);
        lobby.updatePeers();
    }

    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        User user = webSocket.getAttachment();

        System.out.println("[Disconnect] Username: " + user.getUsername() + " - lobbyid: " + user.getLobby().getID());

        user.getLobby().leave(webSocket);
        user.getLobby().updatePeers();
    }

    public void onMessage(WebSocket webSocket, String s) {
        JsonElement msg = jsp.parse(s);

        if (!msg.isJsonObject()) {
            WsPackage.create(Action.ERROR)
                    .addData("error", "JSON parse error")
                    .addData("message", "Message could not be parsed as JSON. Please check the syntax.")
                    .send(webSocket);
            return;
        }

        User user = webSocket.getAttachment();
        user.getLobby().handle(msg.getAsJsonObject());
    }

    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    public void onStart() {
        System.out.println("Websocket ServerHelper started.");
    }
}
