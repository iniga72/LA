package happycraft.network;

import happycraft.network.params.items;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Inv implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            if(args.length == 0){
                ArrayList<items> params = Database.getAllItems(p.getName().toLowerCase());
                for(happycraft.network.params.items s : params){
                    String time = "§cНАВСЕГДА";
                    if(s.getTime() >0)time = "§e" + s.getTime() + " ч.";
                    p.spigot().sendMessage(new TextComponent(ComponentSerializer.parse("[\"\"," +
                            "{" +
                            "\"text\":\"§7[§aИспользовать§7]\"," +
                            "\"clickEvent\":{" +
                            "\"action\":\"run_command\"," +
                            "\"value\":\"/inv "+s.getId()+"\"" +
                            "}" +
                            "}," +
                            "{" +
                            "\"text\":\" §7[§cПродать§7] \"," +
                            "\"clickEvent\":{" +
                            "\"action\":\"run_command\"," +
                            "\"value\":\"/sel "+s.getId()+"\"" +
                            "}"+
                            "}," +
                            "{" +
                            "\"text\":\""+Network.c.getString("case.items." + s.getItem() + ".name").replace("&", "§")+" §f("+time+"§f)\"" +
                            "}" +
                            "]")));
                }
                return true;
            }
            String id = args[0];
            if(!Database.getItembyId(p.getName(),id)){
                p.sendMessage(Network.c.getString("case.inventory.noitem").replace("&", "§"));
                return true;
            }
            try {
                int d = Integer.parseInt(id);
                happycraft.network.params.items item = Database.getItem(id);
                Database.removeItem(d);
                if(item.getTime() == 0){
                    Network.push("pex user " +p.getName()+ " group set " + item.getItem());
                }else {
                    Network.push("pex user " +p.getName()+ " group add " + item.getItem() + " world " + (item.getTime() * 8600));
                }
                p.sendMessage(Network.c.getString("case.inventory.good").replace("&", "§"));
            }catch (Exception ignored){
                p.sendMessage(Network.c.getString("case.inventory.noitem").replace("&", "§"));
                return true;
            }

        }
        return true;
    }
}
