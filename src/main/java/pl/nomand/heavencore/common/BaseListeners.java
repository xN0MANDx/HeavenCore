package pl.nomand.heavencore.common;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.nomand.heavencore.users.User;
import pl.nomand.heavencore.users.UserTemp;

public class BaseListeners implements Listener {

    private final HeavenCore main;

    public BaseListeners(HeavenCore main) {
        this.main = main;
        main.registerListener(this);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player p = (Player) e.getPlayer();
        User user = main.getUserManager().getUser(p);
        UserTemp temp = user.getTemp();
        temp.setOpenedInventory(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        User user = main.getUserManager().getUser(player);
        if (user == null)
            return;
        UserTemp temp = user.getTemp();

        temp.setOpenedGUI(null);
        temp.setOpenedInventory(false);

        for(ItemStack item : player.getInventory().getContents())
            if (item != null && item.getType() != Material.AIR)
                if (main.getItemManager().isDummy(item))
                    player.getInventory().removeItem(item);

        for(ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                if (main.getItemManager().isDummy(item)) {
                    if (player.getInventory().getHelmet().isSimilar(item)) {
                        player.getInventory().setHelmet(null);
                    } else if (player.getInventory().getChestplate().isSimilar(item)) {
                        player.getInventory().setChestplate(null);
                    } else if (player.getInventory().getLeggings().isSimilar(item)) {
                        player.getInventory().setLeggings(null);
                    } else if (player.getInventory().getBoots().isSimilar(item)) {
                        player.getInventory().setBoots(null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();

        if (!e.isCancelled()) {
            Item it = e.getItemDrop();
            if (main.getItemManager().isDummy(it.getItemStack()))
                it.remove();
        }
    }

}
