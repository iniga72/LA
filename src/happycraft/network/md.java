package happycraft.network;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class md {
    public static boolean kick(String p, String admin, String reason) {
        if (Bukkit.getPlayer(p) != null) {
            Player player = Bukkit.getPlayer(p);
            player.kickPlayer(reason);
            return true;
        }
        return false;
    }
    public static String msg(String name, String key) {
        String format = Network.c.getString("discord.title").replace("&", "§");
        String text = Network.c.getString("discord.text").replace("$user", name).replace("&", "§");


        String CHAT_FORMAT = "";

        CHAT_FORMAT = "[\"\",";
        String status = "&eЖми для подключения " + name;

        CHAT_FORMAT += " {\"text\":\"§e" +format + text + "\"," +
                "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/ds "+key+"\"},"

                + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                + "{\"text\":\"\",\"extra\":[{\"text\":\""+status.replace("&", "§")+"\"}]}}}";
        CHAT_FORMAT = CHAT_FORMAT.replace("&", "§");
        CHAT_FORMAT = CHAT_FORMAT + "]";

        return CHAT_FORMAT;
    }
    public static void sendServer(String admin, String player, String punish, String punishtime, String reason) {
        FileConfiguration c = Network.c;
        if (punishtime.equalsIgnoreCase("permanent")) {
            punishtime = c.getString("bans.messages.permanent");
        } else {
            long s = Long.parseLong(punishtime);
            if (c.getString("bans.messages.successfully." + punish + ".title").contains("$time") || c.getString("bans.messages.successfully." + punish + ".subtitle").contains("$time")) {
                punishtime = FDT(s, true);
            } else {
                punishtime = FDT(s, false);
            }

        }


        String title = c.getString("bans.messages.successfully." + punish + ".title").replace("$player", player).replace("&", "§").replace("$time", punishtime).replace("$date", punishtime);
        String subtitle = c.getString("bans.messages.successfully." + punish + ".subtitle").replace("$admin", admin).replace("$reason", reason).replace("$time", punishtime).replace("$date", punishtime);
        subtitle = subtitle.replace("$time", punishtime);
        subtitle = subtitle.replace("$date", punishtime);

        String more = ",{\"text\":\" " + c.getString("bans.messages.more") + "\","
                + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                + "{\"text\":\"\",\"extra\":[{\"text\":\"" + subtitle + "\"}]}}}";

        String CHAT_FORMAT = "[\"\",{\"text\":\"" + title + "\"}" + more + "]";
        Bukkit.spigot().broadcast((new TextComponent(ComponentSerializer.parse(CHAT_FORMAT.replace("&", "§")))));

    }

    public static String FDT(Long time, boolean times) {
        String result;
        FileConfiguration c = Network.c;
        if (times) {
            time = time - System.currentTimeMillis()/1000;
            int d = (int) TimeUnit.SECONDS.toDays(time);
            long h = TimeUnit.SECONDS.toHours(time) - (d * 24);
            long m = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
            long sec = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);
            result = " ";

            if (d > 0) result += d + c.getString("bans.formats.time.day");
            if (h > 0) result += h + c.getString("bans.formats.time.houer");
            if (m > 0) result += m + c.getString("bans.formats.time.min");
            if (sec > 0) result += sec + c.getString("bans.formats.time.sec");
        } else {
            try {
                Date date = new Date(time);
                SimpleDateFormat formater = new SimpleDateFormat(c.getString("bans.formats.data"));
                result = formater.format(date);
            } catch (Exception e) {
                result = "Hide";
            }
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    public static boolean Limit(String admin, String user) {
        FileConfiguration c = Network.c;
        PermissionUser us = PermissionsEx.getUser(user);
        //us.getTimedPermissions()
        String[] usg = us.getGroupNames();

        us = PermissionsEx.getUser(admin);
        String[] adg = us.getGroupNames();
        int a = c.getInt("bans.groups." + usg[0] + ".priority");
        int u = c.getInt("bans.groups." + adg[0] + ".priority");
        if (a < u) return true;
        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean LimitClear(String admin, String user, String punish) {
        BanPropertis last = Database.getLastPunish(user, punish);
        if (last == null) return true;

        FileConfiguration c = Network.c;

        PermissionUser us = PermissionsEx.getUser(admin);
        String[] usg = us.getGroupNames();
        int a = c.getInt("bans.groups." + usg[0] + ".priority");
        int u = Integer.parseInt(last.getPtiority());
        //Bukkit.broadcastMessage(a + " _ " + u);
        if (a >= u) return true;
        return false;
    }

    public static String timeformat(int time) {
        String endtime = "";
        FileConfiguration c = Network.c;
        if (time >= 86400) {//d
            int param = time / 86400;
            endtime = (int) param + c.getString("bans.formats.time.day");
            param = (int) param * 86400;
            time = time - param;
        }
        if (time >= 3600) {//h
            int param = time / 3600;
            endtime = endtime + (int) param + c.getString("bans.formats.time.houer");
            param = (int) param * 3600;
            time = time - param;
        }
        if (time >= 60) {//m
            int param = time / 60;
            endtime = endtime + (int) param + c.getString("bans.formats.time.min");
            param = (int) param * 60;
            time = time - param;
        }
        if (time >= 1) {//sec
            int param = time / 1;
            endtime = endtime + (int) param + c.getString("bans.formats.time.sec");
            param = (int) param * 1;
            time = time - param;
        }
        return endtime;
    }

    public static void help(CommandSender s, String cmd) {
        FileConfiguration c = Network.c;
        for (String msg : c.getStringList("bans.messages.help." + cmd)) {
            s.sendMessage(Network.prefix + msg.replace("&", "§"));
        }
    }

    public static boolean CheckPunish(String name, String punish) {

        name = name.toLowerCase();
        BanPropertis last = Database.getLastPunish(name, punish);

        if (last == null) return false;
        if (last.getTime().equalsIgnoreCase("permanent")) return true;
        long time = Long.parseLong(last.getTime());
        long time2 = System.currentTimeMillis() /1000;
        if (time >= time2) return true;
        return false;
    }
/*
    public static void sendPlayer(Map<String, ArrayList<String>> params, Player p, int pagess, String punish) {
        FileConfiguration c = Network.c;
        if (pagess > 1) ;
        int allp = params.size() / 10;
        if (allp <= 0) allp = 1;
        else if (allp * 10 != params.size()) allp++;
        p.sendMessage(c.getString("bans.messages.successfully." + punish + ".list.message").replace("$pages", allp + "").replace("$page", +pagess + "").replace("&", "§"));
        int pag = 0;
        int pag3 = pagess * 10; // 20
        int pag2 = pag3 - 10; // 10

        for (Map.Entry<String, ArrayList<String>> entry : params.entrySet()) {

            if (pag >= pag2 && pag < pag3) {
                String time = "0";
                ArrayList<String> param = params.get(entry.getKey());
                String status = c.getString("bans.messages.status.there_is_" + punish);
                if (param.get(3).equalsIgnoreCase("permanent")) time = c.getString("bans.messages.permanent");
                else {
                    try {
                        long ltime = Long.parseLong(time);
                        if (ltime <= System.currentTimeMillis())
                            status = c.getString("bans.messages.status.no_" + punish);
                        ltime += System.currentTimeMillis();
                        Date date = new Date(ltime);
                        SimpleDateFormat formater = new SimpleDateFormat(c.getString("bans.formats.data"));
                        time = formater.format(date);
                    } catch (Exception e) {
                        time = "Hide";
                    }
                }
                String title = c.getString("bans.messages.successfully." + param.get(4) + ".list.title").replace("$player", entry.getKey()).replace("&", "§");
                String subtitle = c.getString("bans.messages.successfully." + param.get(4) + ".list.subtitle").replace("$adminclear", param.get(6)).replace("$admin", param.get(0)).replace("$reason", param.get(2)).replace("$time", time).replace("$stats", status);
                String more = ",{\"text\":\" " + c.getString("bans.messages.more") + "\","
                        + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                        + "{\"text\":\"\",\"extra\":[{\"text\":\"" + subtitle + "\"}]}}}";

                String CHAT_FORMAT = "[\"\",{\"text\":\"" + title + "\"}" + more + "]";
                BaseComponent bc = new TextComponent();
                p.spigot().sendMessage(new TextComponent(ComponentSerializer.parse(CHAT_FORMAT.replace("&", "§"))));
            }
            pag++;
        }
    }
*/
    public static boolean CheckIPPunish(String ip) {

        ip = ip.toLowerCase();
        BanPropertis bp = Database.getLastIPPunish(ip, "ban");
        if (bp == null) return false;
        if (bp.getTime().equalsIgnoreCase("permanent")) return true;

        long time = Long.parseLong(bp.getTime());
        long time2 = System.currentTimeMillis() /1000;
        if (time >= time2) return true;
        return false;
    }

    public static String createIPDiscriprion(String ip) {
        String msg = "";
        BanPropertis last = Database.getLastIPPunish(ip, "ban");

        FileConfiguration c = Network.c;
        for (String s : c.getStringList("bans.messages.main.ban-ip")) {
            if (s.contains("$time")) {
                if (last.getTime().equalsIgnoreCase("permanent")) {
                    s = s.replace("$time", c.getString("bans.messages.permanent"));
                } else {
                    String time = FDT(Long.parseLong(last.getTime()), true);
                    s = s.replace("$time", time);
                }

            }
            if (s.contains("$admin")) {
                s = s.replace("$admin", last.getAdmin());
            }
            if (s.contains("$date")) {
                if (last.getTime().equalsIgnoreCase("permanent")) {
                    s = s.replace("$date", c.getString("bans.messages.permanent"));
                } else {
                    String time = FDT(Long.parseLong(last.getTime()), false);
                    s = s.replace("$date", time);
                }
            }
            if (s.contains("$reason")) {
                s = s.replace("$reason", last.getReason());
            }
            msg += s + "\n";
        }
        return msg.replace("&", "§");

    }

    public static String createDiscriprion(String p, String punish) {
        String msg = "";
        //ArrayList<String> data = db.getBanInfo(db.getLastPunish(p.toLowerCase(), punish)).get(p.toLowerCase());
        BanPropertis last = Database.getLastPunish(p, punish);
        FileConfiguration c = Network.c;
        for (String s : c.getStringList("bans.messages.main." + punish)) {
            if (s.contains("$time")) {
                if (last.getTime().equalsIgnoreCase("permanent")) {
                    s = s.replace("$time", c.getString("bans.messages.permanent"));
                } else {
                    String time = FDT(Long.parseLong(last.getTime()), true);
                    s = s.replace("$time", time);
                }

            }
            if (s.contains("$admin")) {
                s = s.replace("$admin", last.getAdmin());
            }
            if (s.contains("$date")) {
                if (last.getTime().equalsIgnoreCase("permanent")) {
                    s = s.replace("$date", c.getString("bans.messages.permanent"));
                } else {
                    String time = FDT(Long.parseLong(last.getTime()), false);
                    s = s.replace("$date", time);
                }
            }
            if (s.contains("$reason")) {
                s = s.replace("$reason", last.getReason());
            }
            msg += s + "\n";
        }
        return msg.replace("&", "§");

    }
}
