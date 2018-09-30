package lobby;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import server.WsPackage;
import server.WsPackage.Action;
import vote.CounterStrike;
import vote.Game;
import vote.OverWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Lobby {
    private enum Mode {
       BO1, BO3, BO5
    }

    public class LobbyState {
        String[] users;
        int state;
        Team a, b;
        Team turn;
        Mode mode;
        Game game;
        Date actionDeadline;
    }

    private String id;
    private int state;
    private Team a, b;
    private Team turn;
    private Mode mode;
    private Game game;
    private WebSocket host;
    private Date lastAction;

    private Gson gson = new Gson();
    private List<WebSocket> users = new ArrayList<>();


    Lobby(String id, String game, String mode) {
        this.id = id;
        this.state = 0;
        this.lastAction = new Date();

        setGame(
                game.equalsIgnoreCase("ow") ? new OverWatch() :
                game.equalsIgnoreCase("cs") ? new CounterStrike() : null
        );
        setMode(Mode.valueOf(mode.toUpperCase()));
    }

    /**
     * Add a user to the lobby
     * @param user to be joined
     */
    public void join(WebSocket user) {
        users.add(user);

        if (users.size() == 1) {
            setHost(user);
            a = new Team();
            a.leader = user;
        } else if (users.size() == 2) {
            b = new Team();
            b.leader = user;
            startVote();
        }
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

    public void handle(JsonObject msg) {
        Action action = Action.valueOf(msg.get("action").getAsString().toUpperCase());
        JsonObject data = msg.get("data").isJsonObject() ? msg.getAsJsonObject("data") : null;

        if(action == Action.SET) {
            /* this.game = data.has("game") ? Game.valueOf(data.get("game").toString().toUpperCase()) : game;
            this.mode = data.has("mode") ? Mode.valueOf(data.get("mode").toString().toUpperCase()) : mode; */

        }

        if(action == Action.VOTE) {

        }

        if(data.get("").toString().equalsIgnoreCase("")) {

        }
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
        ls.a = this.a;
        ls.b = this.b;
        ls.game = this.game;
        ls.mode = this.mode;
        ls.turn = this.turn;
        ls.state = this.state;
        ls.users = this.users.stream()
                .map(ws -> ((User)(ws.getAttachment())).getUsername())
                .sorted().toArray(String[]::new);

        return ls;
    }

    /**
     * Changes the next turn to the other team
     */
    private void nextTurn() {
        if(turn == a)
            turn = b;
        else
            turn = a;
    }

    private void startVote() {
        turn = a;
    }
}
