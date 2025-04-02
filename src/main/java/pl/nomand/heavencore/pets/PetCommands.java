package pl.nomand.heavencore.pets;

import org.bukkit.Bukkit;
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
        main.getCommand("hpc").setExecutor(this);
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
                if (args[0].equalsIgnoreCase("addExp")) {
                    Player player = (Player) sender;
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
                    if (template != null && !template.getId().equals("unknown")) {
                        if (args.length > 2) {
                            Player target = Bukkit.getPlayer(args[2]);
                            if (target != null) {
                                Pet pet = new Pet(target, template, petManager);
                                target.getInventory().addItem(pet.toItem());
                                sender.sendMessage("§aNadano template zwierzaka jako przedmiot dla "+target.getName()+"!");
                            } else {
                                sender.sendMessage("§cPodany gracz nie istnieje!");
                            }
                        } else {
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                Pet pet = new Pet(player, template, petManager);
                                player.getInventory().addItem(pet.toItem());
                                player.sendMessage("§aOtrzymano template zwierzaka jako przedmiot!");
                            } else {
                                sender.sendMessage("§cNie mozesz nadac peta konsoli!");
                            }
                        }
                    } else {
                        sender.sendMessage("§cPodany template nie istnieje!");
                    }
                }
            } else {
                sender.sendMessage("§7/h-zoolog addExp <exp>");
                sender.sendMessage("§7/h-zoolog getPet <templateName> (opcjonalnie: <nazwaGracza>");
            }
        } else if (cmd.equalsIgnoreCase("zoolog")) {
            Player player = (Player) sender;
            main.getPetManager().getGuiManager().open(player);
        } else if (cmd.equalsIgnoreCase("hpc")) {

            if (sender instanceof Player) {
                sender.sendMessage("Tylko konsola moze uzywac tej komendy!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage("/hpc <nick> <exp_dla_peta>");
                return true;
            }

            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("Nie mozna nadac expa dla peta! Gracz jest offline!");
                return true;
            }

            long exp;

            try {
                exp = Long.parseLong(args[1]);
            } catch (Exception e) {
                sender.sendMessage("Nie mozna nadac expa dla peta! Podano bledny exp: "+args[1]);
                return true;
            }

            if (exp <= 0) {
                sender.sendMessage("Exp dla peta nie moze byc 0 ani ujemny!");
                return true;
            }

            User user = main.getUserManager().getUser(player);
            Pet pet = user.getPet();
            if (pet == null)
                return true;

            pet.getExperience().addExp(exp);
        }

        return false;
    }

}
