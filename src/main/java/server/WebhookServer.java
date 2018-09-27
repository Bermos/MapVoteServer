package server;

import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lobby.Handler;

import java.io.IOException;
import java.io.OutputStream;

public class WebhookServer implements HttpHandler {
    private JsonParser jsp = new JsonParser();
    private Handler lobbyHandler;

    public WebhookServer(Handler lobbyHandler) {
        this.lobbyHandler = lobbyHandler;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String localContext = httpExchange.getRequestURI().getPath()
                .replaceFirst(httpExchange.getHttpContext().getPath(), "");
        OutputStream os = httpExchange.getResponseBody();

        switch (localContext) {
            case "/create":
                String lobbyId = lobbyHandler.createNewLobby();
                httpExchange.sendResponseHeaders(200, 0);
                os.write(lobbyId.getBytes());
                break;

            default:
                httpExchange.sendResponseHeaders(404,0);
                os.write("There is nothing here".getBytes());
                break;

        }

        os.close();
    }
}
