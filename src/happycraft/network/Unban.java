package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


import java.sql.SQLException;

public class Unban implements CommandExecutor{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("luckybans.unban")) {
                p.sendMessage(Network.c.getString("bans.messages.noperm").replace("&", "§"));
                return true;
            }
        }
        if(args.length != 1) {
            md.help(sender, "unban");
            return true;
        }
        FileConfiguration c = Network.c;
        if(!md.CheckPunish(args[0],"ban")) {
            sender.sendMessage(c.getString("bans.messages.hasentbanned").replace("$player", args[0]).replace("&", "§"));
            return true;
        }
        if(sender instanceof Player) {
            if(!md.LimitClear(sender.getName(), args[0], "ban")) {
                sender.sendMessage(c.getString("bans.messages.priority_unban").replace("$player", args[0]).replace("&", "§"));
                return true;
            }
        }
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(Cooldown.hasCooldown(p.getName(), "unban")){
                int timecd = (int) (Cooldown.getCooldown(p.getName(), "unban") / 1000);
                String endtime = "";
                if(timecd >= 86400){//d
                    int param = timecd / 86400;
                    endtime = (int)param + c.getString("bans.formats.time.day");
                    param = (int)param * 86400;
                    timecd = timecd - param;
                }
                if(timecd >= 3600){//h
                    int param = timecd / 3600;
                    endtime = endtime + (int)param + c.getString("bans.formats.time.houer");
                    param = (int)param * 3600;
                    timecd = timecd - param;
                }
                if(timecd >= 60){//m
                    int param = timecd / 60;
                    endtime = endtime + (int)param + c.getString("bans.formats.time.min");
                    param = (int)param * 60;
                    timecd = timecd - param;
                }
                if(timecd >= 1){//sec
                    int param = timecd / 1;
                    endtime = endtime + (int)param + c.getString("bans.formats.time.sec");
                    param = (int)param * 1;
                    timecd = timecd - param;
                }
                String stitle_c = c.getString("bans.messages.cooldown").replace("$time", endtime + "").replace("&", "§");
                p.sendTitle(Network.prefix, stitle_c);
                return true;
            }
            PermissionUser us = PermissionsEx.getUser(sender.getName());
            String[] usg = us.getGroupNames();
            int cd = c.getInt("bans.groups." + usg[0] + ".unban.delay");

            Cooldown.setCooldown(p.getName(), cd * 1000L, "unban");
        }
        //sender.sendMessage(c.getString("bans.messages.unban").replace("$user", args[0]).replace("&", "§"));
        for (String s : Network.c.getStringList("bans.messages.successfully.unban")){
            Bukkit.broadcastMessage(s
                    .replace("$player", args[0])
                    .replace("$admin", sender.getName())
                    .replace("&", "§")
            );
        }
        try {
            Database.setAdminClear(sender.getName(), "ban", args[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;

    }

}
