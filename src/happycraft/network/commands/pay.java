package happycraft.network.commands;

import happycraft.network.Database;
import happycraft.network.Network;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class pay implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        FileConfiguration c = Network.c;
        Player p = (Player) sender;
        if(args.length != 2){
            p.sendMessage(c.getString("money.help").replace("&", "§"));
            return true;
        }
        OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);

        if(!pl.isOnline()){
            p.sendMessage(c.getString("money.online").replace("&", "§"));
            return true;
        }
        try {
            float amount = Float.parseFloat(args[1]);
            amount = Precision.round(amount, 3);;
            float money = Database.getMoney(p.getName().toLowerCase());
            if(amount > money || amount < 0 || amount > 2000){
                p.sendMessage(c.getString("money.enough").replace("&", "§"));
                return true;
            }
            money-=amount;
            Database.updateMoney(p.getName().toLowerCase(), money);
            money = Database.getMoney(args[0].toLowerCase());
            money+=amount;
            Database.updateMoney(args[0].toLowerCase(), money);
            p.sendMessage(c.getString("money.send").replace("&", "§"));
            pl.getPlayer().sendMessage(c.getString("money.got").replace("$money", amount + "").replace("&", "§"));
            return true;
        }catch (Exception e){
            p.sendMessage(c.getString("money.help").replace("&", "§"));
            return true;
        }
    }
}
