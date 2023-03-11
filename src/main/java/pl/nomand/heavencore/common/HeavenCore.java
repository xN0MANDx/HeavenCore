package pl.nomand.heavencore.common;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nomand.heavencore.items.ItemManager;
import pl.nomand.heavencore.pets.PetManager;
import pl.nomand.heavencore.ranking.RankingData;
import pl.nomand.heavencore.users.User;
import pl.nomand.heavencore.users.UserManager;

public class HeavenCore extends JavaPlugin {

    private static HeavenCore instance;

    private HeavenConfig heavenConfig;
    private UserManager userManager;
    private PetManager petManager;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;
        this.heavenConfig = new HeavenConfig(this);
        this.userManager = new UserManager(this);
        this.petManager = new PetManager(this);
        this.itemManager = new ItemManager();
        new BaseListeners(this);
        new Commands(this);

        int autosave = heavenConfig.getAutosave() * 20;

        new BukkitRunnable() {
            public void run() {

                for(Player player : Bukkit.getOnlinePlayers()) {

                    // Zapisywanie Rankingu do MySQL
                    RankingData rankingData = new RankingData(instance);
                    rankingData.updateForEveryone();

                    // Zapisywanie Danych Gracza do Plikow
                    User user = userManager.getUser(player);
                    user.save();

                }

            }
        }.runTaskTimerAsynchronously(instance, autosave, autosave);

        Bukkit.getLogger().info("HeavenCore zostal uruchomiony!");
    }

    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            // Zapisywanie Danych Gracza do Plikow
            User user = userManager.getUser(player);
            user.save();

            RankingData rankingData = new RankingData(instance);
            rankingData.updateForEveryone();
        }

        Bukkit.getLogger().info("HeavenCore zostal wylaczony!");
    }

    public void reload() {
        petManager.reload();
        this.heavenConfig = new HeavenConfig(this);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public static HeavenCore getInstance() {
        return instance;
    }

    public HeavenConfig getHeavenConfig() {
        return heavenConfig;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

}
