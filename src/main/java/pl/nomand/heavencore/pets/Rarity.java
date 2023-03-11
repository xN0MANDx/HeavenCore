package pl.nomand.heavencore.pets;

public enum Rarity {

    RARE("§3Rzadkie Zwierzatko"),
    EPIC("§5Epickie Zwierzatko"),
    LEGENDARY("§6Legendarne Zwierzatko");

    private final String name;

    Rarity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
