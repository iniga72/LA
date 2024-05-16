package happycraft.network;

import com.mysql.fabric.xmlrpc.base.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class referal implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        FileConfiguration c = Network.c;
        Player p = (Player) commandSender;
        if(args.length == 0){
            String key = Database.getReferalkey(p.getName());
            String group = p.hasPermission("referal.vip") ? "donatecase" : "case";

            for(String s : c.getStringList("referals.create")){
                p.sendMessage(s
                        .replace("$kit" , c.getString("case.items." + group + ".name"))
                        .replace("$key" , key)
                        .replace("&" , "§")
                );
            }
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("get")){
            ArrayList<String> users = Database.getReferals(p.getName());
            int count = users.size();
            int bonus = 0;
            for(String s : users){

                ReferalGetTime b = Database.ReferalBonus(s);

                if(b.getTime() > b.getCount()){
                    bonus+=b.getTime() - b.getCount();
                    try {
                        Database.updateReferalBonus(s, b.getTime());
                    } catch (SQLException e) {

                    }
                }
            }
            for(String s : c.getStringList("referals.get")){
                p.sendMessage(s
                        .replace("$count" , count + "")
                        .replace("$bonus" , bonus + "")
                        .replace("&" , "§")
                );
            }
            if(bonus > 0)Network.push("money give " + p.getName() + " " + bonus);
            return true;
        }
        if(args.length == 1){
            if(Database.checkReferal(p.getName())){
                p.sendMessage(c.getString("referals.err").replace("&", "§"));
                return true;
            }
            long sec = p.getStatistic(Statistic.PLAY_ONE_TICK) / 20;
            int h = (int) TimeUnit.SECONDS.toMinutes(sec);
            if(h>60){
                p.sendMessage(c.getString("referals.online").replace("&", "§"));
                return true;
            }
        String ref = Database.getReferal(args[0]);

            if(ref == null){
                p.sendMessage(c.getString("referals.noname").replace("&", "§"));
                return true;
            }
            if(ref.equalsIgnoreCase(p.getName())){
                p.sendMessage(c.getString("referals.self").replace("&", "§"));
                return true;
            }

            if(Database.getIP(ref).equals(Database.getIP(p.getName()))){
                p.sendMessage(c.getString("referals.self").replace("&", "§"));
                return true;
            }

            Database.addReferal(p.getName(), ref);
            p.sendMessage(c.getString("referals.good").replace("&", "§"));
            Player pl = Bukkit.getPlayer(ref);

            if(pl != null){
                pl.sendMessage(c.getString("referals.add")
                        .replace("$player", p.getName())
                        .replace("&", "§"));
            }
            Database.setInventoryItem(p.getName(), "case", false, 0);

            return true;
        }

        //проверка сколько играл человек на сервере. больше часа - отмена
        //выдать игроку бонусы
        return true;
    }
}
