package pl.nomand.heavencore.common;

public abstract class Experience {

    // VARIABLES

    protected int level;
    protected long exp;

    // Constructor

    public Experience(int level, long exp) {
        super();
        this.level = level;
        this.exp = exp;
    }

    public Experience() {
        super();
    }

    // Setters & Getters

    public int getLevel() {
        return level;
    }

    public long getExp() {
        return exp;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    // Methods

    public String getProgressBar(int chars, char progressColor, char neutralColor) {
        final double ep = (double) exp / (double) getRequirementExp() * 100d;
        StringBuilder progressLine = new StringBuilder("§").append(progressColor);

        for(int i=1; i<=chars; i++) {
            double j = 100d / chars;
            if (j*i >= ep) {
                progressLine.append("§").append(neutralColor);
                for(int x=i; x<=chars; x++)
                    progressLine.append("-");

                break;
            } else {
                progressLine.append("-");
            }
        }

        return progressLine.toString();
    }

    public String getPercentExp() {
        double percent = ((double) this.exp / (double) this.getRequirementExp()) * 100d;
        return Utils.deleteZero(percent)+"%";
    }

    public float getRatio() {
        return ((float) this.getExp() / (float) this.getRequirementExp());
    }

    public void addExp(long exp) {
        if (this.level != this.getMaxLevel()) {
            this.exp += exp;
            if (this.exp >= this.getRequirementExp()) {
                this.addLevel();
            }
        }
    }

    public void addLevel() {
        if (this.level < this.getMaxLevel() && this.exp >= this.getRequirementExp()) {
            // Nadanie nowych danych
            this.exp -= this.getRequirementExp();
            this.level += 1;

            this.onAddLevel();

            // Sprawdzanie
            if (level == this.getMaxLevel())
                exp = 0;
            else
                addLevel();
        }
    }

    // Abstracts

    public abstract long getRequirementExp();
    public abstract int getMaxLevel();
    public abstract void onAddLevel();

}
