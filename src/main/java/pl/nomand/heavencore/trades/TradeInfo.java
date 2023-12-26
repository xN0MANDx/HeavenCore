package pl.nomand.heavencore.trades;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nomand.heavencore.common.HeavenCore;
import pl.nomand.heavencore.items.ItemManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pl.nomand.heavencore.trades.TradeStage.*;

public class TradeInfo implements Listener {

    private static final Integer[] slotsLeft = {0, 1, 2,    //     6, 7, 8,
            9, 10, 11, 12, //  14,15,16,17,
            18, 19, 20, 21, //  23,24,25,26,
            27, 28, 29, 30, //  32,33,34,35,
            36, 37, 38, 39, //  41,42,43,44,
            45, 46, 47, 48};//  50,51,52

    private static final Integer[] slotsRight = {6, 7, 8,
            14, 15, 16, 17,
            23, 24, 25, 26,
            32, 33, 34, 35,
            41, 42, 43, 44,
            50, 51, 52, 53};

    private final Player trader, who;
    private List<ItemStack> offer1 /*Trader Items*/, offer2 /*Who Items*/;

    private TradeStage traderStage, whoStage;

    public TradeInfo(HeavenCore main, Player player1, Player player2) {
        trader = player1;
        who = player2;

        offer1 = new ArrayList<>();
        offer2 = new ArrayList<>();

        traderStage = PREPARING;
        whoStage = PREPARING;

        Inventory invWho = Bukkit.createInventory(null, 54, "§8Wymiana z §2" + trader.getName());

        Inventory invTrader = Bukkit.createInventory(null, 54, "§8Wymiana z §2" + who.getName());

        { // IRON BARS

            { // TRADER
                ItemStack is = new ItemStack(Material.IRON_FENCE);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("§4");
                im.setLore(Arrays.asList(
                        centerText("§7Ty", 20),
                        centerText("§2<§m----§r", 20),
                        centerText("§7 " + who.getName(), 20),
                        centerText("§2§m----§2>", 20)
                ));
                is.setItemMeta(im);
                is = ItemManager.getDummy(is);

                for (int i = 4; i < 54; i += 9) {
                    invTrader.setItem(i, is);
                }
            }

            { // WHO
                ItemStack is = new ItemStack(Material.IRON_FENCE);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("§4");
                im.setLore(Arrays.asList(
                        centerText("§7Ty", 20),
                        centerText("§2<§m----§r", 20),
                        centerText("§7 " + trader.getName(), 20),
                        centerText("§2§m----§2>", 20)
                ));
                is.setItemMeta(im);
                is = ItemManager.getDummy(is);

                for (int i = 4; i < 54; i += 9) {
                    invWho.setItem(i, is);
                }
            }

        }

        { // WYCZYSC

            {

                ItemStack is = new ItemStack(Material.LAVA_BUCKET);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("§cWyczysc Przedmioty");
                im.setLore(Collections.singletonList("§7Powroca one do twojego ekwipunku."));
                is.setItemMeta(im);

                invWho.setItem(49, ItemManager.getDummy(is));
                invTrader.setItem(49, ItemManager.getDummy(is));
            }

        }

        { // BUTTONS

            { // TRADER

                invTrader.setItem(3, traderStage.generateItemStack());
                invTrader.setItem(5, whoStage.generateOtherSideItemStack());

            }

            { // WHO

                invWho.setItem(3, whoStage.generateItemStack());
                invWho.setItem(5, traderStage.generateOtherSideItemStack());

            }

        }

        who.openInventory(invWho);
        trader.openInventory(invTrader);

        who.sendMessage("§7Rozpoczeto wymiane z §a" + trader.getName() + "§8!");
        trader.sendMessage("§7Rozpoczeto wymiane z §a" + who.getName() + "§8!");

        trader.playSound(who.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
        who.playSound(trader.getLocation(), Sound.ORB_PICKUP, 1f, 1f);

        main.registerListener(this);
    }

    public void failTrade() {

        HandlerList.unregisterAll(this);

        who.closeInventory();

        trader.closeInventory();

        for (ItemStack is : offer1) trader.getInventory().addItem(is);
        for (ItemStack is : offer2) who.getInventory().addItem(is);

        offer1 = new ArrayList<>();
        offer2 = new ArrayList<>();

    }

    public void refreshItems() {

        int i = 0;

        for (int l : slotsLeft) {
            trader.getOpenInventory().getTopInventory().setItem(l, null);
            who.getOpenInventory().getTopInventory().setItem(l, null);
        }
        for (int l : slotsRight) {
            trader.getOpenInventory().getTopInventory().setItem(l, null);
            who.getOpenInventory().getTopInventory().setItem(l, null);
        }

        for (ItemStack is : offer1) {

            if (is == null) continue;
            if (is.getType() == Material.AIR) continue;

            ItemStack nowy = new ItemStack(is);

            nowy = ItemManager.getDummy(nowy);

            trader.getOpenInventory().getTopInventory().setItem(slotsLeft[i], nowy);
            who.getOpenInventory().getTopInventory().setItem(slotsRight[i], nowy);
            i++;
        }

        i = 0;

        for (ItemStack is : offer2) {

            if (is == null) continue;
            if (is.getType() == Material.AIR) continue;

            ItemStack nowy = new ItemStack(is);

            nowy = ItemManager.getDummy(nowy);

            who.getOpenInventory().getTopInventory().setItem(slotsLeft[i], nowy);
            trader.getOpenInventory().getTopInventory().setItem(slotsRight[i], nowy);
            i++;
        }


        trader.getOpenInventory().getTopInventory().setItem(3, traderStage.generateItemStack());
        trader.getOpenInventory().getTopInventory().setItem(5, whoStage.generateOtherSideItemStack());


        who.getOpenInventory().getTopInventory().setItem(3, whoStage.generateItemStack());
        who.getOpenInventory().getTopInventory().setItem(5, traderStage.generateOtherSideItemStack());


    }

    public void endTrade() {
        HandlerList.unregisterAll(this);

        for (ItemStack is : offer2) trader.getInventory().addItem(is);
        for (ItemStack is : offer1) who.getInventory().addItem(is);

        trader.closeInventory();
        who.closeInventory();

        who.sendMessage("§7Zakonczono wymiane z §a" + trader.getName() + "§8!");
        trader.sendMessage("§7Zakonczono wymiane z §a" + who.getName() + "§8!");

        who.playSound(trader.getLocation(), Sound.LEVEL_UP, 1f, 1f);
        trader.playSound(who.getLocation(), Sound.LEVEL_UP, 1f, 1f);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getWhoClicked() == trader || e.getWhoClicked() == who) {

            e.setCancelled(true);
            e.setResult(Result.DENY);

            if (e.getWhoClicked() == trader) {

                if (e.getClickedInventory() != null) {

                    if (!e.getClickedInventory().getTitle().equals("§8Wymiana z §2" + who.getName())) {

                        int slot = e.getSlot();
                        ItemStack is = e.getClickedInventory().getItem(slot);

                        if (is == null) return;
                        if (is.getType() == Material.AIR) return;

                        if (offer1.size() == 23) {
                            return;
                        } else {
                            offer1.add(is);
                        }

                        e.setCurrentItem(null);
                        e.setCursor(null);
                        traderStage = PREPARING;
                        whoStage = PREPARING;
                        refreshItems();

                    } else {

                        int slot = e.getSlot();
                        int i = -3;
                        for (int h = 0; h < slotsLeft.length; h++) {
                            if (slot == slotsLeft[h]) {
                                i = h;
                                break;
                            }
                        }

                        if (i == -3) { // NIE KLIKNAL W SLOT Z ITEMEM

                            if (slot == 3) {

                                if (traderStage == PREPARING) {

                                    traderStage = CONFIRMED;
                                    trader.playSound(trader.getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 0.5f);

                                } else if (traderStage == CONFIRMED && whoStage != PREPARING) {

                                    traderStage = ACCEPTED;
                                    trader.playSound(trader.getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 2f);

                                }

                                if (traderStage == ACCEPTED && whoStage == ACCEPTED) {

                                    endTrade();

                                } else refreshItems();

                            }

                            if (slot == 49) {

                                for (ItemStack is : offer1) e.getWhoClicked().getInventory().addItem(is);
                                offer1.clear();
                                traderStage = PREPARING;
                                whoStage = PREPARING;
                                refreshItems();

                            }

                        } else { // KLIKNAL W SLOT Z ITEMEM

                            if (i < offer1.size()) {// NIE KLIKNAL W POWIETRZE

                                e.getWhoClicked().getInventory().addItem(offer1.get(i));
                                offer1.remove(i);
                                traderStage = PREPARING;
                                whoStage = PREPARING;
                                refreshItems();
                                trader.playSound(trader.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);


                            }

                        }


                    }

                }

            } else {
                if (e.getClickedInventory() != null) {

                    if (!e.getClickedInventory().getTitle().equals("§8Wymiana z §2" + trader.getName())) {

                        int slot = e.getSlot();
                        ItemStack is = e.getClickedInventory().getItem(slot);

                        if (is == null) return;
                        if (is.getType() == Material.AIR) return;

                        if (offer2.size() == 22) {
                            return;
                        } else {
                            offer2.add(is);
                        }

                        e.setCurrentItem(null);
                        e.setCursor(null);
                        traderStage = PREPARING;
                        whoStage = PREPARING;
                        refreshItems();

                    } else {

                        int slot = e.getSlot();
                        int i = -3;
                        for (int h = 0; h < slotsLeft.length; h++) {
                            if (slot == slotsLeft[h]) {
                                i = h;
                                break;
                            }
                        }

                        if (i == -3) { // NIE KLIKNAL W SLOT Z ITEMEM

                            if (slot == 49) {

                                for (ItemStack is : offer2) e.getWhoClicked().getInventory().addItem(is);
                                offer2.clear();
                                traderStage = PREPARING;
                                whoStage = PREPARING;
                                refreshItems();

                            }

                            if (slot == 3) {
                                if (whoStage == PREPARING) {

                                    whoStage = CONFIRMED;
                                    who.playSound(who.getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 0.5f);

                                } else if (whoStage == CONFIRMED && traderStage != PREPARING) {

                                    whoStage = ACCEPTED;
                                    who.playSound(who.getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 2f);

                                }

                                if (whoStage == ACCEPTED && traderStage == ACCEPTED) {

                                    endTrade();

                                } else refreshItems();
                            }

                        } else { // KLIKNAL W SLOT Z ITEMEM

                            if (i < offer2.size()) {// NIE KLIKNAL W POWIETRZE

                                e.getWhoClicked().getInventory().addItem(offer2.get(i));
                                offer2.remove(i);
                                traderStage = PREPARING;
                                whoStage = PREPARING;
                                refreshItems();
                                who.playSound(trader.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);


                            }

                        }


                    }

                }
            }

        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        if (e.getPlayer() == trader || e.getPlayer() == who) {

            if (e.getPlayer() == trader) {

                e.getPlayer().sendMessage("§7Przerwales wymiane z §2" + who.getName());
                who.sendMessage("§7Gracz §2" + e.getPlayer().getName() + "§7 przerwal oferte wymiany!");

            } else {

                e.getPlayer().sendMessage("§7Przerwales wymiane z §2" + trader.getName());
                trader.sendMessage("§7Gracz §2" + e.getPlayer().getName() + "§7 przerwal oferte wymiany!");

            }

            failTrade();

        }

    }

    public static String centerText(String text, int lineLength) {
        StringBuilder builder = new StringBuilder(text);
        char space = ' ';
        int distance = (lineLength - ChatColor.stripColor(text).length()) / 2;
        for (int i = 0; i < distance; ++i) {
            builder.insert(0, space);
            builder.append(space);
        }
        return builder.toString();
    }

}
