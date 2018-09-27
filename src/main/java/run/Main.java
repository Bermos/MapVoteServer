package run;

import com.sun.net.httpserver.HttpServer;
import lobby.Handler;
import server.LobbyServer;
import server.WebhookServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {

        Handler hd = new Handler();
        System.out.println(hd.createNewLobby());

        // Setup websocket server
        LobbyServer ls = new LobbyServer(7666, hd);
        ls.setReuseAddr(true);
        ls.start();

        // Setup static http server
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(7665), 0);
        String context = "/api";
        httpServer.createContext(context, new WebhookServer(hd));
        httpServer.setExecutor(null);
        httpServer.start();
    }
}
