package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lobby.Handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class WebhookServer implements HttpHandler {
    private Handler lobbyHandler;

    public WebhookServer(Handler lobbyHandler) {
        this.lobbyHandler = lobbyHandler;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String localContext = httpExchange.getRequestURI().getPath()
                .replaceFirst(httpExchange.getHttpContext().getPath(), "");
        OutputStream os = httpExchange.getResponseBody();

        System.out.println(localContext);
        System.out.println(httpExchange.getRequestURI().getQuery());

        switch (localContext) {
            case "/create":
                Map<String, String> params = ServerHelper.getUrlParams(httpExchange.getRequestURI().getQuery());
                if(!params.containsKey("game") || !params.containsKey("mode")) {
                    httpExchange.sendResponseHeaders(400, 0);
                    break;
                }

                String lobbyId = lobbyHandler.createNewLobby(params.get("game"), params.get("mode"));
                httpExchange.sendResponseHeaders(200, lobbyId.length());
                os.write(lobbyId.getBytes());
                break;

            default:
                String response = "There is nothing here";
                httpExchange.sendResponseHeaders(404,response.length());
                os.write(response.getBytes());
                break;

        }

        os.close();
    }
}
