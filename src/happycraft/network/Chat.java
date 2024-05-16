package happycraft.network;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat implements Listener, CommandExecutor {
    public String form(String msg, String format, String dc, Player p, String name) throws SQLException {
        if(msg.startsWith("!"))msg = msg.substring(1);

        if(msg.toUpperCase().equals(msg))msg = msg.toLowerCase();
        if(p.isOp())msg = PlaceholderAPI.setPlaceholders(p, msg);
        long sec = p.getStatistic(Statistic.PLAY_ONE_TICK) / 20;
        int d = (int) TimeUnit.SECONDS.toDays(sec);
        long h = TimeUnit.SECONDS.toHours(sec) - (d *24);
        long m = TimeUnit.SECONDS.toMinutes(sec) - (TimeUnit.SECONDS.toHours(sec)* 60);
        long se = TimeUnit.SECONDS.toSeconds(sec) - (TimeUnit.SECONDS.toMinutes(sec) *60);
        String time = d+ "д. " + h + "ч. " + m+ "м. " + se + "с. ";
        String CHAT_FORMAT = "";

        CHAT_FORMAT = "[\"\","
                + "{\"text\":\"" + format + "\"}";

        String usg = PlaceholderAPI.setPlaceholders(p, "%vault_prefix%");
        String don = PlaceholderAPI.setPlaceholders(p, "%vault_rankprefix%");

        String status = "";
        status+="&eСтатус: "+don+"\n";
        Bukkit.getLogger().info(don + "§c"+p.getName() + ": " + msg);
        int rep = Database.getReputationCount(p.getName());
        rep-=(3 * Database.getWarn(p.getName()));
        int donate = Database.getAllDonate(p.getName()) / 100;
        rep+=donate;
        String rank = Database.getRank(p.getName()) != null ? Database.getRank(p.getName()) : "&cПусто";
        String clan = Database.getClan(p.getName()) != null ? Database.getClan(p.getName()) : "&cПусто";

        String reps = "&cНеизвестна";
        if(rep < 0)reps = "&cПлохая";
        if(rep < -5)reps = "&cУжасная";
        if(rep > 5)reps = "&cНормальная";
        if(rep > 20)reps = "&cХорошая";
        if(rep > 49)reps = "&cОтличная";
        if(rep > 80)reps = "&cЛучшая";
        if(rep > 150)reps = "&cНаилучшая";

        status+="&eКлан: &c"+clan+"\n";
        status+="&eОнлайн: &c"+time+"\n";
        status+="&eНик: &c"+p.getName()+"\n";
        status+="&eРепутация: &c"+reps+"\n";

        status+="&eРанг: "+rank+"\n";
        //&a&l✔
        String bonus ="";
        if(Database.getVK(p.getName().toLowerCase())){
            bonus = "&a&l✔ ";
            status+="&eСтатус: "+Network.c.getString("chat.verefycation.good")+"\n";
        }else status+="&eСтатус: "+Network.c.getString("chat.verefycation.bad")+"\n";
        bonus += usg;
        status+="&eПредупреждения: &c" + Database.getWarn(p.getName());
        CHAT_FORMAT += " ,{\"text\":\"§e" + bonus + "&e" + name + "\","
                + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                + "{\"text\":\"\",\"extra\":[{\"text\":\""+status.replace("&", "§")+"\",\"color\":\"GRAY\"}]}}}";
        CHAT_FORMAT = CHAT_FORMAT.replace("&", "§");
        CHAT_FORMAT += " " + ",{\"text\":\": "  + msg + "\",\"color\":\"gray\"}";
        CHAT_FORMAT = CHAT_FORMAT + "]";

        return CHAT_FORMAT;
    }
    public boolean control(String m, Player p){

        FileConfiguration c = Network.c;
        if(c.getBoolean("protect.chat.bypass") && p.hasPermission("happycraft.chat.bypass"))return true;
        if(Database.ChatControl(m))return true;
        String[] message = m.toLowerCase().split(" ");
        List<String> checks = c.getStringList("protect.chat.words.block");
        String key = Database.getReferalkey(p.getName());
        if(key != null){
            checks.add(key);
        }
        List<String> wl = c.getStringList("protect.chat.words.wl");

        String ms = m.toLowerCase();
        boolean check = false;

        Pattern pat1 = Pattern.compile("((\\d{2,3}.){3,10}|\\.ru|\\.com|mc\\.|\\.su|[A-z][А-я]|[А-я][A-z])");
        Matcher matcher1 = pat1.matcher(m);
        StringBuilder err = new StringBuilder();
        while (matcher1.find()) {
            ms = ms.replace(matcher1.group(),"§c§l§n" + matcher1.group() + "§f");
            check = true;
        }
        for(String s : message) {
            for(String d : checks) {
                OfflinePlayer pip = Bukkit.getOfflinePlayer(s);
                if(!pip.isOnline()) {
                    if(s.toLowerCase().contains(d) && !wl.contains(s.toLowerCase())) {
                        ms = ms.replace(s, "§c§l§n" + s + "§f");
                        check = true;
                    }
                }

            }
        }
        if(check) {
            String msggg = c.getString("protect.chat.format").replace("$msg", ms).replace("$player", p.getName()).replace("&", "§");
            for(Player pl : Bukkit.getOnlinePlayers()) {
                if(pl.hasPermission("happycraft.see")) {
                    pl.sendMessage(msggg);
                }

            }
            Database.addReputation(p.getName(), "-", (float) 0.07);
            return false;
        }
        return true;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void chat(AsyncPlayerChatEvent e) throws SQLException {
        int cd_c = Network.c.getInt("chat.cooldown.chat.time");
        if(Cooldown.hasCooldown(e.getPlayer().getName(), "chat")){
            int time = (int) (Cooldown.getCooldown(e.getPlayer().getName(), "chat") / 1000);
            String title_c = Network.c.getString("chat.cooldown.chat.title").replace("$time", time + "").replace("&", "§");
            String stitle_c = Network.c.getString("chat.cooldown.chat.subtitle").replace("$time", time + "").replace("&", "§");
            e.getPlayer().sendTitle(title_c, stitle_c);
            e.setCancelled(true);
            return;
        }


        if(!Database.isOwner(e.getPlayer().getName()))Cooldown.setCooldown(e.getPlayer().getName(), cd_c * 1000L, "chat");
        String message = e.getMessage().replace("\\", "").replace("'", "").trim();

        if(e.isCancelled()) return;
        Pattern pat1 = Pattern.compile("(!|)((([A-z\\dА-я#\\+\\-,/?\\.():<>=%$&*])+)( |$))+");
        Matcher matcher1 = pat1.matcher(message);
        StringBuilder m2 = new StringBuilder();
        while (matcher1.find()) {
            m2.append(matcher1.group());
        }



        if((m2.toString().startsWith("!") && m2.length() <=1) || m2.length() <=0){
            e.setCancelled(true);
            return;
        }
        String m;
        try {
            String don = PlaceholderAPI.setPlaceholders(e.getPlayer(), "%vault_group%");
            Database.addlog(e.getPlayer().getName(), e.getMessage(), "chat", don);
        }catch (Exception ignored){
        }


        String format = "";

        String dc = "§" + Network.c.getString("chat.defaultcolor");
        if(m2.toString().startsWith("!")) {
            format = Network.c.getString("chat.format.Global");

            if(Bukkit.getOnlinePlayers().size() == 0) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Network.c.getString("chat.emptiness").replace("&", "§"));
            }else {
                e.setCancelled(true);
                String msg = m2.substring(1);
                if(e.getPlayer().hasPermission("chat.color")){
                    msg=ChatColor.translateAlternateColorCodes('&', msg);
                }
                e.setCancelled(true);
                TextComponent comp = new TextComponent(ComponentSerializer.parse(form(msg, format, dc, e.getPlayer(), e.getPlayer().getDisplayName())));

                if(!control(m2.toString(), e.getPlayer())){
                    e.getPlayer().spigot().sendMessage(comp);
                    return;
                }
                
                Bukkit.broadcast(comp);
                m = Database.botAnswer(msg.replace("!", "").replace("?", "")
                        .replace("&", "")
                        .replace(".", "")
                        .replace(",", "")
                        .replace("-", "")
                );
                if(m != null)Bukkit.broadcastMessage("§7[§aДобрый бот§7]§f: " + m);

            }
        }else {
            e.setCancelled(true);
            if(!control(m2.toString(), e.getPlayer())){
                format = Network.c.getString("chat.format.Local");
                String msg = m2.toString();
                TextComponent comp = new TextComponent(ComponentSerializer.parse(form(msg, format, dc, e.getPlayer(), e.getPlayer().getDisplayName())));
                e.getPlayer().sendMessage(comp);
                return;
            }

            format = Network.c.getString("chat.format.Local");

            int all = 0;
            int range = Network.c.getInt("chat.range");
            String msg = m2.toString();
            if(e.getPlayer().hasPermission("chat.color"))msg=msg.replace("&", "§");
            TextComponent comp = new TextComponent(ComponentSerializer.parse(form(msg, format, dc, e.getPlayer(), e.getPlayer().getDisplayName())));
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(e.getPlayer().getWorld().equals(p.getWorld())) {
                    if(e.getPlayer().getLocation().distance(p.getLocation())<range) {
                        if(e.getPlayer() != p) {
                            p.sendMessage(comp);
                            all++;
                        }
                    }
                }
            }
            if(all == 0) {
                e.getPlayer().sendMessage(Network.c.getString("chat.emptiness").replace("&", "§"));
            }else {
                if(e.getPlayer().hasPermission("chat.color"))msg=msg.replace("&", "§");
                e.getPlayer().sendMessage(comp);
            }
            m = Database.botAnswer(msg.replace("!", ""));
            if(m != null)Bukkit.broadcastMessage("§7[§aДобрый бот§7]§f: " + m);
        }
    }



    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            if (!p.hasPermission("clearchat")){
                p.sendMessage(Network.c.getString("chat.prem").replace("&", "§"));
                return true;
            }
        }
        for (int i1 = 100; i1 >=0; i1--){
            Bukkit.broadcastMessage(" ");
        }
        Bukkit.broadcastMessage(Network.c.getString("chat.clear").replace("$user", commandSender.getName()).replace("&", "§"));
        return false;
    }
}
