package pl.nomand.heavencore.common;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

    private final HeavenCore main;

    public Commands(HeavenCore main) {
        this.main = main;
        main.getCommand("h-reload").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("h-reload")) {
            if (!sender.hasPermission("Admin")) {
                sender.sendMessage("§cNie posiadasz uprawnien!");
                return true;
            }

            sender.sendMessage("§7[§bHeavenCore§7] §fPrzeladowywanie...");
            main.reload();
            sender.sendMessage("§7[§bHeavenCore§7] §fPrzeladowano §bpomyslnie§f.");
        }

        return false;
    }
}
