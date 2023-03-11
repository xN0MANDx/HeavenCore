package pl.nomand.heavencore.pets;

import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nomand.heavencore.common.HeavenCore;
import pl.nomand.heavencore.users.GUI;
import pl.nomand.heavencore.users.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PetManager implements Listener {

    private final HeavenCore main;
    private final PetGUI guiManager;

    private final long[] requirementExp = new long[100];
    private HashMap<String, PetTemplate> templates;

    private int expForMob;

    public PetManager(HeavenCore main) {
        this.main = main;
        main.registerListener(this);
        new PetCommands(main);
        guiManager = new PetGUI(main);
        load();
    }

    public void reload() {
        load();
    }

    public void load() {
        Bukkit.getLogger().info("Ladowanie ustawien zwierzakow...");

        templates = new HashMap<>();

        File dir = new File(main.getDataFolder(), "pets");
        if (!dir.exists())
            dir.mkdir();

        File file;
        YamlConfiguration yml;

        file = new File(dir, "experience.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();

                // Generowanie bazowego expa
                yml = YamlConfiguration.loadConfiguration(file);

                yml.set("expForMob", 10);

                long reqExp = 2000;
                for(int i=1; i<=100; i++) {
                    yml.set("levels." + i, reqExp);
                    reqExp += 500;
                }

                yml.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        yml = YamlConfiguration.loadConfiguration(file);
        for(int i=1; i<=100; i++)
            requirementExp[i-1] = yml.getLong("levels."+i);

        expForMob = yml.getInt("expForMob");


        file = new File(dir, "templates.yml");
        if (!file.exists()) {
            try {
                FileUtils.copyInputStreamToFile(main.getResource("templates.yml"), file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        yml = YamlConfiguration.loadConfiguration(file);
        if (yml.isSet("templates"))
            for(String id : yml.getConfigurationSection("templates").getKeys(false))
                templates.put(id, new PetTemplate(yml, "templates."+id+".", id));

        Bukkit.getLogger().info("Zaladowano "+templates.size()+" szablonow zwierzakow!");
    }

    public PetTemplate getTemplate(String id) {
        return templates.get(id);
    }

    public HeavenCore getMain() {
        return main;
    }

    public PetGUI getGuiManager() {
        return guiManager;
    }

    public long[] getRequirementExp() {
        return requirementExp;
    }

    public boolean isPet(ItemStack item) {
        NBTItem ni = new NBTItem(item);
        return ni.hasKey("pet");
    }

    // Listeners

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        User user = main.getUserManager().getUser(player);

        for(Player loopPlayer : player.getWorld().getPlayers()) {
            User loopUser = main.getUserManager().getUser(loopPlayer);
            Pet loopPet = loopUser.getPet();
            if (loopPet != null)
                loopPet.showTo(player);
        }

        new BukkitRunnable() {
            public void run() {
                if (!player.isOnline())
                    return;

                Pet pet = user.getPet();
                if (pet != null)
                    pet.spawn();
            }
        }.runTaskLater(main, 1);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        User user = main.getUserManager().getUser(player);
        Pet pet = user.getPet();
        if (pet != null)
            pet.despawn();

        for(Player loopPlayer : player.getWorld().getPlayers()) {
            User loopUser = main.getUserManager().getUser(loopPlayer);
            Pet loopPet = loopUser.getPet();
            if (loopPet != null)
                loopPet.hideFrom(player);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();

        for(Player loopPlayer : e.getFrom().getPlayers()) {
            User loopUser = main.getUserManager().getUser(loopPlayer);
            Pet loopPet = loopUser.getPet();
            if (loopPet != null)
                loopPet.hideFrom(player);
        }

        for(Player loopPlayer : player.getWorld().getPlayers()) {
            User loopUser = main.getUserManager().getUser(loopPlayer);
            Pet loopPet = loopUser.getPet();
            if (loopPet != null)
                loopPet.showTo(player);
        }

        User user = main.getUserManager().getUser(player);
        Pet pet = user.getPet();
        if (pet != null)
            pet.despawn();

        new BukkitRunnable() {
            public void run() {
                if (!player.isOnline())
                    return;

                Pet pet = user.getPet();
                if (pet != null)
                    pet.spawn();
            }
        }.runTaskLater(main, 1);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();

        Player player = e.getEntity().getKiller();
        if (player == null)
            return;

        User user = main.getUserManager().getUser(player);
        if (user == null)
            return;

        Pet pet = user.getPet();
        if (pet != null) {
            pet.getExperience().addExp(expForMob);
        }

    }

    @EventHandler
    public void onNPC(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() != null) {
            String n = e.getRightClicked().getName();
            Player p = e.getPlayer();

            if (n.equals("§e§lZoolog"))
                guiManager.open(p);
        }
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getItemInHand().clone();
        Action a = e.getAction();
        if (((a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) && item != null) && item.getType() != Material.AIR) {
            if (!item.hasItemMeta())
                return;

            if (!isPet(item))
                return;

            if (item.getAmount() > 1) {
                player.sendMessage("§cMozesz wezwac tylko 1 zwierzaka na raz!");
                return;
            }

            User user = main.getUserManager().getUser(player);
            Pet pet = user.getPet();

            if (pet != null) {
                // Schowanie Starego
                pet.despawn();
                player.setItemInHand(pet.toItem());
                // Zalozenie Nowego
                pet = new Pet(player, item, this);
                user.setPet(pet);
                pet.spawn();
            } else {
                pet = new Pet(player, item, this);
                user.setPet(pet);
                pet.spawn();
                player.setItemInHand(null);
            }

            player.sendMessage("§aWezwano nowe zwierzatko.");
        }
    }

}
