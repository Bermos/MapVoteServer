package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lobby.Lobby;
import org.java_websocket.WebSocket;

public class WsPackage {
    public enum Action {
        GET, SET, ADD, DELETE, LOGIN, LOGOUT, INFORM, VOTE, SUCCESS, ERROR, DATA, UPDATE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private Action action;
    private JsonElement data;
    private JsonObject dynamicData;

    /**
     * Create a new and empty WebSocketPackage object. Cannot be send without resource and action set!
     *
     * @return new WebSocketPackage object
     */
    public static WsPackage create() {
        return new WsPackage();
    }

    /**
     * Create a new WebSocketPackage object with the given action.
     *
     * @param action of the package
     * @return new WebSocketPackage object
     */
    public static WsPackage create(Action action) {
        return new WsPackage(action);
    }

    /**
     * Create a new WebSocketPackage object with the given action and data.
     * Only use this if data is going to be a single JsonElement.
     *
     * @param action of the package
     * @param data for the package
     * @return new WebSocketPackage object
     */
    public static WsPackage create(Action action, JsonObject data) {
        return new WsPackage(action, data);
    }

    private WsPackage() {}

    private WsPackage(Action action) {
        this.action = action;
    }

    private WsPackage(Action action, JsonObject data) {
        this.action = action;
        this.data = data;
    }

    /**
     * Set the action property of the package
     *
     * @param action to be set
     * @return this object for chaining
     */
    public WsPackage action(Action action) {
        this.action = action;

        return this;
    }

    /**
     * Set the data property of the package. Only use this if the
     * data is going to be a single JsonElement. For multiple elements use WsPackage#addData.
     * @param data to be set
     * @return this object for chaining
     */
    public WsPackage data(JsonElement data) {
        this.data = data;

        return this;
    }

    /**
     * Add a element to the data of the package
     *
     * @param property the name of the property
     * @param value    of the property
     * @return this object for chaining
     */
    public WsPackage addData(String property, String value) {
        if (dynamicData == null) {
            dynamicData = new JsonObject();
        }

        dynamicData.addProperty(property, value);

        return this;
    }

    /**
     * Add a element to the data of the package
     *
     * @param property the name of the property
     * @param value    of the property
     * @return this object for chaining
     */
    public WsPackage addData(String property, Number value) {
        if (dynamicData == null) {
            dynamicData = new JsonObject();
        }

        dynamicData.addProperty(property, value);

        return this;
    }

    /**
     * Add a element to the data of the package
     *
     * @param property the name of the property
     * @param value    of the property
     * @return this object for chaining
     */
    public WsPackage addData(String property, Boolean value) {
        if (dynamicData == null) {
            dynamicData = new JsonObject();
        }

        dynamicData.addProperty(property, value);

        return this;
    }


    /**
     * Add a element to the data of the package
     *
     * @param property the name of the property
     * @param value    of the property
     * @return this object for chaining
     */
    public WsPackage addDataElement(String property, JsonElement value) {
        if (dynamicData == null) {
            dynamicData = new JsonObject();
        }

        dynamicData.add(property, value);

        return this;
    }

    private JsonObject makeJson() {
        JsonObject paeckli = new JsonObject();

        paeckli.addProperty("action", action.toString().toLowerCase());

        if (data != null && dynamicData == null)
            paeckli.add("data", data);

        if (data == null && dynamicData != null)
            paeckli.add("data", dynamicData);

        if (data == null && dynamicData == null)
            paeckli.add("data", null);

        if (data != null && dynamicData != null) {
            dynamicData.add("data", data);
            paeckli.add("data", dynamicData);
        }

        return paeckli;
    }

    /**
     * Sends this package to all open websocket connections in the given lobby
     * @param lobby the package should be sent over
     * @throws NullPointerException if resource or action are not set.
     */
    public void broadcast(Lobby lobby) {
        lobby.broadcast(makeJson().toString());
    }

    /**
     * Sends this package to the given connection
     * @param webSocket to be contacted
     * @throws NullPointerException if resource or action are not set.
     */
    public void send(WebSocket webSocket) {
        webSocket.send(makeJson().toString());
    }

    @Override
    public String toString() {
        return makeJson().toString();
    }
}
