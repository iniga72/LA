package happycraft.network;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class sel  implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player){
            if(args.length == 1){
                if(Network.selchat.containsKey(args[0])){
                    SelParams sp = Network.selchat.get(args[0]);
                    float money = Database.getMoney(commandSender.getName().toLowerCase());
                    if(sp.getPrice() > money){
                        commandSender.sendMessage(Network.c.getString("sel.title").replace("&", "§") + Network.c.getString("sel.nomoney").replace("&", "§"));
                        return true;
                    }
                    if(!Database.getItembyId( Network.selchat.get(args[0]).getPlayer(),args[0])){
                        commandSender.sendMessage(Network.c.getString("sel.title").replace("&", "§") +Network.c.getString("sel.sell").replace("&", "§"));
                        return true;
                    }
                    String name = Network.c.getString("case.items." + sp.getDonate() + ".name").replace("&", "§");
                    String msg = Network.c.getString("sel.title").replace("&", "§") +Network.c.getString("sel.broadcast").replace("$player", commandSender.getName()).replace("$donate", name).replace("$price", sp.getPrice() + "").replace("&", "§");

                    Bukkit.broadcastMessage(msg.replace("&", "§"));
                    Network.push("money give "+sp.getPlayer()+" " + sp.getPrice());
                    try {
                        Database.updateItem(commandSender.getName(), args[0]);
                    } catch (SQLException e) {
                    }
                    //Database.setInventoryItem(commandSender.getName(), sp.getDonate());

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
                    if(!Database.getItembyId(commandSender.getName(),args[0])){
                        commandSender.sendMessage(Network.c.getString("sel.title").replace("&", "§") +Network.c.getString("sel.sell").replace("&", "§"));
                        return true;
                    }else {
                        for(String ss : Network.c.getStringList("sel.help")){
                            commandSender.sendMessage(ss.replace("[id]", args[0]).replace("&", "§"));
                        }
                    }
                    return true;
                }
            }
            if(args.length != 2) {
                for(String ss : Network.c.getStringList("sel.help")){
                    commandSender.sendMessage(ss.replace("&", "§"));
                }
                return true;
            }
            Player p = (Player) commandSender;
            String id = args[0];
            if(!Database.getItembyId(p.getName(),id)){
                p.sendMessage(Network.c.getString("case.inventory.noitem").replace("&", "§"));
                return true;
            }
            double money = 0;
            try {
                money = Double.parseDouble(args[1]);
                money = Precision.round(money, 3);;
                if(money < 0.001) money = 0.001;
                if(money < 0 || money > 2000){
                    for(String ss : Network.c.getStringList("sel.help")){
                        commandSender.sendMessage(ss.replace("&", "§"));
                    }
                    return true;
                }
            }catch (Exception ignored){
                for(String ss : Network.c.getStringList("sel.help")){
                    commandSender.sendMessage(ss.replace("&", "§"));
                }
                return true;
            }

            //Database.removeItem(Database.getItemId(p.getName().toLowerCase(), item));
            try {
                FileConfiguration c = Network.c;
                happycraft.network.params.items item = Database.getItem(args[0]);
                String name = c.getString("case.items." + item.getItem() + ".name").replace("&", "§");
                if(name == null){
                    commandSender.sendMessage(Network.c.getString("sel.title").replace("&", "§") +Network.c.getString("sel.noinv").replace("&", "§"));
                    for(String ss : Network.c.getStringList("sel.help")){
                        commandSender.sendMessage(ss.replace("&", "§"));
                    }
                    return true;
                }
                TextComponent comp = new TextComponent(ComponentSerializer.parse(msg(commandSender.getName(), name, args[0], money + "", item.getTime())));
                Bukkit.broadcast(comp);
                SelParams sp = new SelParams(item.getItem(), commandSender.getName().toLowerCase(), money);
                Network.selchat.put(args[0], sp);
                Plugin pl = Bukkit.getPluginManager().getPlugin("Network");
                new BukkitRunnable(){
                    boolean b = false;
                    public void run() {
                        if (b) {
                            Network.selchat.remove(args[0]);
                            this.cancel();
                        }

                    }
                }.runTaskTimer(pl, 120, 120);


            }catch (Exception e){
                for(String ss : Network.c.getStringList("sel.help")){
                    commandSender.sendMessage(ss.replace("&", "§"));
                }
                return true;
            }

            //selchat
        }else commandSender.sendMessage("Only for players");


        return false;
    }
    public String msg(String p, String name, String key, String price, int time) {
        String format = Network.c.getString("sel.title").replace("&", "§");
        String text = Network.c.getString("sel.text").replace("$player", p).replace("$donate", name)
                .replace("$price", price)
                .replace("$time", time + "")
                .replace("&", "§");


        String CHAT_FORMAT = "";

        CHAT_FORMAT = "[\"\",";
        String status = "&eЖми для покупки " + name;

        CHAT_FORMAT += " {\"text\":\"§e" +format + text + "\"," +
                "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/sel "+key+"\"},"

                + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                + "{\"text\":\"\",\"extra\":[{\"text\":\""+status.replace("&", "§")+"\"}]}}}";
        CHAT_FORMAT = CHAT_FORMAT.replace("&", "§");
        CHAT_FORMAT = CHAT_FORMAT + "]";

        return CHAT_FORMAT;
    }
}
