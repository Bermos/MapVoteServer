package lobby;

import java.util.*;

public class Handler {
    private Map<String, Lobby> lobbies = new HashMap<String, Lobby>();
    private List<String> adjectives = readList("adjectives");
    private List<String> nouns = readList("nouns");

    /**
     * Creates a new lobby with a random id.
     *
     * @return the id of the new lobby
     */
    public String createNewLobby() {
        String id;
        while(lobbies.containsKey(id = generateId()));

        lobbies.put(id, new Lobby(id));

        return id;
    }

    /**
     * Creates a new lobby with the given id. If a lobby with
     * that id already exists does nothing.
     *
     * @param id for the new lobby
     * @return true if a new lobby was created
     */
    public boolean createNewLobby(String id) {
        if (lobbies.containsKey(id))
            return false;

        Lobby lobby = new Lobby(id);
        lobbies.put(id, lobby);
        return true;
    }

    /**
     * Returns a lobby with the given id or null if no such
     * lobby exists.
     *
     * @param id of the requested lobby
     * @return the lobby or null
     */
    public Lobby getLobbyById(String id) {
        return lobbies.getOrDefault(id, null);
    }

    private String generateId() {
        Random rng = new Random();
        String id = "";

        id += adjectives.get(rng.nextInt(adjectives.size()));
        id += adjectives.get(rng.nextInt(adjectives.size()));
        id += nouns.get(rng.nextInt(nouns.size()));

        return id;
    }

    private List<String> readList(String fileName) {
        List<String> words = new ArrayList<String>();
        Scanner sc = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));

        while (sc.hasNext()) {
            words.add(sc.nextLine());
        }

        return words;
    }
}
