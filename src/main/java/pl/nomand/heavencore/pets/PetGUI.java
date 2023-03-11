package pl.nomand.heavencore.pets;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.nomand.heavencore.common.HeavenCore;
import pl.nomand.heavencore.users.GUI;
import pl.nomand.heavencore.users.User;
import pl.nomand.heavencore.users.UserTemp;

import java.util.ArrayList;
import java.util.List;

public class PetGUI implements Listener {

    private final HeavenCore main;

    public PetGUI(HeavenCore main) {
        this.main = main;
        main.registerListener(this);
    }

    public void open(Player player) {
        User user = main.getUserManager().getUser(player);
        Pet pet = user.getPet();

        Inventory inv = Bukkit.createInventory(null, 9, "§lZarzadzanie Zwierzakiem");

        ItemStack item;

        item = main.getItemManager().getItemByIndex(0);
        for(int i=1; i<8; i++)
            inv.setItem(i, item);

        item = main.getItemManager().getItemByIndex(1);
        inv.setItem(0, item);
        inv.setItem(8, item);

        if (pet != null) {
            item = pet.toItem();
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            List<String> lore = new ArrayList<>(meta.getLore());
            lore.add("");
            lore.add("§aKliknij, aby schowac zwierzaka.");
            meta.setLore(lore);
            item.setItemMeta(meta);

            NBTItem ni = new NBTItem(item);
            ni.setBoolean("dummy", true);

            inv.setItem(4, ni.getItem());
        } else {
            inv.setItem(4, main.getItemManager().getItemByIndex(2));
        }

        player.openInventory(inv);
        user.getTemp().setOpenedGUI(GUI.PET);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        User user = main.getUserManager().getUser(p);
        UserTemp temp = user.getTemp();
        if (temp.getOpenedGUI() == GUI.PET) {
            if (e.getClickedInventory() == p.getOpenInventory().getTopInventory()) {
                e.setCancelled(true);
                if (e.getRawSlot() == 4 && e.getCurrentItem().getType() == Material.SKULL_ITEM) {
                    Pet pet = user.getPet();
                    pet.despawn();
                    p.getInventory().addItem(pet.toItem());
                    user.setPet(null);
                    p.playSound(p.getLocation(), Sound.CLICK, 1f, 1f);
                    e.getClickedInventory().setItem(4, main.getItemManager().getItemByIndex(2));
                }
            } else if (e.getClickedInventory() == p.getOpenInventory().getBottomInventory()) {
                e.setCancelled(true);
                ItemStack clickedItem = e.getCurrentItem();
                ItemStack clickedItemCopy = e.getCurrentItem().clone();
                if (clickedItem != null && clickedItem.getType() == Material.SKULL_ITEM) {
                    NBTItem ni = new NBTItem(clickedItem);
                    if (ni.hasKey("pet")) {

                        if (clickedItem.getAmount() > 1) {
                            clickedItem.setAmount(clickedItem.getAmount() - 1);
                        } else {
                            e.getClickedInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
                        }

                        Pet oldPet = user.getPet();
                        if (oldPet != null) {
                            ItemStack oldPetItem = user.getPet().toItem();
                            p.getInventory().addItem(oldPetItem);
                            oldPet.despawn();
                        }

                        Pet newPet = new Pet(p, clickedItemCopy, main.getPetManager());
                        newPet.spawn();
                        user.setPet(newPet);
                        p.playSound(p.getLocation(), Sound.CLICK, 1f, 1f);
                        p.getOpenInventory().getTopInventory().setItem(4, newPet.toItem());
                    }
                }
            }
        }
    }

}
