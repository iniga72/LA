package LuckyAuthServer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.sql.Statement;

public class reload implements CommandExecutor {
    public static void setVK(String name, String param) {
        String sql = "UPDATE LuckyAuth SET `vk` = '"+param+"' WHERE `nick` = '"+name.toLowerCase()+"'";
        try {
            Statement pstmt = main.connection.createStatement();

            pstmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void setphone(String name, String param) {
        String sql = "UPDATE LuckyAuth SET `code` = '"+param+"' WHERE `nick` = '"+name.toLowerCase()+"'";
        try {
            Statement pstmt = main.connection.createStatement();

            pstmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length != 1) return true;

        if (!(sender instanceof Player))return true;

        Player p = (Player) sender;
        if(args[0].equals("accept")){
            if(main.connect.containsKey(p)){
                String id = main.connect.get(p);
                String msg = main.config.getString("vk.connect.accept");
                p.sendMessage(main.config.getString("messages.vk.accept").replace("&", "§"));
                main.api.sendMessage(msg, id, "{\"buttons\":[[{\"action\":{\"type\":\"text\",\"label\":\"Восстановить\",\"payload\":\"\"},\"color\":\"positive\"}],[{\"action\":{\"type\":\"text\",\"label\":\"Бонусы\",\"payload\":\"\"},\"color\":\"negative\"}]],\"inline\":false}");
                main.connect.remove(p);
                setVK(sender.getName(), id);
            }else{
                p.sendMessage(main.config.getString("messages.vk.empty").replace("&", "§"));
            }
            return true;
        }
        if(args[0].equals("decline")){
            if(main.connect.containsKey(p)){
                String id = main.connect.get(p);
                String msg = main.config.getString("vk.connect.decline");
                main.api.sendMessage(msg, id, "");
                p.sendMessage(main.config.getString("messages.vk.decline").replace("&", "§"));
                main.connect.remove(p);
            }else{
                p.sendMessage(main.config.getString("messages.vk.empty").replace("&", "§"));
            }
            return true;
        }


        //decline
        return true;
    }

}