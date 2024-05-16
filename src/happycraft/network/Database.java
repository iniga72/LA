package happycraft.network;

import happycraft.network.params.Case;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class Database {



    public static Case getPlayerCase(String nick, String type) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `Cases` WHERE `nick` = '"+nick.toLowerCase()+"' and `type` = '"+type+"'");
            int count = 0;
            if(rs.next())
                if(rs.getString("donate").equalsIgnoreCase("true"))
                    return new Case(true, rs.getInt("id"));
                else  return new Case(false, rs.getInt("id"));
        } catch (SQLException ignored) {
        }
        return null;
    }
    public static Integer getPlayerCountCase(String nick, String type) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT COUNT(*) FROM `Cases` WHERE `nick` = '"+nick.toLowerCase()+"' and `type` = '"+type+"'");
            if(rs.next())return rs.getInt("COUNT(*)");
        } catch (SQLException ignored) {
        }
        return 0;
    }
    // Exact same method here, Except as mentioned above i am looking for total!
    public static boolean getItembyId(String nick, String id) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `inventory` WHERE `nick` = '"+nick+"' AND `id` = '"+id+"';");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static Integer getItemId(String nick, String type) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `inventory` WHERE `nick` = '"+nick+"' AND `item` = '"+type+"';");
            if(rs.next())return rs.getInt("id");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getPromoId(String code) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `code` WHERE BINARY `code` = '"+code+"';");
            if(rs.next())return rs.getInt("id");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getPromoMax(String code) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `code` WHERE `code` = '"+code+"' AND `date` > DATE_SUB(CURDATE(), INTERVAL 1 DAY);");
            if(rs.next())return rs.getInt("count");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getPromoMoney(String code) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `code` WHERE `code` = '"+code+"';");
            if(rs.next())return rs.getInt("money");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getPromoCount(String code) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT COUNT(*) FROM `promo` WHERE BINARY `code` = '"+code+"';");
            if(rs.next())return rs.getInt("COUNT(*)");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getReputationCount(String user) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+user+"'");
            if(rs.next()){
                float rep = rs.getFloat("reputation");
                return (int)rep;
            }
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getUserPromoId(String code, String user) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `promo` WHERE `code` = '"+code+"' AND `user` = '"+user.toLowerCase()+"';");
            if(rs.next())return rs.getInt("id");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static void addPromoCode(String player, String name) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            //INSERT INTO `promo` (`id`, `user`, `code`) VALUES (NULL, 'asfasf', 'asfasf');
            ps.execute("INSERT INTO `promo` (`id`, `user`, `code`) VALUES(NULL,'"+player.toLowerCase()+"','"+name+"')");
        } catch (SQLException ignored) {

        }
    }
    public static void addPromo(String code, int count, int money) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            //INSERT INTO `promo` (`id`, `user`, `code`) VALUES (NULL, 'asfasf', 'asfasf');
            ps.execute("INSERT INTO `code` (`id`, `code`, `count`, `money`, `date`) VALUES(NULL,'"+code+"','"+count+"', '"+money+"', NOW())");
        } catch (SQLException ignored) {

        }
    }
    public static Integer getQuestId(String nick, String type) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `quest` WHERE `nick` = '"+nick+"' AND `name` = '"+type+"';");
            int count = 0;
            while(rs.next()){
                count =  rs.getInt("id");
            }
            return count;
        } catch (SQLException ignored) {
            
        }
        return 0;
    }
    public static Integer getQuestEnableId(String nick) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `quest` WHERE `nick` = '"+nick+"' AND `status` = 'enable';");
            int count = 0;
            while(rs.next()){
                count =  rs.getInt("id");
            }
            return count;
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getAllDonate(String nick) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `donathistory` WHERE `nick` = '"+nick+"'");
            int count = 0;
            while(rs.next()){
                count +=rs.getInt("bal");
            }
            return count;
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getQuestCount(int id) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `quest` WHERE `id` = '"+id+"';");
            int count = 0;
            while(rs.next()){
                count =  rs.getInt("count");
            }
            return count;
        } catch (SQLException ignored) {
            
        }
        return 0;
    }
    public static void setphone(String name, String param, String ip) {
        String sql = "UPDATE Protector SET `ip` = '"+ip+"', `code` = '"+param+"' WHERE `nick` = '"+name.toLowerCase()+"'";
        try {
            Statement ps = Network.connection.createStatement();

            ps.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static String getQuestName(int id) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `quest` WHERE `id` = '"+id+"';");
            String count = "";
            while(rs.next()){
                count =  rs.getString("name");
            }
            return count;
        } catch (SQLException ignored) {

        }
        return "";
    }
    public static String getDiscord(String player) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player.toLowerCase()+"'");
            if(rs.next())return rs.getString("discord");
        } catch (SQLException ignored) {
        }
        return "";
    }
    public static String getIP(String player) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player.toLowerCase()+"'");
            if(rs.next())return rs.getString("ip");
        } catch (SQLException ignored) {
        }
        return "";
    }
    public static String getDiscordPlayer(String ds) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `discord` = '"+ds+"'");

            if(rs.next())return rs.getString("nick");
        } catch (SQLException ignored) {
        }
        return "";
    }
    public static String botAnswer(String quest) {
        quest = quest.replace("?", "");
        quest = quest.replace(",", "");
        quest = quest.replace(".", "");
        quest = quest.replace("!", "").trim();
        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `bot` WHERE `question` = '"+quest+"'");
            if(rs.next()){
                String id = rs.getString("answer");
                ps = Network.connection.createStatement();
                rs = ps.executeQuery("SELECT * FROM `botanswer` WHERE `ask` = '"+id+"' order by rand()");
                rs.next();
                return rs.getString("answer");
            }



        } catch (SQLException ignored) {
        }
        return null;
    }
    public static void AutoMessage(Player p) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `automessage` order by rand()");
            while (rs.next()){
                if(p.hasPermission(rs.getString("permission"))){
                    if(!p.hasPermission(rs.getString("unpermission"))){
                        long sec = p.getStatistic(Statistic.PLAY_ONE_TICK) / 20;
                        long m = TimeUnit.SECONDS.toMinutes(sec);
                        if(m < rs.getInt("mintime")){
                            if(m > rs.getInt("maxtime")){
                                String msg = rs.getString("msg")
                                        .replace("$player", p.getName())
                                        .replace("&", "§")
                                        ;
                                TextComponent comp = new TextComponent(ComponentSerializer.parse(msg));
                                p.sendMessage(comp);
                                return;
                            }
                        }
                    }
                }

            }



        } catch (SQLException ignored) {
        }
    }
    public static void updateDiscord(String player, String ds) throws SQLException {
        String sql = "UPDATE `LuckyAuth` SET `discord` = '"+ds+"' WHERE `nick` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);
    }
    public static void updateQuest(int id, String type) throws SQLException {
        String sql = "UPDATE `quest` SET `status` = '"+type+"' WHERE `id` = '"+id+"'";
             Statement pstmt = Network.connection.createStatement();
            pstmt.execute(sql);
    }
    public static void updateQuestCount(int id, int count) throws SQLException {
        String sql1 = "UPDATE `quest` SET `count` = '"+count+"' WHERE `id` = '"+id+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql1);

    }
    public static void addQuestNew(String player, String name) {
         
        Statement ps;
        try {
             
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("INSERT INTO `quest` (`id`,`nick`,`name`,`status`,`count`) VALUES(NULL,'"+player.toLowerCase()+"','"+name+"','enable',0)");
        } catch (SQLException ignored) {

        }
    }
//    public static String getVK(String player) {
//
//        Statement ps;
//        ResultSet rs;
//        try {
//
//            ps = Network.connection.createStatement();
//            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player.toLowerCase()+"';");
//
//
//            if(rs.next())return rs.getString("vk");
//        } catch (SQLException ignored) {
//
//        }
//        return null;
//    }
    public static String getProtector(String region) {
         
        Statement ps;
        ResultSet rs;
        try {
             
            ps = Network.connection.createStatement();

            rs = ps.executeQuery("SELECT * FROM `RegionProtector` WHERE `region` = '"+region+"';");

            while(rs.next()){
                if(rs.getString("region").equalsIgnoreCase(region.toLowerCase())){ // Tell database to search for the player you sent into the method. e.g getTokens(sam) It will look for sam.
                    return rs.getString("player"); // Return the players ammount of kills. If you wanted to get total (just a random number for an example for you guys) You would change this to total!
                }
            }
        } catch (SQLException ignored) {
            
        } 
        return null;
    }
    public static void removeProtector(String name) {
         
        Statement ps;
        try {
            ps = Network.connection.createStatement();
            ps.execute("DELETE FROM `RegionProtector` WHERE `region` = '"+name +  "'");
        } catch(Exception ignored) {

        }
    }
    public static void setProtector(String region, String player) {
         
        Statement ps;
        try {
             
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("INSERT INTO `RegionProtector` (`player`,`region`) VALUES('"+player+"','"+region+"')");
        } catch (SQLException ignored) {
            
        }
    }
    public static float getMoney(String nick) {

        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+nick.toLowerCase()+"';");
            if(rs.next())return rs.getFloat("money");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static float getClanMoney(String clan) {

        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `Clans` WHERE `name` = '"+clan+"';");
            if(rs.next())return rs.getFloat("balance");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static String getDate(String nick) {
         
        Statement ps;
        ResultSet rs;
        try {
             
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `EveryDay` WHERE `nick` = '"+nick+"';");
            String count = "";
            while(rs.next()){
                count =  rs.getString("date");
            }
            return count;
        } catch (SQLException ignored) {
            
        } 
        return "";
    }

    public static Integer getDay(String nick) {
         
        Statement ps;
        ResultSet rs;
        try {
             
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `EveryDay` WHERE `nick` = '"+nick+"';");
            int count = 0;
            while(rs.next()){
                count =  rs.getInt("day");
            }
            return count;
        } catch (SQLException ignored) {
            
        } 
        return 0;
    }
    public static Integer getJail(String nick) {

        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `jail` WHERE `user` = '"+nick.toLowerCase()+"' AND `count` >= 1;");
            int count = 0;
            while (rs.next()){
                count +=  rs.getInt("count");
            }
            return count;
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static String getJailBlock(String nick) {
        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `jail` WHERE `user` = '"+nick.toLowerCase()+"' AND `count` >= 1 ORDER by id");
            if(rs.next())return rs.getString("block");

        } catch (SQLException ignored) {

        }
        return null;
    }/*
    public Integer getFreeCount(String nick) {
         
        Statement ps;
        ResultSet rs;
        try {
             
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `Free` WHERE `nick` = '"+nick+"';");
            int count = 0;
            while(rs.next()){
                count =  rs.getInt("count");
            }
            return count;
        } catch (SQLException ignored) {
            
        } 
        return 0;
    }
    public static Integer getTimeID(String nick) {
         
        Statement ps;
        ResultSet rs;
        try {
             
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `Free` WHERE `nick` = '"+nick+"';");
            int count = 0;
            while(rs.next()){
                count =  rs.getInt("id");
            }
            return count;
        } catch (SQLException ignored) {
            
        } 
        return 0;
    }*/

    public static ArrayList<String> getAccounts(String ip) {

        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `ip` = '"+ip+"';");
            ArrayList<String> count = new ArrayList<>();
            while (rs.next()){
                count.add(rs.getString("realname"));

            }
            return count;
        } catch (SQLException ignored) {

        }
        return new ArrayList<>();
    }

    public static void updateMoney(String player, float count) throws SQLException {
        String sql = "UPDATE `LuckyAuth` SET `money` = "+count+" WHERE `nick` = '"+player.toLowerCase()+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);

        sql = "INSERT INTO `zlogs` (`id`, `user`, `log`, `type`, `date`, `don`) VALUES (NULL, '"+player+"', '"+count+"', 'bal', NOW(), '-');";
        pstmt = Network.connection.createStatement();

        pstmt.execute(sql);


    }
    public static void updateClanMoney(String player, float count) throws SQLException {
        String sql = "UPDATE `Clans` SET `balance` = '"+count+"' WHERE `name` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);
    }


    public static void updateEveryDay(String player, String date, int day) throws SQLException {
        String sql = "UPDATE EveryDay SET date = '"+date+"', day = '"+day+"' WHERE nick = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
            pstmt.execute(sql);
    }
    public static void updateWarn(String player, String warns) throws SQLException {
        String sql = "INSERT INTO `warning` (`id`, `player`, `warn`, `time`) VALUES (NULL, '"+player.toLowerCase()+"', '"+warns+"', NOW());";
        Statement pstmt = Network.connection.createStatement();

        pstmt.execute(sql);
    }
    public static void addlog(String player, String args, String type, String don) throws SQLException {
        String sql = "INSERT INTO `zlogs` (`id`, `user`, `log`, `type`, `date`, `don`) VALUES (NULL, '"+player+"', '"+args+"', '"+type+"', NOW(), '"+don+"');";
        Statement pstmt = Network.connection.createStatement();

        pstmt.execute(sql);
    }

    public static void updatejail(String player, String block) throws SQLException {
        Statement ps;
        ResultSet rs;
        ps = Network.connection.createStatement();
        rs = ps.executeQuery("SELECT * FROM `jail` WHERE `user` = '"+player.toLowerCase()+"' AND `count` >= 1 ORDER BY `id`");
        rs.next();
        int id = rs.getInt("id");
        int count = rs.getInt("count");
        count--;

        String sql = "UPDATE `jail` SET `count` = '"+count+"' WHERE `id` = '"+id+"';";
        ps = Network.connection.createStatement();
        ps.execute(sql);
        sql = "UPDATE `jail` SET  `block` = '"+block+"' WHERE `user` = '"+player.toLowerCase()+"';";
        ps = Network.connection.createStatement();
        ps.execute(sql);
    }
    public static void setjail(String player, int warns,String admin, String block) throws SQLException {
        Statement ps;
        ps = Network.connection.createStatement();
        ps.execute("INSERT INTO `jail` (`id`, `user`, `count`, `admin`, `block`) VALUES (NULL, '"+player+"', '"+warns+"', '"+admin+"', '"+block+"');");

    }


    public static void adddonate(String donate) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("INSERT INTO `donate` (`id`,`donate`) VALUES(NULL,'"+donate+"')");
        } catch (SQLException ignored) {

        }
    }
    public static ArrayList<String> getReferals(String user) {
        Statement ps;
        ResultSet rs;
        ArrayList<String> users = new ArrayList<>();
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `referaladmin` = '"+user.toLowerCase()+"';");

            while (rs.next()){
                users.add(rs.getString("realname"));
            }
        } catch (SQLException ignored) {

        }
        return users;
    }

    public static Integer donLimit(String code) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT COUNT(*) FROM `donate` WHERE `donate` = '"+code+"';");
            if(rs.next())return rs.getInt("COUNT(*)");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static void donLimitRemove(String code) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `donate` WHERE `donate` = '"+code+"'");
            if(rs.next()){
                int id = rs.getInt("id");
                ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
                ps.execute("DELETE FROM `donate` WHERE `id` = '"+id+"'");
            }
        } catch (SQLException ignored) {

        }
    }
    public static boolean checkReputation(String user, String admin) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `reputation` WHERE `user` = '"+user.toLowerCase()+"' AND `admin` = '"+admin.toLowerCase()+"';");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static void addReputation(String player, String type, Float count) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("UPDATE `LuckyAuth` SET `reputation` = `reputation` " + type + " " + count +" WHERE `nick` = '"+player+"'");
        } catch (SQLException ignored) {

        }
    }
    public static boolean checkReferal(String user) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+user.toLowerCase()+"' and `referaladmin` != '';");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static boolean isCommandSpy(String user) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+user.toLowerCase()+"' and `cspy` = 'true';");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static boolean isOwner(String user) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+user.toLowerCase()+"' and `owner` = 'true';");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static boolean isModerator(String user) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+user.toLowerCase()+"' and `moderator` = 'true';");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }

    public static void setCspy(String player, boolean st) throws SQLException {
        String sql = "UPDATE `LuckyAuth` SET  `cspy` = '"+st+"' WHERE `nick` = '"+player.toLowerCase()+"';";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);
    }
    public static void updateTime(String player, int time) throws SQLException {
        String sql = "UPDATE `LuckyAuth` SET `time` = '"+time+"' WHERE `nick` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);
    }
    public static void updateDonate(String player, String donate) throws SQLException {
        String sql = "UPDATE `LuckyAuth` SET `donate` = '"+donate+"' WHERE `nick` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);
    }
    public static String getDonate(String player) throws SQLException {
        String sql = "SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
        ResultSet rs = pstmt.executeQuery(sql);
        if(rs.next()) return rs.getString("donate");
        return "user";
    }
    public static String getJoin(String player) throws SQLException {
        String sql = "SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
        ResultSet rs = pstmt.executeQuery(sql);
        if(rs.next()) return rs.getString("lastjoin");
        return "очень давно";
    }
    public static void deleteReferal(String player) throws SQLException {
        String sql = "UPDATE `LuckyAuth` SET `referaladmin` = '' WHERE `nick` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);
    }
    public static String getReferalOwner(String player) throws SQLException {
        String sql = "SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement();
        ResultSet rs = pstmt.executeQuery(sql);
        if(rs.next()) return rs.getString("referaladmin");
        return "";
    }
    public static boolean getVK(String player) throws SQLException {
        String sql = "SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player+"' and `vk` != 0";
        Statement pstmt = Network.connection.createStatement();
        ResultSet rs = pstmt.executeQuery(sql);
        if(rs.next()) return true;
        return false;
    }
    public static ArrayList<BalTOP> getbaltop(int str) {

        Statement ps;
        ArrayList<BalTOP> users = new ArrayList<>();
        try {

            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM LuckyAuth ORDER BY `money` DESC LIMIT 15");
            while (rs.next()){
                users.add(new BalTOP(rs.getString("realname"), rs.getFloat("money")));
            }
        } catch (SQLException ignored) {

        }
        return users;
    }
    public static ArrayList<DonatHistory> DonatHystory() {

        Statement ps;
        ArrayList<DonatHistory> users = new ArrayList<>();
        try {

            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM donathistory ORDER BY `date` DESC LIMIT 9");
            while (rs.next()){
                users.add(new DonatHistory(rs.getInt("id"), rs.getInt("bal"), rs.getString("nick"), rs.getString("date")));
            }
        } catch (SQLException ignored) {

        }
        return users;
    }
    public static String getReferalkey(String player) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

            ResultSet rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player+"'");
            if(rs.next()){
                return rs.getString("referalkey");
            }
            return null;
        } catch (SQLException ignored) {
        }
        return null;
    }
    public static boolean isFreePrefix(String player) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

            ResultSet rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player+"' and `prefixtime` >= NOW() - INTERVAL 5 MINUTE");
            if(rs.next())return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    public static boolean isKonkurs(String player) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

            ResultSet rs = ps.executeQuery("SELECT * FROM `game` WHERE `user` = '"+player+"'");
            if(rs.next())return true;
        } catch (SQLException ignored) {
        }
        return false;
    }
    public static void setFreePrefix(String player) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

           ps.execute("UPDATE `LuckyAuth` SET `prefixtime`= NOW() WHERE `nick` = '"+player+"'");

        } catch (SQLException ignored) {
        }
    }
public static void addConsole(String player, String type) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

           ps.execute("UPDATE `LuckyAuth` SET `console`= '"+type+"' WHERE `nick` = '"+player+"'");

        } catch (SQLException ignored) {
        }
    }


    public static void updateProtectIP(String player, String host) {

        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("UPDATE `Protector` SET `ip`='"+host+"' WHERE `nick` = '"+player+"'");
        } catch (SQLException ignored) {

        }
    }
    public static void addReferal(String player, String admin) {
        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("UPDATE `LuckyAuth` SET `referaladmin` = '"+admin+ "' WHERE `nick` = '"+player+"'");
        } catch (SQLException ignored) {

        }
    }


    public static void ClanCreate(String tag) {
         
        Statement ps;
        try {
             
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("INSERT INTO `Clans` (`id`,`name`,`home`) VALUES(NULL,'"+tag+"','')");

        } catch (SQLException ignored) {
            
        } 
    }
    public static void ClanJoin(String p, String tag, String st) {
         
        Statement ps;
        try {
             
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("INSERT INTO `ClanUser` (`id`,`nick`,`clan`,`status`) VALUES(NULL,'" +p.toLowerCase()+ "','" +tag + "','" + st+ "')");
        } catch (SQLException ignored) {
            
        } 
    }
    public static Integer getClanID(String tag) {

        Statement ps;

        try {

            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM `Clans` WHERE `name` = '"+tag+"';");
            if(rs.next())return rs.getInt("id");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static Integer getProgressDonate(String donate) {
        Statement ps;
        try {

            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT `bal` FROM `progress` WHERE `donate` = '"+donate+"'");
            if(rs.next())return rs.getInt("bal");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static boolean ChatControl(String msg) {



        try {

            Statement ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM `chat` WHERE `msg` = '"+msg+"';");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static String getClan(String nick) {
         
        Statement ps;

        try {
             
            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM `ClanUser` WHERE `nick` = '"+nick.toLowerCase()+"';");
            if(rs.next())return rs.getString("clan");
        } catch (SQLException ignored) {
            
        } 
        return null;
    }//INSERT INTO `grant` (`id`, `admin`, `user`, `donat`, `date`) VALUES (NULL, 'admin', 'user', 'fly', NOW());
    public static int getMyGrant(String nick, String don) {

        Statement ps;

        try {
            //SELECT * FROM warning WHERE
            ps = Network.connection.createStatement();
//          SELECT COUNT(*) FROM `grant` WHERE `date` > DATE_SUB(CURDATE(), INTERVAL 1 MONTH);
            ResultSet rs = ps.executeQuery("SELECT COUNT(*) FROM `grant` WHERE  `admin` = '"+nick.toLowerCase()+"' AND `donat` = '"+don+"' AND `date` > DATE_SUB(CURDATE(), INTERVAL 1 MONTH);");
            if(rs.next())return rs.getInt("COUNT(*)");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static int addGrant(String nick, String don, String user) {

        Statement ps;

        try {
            //SELECT * FROM warning WHERE
            ps = Network.connection.createStatement();
            ps.execute("INSERT INTO `grant` (`id`, `admin`, `user`, `donat`, `date`) VALUES (NULL, '"+nick+"', '"+user+"', '"+don+"', NOW());");

        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static int getWarn(String nick) {

        Statement ps;

        try {
            //SELECT * FROM warning WHERE
            ps = Network.connection.createStatement();
//          SELECT COUNT(*) FROM `grant` WHERE `date` > DATE_SUB(CURDATE(), INTERVAL 1 MONTH);
            ResultSet rs = ps.executeQuery("SELECT COUNT(*) FROM `warning` WHERE `player` = '"+nick.toLowerCase()+"' AND `time` > DATE_SUB(CURDATE(), INTERVAL 1 MONTH);");
            if(rs.next())return rs.getInt("COUNT(*)");
        } catch (SQLException ignored) {

        }
        return 0;
    }
    public static boolean isJaildonat(String nick) {

        Statement ps;

        try {
            //SELECT * FROM warning WHERE
            ps = Network.connection.createStatement();
//          SELECT COUNT(*) FROM `grant` WHERE `date` > DATE_SUB(CURDATE(), INTERVAL 1 MONTH);
            ResultSet rs = ps.executeQuery("SELECT * FROM `donathistory` WHERE `nick` = '"+nick+"' AND `date` > DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND `bal` > 100;");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static void removeClan(String tag) throws SQLException {
        String sql = "DELETE FROM `Clans` WHERE `name` = '" +tag + "'";
        Statement pstmt = Network.connection.createStatement();
            pstmt.execute(sql);
        sql = "DELETE FROM `ClanUser` WHERE `clan` = '" + tag+ "'";

              pstmt = Network.connection.createStatement();
            pstmt.execute(sql);

    }
    public static void ClanLeave(String nick) throws SQLException {
        String sql = "DELETE FROM `ClanUser` WHERE `nick` = '" + nick.toLowerCase()+ "'";

        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);
    }
    public static void addProtect(String nick, String key, String ip) throws SQLException {
        String sql = "INSERT INTO `Protector` (`id`, `nick`, `code`, `ip`) VALUES (NULL, '"+nick+"', '"+key+"', '"+ip+"');";

        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);
    }

    public static String getStatus(String nick) {
         
        Statement ps;

        try {
             
            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM `ClanUser` WHERE `nick` = '"+nick.toLowerCase()+"';");
            if(rs.next())return rs.getString("status");
        } catch (SQLException ignored) {
            
        } 
        return null;
    }

    public static void loadRank(String player) {
         
        Statement ps;
        try {
             
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

            ps.execute("INSERT INTO Rank (`id`,`nick`,`rank`,`kills`) VALUES(NULL,'" + player.toLowerCase()+ "',NULL,0)");
        } catch (SQLException ignored) {
            
        } 
    }
    public static void removeItem(int id) throws SQLException {
        String sql = "DELETE FROM `inventory` WHERE `id` = "+id;
        Statement pstmt = Network.connection.createStatement() ;
        pstmt.execute(sql);
    }
    public static void updateItem(String player, String id) throws SQLException {
        String sql = "UPDATE `inventory` SET `nick` = '"+player+"' WHERE `id` = '"+id+"'";
        Statement pstmt = Network.connection.createStatement() ;
        pstmt.execute(sql);
    }
    public static void clearProtect(String player) throws SQLException {
        String sql = "DELETE FROM `Protector` WHERE `nick` = '"+player+"'";
        Statement pstmt = Network.connection.createStatement() ;
        pstmt.execute(sql);
    }
    // Now we need methods to save things to the database
    public static void addCase(String player, String type, boolean donate) {
        Statement ps;
        try {

            ps = Network.connection.createStatement();

            ps = Network.connection.createStatement();
            ps.execute("INSERT INTO `Cases` (`id`, `nick`, `type`, `donate`) VALUES (NULL, '"+player+"', '"+type+"', '"+donate+"');");
        } catch (SQLException ignored) {

        }
    }
    public static void removeCase(int id) {
        Statement ps;
        try {

            ps = Network.connection.createStatement();

            ps = Network.connection.createStatement();
            ps.execute("DELETE FROM `Cases` WHERE `id` = '"+id+"'");
        } catch (SQLException ignored) {

        }
    }
    public static void updateProgressDonate(String donate, int money) {
        Statement ps;
        try {
            ps = Network.connection.createStatement(); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.execute("UPDATE `progress` SET `bal` = "+money+" WHERE `donate` = '"+donate+"'");//`type` = '"+type+"'
            //          UPDATE `Cases` SET `id`=[value-1],`nick`=[value-2],`type`=[value-3],`count`=[value-4] WHERE 1
        } catch (SQLException ignored) {

        }
    }

    public static void setInventoryItem(String player, String type, boolean don, int time) {
         
        Statement ps;
        try {
             
            ps = Network.connection.createStatement();
            ps.execute("INSERT INTO `inventory` (`id`,`nick`,`item`, `donate`, `time`) VALUES(NULL,'" +player.toLowerCase() + "','" +type + "', '"+don+"', "+time+")");

        } catch (SQLException ignored) {
            
        }
    }
    public static void newProtect(String player) {

        Statement ps;
        try {

            ps = Network.connection.createStatement();
            ps.execute("UPDATE `LuckyAuth` SET `op`='true' WHERE `nick` = '"+player+"'");

        } catch (SQLException ignored) {

        }
    }
    public static void clearQuest(String nick) throws SQLException {
        String sql = "DELETE FROM `quest` WHERE `nick` = '" + nick.toLowerCase()+ "'";

             Statement pstmt = Network.connection.createStatement();
            pstmt.execute(sql);

    }
    public static String getProtectHost(String nick) {

        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `Protector` WHERE `nick` = '"+nick+"';");
            if(rs.next())return rs.getString("ip");
        } catch (SQLException ignored) {

        }
        return "";
    }
    public static boolean isProtect(String nick) {

        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+nick+"';");
            if(rs.next() && rs.getString("op").equalsIgnoreCase("true"))return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static boolean checkProtect(String nick, String key) {

        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `Protector` WHERE `nick` = '"+nick+"' AND `code` = '"+key+"';");
            if(rs.next())return true;
        } catch (SQLException ignored) {

        }
        return false;
    }
    public static String getRank(String nick) {
         
        Statement ps;

        try {
             
            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM `Rank` WHERE `nick` = '"+nick.toLowerCase()+"';");
            if(rs.next())return rs.getString("rank");
        } catch (SQLException ignored) {
            
        } 
        return null;
    }
    public static Integer getRankID(String nick) {
         
        Statement ps;

        try {
             
            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM `Rank` WHERE `nick` = '"+nick.toLowerCase()+"';");
            if(rs.next())return rs.getInt("id");
        } catch (SQLException ignored) {
            
        } 
        return 0;
    }
    public static Integer getKillCount(String nick) {
         
        Statement ps;

        try {
             
            ps = Network.connection.createStatement();
            ResultSet rs = ps.executeQuery("SELECT * FROM `Rank` WHERE `nick` = '"+nick.toLowerCase()+"';");
            if(rs.next())return rs.getInt("kills");
        } catch (SQLException ignored) {
            
        } 
        return 0;
    }
    public static void updateKillCount(String player, int count) throws SQLException {
        String sql = "UPDATE `Rank` SET `kills` = " + count+ " WHERE `nick` = '"+player.toLowerCase()+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);

    }
    public static void updateReferalBonus(String player, int count) throws SQLException {
        String sql = "UPDATE `LuckyAuth` SET `referalbonus` = " + count+ " WHERE `nick` = '"+player.toLowerCase()+"'";
        Statement pstmt = Network.connection.createStatement();
        pstmt.execute(sql);

    }
    public static void updateRank(String player, String name) throws SQLException {
        String sql = "UPDATE `Rank` SET `rank` = '" + name+ "' WHERE `nick` = '"+player.toLowerCase()+"'";


             Statement pstmt = Network.connection.createStatement();
            pstmt.execute(sql);

    }
    public static void updateProtect(String player, String ip) throws SQLException {
        String sql = "UPDATE `Protector` SET `ip` = '" +ip + "' WHERE `nick` = '"+player.toLowerCase()+"'";

             Statement pstmt = Network.connection.createStatement();
            pstmt.execute(sql);
    }
    public static happycraft.network.params.items getItem(String id){
        Statement ps;
        ResultSet rs;
        try {

            ps = Network.connection.createStatement();

            rs = ps.executeQuery("SELECT * FROM `inventory` WHERE `id` = '"+id+"';");
            if(rs.next())return new happycraft.network.params.items(rs.getInt("id"), rs.getInt("time"), rs.getString("item"));
        } catch (SQLException ignored) {

        }
        return null;
    }
    public static ArrayList<happycraft.network.params.items> getAllItems(String player) {
         
        Statement ps;
        ResultSet rs;

        ArrayList<happycraft.network.params.items> items = new ArrayList<>();
        try {
             
            ps = Network.connection.createStatement();

            rs = ps.executeQuery("SELECT * FROM `inventory` WHERE `nick` = '"+player.toLowerCase()+"' ORDER by `item`;");
            while(rs.next()){
                items.add(new happycraft.network.params.items(rs.getInt("id"), rs.getInt("time"), rs.getString("item")));
            }
            return items;
        } catch (SQLException ignored) {
            
        } 
        return items;
    }

    public static BanPropertis getLastPunish(String player, String punish) {
         
        Statement ps;
        ResultSet rs;
        try {
             
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyBans` WHERE `player` = '"+player.toLowerCase()+"' AND `punish` = '"+punish+"' AND `statusclear` = '' ORDER BY date DESC LIMIT 9;");
            if(rs.next()){
                return new BanPropertis(rs.getInt("id"),
                        rs.getString("admin"),
                        rs.getString("date"),
                        rs.getString("reason"),
                        rs.getString("time"),
                        rs.getString("punish"),
                        rs.getString("priority"),
                        rs.getString("statusclear")
                        );
            }
        } catch (SQLException ignored) {
            
        } 
        return null;
    }
    public static BanPropertis getLastIPPunish(String ip, String punish) {
         
        Statement ps;
        ResultSet rs;
        try {
             
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyBans` WHERE `ip` = '"+ip+"' AND `punish` = '" + punish + "' AND `statusclear` = '';");

            if(rs.next()){
                return new BanPropertis(rs.getInt("id"),
                        rs.getString("admin"),
                        rs.getString("date"),
                        rs.getString("reason"),
                        rs.getString("time"),
                        rs.getString("punish"),
                        rs.getString("ptiority"),
                        rs.getString("statusclear")
                );
            }
        } catch (SQLException ignored) {
            
        } 
        return null;
    }

    public static ReferalGetTime ReferalBonus(String player) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `nick` = '"+player+"';");
            if(rs.next()){
                ReferalGetTime res = new ReferalGetTime((rs.getInt("time")/ 60), rs.getInt("referalbonus"));
                return res;
            }
        } catch (SQLException ignored) {

        }
        return null;
    }
    public static String getReferal(String key) {
        Statement ps;
        ResultSet rs;
        try {
            ps = Network.connection.createStatement();
            rs = ps.executeQuery("SELECT * FROM `LuckyAuth` WHERE `referalkey` = '"+key+"';");
            if(rs.next()){
                return rs.getString("nick");
            }
        } catch (SQLException ignored) {

        }
        return null;
    }
    /*public int getPriority(String date) {
         
        Statement ps;
        ResultSet rs;
        try {
             
            ps = Network.connection.createStatement();

            rs = ps.executeQuery("SELECT * FROM `LuckyBans` WHERE `date` = '"+date+"';");
            while(rs.next()){
                if(rs.getString("date").equalsIgnoreCase(date.toLowerCase())){ // Tell database to search for the player you sent into the method. e.g getTokens(sam) It will look for sam.
                    return Integer.parseInt(rs.getString("priority")); // Return the players ammount of kills. If you wanted to get total (just a random number for an example for you guys) You would change this to total!
                }
            }
        } catch (SQLException ignored) {
            
        } 
        return 0;
    }*/

    public static void setAdminClear(String name, String punish, String player) throws SQLException {
        String sql = "UPDATE LuckyBans SET `statusclear` = '" + name+ "' WHERE `player` = '"+player.toLowerCase()+"' AND `punish` = '"+punish+"' AND `statusclear` = ''";

             Statement pstmt = Network.connection.createStatement();
            pstmt.execute(sql);
    }


    public static void setPunish(String admin, String player, String reason, String punish, String priority, String ip, String time) {
        Statement ps;
        try {
             
            ps = Network.connection.createStatement();
            ps.execute("INSERT INTO `LuckyBans` (`id`,`player`,`punish`,`admin`,`date`,`priority`,`reason`,`ip`,`time`,`status`,`statusclear`) VALUES(NULL,'" + player.toLowerCase()
                    + "','" +punish
                    + "','" +admin
                    + "'," +"NOW()"
                    + ",'" +priority
                    + "','" +reason
                    + "','" +ip
                    + "','" +time
                    + "','','')");
            //
        } catch (SQLException ignored) {
            
        }
    }










































}

