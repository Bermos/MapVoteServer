package lobby;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import server.WsPackage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Lobby {
    private enum Mode {
       BO1, BO3, BO5
    }

    private enum Game {
        CS, OW
    }

    public class LobbyState {
        String[] users;
        int state;
        Mode mode;
        Game game;
        Date actionDeadline;
    }

    private String id;
    private List<WebSocket> users = new ArrayList<>();
    private WebSocket host;
    private int state;
    private Mode mode;
    private Game game;
    private Date lastAction;

    private Gson gson = new Gson();

    Lobby(String id) {
        this.id = id;
        this.state = 0;
        this.lastAction = new Date();
    }

    /**
     * Add a user to the lobby
     * @param user to be joined
     */
    public void join(WebSocket user) {
        users.add(user);
    }

    /**
     * Removes a user from the lobby
     * @param user to remove
     */
    public void leave(WebSocket user) {
        users.remove(user);
    }

    /**
     * Set the game for this lobby
     * @param game to be set
     */
    public void setGame(Game game) {
        this.game = game;
        lastAction = new Date();
    }

    /**
     * Set the mode for the lobby
     * @param mode to be set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        lastAction = new Date();
    }

    /**
     * Set the host for the lobby
     * @param host to be set
     */
    public void setHost(WebSocket host) {
        this.host = host;
    }

    /**
     * Get the host of this lobby
     * @return the host of this lobby
     */
    public WebSocket getHost() {
        return this.host;
    }

    /**
     * Get the id of this lobby
     * @return the id of this lobby
     */
    public String getID() {
        return id;
    }

    /**
     * Broadcasts the current state of the lobby to all connected websockets
     */
    public void updatePeers() {
        WsPackage.create(WsPackage.Action.UPDATE)
                .addDataElement("lobbyState", gson.toJsonTree(getLobbyState()))
                .broadcast(this);
    }

    /**
     * Broadcast a message to all connected websockets
     * @param message to be broadcast
     */
    public void broadcast(String message) {
        for (WebSocket user: users) {
            try {
                user.send(message);
            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get a LobbyState object that represents the current state of the lobby
     * @return the current state of the lobby
     */
    private LobbyState getLobbyState() {
        LobbyState ls = new LobbyState();
        ls.actionDeadline = new Date(this.lastAction.getTime() + 30*60*1000);
        ls.game = this.game;
        ls.mode = this.mode;
        ls.state = this.state;
        ls.users = this.users.stream()
                .map(ws -> ((User)(ws.getAttachment())).getUsername())
                .sorted().toArray(String[]::new);

        return ls;
    }
}
