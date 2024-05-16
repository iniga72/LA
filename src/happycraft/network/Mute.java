package happycraft.network;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;


public class Mute implements CommandExecutor{
    @SuppressWarnings({ "deprecation" })
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("luckybans.mute.permanent")) {
                p.sendMessage(Network.c.getString("bans.messages.noperm").replace("&", "§"));
                return true;
            }
        }
        if(args.length == 0) {
            md.help(sender, "mute");
            return true;
        }
        Player user = Bukkit.getPlayer(args[0]);
        if(user == null) {
            sender.sendMessage(Network.c.getString("bans.messages.offline"));
            return true;
        }
        if(args.length >= 1) {
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
            String reason = "";
                if(args.length == 1) reason = c.getString("bans.messages.no_reason");
                else for(int i = 1; i < args.length; i++) {
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
                String punishtime = "permanent";
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    if(Cooldown.hasCooldown(p.getName(), "mute")){
                        int timecd = (int) (Cooldown.getCooldown(p.getName(), "mute") / 1000);
                        String endtime = md.timeformat(timecd);
                        String stitle_c = c.getString("bans.messages.cooldown").replace("$time", endtime + "").replace("&", "§");
                        p.sendTitle(Network.prefix, stitle_c);
                        return true;
                    }
                    PermissionUser us = PermissionsEx.getUser(sender.getName());
                    String[] usg = us.getGroupNames();
                    int cd = c.getInt("bans.groups." +usg[0] + ".mute.delay");

                    Cooldown.setCooldown(p.getName(), cd * 1000L, "mute");
                }
                md.sendServer(sender.getName(), user.getName(), "mute", punishtime, reason);
            Database.setPunish(sender.getName().toLowerCase(), user.getName().toLowerCase(), reason, "mute", priority + "", "NUL", punishtime);

            return true;
        }


        return false;
    }
}
