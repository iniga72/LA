package happycraft.network.commands;

import happycraft.network.Database;
import happycraft.network.Network;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class commandspy implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;
        if(!p.hasPermission("network.cspy")){
            p.sendMessage(Network.c.getString("cspy.perm").replace("&", "§"));
            return true;
        }
        if(Database.isCommandSpy(p.getName())){
            try {
                //
                Database.setCspy(p.getName(), false);
                p.sendMessage(Network.c.getString("cspy.enable").replace("&", "§"));
            } catch (SQLException ignored) {

            }
            return true;
        }
        try {
            Database.setCspy(p.getName(), true);
            p.sendMessage(Network.c.getString("cspy.disable").replace("&", "§"));
        } catch (SQLException ignored) {

        }
        return true;
    }
}
