package happycraft.network.commands;

import happycraft.network.Database;
import happycraft.network.Network;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class prefix implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("Ты не игрок");
            return true;
        }
        Player p = (Player) commandSender;


        float money = Database.getMoney(p.getName().toLowerCase());
        if(!Database.isFreePrefix(p.getName())){
            p.sendMessage(Network.c.getString("prefix.price"));

            if(10 > money){
                p.sendMessage(Network.c.getString("money.enough").replace("&", "§"));
                return true;
            }

            if(args.length != 2 || (!args[0].equalsIgnoreCase("chat") && !args[0].equalsIgnoreCase("tab"))){
                for (String s : Network.c.getStringList("prefix.msg")){
                    p.sendMessage(s);
                }
                return true;
            }
            try {
                Database.setFreePrefix(p.getName());
                money-=10;
                Database.updateMoney(p.getName().toLowerCase(), money);
            } catch (SQLException e) {
            }
        }else p.sendMessage(Network.c.getString("prefix.noprice"));

        if(args.length != 2 || (!args[0].equalsIgnoreCase("chat") && !args[0].equalsIgnoreCase("tab"))){
            for (String s : Network.c.getStringList("prefix.msg")){
                p.sendMessage(s);
            }
            return true;
        }


        if(args[0].equalsIgnoreCase("chat")){
            Network.push("pex user " + p.getName() + " prefix \"" + args[1] + " \"");
            p.sendMessage(Network.c.getString("prefix.good"));
            return true;
        }
        if(args[0].equalsIgnoreCase("tab")){
            Network.push("tab player " + p.getName() + " tabprefix " + args[1] + " &7");
            p.sendMessage(Network.c.getString("prefix.good"));
            return true;
        }
        return false;
    }
}
