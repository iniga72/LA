package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class TempMute implements CommandExecutor {
    @SuppressWarnings({ "deprecation" })
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("luckybans.mute.temp")) {
                p.sendMessage(Network.c.getString("bans.messages.noperm").replace("&", "§"));
                return true;
            }
        }
        if(args.length == 0) {
            md.help(sender, "tempmute");
            return true;
        }
        Player user = Bukkit.getPlayer(args[0]);
        if(user == null) {
            sender.sendMessage(Network.c.getString("bans.messages.offline"));
            return true;
        }
        FileConfiguration c = Network.c;
        if(md.CheckPunish(user.getName(),"mute")) {
            sender.sendMessage(c.getString("bans.messages.hasmuted").replace("$player", user.getName()).replace("&", "§"));
            return true;
        }
        if(sender instanceof Player) {
            if(!md.Limit(sender.getName(), user.getName())) {
                sender.sendMessage(c.getString("bans.messages.priority_mute").replace("$player", user.getName()).replace("&", "§"));
                return true;
            }
        }
        long time = 0;
        String reason = "";
        if(args.length >=2) {

            if(args[1].endsWith("d")) {
                try {
                    long d = Long.parseLong(args[1].replace("d", ""));
                    time = d * 3600000 * 24;
                } catch (Exception ignored) {

                }
            }else if(args[1].endsWith("m")) {
                try {
                    long m = Long.parseLong(args[1].replace("m", ""));
                    time = m * 60000;
                } catch (Exception ignored) {

                }
            }else if(args[1].endsWith("s")) {
                try {
                    long s = Long.parseLong(args[1].replace("s", ""));
                    time = s *1000;
                } catch (Exception ignored) {

                }
            }
            else if(args[1].endsWith("h")) {
                try {
                    long h = Long.parseLong(args[1].replace("h", ""));
                    time = h * 3600000;
                } catch (Exception ignored) {

                }
            }
            if(time > 0) {
                if(args.length == 2) reason = c.getString("bans.messages.no_reason");
            }

        }else {
            md.help(sender, "tempmute");
            return true;
        }

        if(args.length == 1) reason = c.getString("bans.messages.no_reason");
        else if(time == 0) {
            for(int i = 1; i < args.length; i++) {
                reason += args[i] + " ";
            }
        }else if(args.length >= 3) {
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
            priority = 2147483647;
        }
        String punishtime = "";
        if(time == 0) {
            md.help(sender, "tempmute");
            return true;
        }else {
            if(sender instanceof Player) {
                Player p = (Player) sender;

                int lim = (int)(time/1000);
                PermissionUser us = PermissionsEx.getUser(sender.getName());
                String[] usg = us.getGroupNames();
                int maximum = c.getInt("bans.groups." + usg[0] + ".tempmute.limite");

                if(lim > maximum) {
                    sender.sendMessage(c.getString("bans.messages.limite_mute").replace("$time", md.timeformat(lim)).replace("$limite", md.timeformat(maximum)).replace("&", "§"));
                    return true;
                }

            }

        }
        time = (System.currentTimeMillis() + time) / 1000;

        punishtime = time + "";
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(Cooldown.hasCooldown(p.getName(), "mute")){
                int timecd = (int) (Cooldown.getCooldown(p.getName(), "mute") / 1000);
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
            int cd = c.getInt("bans.groups." + usg[0] + ".tempmute.delay");

            Cooldown.setCooldown(p.getName(), cd * 1000L, "tempmute");
        }
        md.sendServer(sender.getName(), user.getName(), "mute", punishtime, reason);
        Database.setPunish(sender.getName().toLowerCase(), user.getName().toLowerCase(), reason, "mute", priority + "", "NUL", punishtime);

        return true;
    }
}