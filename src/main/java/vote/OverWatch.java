package vote;

import java.util.HashMap;

public class OverWatch extends Game {

    public OverWatch() {
        super();

        name = "Overwatch";
        mapCategories = new HashMap<>();

        mapCategories.put("Assault", new HashMap<String, GameMap>(){{
            put("Hanamura", new GameMap());
            put("Horizon Lunar Colony", new GameMap());
            put("Temple of Anubis", new GameMap());
            put("Volskaya Industries", new GameMap());
        }});

        mapCategories.put("Escort", new HashMap<String, GameMap>(){{
            put("Dorado", new GameMap());
            put("Junkertown", new GameMap());
            put("Rialto", new GameMap());
            put("Route 66", new GameMap());
            put("Watchpoint: Gibraltar", new GameMap());
        }});

        mapCategories.put("Hybrid", new HashMap<String, GameMap>(){{
            put("Blizzard World", new GameMap());
            put("Eichenwalde", new GameMap());
            put("Hollywood", new GameMap());
            put("King's Row", new GameMap());
            put("Numbani", new GameMap());
        }});

        mapCategories.put("Control", new HashMap<String, GameMap>(){{
            put("Busan", new GameMap());
            put("Ilios", new GameMap());
            put("Lijiang Tower", new GameMap());
            put("Nepal", new GameMap());
            put("Oasis", new GameMap());
        }});

        mapCategories.put("Arcade", new HashMap<String, GameMap>(){{
            put("Ayutthaya", new GameMap());
            put("Black Forest", new GameMap());
            put("Castillo", new GameMap());
            put("Château Guillard", new GameMap());
            put("Ecopoint: Antarctica", new GameMap());
            put("Necropolis", new GameMap());
            put("Petra", new GameMap());
        }});

        mapCount = 0;
        mapCategories.forEach((key, category) -> mapCount += category.size());
    }
}
