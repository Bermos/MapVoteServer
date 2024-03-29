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
        Team.WebSafeTeam a, b;
        Team.WebSafeTeam turn;
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
            a.setLeader(user);
        } else if (users.size() == 2) {
            b = new Team();
            b.setLeader(user);
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

    public void handle(JsonObject msg, WebSocket user) {
        Action action = Action.valueOf(msg.get("action").getAsString().toUpperCase());
        JsonObject data = msg.get("data").isJsonObject() ? msg.getAsJsonObject("data") : null;
        boolean lobbyStateChanged = false;

        switch (action) {
            case SET:
                if (data.has("teamName")) {
                    if (a.getLeader() == user) {
                        a.name = data.get("teamName").getAsString();
                        lobbyStateChanged = true;
                    } else if (b.getLeader() == user) {
                        b.name = data.get("teamName").getAsString();
                        lobbyStateChanged = true;
                    } else {
                        WsPackage.create(Action.ERROR)
                                .addData("error", "Unauthorized")
                                .addData("message", "Only team leaders can set a team name.")
                                .send(user);
                    }
                }

                if (data.has("teamReady")) {
                    if (a.getLeader() == user) {
                        a.ready = data.get("teamReady").getAsBoolean();
                        lobbyStateChanged = true;
                    }
                    else if (b.getLeader() == user) {
                        b.ready = data.get("teamReady").getAsBoolean();
                        lobbyStateChanged = true;
                    }
                    else {
                        WsPackage.create(Action.ERROR)
                                .addData("error", "Unauthorized")
                                .addData("message", "Only team leaders can set the team ready status.")
                                .send(user);
                    }

                    if (a.ready && b.ready)
                        startVote();
                }

                if (lobbyStateChanged)
                    updatePeers();
                break;

            case VOTE:
                if(turn.getLeader() == user) {
                    if (!voteMap(msg.get("map").getAsJsonObject(), msg.get("vote").getAsInt())) {
                        WsPackage.create(Action.ERROR)
                                .addData("error", "Bad Request")
                                .addData("message", "This map has already been voted on.")
                                .send(user);
                        break;
                    }

                    nextTurn();
                    updatePeers();
                }
                break;

        }
    }

    /**
     * Broadcasts the current state of the lobby to all connected websockets
     */
    public void updatePeers() {
        WsPackage.create(WsPackage.Action.UPDATE)
                .addDataElement("lobbyState", gson.toJsonTree(getLobbyState())) //getLobbyState()))
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
    public LobbyState getLobbyState() {
        LobbyState ls = new LobbyState();

        ls.actionDeadline = new Date(this.lastAction.getTime() + 30*60*1000);
        ls.a = this.a == null ? null : this.a.getWebSafeTeam();
        ls.b = this.b == null ? null : this.b.getWebSafeTeam();
        ls.game = this.game;
        ls.mode = this.mode;
        ls.turn = this.turn == null ? null : this.turn.getWebSafeTeam();
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
        state = 1;
        updatePeers();
    }

    private boolean voteMap(JsonObject map, int vote) {
        Game.GameMap gameMap = game.mapCategories.get(map.get("category").getAsString()).get(map.get("name").getAsString());

        if (gameMap.voteState == 0)
            return false;

        gameMap.voteState = vote;
        gameMap.votedBy = turn;
        return true;
    }
}
