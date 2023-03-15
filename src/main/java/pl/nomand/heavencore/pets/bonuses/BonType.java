package pl.nomand.heavencore.pets.bonuses;

import java.util.HashMap;
import java.util.Map;

public enum BonType {

    HUMAN_DAMAGE("Silny Przeciwko Ludziom", false, 0, "ludzie.pety::"),
    MOB_DAMAGE("Silny Przeciwko Potworom", false, 1, "potwory.pety::"),
    MEDIUM_DAMAGE("Srednie Obrazenia", false, 2, "srednie.pety::"),
    DAMAGE("Wartosc Ataku", true, 3, "wartosc.pety::"),
    EXP("Dodatkowy EXP", false, 4, "exp.pety::"),
    CHANCE_FOR_SHOCK("Szansa Na Porazenie", false, 5, "porazenie.pety::"),
    CHANCE_FOR_SLOW("Szansa Na Spowolnienie", false, 6, "spowolnienie.pety::"),
    CHANCE_FOR_AVOID("Szansa Na Blok Ciosu", false, 7, "blok.pety::"),
    CHANCE_FOR_PENETRATION("Szansa Na Przebicie Bloku Ciosu", false, 8, "przebicie.pety::"),
    MEDIUM_DEFENSE("Srednia Odpornosc", false, 9, "odpornosc.pety::"),
    CHANCE_FOR_EXPLOSION("Szansa Na Wybuch", false, 10, "wybuch.pety::");

    private final String name;
    private final boolean unit;
    private final int id;
    private final String variable;

    // Constructor

    BonType(String name, boolean unit, int id, String variable) {
        this.name = name;
        this.unit = unit;
        this.id = id;
        this.variable = variable;
    }

    // Getters

    public String getName() {
        return name;
    }

    public boolean isUnit() {
        return unit;
    }

    public int getId() {
        return id;
    }

    public String getVariable() {
        return variable;
    }

    // Numerowanie

    private static final Map<Object, Object> map = new HashMap<>();

    static {
        for (BonType type : BonType.values()) {
            map.put(type.id, type);
        }
    }

    public static BonType valueOf(int id) {
        return (BonType) map.get(id);
    }

}
