package run;

import lobby.Handler;
import server.LobbyServer;

public class Main {

    public static void main(String[] args) {

        Handler hd = new Handler();
        System.out.println(hd.createNewLobby());

        LobbyServer ls = new LobbyServer(7666, hd);
        ls.setReuseAddr(true);
        ls.start();
    }
}
