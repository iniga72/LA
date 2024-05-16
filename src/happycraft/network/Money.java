package happycraft.network;

import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;

public class Money implements CommandExecutor {
    public static void givemoney(String player, double count) throws SQLException {
        float money = Database.getMoney(player.toLowerCase());
        money+=count;
        Database.updateMoney(player.toLowerCase(), money);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration c = Network.c;
        if(!(sender instanceof Player)) {
            if(sender.getName().equals("Rcon") || sender.getName().equals("CONSOLE")){
                Player pl = Bukkit.getPlayer(args[1]);

                try {
                    float amount = Float.parseFloat(args[2]);
                    float money = Database.getMoney(args[1].toLowerCase());
                    money+=amount;
                    Database.updateMoney(args[1].toLowerCase(), money);
                    sender.sendMessage(c.getString("money.send").replace("&", "§"));
                    if(pl != null){
                        pl.sendMessage(c.getString("money.got").replace("$money", amount + "").replace("&", "§"));
                        return true;
                    }
                     return true;
                }catch (Exception ignored){
                }
                return true;
            }
            return true;
        }
        Player p = (Player) sender;


        if(args.length == 0){


            String money = Database.getMoney(p.getName().toLowerCase()) + "";

            p.sendMessage(c.getString("money.balance").replace("$balanse", money).replace("&", "§"));
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("top")){
            ArrayList<BalTOP> top= Database.getbaltop(0);
            p.sendMessage("");
            p.sendMessage(c.getString("money.topmain").replace("&", "§"));
            String msg = c.getString("money.topuser");
            for(BalTOP bt : top){
                p.sendMessage( msg
                        .replace("$user", bt.getUser())
                        .replace("$balanse", bt.getBal() + "")
                        .replace("&", "§")
                );

            }
            return true;
        }
        if(args.length == 1){
            String money = Database.getMoney(args[0].toLowerCase()) + "" ;
            p.sendMessage(c.getString("money.balance").replace("$balanse", money).replace("&", "§"));
            return true;
        }

        if(args.length == 3 && args[0].equalsIgnoreCase("pay")){
            Player pl = Bukkit.getPlayer(args[1]);
            if(pl == null){
                p.sendMessage(c.getString("money.online").replace("&", "§"));
                return true;
            }
            try {
                float amount = Float.parseFloat(args[2]);
                amount = Precision.round(amount, 3);;
                float money = Database.getMoney(p.getName().toLowerCase());
                if(amount > money || amount < 0 || amount > 2000){
                    p.sendMessage(c.getString("money.enough").replace("&", "§"));
                    return true;
                }
                money-=amount;
                Database.updateMoney(p.getName().toLowerCase(), money);
                money = Database.getMoney(args[1].toLowerCase());
                money+=amount;
                Database.updateMoney(args[1].toLowerCase(), money);
                p.sendMessage(c.getString("money.send").replace("&", "§"));
                pl.sendMessage(c.getString("money.got").replace("$money", amount + "").replace("&", "§"));
                return true;
            }catch (Exception e){
                p.sendMessage(c.getString("money.help").replace("&", "§"));

                return true;
            }
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("give") && Database.isOwner(sender.getName())){

            Player pl = Bukkit.getPlayer(args[1]);
            if(pl == null){
                p.sendMessage(c.getString("money.online").replace("&", "§"));
                return true;
            }
            try {
                float amount = Float.parseFloat(args[2]);
                float money = Database.getMoney(args[1].toLowerCase());
                money+=amount;
                Database.updateMoney(args[1].toLowerCase(), money);
                p.sendMessage(c.getString("money.send").replace("&", "§"));
                pl.sendMessage(c.getString("money.got").replace("$money", amount + "").replace("&", "§"));
                return true;
            }catch (Exception e){
            }
        }
        p.sendMessage(c.getString("money.help").replace("&", "§"));

        return true;
    }

}
