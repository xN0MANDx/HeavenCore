package pl.nomand.heavencore.trades;

import ch.njol.skript.variables.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nomand.heavencore.common.HeavenCore;

import java.util.HashMap;
import java.util.Map;

public class Trade implements Listener{

	private final HeavenCore main;

	public Map<Player, Player> offerSent = new HashMap<>();
	public Map<Player, Boolean> cA = new HashMap<>();

	public Trade(HeavenCore main) {
		this.main = main;
		main.registerListener(this);
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent e) {
		
		if(e.getRightClicked() != null) {
			
			if(e.getRightClicked() instanceof Player) {

				if (e.getRightClicked().hasMetadata("NPC"))
					return;

				// Weryfikacja kodu
				String a1 = "kod::gracz::" + e.getPlayer().getName().toLowerCase();
				Boolean result1 = (Boolean) Variables.getVariable(a1, null, false);
				if (result1 == null || !result1) {
					e.getPlayer().sendMessage("§7[§e§lHC§7] §8» §cTwoje konto nie ma wpisanego kodu! /hc [kod]");
					e.getPlayer().sendMessage("§7[§e§lHC§7] §8» §cJezeli nie masz jeszcze kodu wpisz /ustawhc [kod]");
					return;
				}

				String a2 = "kod::gracz::" + e.getRightClicked().getName().toLowerCase();
				Boolean result2 = (Boolean) Variables.getVariable(a2, null, false);
				if (result2 == null || !result2) {
					e.getPlayer().sendMessage("§7[§e§lHC§7] §8» §cTen gracz nie ma wpisanego kodu! /hc [kod]");
					return;
				}
				// Koniec Weryfikacji
				
				if(cA.containsKey(e.getPlayer())) return;
				cA.put(e.getPlayer(), true);
				new BukkitRunnable() {
					public void run() {
						cA.remove(e.getPlayer());
					}
                }.runTaskLater(main, 20);
				
				Player trader = e.getPlayer();
				Player who = (Player) e.getRightClicked();
				
				if(who.getName().contains("§")) return;
				
				if(trader.isSneaking()) {
					
					if(offerSent.containsKey(trader) && offerSent.get(who) != trader) { // CHCE WYSLAC TRADE PO TYM JAK KOGOS JUZ SPYTAL O NIEGO
						
						trader.sendMessage("§6§lWYMIANA §8»§7 Poczekaj chwile, zanim wyslesz oferte wymiany kolejnej osobie.");
						
					} else if (offerSent.get(who) == trader) {
						
						offerSent.remove(who);
						offerSent.remove(trader);
						
						new TradeInfo(main, who, trader);
						
						
					} else if (!offerSent.containsKey(trader)) {
						
						offerSent.put(trader, who);
						trader.sendMessage("§6§lWYMIANA §8»§7 Wyslano oferte wymiany do §e"+who.getName()+"§7!§7 Wygasnie za §65 §esekund§7!");
						who.sendMessage("§6§lWYMIANA §8»§7 Otrzymales oferte wymiany od gracza §e"+trader.getName()+"§7!");
						
						new BukkitRunnable() {
							public void run() {
								if(offerSent.get(trader) == who) {
									offerSent.remove(trader);
									trader.sendMessage("§6§lWYMIANA §8»§7 Twoje zaproszenie do gracza §e"+who.getName()+"§7 wygaslo§7!");
								}
                            }
						}.runTaskLater(main, 100);
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
