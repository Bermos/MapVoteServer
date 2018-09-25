package lobby;

public class User {
    private String username;
    private Lobby lobby;

    public User(String username, Lobby lobby) {
        this.username = username;
        this.lobby = lobby;
    }

    public String getUsername() {
        return this.username;
    }

    public Lobby getLobby() {
        return this.lobby;
    }
}
