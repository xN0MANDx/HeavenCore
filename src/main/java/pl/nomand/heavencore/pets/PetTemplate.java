package pl.nomand.heavencore.pets;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.nomand.heavencore.pets.bonuses.Bon;
import pl.nomand.heavencore.pets.bonuses.BonType;

import java.util.ArrayList;
import java.util.List;

public class PetTemplate {

    private final String id;

    private final String name;
    private final ItemStack skull;
    private final net.minecraft.server.v1_8_R3.ItemStack netSkull;

    private Rarity rarity;
    private List<Bon> defaultBons = new ArrayList<>();
    private List<Bon> bonsPerLevel = new ArrayList<>();

    public PetTemplate(String id, String name, String skin, Rarity rarity, List<Bon> defaultBons, List<Bon> bonsPerLevel) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.defaultBons = defaultBons;
        this.bonsPerLevel = bonsPerLevel;

        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(skin);
        item.setItemMeta(meta);
        skull = item;

        this.netSkull = CraftItemStack.asNMSCopy(skull);
    }

    public PetTemplate(YamlConfiguration yml, String path, String id) {
        this.id = id;
        this.name = yml.getString(path+"name");

        try {
            rarity = Rarity.valueOf(yml.getString(path+"rarity"));
        } catch (Exception e) {
            rarity = Rarity.RARE;
        }

        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(yml.getString(path+"skin"));
        item.setItemMeta(meta);
        skull = item;

        this.netSkull = CraftItemStack.asNMSCopy(skull);

        for(String x : yml.getConfigurationSection(path+"bons.default").getKeys(false)) {
            BonType bonType = BonType.valueOf(x);
            double value = yml.getDouble(path+"bons.default."+x);
            defaultBons.add(new Bon(bonType, value));
        }

        for(String x : yml.getConfigurationSection(path+"bons.onLevel").getKeys(false)) {
            BonType bonType = BonType.valueOf(x);
            double value = yml.getDouble(path+"bons.onLevel."+x);
            bonsPerLevel.add(new Bon(bonType, value));
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getSkull() {
        return skull;
    }

    public net.minecraft.server.v1_8_R3.ItemStack getNetSkull() {
        return netSkull;
    }

    public List<Bon> getDefaultBons() {
        return defaultBons;
    }

    public List<Bon> getBonsPerLevel() {
        return bonsPerLevel;
    }

    public Rarity getRarity() {
        return rarity;
    }

}
