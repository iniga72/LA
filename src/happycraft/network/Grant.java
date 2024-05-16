package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Grant  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player){
            Player p = (Player) commandSender;
            if(!p.hasPermission("grant.give")){
                commandSender.sendMessage(Network.c.getString("grant.prefix").replace("&", "§") +Network.c.getString("grant.perm").replace("&", "§"));
                return true;
            }
            if(args.length != 2){
                for(String ss : Network.c.getStringList("grant.help")){
                    commandSender.sendMessage(ss.replace("&", "§"));
                }
                return true;
            }

            /*if(args.length == 1){
                if(Network.selchat.containsKey(args[0])){
                    SelParams sp = Network.selchat.get(args[0]);
                    double money = Database.getMoney(commandSender.getName().toLowerCase());
                    if(sp.getPrice() > money){
                        commandSender.sendMessage(Network.c.getString("sel.title").replace("&", "§") + Network.c.getString("sel.nomoney").replace("&", "§"));
                        return true;
                    }
                    int don = Database.getItemId(sp.getPlayer().toLowerCase(), sp.getDonate());
                    if(don == 0){
                        commandSender.sendMessage(Network.c.getString("sel.title").replace("&", "§") +Network.c.getString("sel.sell").replace("&", "§"));
                        return true;
                    }
                    String name = Network.c.getString("case.items." + sp.getDonate() + ".name").replace("&", "§");
                    String msg = Network.c.getString("sel.title").replace("&", "§") +Network.c.getString("sel.broadcast").replace("$player", commandSender.getName()).replace("$donate", name).replace("$price", sp.getPrice() + "").replace("&", "§");

                    Bukkit.broadcastMessage(msg.replace("&", "§"));
                    Network.push("money give "+sp.getPlayer()+" " + sp.getPrice());
                    try {
                        Database.removeItem(don);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Database.setInventoryItem(commandSender.getName(), sp.getDonate());

                    Network.selchat.remove(args[0]);

                    money = Database.getMoney(commandSender.getName().toLowerCase());
                    money-=sp.price;
                    try {
                        Database.updateMoney(commandSender.getName().toLowerCase(), money);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    return true;
                }else {
                    commandSender.sendMessage(Network.c.getString("sel.title").replace("&", "§") +Network.c.getString("sel.sell").replace("&", "§"));
                    return true;
                }
                if(args.length != 2) {
                    for(String ss : Network.c.getStringList("sel.help")){
                        commandSender.sendMessage(ss.replace("&", "§"));
                    }
                    return true;
                }
            }*/
            FileConfiguration c = Network.c;
            String don = args[1];
            int max = c.getInt("grant.items." + don);
            if((max - Database.getMyGrant(p.getName(), don) )<1){
                commandSender.sendMessage(Network.c.getString("grant.prefix").replace("&", "§") +Network.c.getString("grant.noinv").replace("&", "§"));
                return true;
            }

            //Database.removeItem(Database.getItemId(p.getName().toLowerCase(), item));
            try {
                String name = c.getString("case.items." + don + ".name").replace("&", "§");

                if(name == null){
                    commandSender.sendMessage(Network.c.getString("grant.prefix").replace("&", "§") +Network.c.getString("grant.noinv").replace("&", "§"));
                    for(String ss : Network.c.getStringList("grant.help")){
                        commandSender.sendMessage(ss.replace("&", "§"));
                    }
                    return true;
                }

                String prefix = Network.c.getString("grant.prefix").replace("&", "§");
                for(String msg : Network.c.getStringList("grant.broadcast")){
                    Bukkit.broadcastMessage(prefix + msg
                            .replace("$admin", ((Player) commandSender).getDisplayName())
                            .replace("$donate", name)
                            .replace("$player", args[0])
                            .replace("&", "§")

                    );
                }
                Database.setInventoryItem(args[0], args[1], false, 720);
                Database.addGrant(commandSender.getName(), don, args[0]);
            }catch (Exception e){
                for(String ss : Network.c.getStringList("sel.help")){
                    commandSender.sendMessage(ss.replace("&", "§"));
                }
                return true;
            }

            //selchat
        }


        return false;
    }
}
