package pl.nomand.heavencore.users;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.nomand.heavencore.common.HeavenCore;
import pl.nomand.heavencore.ranking.RankingData;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class UserManager implements Listener {

    private final HeavenCore main;

    private final HashMap<Player, User> cache = new HashMap<>();

    public UserManager(HeavenCore main) {
        this.main = main;
        main.registerListener(this);

        File dir = new File(main.getDataFolder(), "users");
        if (!dir.exists())
            dir.mkdir();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = new User(main, player);
        cache.put(player, user);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = cache.get(player);
        user.save();
        cache.remove(player);

//        RankingData rankingData = new RankingData(main, player);
//        rankingData.update();
    }

    public User getUser(Player player) {
        return cache.get(player);
    }

    public HashMap<Player, User> getUsers() {
        return this.cache;
    }


}
