package happycraft.network;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PromoCode  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player){
            if(args.length != 1){
                commandSender.sendMessage( Network.c.getString("promo.help").replace("&", "§"));
                return true;
            }
            if(Database.getPromoId(args[0]) == 0){
                commandSender.sendMessage(Network.c.getString("promo.havent").replace("&", "§"));
                return true;
            }
            if(Database.getPromoCount(args[0]) >= Database.getPromoMax(args[0])){
                commandSender.sendMessage(Network.c.getString("promo.limite").replace("&", "§"));
                return true;
            }
            if(Database.getUserPromoId(args[0], commandSender.getName().toLowerCase()) > 0){
                commandSender.sendMessage(Network.c.getString("promo.implements").replace("&", "§"));
                return true;
            }
            int money = Database.getPromoMoney(args[0]);
            Network.push("money give " + commandSender.getName() + " " + money);
            commandSender.sendMessage(Network.c.getString("promo.have").replace("&", "§"));
            Database.addPromoCode(commandSender.getName(), args[0]);
            //selchat
        }


        return false;
    }
}
