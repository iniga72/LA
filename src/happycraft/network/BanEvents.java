package happycraft.network;

import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;


import java.util.ArrayList;

public class BanEvents implements Listener{
        @EventHandler
        public void join(PlayerJoinEvent e) {
            if(md.CheckIPPunish(e.getPlayer().getAddress().getAddress().getHostAddress().replace("/", ""))) {
                //e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, md.createIPDiscriprion(e.getAddress().toString().replace("/", "")));
                e.getPlayer().kickPlayer(md.createIPDiscriprion(e.getPlayer().getAddress().toString().replace("/", "")));
                FileConfiguration c = Network.c;
                if(c.getBoolean("bans.notify.ban.enable")) {
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(p.hasPermission("luckybans.notyfy")) {
                            BanPropertis bp = Database.getLastIPPunish(e.getPlayer().getAddress().toString().replace("/", "").toLowerCase(), "ban");
                            String message = c.getString("bans.notify.ban.message");
                            if (!bp.getTime().equalsIgnoreCase("permanent"))
                                message = message.replace("$time", md.FDT(Long.parseLong(bp.getTime()), true));
                            else {
                                message = message.replace("$time", c.getString("bans.messages.permanent"));

                            }
                            message = message.replace("$player", e.getPlayer().getName());
                            p.sendMessage(message.replace("&", "§"));
                        }
                    }
                }
                return;
            }
            if(md.CheckPunish(e.getPlayer().getName(),"ban")) {
                //e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST.KICK_WHITELIST, md.createDiscriprion(e.getPlayer().getName(), "ban"));
                e.getPlayer().kickPlayer(md.createDiscriprion(e.getPlayer().getName(), "ban"));
                FileConfiguration c = Network.c;
                if(c.getBoolean("bans.notify.ban.enable")) {
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(p.hasPermission("luckybans.notyfy")) {
                            BanPropertis bp = Database.getLastPunish(e.getPlayer().getName().toLowerCase(), "ban");
                            String message = c.getString("bans.notify.ban.message");
                            if(!bp.getTime().equalsIgnoreCase("permanent"))message =message.replace("$time", md.FDT(Long.parseLong(bp.getTime()),true));
                            else {
                                message = message.replace("$time", c.getString("bans.messages.permanent"));

                            }
                            message =message.replace("$player",e.getPlayer().getName());
                            p.sendMessage(message.replace("&", "§"));
                        }
                    }
                }
                return;
            }

            if(Database.getRankID(e.getPlayer().getName()) ==0)Database.loadRank(e.getPlayer().getName());
            //getAccounts
            //p
            ArrayList<String> users = Database.getAccounts(e.getPlayer().getAddress().getAddress().toString().replace("/", ""));
            if(users.size() > 1){
                String msg = Network.c.getString("protect.accounts").replace("&", "§");
                for(String s : users){
                    if(md.CheckPunish(s,"ban"))msg+= " §c" + s + "§d,";
                    else if(e.getPlayer().getName().equals(s))msg+= " §e" + s + "§d,";
                    else if(Bukkit.getPlayer(s) != null)msg+= " §a" + s + "§d,";
                    else msg+= " §7" + s + "§d,";
                }
                msg = msg.substring(0, msg.length()-1);
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(p != e.getPlayer() && p.hasPermission("*"))p.sendMessage(msg);
                }
            }

/*
            if((Bukkit.getOnlinePlayers().size() -Network.online) >=1){
                Network.online = Bukkit.getOnlinePlayers().size();
                String code = RandomStringUtils.randomAlphabetic(5) + "-" +
                        RandomStringUtils.randomAlphabetic(5) + "-" +
                        RandomStringUtils.randomAlphabetic(5) + "-" +
                        RandomStringUtils.randomAlphabetic(5) + "-" +
                        RandomStringUtils.randomAlphabetic(5);
                int pmoney = 1 + (int)(Math.random() * (Network.online/4));
                int pcount = 1 + (int)(Math.random() * (Network.online/6));
                Database.addPromo(code, pcount, pmoney);
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.sendTitle("§cИщи в чате промо код за онлайн " + Network.online, "§d"+pmoney+"р §b/pc " + code);
                }
                Bukkit.broadcastMessage(Network.c.getString("promo.online")
                        .replace("$code", code)
                        .replace("$count", pcount + "")
                        .replace("&", "§"));
            }else if((Bukkit.getOnlinePlayers().size() - Network.online) >-5){
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.sendTitle("§cИгроков до промо кода: §c " + (1+Network.online - Bukkit.getOnlinePlayers().size() ), "§b/pc");
                }
            }else if((Bukkit.getOnlinePlayers().size() - Network.online) >-15){
                Network.online = Bukkit.getOnlinePlayers().size() + 3;
            }*/
        }
        @EventHandler
        public void eventChat(AsyncPlayerChatEvent e) {
            if(md.CheckPunish(e.getPlayer().getName(),"mute")) {
                e.setCancelled(true);
                FileConfiguration c = Network.c;
                e.getPlayer().sendMessage(md.createDiscriprion(e.getPlayer().getName(), "mute"));
                if(c.getBoolean("bans.notify.mute.enable")) {
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(p.hasPermission("luckybans.notyfy") && p != e.getPlayer()) {
                            BanPropertis bp = Database.getLastPunish(e.getPlayer().getName().toLowerCase(), "mute");
                            String message = c.getString("bans.notify.mute.message");
                            if(!bp.getTime().equalsIgnoreCase("permanent"))message =message.replace("$time", md.FDT(Long.parseLong(bp.getTime()),true));
                            else {
                                message = message.replace("$time", c.getString("bans.messages.permanent"));
                            }
                            message =message.replace("$player",e.getPlayer().getName());
                            p.sendMessage(message.replace("&", "§"));
                        }
                    }
                }
            }
        }
        @EventHandler
        public void cmdChat(PlayerCommandPreprocessEvent e) {
            if(md.CheckPunish(e.getPlayer().getName(),"mute")) {
                FileConfiguration c = Network.c;
                for (String command : c.getStringList("bans.blacklist")) {
                    String com = e.getMessage();
                    if (com.toLowerCase().startsWith("/" + command + " ") || com.equalsIgnoreCase("/" + command)) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(md.createDiscriprion(e.getPlayer().getName(), "mute"));
                        if(c.getBoolean("bans.notify.mute.enable")) {
                            for(Player p : Bukkit.getOnlinePlayers()) {
                                if(p.hasPermission("luckybans.notyfy") && p != e.getPlayer()) {
                                    BanPropertis bp = Database.getLastPunish(e.getPlayer().getName().toLowerCase(), "mute");
                                    String message = c.getString("bans.notify.mute.message");
                                    if(!bp.getTime().equalsIgnoreCase("permanent"))message =message.replace("$time", md.FDT(Long.parseLong(bp.getTime()),true));
                                    else {
                                        message = message.replace("$time", c.getString("bans.messages.permanent"));
                                    }message =message.replace("$player",e.getPlayer().getName());
                                    p.sendMessage(message.replace("&", "§"));
                                }
                            }
                        }
                        return;
                    }

                }
            }
        }


}
