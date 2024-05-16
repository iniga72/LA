package happycraft.network;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Clan implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if(args.length == 0) return help(p);
        if(args.length == 2 && args[0].equalsIgnoreCase("create")) {
            try {
                return create(p,args[1]);
            } catch (SQLException e) {

            }
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("delete")) {
            try {
                return delete(p);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("accept"))return accept(p);
        if(args.length == 1 && args[0].equalsIgnoreCase("bal"))return bal(p);
        if(args.length == 1 && args[0].equalsIgnoreCase("decline"))return decline(p);
        if(args.length == 1 && args[0].equalsIgnoreCase("leave")) {
            try {
                return leave(p);
            } catch (SQLException e) {
                
            }
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("invite"))return invite(p, args[1]);
        if(args.length == 2 && args[0].equalsIgnoreCase("pay")) return pay(p, args[1]);
        if(args.length == 2 && args[0].equalsIgnoreCase("cash")) return cash(p, args[1]);
        help(p);
        return true;
    }
    public boolean help(Player p){
        p.sendMessage("§7[§6Кланы§7]§f: Создать клан: /c create название. Цена: 1 монета");
        p.sendMessage("§7[§6Кланы§7]§f: Удалить клан: /c delete ");
        p.sendMessage("§7[§6Кланы§7]§f: Покинуть клан: /c leave ");
        p.sendMessage("§7[§6Кланы§7]§f: Инвайт игрока в клан: /c invite ");
        p.sendMessage("§7[§6Кланы§7]§f: Передать деньги клану: /c pay сумма ");
        p.sendMessage("§7[§6Кланы§7]§f: Баланс клана: /c bal ");
        p.sendMessage("§7[§6Кланы§7]§f: Взять деньги с казны: /c cash сумма");
        return true;
    }
    public boolean create(Player p, String tag) throws SQLException {

        if(Database.getClan(p.getName()) != null){
            p.sendMessage(Network.c.getString("clan.msg.haveclan").replace("$clan", Database.getClan(p.getName())).replace("&", "§"));
            return true;
        }
        float money = Database.getMoney(p.getName().toLowerCase());
        if(Network.c.getInt("clan.price") > money){
            p.sendMessage(Network.c.getString("clan.msg.price").replace("&", "§"));
            return true;
        }
        if(Database.getClanID(tag) != 0){
            p.sendMessage(Network.c.getString("clan.msg.exists").replace("&", "§"));
            return true;
        }
        if(tag.length()> Network.c.getInt("clan.max")){
            p.sendMessage(Network.c.getString("clan.msg.max").replace("&", "§"));
            return true;
        }
        if(tag.length()< Network.c.getInt("clan.min")){
            p.sendMessage(Network.c.getString("clan.msg.min").replace("&", "§"));
            return true;
        }
        money-=Network.c.getInt("clan.price");
        Database.ClanCreate(tag);
        Database.ClanJoin(p.getName(), tag, "owner");
        Database.updateMoney(p.getName(),money);
        p.sendMessage(Network.c.getString("clan.msg.create").replace("$clan", Database.getClan(p.getName())).replace("&", "§"));
        Network.claninvite.remove(p);
        return true;
    }
    public boolean delete(Player p) throws SQLException {
        if(Database.getClan(p.getName()) == null){
            p.sendMessage(Network.c.getString("clan.msg.haventclan").replace("&", "§"));
            return true;
        }
        if(!Database.getStatus(p.getName()).equalsIgnoreCase("owner")){
            p.sendMessage(Network.c.getString("clan.msg.create").replace("&", "§"));
            return true;
        }
        p.sendMessage(Network.c.getString("clan.msg.delete").replace("$clan", Database.getClan(p.getName())).replace("&", "§"));
        Database.removeClan(Database.getClan(p.getName()));
        return true;
    }
    public boolean leave(Player p) throws SQLException {
        if(Database.getClan(p.getName()) == null){
            p.sendMessage(Network.c.getString("clan.msg.haventclan").replace("&", "§"));
            return true;
        }
        if(Database.getStatus(p.getName()).equalsIgnoreCase("owner")){
            p.sendMessage(Network.c.getString("clan.msg.err").replace("&", "§"));
            return true;
        }
        p.sendMessage(Network.c.getString("clan.msg.leave").replace("$clan", Database.getClan(p.getName())).replace("&", "§"));
        Database.ClanLeave(p.getName());
        return true;
    }
    public boolean pay(Player p, String args) {
        String clan = Database.getClan(p.getName());
        if(clan == null){
            p.sendMessage(Network.c.getString("clan.msg.haventclan").replace("&", "§"));
            return true;
        }
        try {
            float amount = Float.parseFloat(args);
            amount = Precision.round(amount, 3);;
            float money = Database.getMoney(p.getName().toLowerCase());
            if(amount > money){
                p.sendMessage(Network.c.getString("money.enough").replace("&", "§"));
                return true;
            }
            money-=amount;
            Database.updateMoney(p.getName().toLowerCase(), money);
            money = Database.getClanMoney(clan);
            money+=amount;
            Database.updateClanMoney(clan, money);
            p.sendMessage(Network.c.getString("money.send").replace("&", "§"));

            return true;
        }catch (Exception e){
            p.sendMessage(Network.c.getString("clan.msg.moneypelp").replace("&", "§"));
            return true;
        }
    }
    public boolean cash(Player p, String args) {
        String clan = Database.getClan(p.getName());
        if(clan == null){
            p.sendMessage(Network.c.getString("clan.msg.haventclan").replace("&", "§"));
            return true;
        }
        if(!Database.getStatus(p.getName()).equalsIgnoreCase("owner")){
            p.sendMessage(Network.c.getString("clan.msg.create").replace("&", "§"));
            return true;
        }

        try {
            float amount = Float.parseFloat(args);
            amount = Precision.round(amount, 3);;


            float money = Database.getClanMoney(clan);//
            if(amount > money){
                p.sendMessage(Network.c.getString("money.send").replace("&", "§"));
                return true;
            }
            money-=amount;
            Database.updateClanMoney(clan, money);
            money = Database.getMoney(p.getName().toLowerCase());
            money+=amount;
            Database.updateMoney(p.getName().toLowerCase(), money);
            p.sendMessage(Network.c.getString("money.send").replace("&", "§"));
            return true;
        }catch (Exception e){
            p.sendMessage(Network.c.getString("clan.msg.cashhelp").replace("&", "§"));
            return true;
        }
    }
    public boolean bal(Player p) {
        String clan = Database.getClan(p.getName());
        if(clan == null){
            p.sendMessage(Network.c.getString("clan.msg.haventclan").replace("&", "§"));
            return true;
        }
        p.sendMessage(Network.c.getString("clan.msg.bal").replace("$bal", "" + Database.getClanMoney(clan)).replace("&", "§"));
        return true;
    }
    public boolean invite(Player p, String user){
        if(Database.getClan(p.getName()) == null){
            p.sendMessage(Network.c.getString("clan.msg.haventclan").replace("&", "§"));
            return true;
        }
        if(!Database.getStatus(p.getName()).equalsIgnoreCase("owner")){
            p.sendMessage(Network.c.getString("clan.msg.create").replace("&", "§"));
            return true;
        }
        Player us = Bukkit.getPlayer(user);
        if(us == null){
            p.sendMessage(Network.c.getString("clan.msg.invite.online").replace("$player", user).replace("&", "§"));
            return true;
        }
        if(Database.getClan(user) != null){
            p.sendMessage(Network.c.getString("clan.msg.invite.haveclan").replace("$player", user).replace("&", "§"));
            return true;
        }
        Network.claninvite.put(us,new CInvite(p, Database.getClan(p.getName())));

        String accept = Network.c.getString("clan.msg.invite.accept").replace("&", "§");
        String decline = Network.c.getString("clan.msg.invite.decline").replace("&", "§");
        String text = Network.c.getString("clan.msg.invite.msg").replace("$clan", Database.getClan(p.getName())).replace("$player", p.getName());
        accept = " " + ",{\"text\":\" " + accept + "\",\"clickEvent\":"
                + "{\"action\":\"run_command\",\"value\":\"/c accept" + "\"},"
                + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                + "{\"text\":\"\",\"extra\":[{\"text\":\" " + accept + "\",\"color\":\"gray\"}]}}}";
        decline = " " + ",{\"text\":\" " + decline + "\",\"clickEvent\":"
                + "{\"action\":\"run_command\",\"value\":\"/c decline" + "\"},"
                + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                + "{\"text\":\"\",\"extra\":[{\"text\":\" " + decline + "\",\"color\":\"gray\"}]}}}";
        String CHAT_FORMAT = "[\"\",{\"text\":\"" + text + "\"}" + accept + decline + "]";
        us.spigot().sendMessage(new TextComponent(ComponentSerializer.parse(CHAT_FORMAT.replace("&", "§"))));
        return true;
    }
    public boolean accept(Player p){
        if(!Network.claninvite.containsKey(p)){
            p.sendMessage(Network.c.getString("clan.msg.invite.haventinvite").replace("&", "§"));
            return true;
        }
        Database.ClanJoin(p.getName(), Network.claninvite.get(p).getTag(), "user");
        p.sendMessage(Network.c.getString("clan.msg.invite.join").replace("$clan", Network.claninvite.get(p).getTag()).replace("&", "§"));
        if(Network.claninvite.get(p).getP() != null){
            Network.claninvite.get(p).getP().sendMessage(Network.c.getString("clan.msg.invite.good").replace("$player", p.getName()).replace("&", "§"));
        }
        Network.claninvite.remove(p);
        return true;
    }

    public boolean decline(Player p){
        if(!Network.claninvite.containsKey(p)){
            p.sendMessage(Network.c.getString("clan.msg.invite.haventinvite").replace("&", "§"));
            return true;
        }
        p.sendMessage(Network.c.getString("clan.msg.invite.none").replace("$clan", Network.claninvite.get(p).getTag()).replace("&", "§"));
        if(Network.claninvite.get(p).getP() != null){
            Network.claninvite.get(p).getP().sendMessage(Network.c.getString("clan.msg.invite.err").replace("$player", p.getName()).replace("&", "§"));
        }
        Network.claninvite.remove(p);
        return true;
    }
}
