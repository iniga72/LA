package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class Ban implements CommandExecutor {

    @SuppressWarnings({ "deprecation" })
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("luckybans.ban.permanent")) {
                p.sendMessage(Network.c.getString("bans.messages.noperm").replace("&", "§"));
                return true;
            }
        }

        if(args.length == 0) {
            md.help(sender, "ban");
            return true;
        }

//        if(user == null) {
//            sender.sendMessage(Network.c.getString("bans.messages.offline"));
//            return true;
//
//        }
            FileConfiguration c = Network.c;
            if(sender instanceof Player) {

                if(!md.Limit(sender.getName(), args[0])) {
                    sender.sendMessage(c.getString("bans.messages.priority_ban").replace("$player", args[0]).replace("&", "§"));
                    return true;
                }
            }
            if(md.CheckPunish(args[0],"ban")) {
                sender.sendMessage(c.getString("bans.messages.hasbanned").replace("$player", args[0]).replace("&", "§"));
                return true;
            }
            String reason = "";

                if(args.length == 1) reason = c.getString("bans.messages.no_reason");
                    for(int i = 1; i < args.length; i++) {
                        reason += args[i] + " ";
                    }
                if(args.length >= 3) {
                    for(int i = 2; i < args.length; i++) {
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
                    if(Cooldown.hasCooldown(p.getName(), "ban")){
                        int timecd = (int) (Cooldown.getCooldown(p.getName(), "ban") / 1000);
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
                    int cd = c.getInt("bans.groups." + usg[0] + ".ban.delay");
                    Cooldown.setCooldown(p.getName(), cd * 1000L, "ban");

                }
                md.sendServer(sender.getName(), args[0], "ban", punishtime, reason);
                Database.setPunish(sender.getName().toLowerCase(), args[0].toLowerCase(), reason, "ban", priority + "", "NUL", punishtime);

                punishtime = c.getString("bans.messages.permanent");
                StringBuilder mainreason = new StringBuilder();
                for(String s : c.getStringList("bans.messages.main.ban")) {
                    s = s.replace("$reason", reason);
                    s = s.replace("$admin", sender.getName());
                    s = s.replace("$player",args[0]);
                    s = s.replace("$date", punishtime);
                    s = s.replace("$time", punishtime);
                    mainreason.append(s).append("\n");
                }
                OfflinePlayer z = Bukkit.getOfflinePlayer(args[0]);
                if(z.isOnline()){
                    Player user = (Player) z;
                    md.kick(user.getName(), sender.getName(), mainreason.toString().replace("&", "§"));
                }
                return true;

    }

}
