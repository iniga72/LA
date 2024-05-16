package happycraft.network;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reputation implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player))return  true;
        Player p = (Player) sender;
        if(!Database.isModerator(p.getName())){
            p.sendMessage("§c§lАдминистрация §6>§fВы не являетесь администратором");
            return true;
        }
        if(args.length != 2) {
            md.help(sender, "reputatuin");
            return true;
        }
        if(!args[1].equalsIgnoreCase( "+") && !args[1].equalsIgnoreCase( "-")) {
            md.help(sender, "reputatuin");
            return true;
        }
        Database.addReputation(args[0], args[1], (float) 1.0);

        sender.sendMessage(Network.c.getString("reputation.good").replace("&", "§"));
        return false;
    }
}
