package pl.nomand.heavencore.pets;

import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.nomand.heavencore.common.Experience;
import pl.nomand.heavencore.pets.bonuses.Bon;
import pl.nomand.heavencore.users.User;

public class PetExperience extends Experience {

    private final Pet pet;

    public PetExperience(Pet pet, int level, long exp) {
        super(level, exp);
        this.pet = pet;
    }

    public PetExperience(Pet pet, YamlConfiguration yml) {
        level = yml.getInt("pet.experience.level");
        exp = yml.getLong("pet.experience.exp");
        this.pet = pet;
    }

    public void save(YamlConfiguration yml) {
        yml.set("pet.experience.level", level);
        yml.set("pet.experience.exp", exp);
    }

    @Override
    public long getRequirementExp() {
        if (level == getMaxLevel())
            return 0;

        return pet.getManager().getRequirementExp()[level];
    }

    @Override
    public int getMaxLevel() {
        return 100;
    }

    @Override
    public void onAddLevel() {
        Player owner = pet.getOwner();
        User user = pet.getManager().getMain().getUserManager().getUser(owner);

        // Some Effects
        owner.sendMessage("§eTwoje zwierzatko zdobylo §6"+level+" §epoziom!");
        owner.playSound(pet.getLocation(), Sound.LEVEL_UP, 3f, 1f);

        pet.despawn();
        pet.spawn();

        // Update User Bons
        user.addBons(pet.getTemplate().getBonsPerLevel());

        // Update Pet Bons
        for(Bon templateBon : pet.getTemplate().getBonsPerLevel())
            for(Bon petBon : pet.getBons())
                if (templateBon.getType() == petBon.getType())
                    petBon.setValue(petBon.getValue() + templateBon.getValue());
    }

}
