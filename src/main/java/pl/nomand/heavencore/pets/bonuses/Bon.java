package pl.nomand.heavencore.pets.bonuses;

public class Bon {

    // Variables

    private BonType type;
    private double value;

    // Constructors

    public Bon() {
        super();
    }

    public Bon(int id, double value) {
        super();
        this.type = BonType.valueOf(id);
        this.value = value;
    }

    public Bon(BonType type, double value) {
        super();
        this.type = type;
        this.value = value;
    }

    public Bon(Bon bon) {
        this.type = bon.getType();
        this.value = bon.getValue();
    }

    // Setters & Getters

    public BonType getType() {
        return type;
    }

    public void setType(BonType type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
