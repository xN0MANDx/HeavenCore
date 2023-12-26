package pl.nomand.heavencore.trades;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.nomand.heavencore.items.ItemManager;

import java.util.Arrays;
import java.util.Collections;

public enum TradeStage {

	PREPARING,
	CONFIRMED,
	ACCEPTED;
	
	public ItemStack generateItemStack() {
		if(this == PREPARING) {
			ItemStack is = new ItemStack(Material.STAINED_GLASS);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§fKliknij, aby potwierdzic twoje przedmioty.");
			im.setLore(Arrays.asList("§7Kliknij ten guzik, kiedy wlozysz do","§7Wymiany wszystko czym chcesz sie wymienic."));
			is.setItemMeta(im);
			return ItemManager.getDummy(is);
		}
		if(this == CONFIRMED) {
			ItemStack is = new ItemStack(Material.STAINED_GLASS, 1, (short) 4);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§6Kliknij, aby potwierdzic wymiane.");
			im.setLore(Arrays.asList("§7Kliknij ten guzik, aby potwierdzic przedmioty","§7ktore chcesz otrzymac w wymianie.","§7Sa to przedmioty po prawej stronie.","§7Jezeli obie strony zaakceptuja obecna oferte","§7Przedmioty zostana przekazane.","§cAdministracja nie odpowiada za przedmioty","§cWymienione przez przypadek lub inne oszustwa."));
			is.setItemMeta(im);
			return ItemManager.getDummy(is);
		}
		
		if(this == ACCEPTED) {
			ItemStack is = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§2Zaakceptowales wymiane.");
			im.setLore(Arrays.asList("§7Musisz poczekac az druga strona wymiany zaakceptuje oferte","§7aby przedmioty zostaly przekazane.","§cAdministracja nie odpowiada za przedmioty","§cWymienione przez przypadek lub inne oszustwa."));
			is.setItemMeta(im);
			return ItemManager.getDummy(is);
		}
		return null;
	}
	
	public ItemStack generateOtherSideItemStack() {
		if(this == PREPARING) {
			ItemStack is = new ItemStack(Material.STAINED_GLASS);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§fDruga strona przygotowywuje przedmioty.");
			is.setItemMeta(im);
			return ItemManager.getDummy(is);
		}
		if(this == CONFIRMED) {
			ItemStack is = new ItemStack(Material.STAINED_GLASS, 1, (short) 4);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§6Druga strona skonczyla swoja oferte");
			im.setLore(Arrays.asList("§7Przejrzyj dokladnie przedmioty przygotowane po tej stronie","§7upewnij sie, ze za to chcesz sie wymienic!"));
			is.setItemMeta(im);
			return ItemManager.getDummy(is);
		}
		
		if(this == ACCEPTED) {
			ItemStack is = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§2Druga strona skonczyla zaakceptowala oferte");
			im.setLore(Collections.singletonList("§7Teraz twoja kolej."));
			is.setItemMeta(im);
			return ItemManager.getDummy(is);
		}
		return null;
	}
	
}
