package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class BanIP implements CommandExecutor{
    @SuppressWarnings({ "deprecation" })
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("luckybans.banip.permanent")) {
                p.sendMessage(Network.c.getString("bans.messages.noperm").replace("&", "§"));
                return true;
            }
        }
        if(args.length == 0) {
            md.help(sender, "banip");
            return true;
        }
        Player user = Bukkit.getPlayer(args[0]);
        if(user == null) {
            sender.sendMessage(Network.c.getString("bans.messages.offline"));
            return true;
        }
        Player pl = Bukkit.getPlayer(user.getName());
        FileConfiguration c = Network.c;
        if(pl == null) {
            sender.sendMessage(c.getString("bans.messages.online").replace("&", "§"));
            return true;
        }

        if(md.CheckPunish(user.getName(),"ban")) {
            sender.sendMessage(c.getString("bans.messages.hasbanned").replace("$player", user.getName()).replace("&", "§"));
            return true;
        }
        if(sender instanceof Player) {
            if(!md.Limit(sender.getName(), user.getName())) {
                sender.sendMessage(c.getString("bans.messages.priority_ban").replace("$player", user.getName()).replace("&", "§"));
                return true;
            }
        }
        long time = 0;
        String reason = "";

        if(args.length == 1) reason = c.getString("bans.messages.no_reason");
        else {
            for(int i = 1; i < args.length; i++) {
                reason += args[i] + " ";
            }
        }
        int priority = 0;
        if(sender instanceof Player) {

            PermissionUser us = PermissionsEx.getUser(sender.getName());
            String[] usg = us.getGroupNames();
            priority = c.getInt("bans.groups." + usg[0] + ".priority");
        }else {
            priority = 999999999;
        }
        String punishtime = "permanent";

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(Cooldown.hasCooldown(p.getName(), "banip")){
                int timecd = (int) (Cooldown.getCooldown(p.getName(), "banip") / 1000);
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
                    endtime = endtime + (int)param + c.getString("bans.ormats.time.sec");
                    param = (int)param * 1;
                    timecd = timecd - param;
                }
                String stitle_c = c.getString("bans.messages.cooldown").replace("$time", endtime + "").replace("&", "§");
                p.sendTitle(Network.prefix, stitle_c);
                return true;
            }
            PermissionUser us = PermissionsEx.getUser(sender.getName());
            String[] usg = us.getGroupNames();
            int cd = c.getInt("bans.groups." + usg[0] + ".banip.delay");

            Cooldown.setCooldown(p.getName(), cd * 1000L, "banip");
        }
        md.sendServer(sender.getName(), user.getName(), "ban", punishtime, reason);
        long ltime = 0;
        Database.setPunish(sender.getName().toLowerCase(), user.getName().toLowerCase(), reason, "ban", priority + "", pl.getAddress().getAddress().getHostAddress().replace("/", ""), punishtime);
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("luckybans.banip.permanent")) {
                p.sendMessage(Network.c.getString("bans.messages.noperm").replace("&", "§"));
                return true;
            }
        }
        punishtime = c.getString("bans.messages.permanent");
        String mainreason = "";
        for(String s : c.getStringList("bans.messages.main.ban-ip")) {
            s = s.replace("$reason", reason);
            s = s.replace("$admin", sender.getName());
            s = s.replace("$player", user.getName());
            s = s.replace("$date", punishtime);
            if(s.contains("$time") && ltime > 0) {
                s = s.replace("$time", md.FDT(ltime, true));
            }else {
                s = s.replace("$time", punishtime);
            }
            mainreason += s + "\n";
        }
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getAddress().getAddress().getHostAddress().replace("/", "").equalsIgnoreCase(pl.getAddress().getHostName().replace("/", ""))) {
                md.kick(p.getName(), sender.getName(), mainreason.replace("&", "§"));
            }
        }
        md.kick(user.getName(), sender.getName(), mainreason.replace("&", "§"));
        return true;


    }

}

