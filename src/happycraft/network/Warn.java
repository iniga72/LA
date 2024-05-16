package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Warn  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("network.warn")) {
                p.sendMessage(Network.c.getString("warn.perm").replace("&", "§"));
                return true;
            }
        }
        if(args.length == 0) {
            md.help(sender, "warn");
            return true;
        }

        int warn = Database.getWarn(args[0]);
        warn++;
        String reason = "";
        if(args.length == 1) reason = Network.c.getString("bans.messages.no_reason");
        for(int i = 1; i < args.length; i++) {
            reason += args[i] + " ";
        }
        try {
            Database.updateWarn(args[0].toLowerCase(), reason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        md.sendServer(sender.getName(),args[0], "warn", "permanent", reason);
        if(Network.c.getString("warn.warns." + warn) != null)Network.push(Network.c.getString("warn.warns." + warn).replace("$player", args[0]));
        return false;
    }
}
