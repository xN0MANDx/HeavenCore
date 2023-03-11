package pl.nomand.heavencore.pets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.nomand.heavencore.common.HeavenCore;
import pl.nomand.heavencore.users.User;

public class PetCommands implements CommandExecutor {

    private final HeavenCore main;

    public PetCommands(HeavenCore main) {
        this.main = main;
        main.getCommand("h-zoolog").setExecutor(this);
        main.getCommand("zoolog").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName();

        if (cmd.equalsIgnoreCase("h-zoolog")) {
            if (!sender.hasPermission("Admin")) {
                sender.sendMessage("§cNie posiadasz uprawnien!");
                return true;
            }

            if (args.length > 1) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("addExp")) {
                    long value = Long.parseLong(args[1]);
                    User user = main.getUserManager().getUser(player);
                    Pet pet = user.getPet();
                    if (pet != null) {
                        pet.getExperience().addExp(value);
                        player.sendMessage("§aDodano "+value+" expa do peta!");
                    } else {
                        player.sendMessage("§cNie posiadasz zadnego zwierzaka!");
                    }
                } else if (args[0].equalsIgnoreCase("getPet")) {
                    PetManager petManager = main.getPetManager();
                    PetTemplate template = petManager.getTemplate(args[1]);
                    if (template != null) {
                        Pet pet = new Pet(player, template, petManager);
                        player.getInventory().addItem(pet.toItem());
                        player.sendMessage("§aOtrzymano template zwierzaka jako przedmiot!");
                    } else {
                        sender.sendMessage("§cPodany template nie istnieje!");
                    }
                }
            } else {
                sender.sendMessage("§7/h-zoolog addExp <exp>");
                sender.sendMessage("§7/h-zoolog getPet <templateName>");
            }
        } else if (cmd.equalsIgnoreCase("zoolog")) {
            Player player = (Player) sender;
            main.getPetManager().getGuiManager().open(player);
        }

        return false;
    }

}
