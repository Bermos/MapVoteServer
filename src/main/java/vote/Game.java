package vote;

import lobby.Team;

import java.util.Map;

public class Game {
    public class GameMap {
        public String imageUrl;
        public int voteState;
        public Team votedBy;

        GameMap() {
            this.voteState = 0;
        }
    }

    public String name;
    public int mapCount;
    public Map<String, Map<String, GameMap>> mapCategories;

    Game() {

    }
}
