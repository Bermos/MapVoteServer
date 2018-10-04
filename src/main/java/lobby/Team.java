package lobby;

import org.java_websocket.WebSocket;

public class Team {
    public class WebSafeTeam {
        public String leader;
        public String name;
        public boolean ready;

        WebSafeTeam(String leader, String name, boolean ready) {
            this.leader = leader;
            this.name = name;
            this.ready = ready;
        }
    }

    public WebSocket leader;
    public String name;
    public boolean ready;

    Team() {
        this.ready = false;
    }

    public WebSocket getLeader() {
        return leader;
    }

    public void setLeader(WebSocket leader) {
        this.leader = leader;
    }

    public WebSafeTeam getWebSafeTeam() {
        return new WebSafeTeam(((User)leader.getAttachment()).getUsername(), name, ready);
    }
}
