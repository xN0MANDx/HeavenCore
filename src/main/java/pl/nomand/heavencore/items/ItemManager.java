package pl.nomand.heavencore.items;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.plugin.NBTAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private final List<ItemStack> cache = new ArrayList<>();

    public ItemManager() {
        ItemStack item;
        ItemMeta meta;

        item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        cache.add(setDummy(item, true));

        item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        cache.add(setDummy(item, true));

        item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
        meta = item.getItemMeta();
        meta.setDisplayName("§6Wybierz Zwierzatko");
        item.setItemMeta(meta);
        cache.add(setDummy(item, true));
    }

    public boolean isDummy(ItemStack item) {
        NBTItem ni = new NBTItem(item);
        return ni.hasKey("dummy");
    }

    public ItemStack setDummy(ItemStack item, boolean bool) {
        NBTItem ni = new NBTItem(item);
        if (bool) {
            ni.setBoolean("dummy", true);
        } else {
            if (ni.hasKey("dummy"))
                ni.removeKey("dummy");
        }

        return ni.getItem();
    }

    public ItemStack getItemByIndex(int index) {
        return cache.get(index);
    }

}
