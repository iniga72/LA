package happycraft.network;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class Kick implements CommandExecutor{
    @SuppressWarnings({ "deprecation" })
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("luckybans.kick")) {
                p.sendMessage(Network.c.getString("bans.messages.noperm").replace("&", "§"));
                return true;
            }
        }
        if(args.length == 0) {
            md.help(sender, "kick");
            return true;
        }
        Player user = Bukkit.getPlayer(args[0]);
        if(user == null) {
            sender.sendMessage(Network.c.getString("bans.messages.offline"));
            return true;
        }
            if(Bukkit.getPlayer(user.getName()) == null) {
                sender.sendMessage(Network.c.getString("bans.messages.offline").replace("$player", user.getName()).replace("&", "§"));
                return true;
            }
            if(sender instanceof Player) {
                if(!md.Limit(sender.getName(), user.getName())) {
                    sender.sendMessage(Network.c.getString("bans.messages.priority_kick").replace("$player", user.getName()).replace("&", "§"));
                    return true;
                }
            }
                String reason = "";
                if(args.length == 1) reason = Network.c.getString("bans.messages.no_reason");
                for(int i = 1; i < args.length; i++) {
                    reason += args[i] + " ";
                }
                int priority = 0;
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    PermissionUser us = PermissionsEx.getUser(sender.getName());
                    String[] usg = us.getGroupNames();
                    priority = Network.c.getInt("bans.groups." + usg[0] + ".priority");
                }else {
                    priority = 2147483647;

                }
                //остановился тут.
                String punishtime = "" + System.currentTimeMillis();
                String mainreason = "";
                for(String s : Network.c.getStringList("bans.messages.main.kick")) {
                    s = s.replace("$reason", reason);
                    s = s.replace("$admin", sender.getName());
                    s = s.replace("$time", punishtime);
                    s = s.replace("$player", user.getName());

                    mainreason += s + "\n";
                }
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    if(Cooldown.hasCooldown(p.getName(), "kick")){
                        int timecd = (int) (Cooldown.getCooldown(p.getName(), "kick") / 1000);
                        String endtime = "";
                        if(timecd >= 86400){//d
                            int param = timecd / 86400;
                            endtime = (int)param + Network.c.getString("bans.formats.time.day");
                            param = (int)param * 86400;
                            timecd = timecd - param;
                        }
                        if(timecd >= 3600){//h
                            int param = timecd / 3600;
                            endtime = endtime + (int)param + Network.c.getString("bans.formats.time.houer");
                            param = (int)param * 3600;
                            timecd = timecd - param;
                        }
                        if(timecd >= 60){//m
                            int param = timecd / 60;
                            endtime = endtime + (int)param + Network.c.getString("bans.formats.time.min");
                            param = (int)param * 60;
                            timecd = timecd - param;
                        }
                        if(timecd >= 1){//sec
                            int param = timecd / 1;
                            endtime = endtime + (int)param + Network.c.getString("bans.formats.time.sec");
                            param = (int)param * 1;
                            timecd = timecd - param;
                        }

                        String stitle_c = Network.c.getString("bans.messages.cooldown").replace("$time", endtime + "").replace("&", "§");
                        p.sendTitle(Network.prefix, stitle_c);
                        return true;
                    }
                    PermissionUser us = PermissionsEx.getUser(sender.getName());
                    String[] usg = us.getGroupNames();
                    int cd = Network.c.getInt("bans.groups." + usg[0] + ".kick.delay");

                    Cooldown.setCooldown(p.getName(), cd * 1000L, "kick");
                }
        md.kick(user.getName(), sender.getName(), mainreason.replace("&", "§"));
        md.sendServer(sender.getName(), user.getName(), "kick", punishtime, reason);
        Database.setPunish(sender.getName().toLowerCase(), user.getName().toLowerCase(), reason, "kick", priority + "", "NUL", punishtime);
        return true;
    }
}

