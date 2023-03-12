package pl.nomand.heavencore.users;

import ch.njol.skript.variables.Variables;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.nomand.heavencore.common.HeavenCore;
import pl.nomand.heavencore.pets.Pet;
import pl.nomand.heavencore.pets.bonuses.Bon;
import pl.nomand.heavencore.pets.bonuses.BonType;
import pl.nomand.heavencore.ranking.RankingData;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class User {

    private final HeavenCore main;
    private final Player player;

    private final UserTemp temp;

    private Pet pet;

    public User(HeavenCore main, Player player) {
        this.main = main;
        this.player = player;
        this.temp = new UserTemp();

        File file = new File(main.getDataFolder(), "users/" + player.getName().toLowerCase()+".yml");
        if (!file.exists()) {
            try {

                // Tworzenie pliku gracza
                file.createNewFile();

                // Tworzenie wpisu w bazie danych o rankingu
//                RankingData rankingData = new RankingData(main, player);
//                rankingData.create();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return;
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        if (yml.isSet("pet"))
            this.pet = new Pet(player, main.getPetManager(), yml);
    }

    public void save() {
        File file = new File(main.getDataFolder(), "users/" + player.getName().toLowerCase()+".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        yml.set("pet", null);
        if (pet != null)
            pet.save(yml);

        try {
            yml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters & Setters

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public UserTemp getTemp() {
        return temp;
    }

    // Bons

    public void resetBons() {
        for(BonType bonType : BonType.values())
            Variables.setVariable(bonType.getVariable() + player.getName().toLowerCase(), 0.0, null, false);
    }

    public void addBons(List<Bon> bons) {
        bons.forEach(this::addBon);
    }

    public void removeBons(List<Bon> bons) {
        bons.forEach(this::removeBon);
    }

    public void addBon(Bon bon) {
        BonType type = bon.getType();
        String targetVariable = type.getVariable() + player.getName().toLowerCase();

        Double prevValue = (Double) Variables.getVariable(targetVariable, null, false);
        if (prevValue != null) {
            Variables.setVariable(targetVariable, (prevValue + bon.getValue()), null, false);
        } else {
            Variables.setVariable(targetVariable, bon.getValue(), null, false);
        }
    }

    public void removeBon(Bon bon) {
        BonType type = bon.getType();
        String targetVariable = type.getVariable() + player.getName().toLowerCase();

        Double prevValue = (Double) Variables.getVariable(targetVariable, null, false);
        if (prevValue != null) {
            Variables.setVariable(targetVariable, (prevValue - bon.getValue()), null, false);
        } else {
            Variables.setVariable(targetVariable, (0 - bon.getValue()), null, false);
        }
    }

}
