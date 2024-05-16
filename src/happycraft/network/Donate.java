package happycraft.network;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Donate implements Listener, CommandExecutor, Runnable {
    @EventHandler
    public void onclose(InventoryCloseEvent e) {
        Network.donkmap.remove(e.getPlayer());
    }
    public double getPrice(double discount, int price, String s, Player p){

        if(Network.c.getBoolean("donate.donats." + s + ".doplata")){
            discount*=0.01;
            int res = (int)(price - (price * discount));
            return res > 0 ? res : 1;
        }
        if(p.hasPermission("donate." + s)){
            Network.doplata.put(p, price);
            return 0;
        }
        price-=Network.doplata.get(p);
        discount*=0.01;
        int res = (int)(price - (price * discount));
        return res > 0 ? res : 1;

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player))return true;

        Player p = (Player)commandSender;
        if(args.length == 1 && args[0].equalsIgnoreCase("save")){
            PermissionUser us = PermissionsEx.getUser(p);
            String[] usg = us.getGroupNames();
            try {
                Database.updateDonate(p.getName(),usg[0]);
                p.sendMessage(Network.c.getString("donate.save").replace("&", "§"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("reset")){
            try {
                Network.push("pex user " + p.getName() + " group set "+ Database.getDonate(p.getName()) );
                p.sendMessage(Network.c.getString("donate.reset").replace("&", "§"));
            } catch (SQLException ignored) {

            }
            return true;
        }
        if(args.length == 1){

            Double count = 1.0;
            try {
                count = Double.parseDouble(args[0]);
            }catch (Exception e){
                return true;
            }

            for(String s : Network.c.getStringList("donate.buydonate.geturl")){
                String msg2 = Network.c.getString("donate.buydonate.hover");
                String CHAT_FORMAT = "[\"\"";
                String clickvalue = geturl(p.getName(), count);
                String click = "\"clickEvent\":{\"action\":\"OPEN_URL\",\"value\":\""+clickvalue+"\"},";
                CHAT_FORMAT += ",{\"text\":\"§e" + s.replace("$count", count + "").replace("&", "§") + "\","+click
                        + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                        + "{\"text\":\"\",\"extra\":[{\"text\":\""+msg2.replace("&", "§")+"\",\"color\":\"gray\"}]}}}]";
                p.sendMessage(new TextComponent(ComponentSerializer.parse(CHAT_FORMAT)));
            }
            return true;
        }
        Inventory i;
        int rows = Network.c.getInt("donate.rows");

        String namemenu = Network.c.getString("donate.defaultname");
        i = Bukkit.createInventory(null, rows * 9, namemenu.replace("&", "§"));
        ConfigurationSection all = Network.c.getConfigurationSection("donate.donats");

        String buy = Network.c.getString("donate.buy");
        Network.doplata.put(p, 0);
        HashMap<Integer, String> slotname = new HashMap<>();
        double money = (int)Database.getMoney(p.getName().toLowerCase());
        for(String s : all.getKeys(false)) {

            ItemStack item;
            if(Network.c.getString("donate.donats." + s + ".id").contains(":")) {
                String[] id2 = Network.c.getString("donate.donats." + s + ".id").split(":");
                int id = Integer.parseInt(id2[0]);
                int id22 = Integer.parseInt(id2[1]);
                item = new ItemStack(id, 1,(short) id22);
            }else {
                int id = Integer.parseInt(Network.c.getString("donate.donats." + s + ".id"));
                item = new ItemStack(id, 1);
            }

            int oldprice = Network.c.getInt("donate.donats."+s+".price");//m
            String nd = Network.c.getString("donate.donats."+s+".discount");//m
            int slot = Network.c.getInt("donate.donats."+s+".slot");//m
            long diff = 0;
            int discount = 0;
            if(nd != null){
                try {
                    long timeUp  = format.parse(nd).getTime();
                    diff = timeUp - System.currentTimeMillis();
                    if(diff > 0){
                        discount = Network.c.getInt("donate.donats."+s+".count");
                        Network.discounditems.put(slot, nd);
                    }
                }catch (Exception e){}

            }
            double price = getPrice(discount, oldprice, s, p);
            String pf = "";
            if(oldprice == price){
                pf = Network.c.getString("donate.price.default").replace("$price", price + "");
            }else {
                pf = Network.c.getString("donate.price.discount")
                        .replace("$price", price + "")
                        .replace("$oldprice", oldprice + "")
                ;
            }
            String name = Network.c.getString("donate.donats." + s + ".name").replace("$price", pf+"");
            if(!Network.c.getBoolean("donate.donats." + s + ".doplata") && p.hasPermission("donate." + s))name+=buy;
            List<String> lore = new ArrayList<>();
            for(String ss : Network.c.getStringList("donate.donats." + s + ".lore")) lore.add(ss.replace("&", "§"));


            String sublore = Network.c.getString("donate.buydonate.info").replace("&", "§");
            lore.add(sublore);
            if(money >= price)sublore = Network.c.getString("donate.buydonate.have").replace("$price", price+"").replace("&", "§");
            else {
                int hav = (int) money;
                double count = price -hav;
                sublore = Network.c.getString("donate.buydonate.havent").replace("$money", money+"").replace("$count", count+"").replace("&", "§");
            }
            lore.add(sublore);
            int progres = progress(s, oldprice)+4;
            if(Network.c.getBoolean("donate.donats." + s + ".limited")){
                lore.add(Network.c.getString("donate.limited").replace("$count", Database.donLimit(s) + "")
                        .replace("&", "§")

                );

                String prog = "§a§l||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||";
                prog+=" §f(" +  (oldprice - Database.getProgressDonate(s)) + ")";
                StringBuilder myString = new StringBuilder(prog);
                myString.setCharAt(progres, '§');
                myString.setCharAt(progres+1, 'c');
                lore.add(myString.toString());
            }

            if(nd != null && diff > 0){
                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffDays = diff / (24 * 60 * 60 * 1000);
                StringBuilder sb = new StringBuilder("§cСкидка закончится через §f");
                if (diff > 0) sb.append(diffDays + "д. ");
                if (diffHours > 0) sb.append(diffHours + "ч. ");
                if (diffMinutes > 0) sb.append(diffMinutes + "м. ");
                if (diffSeconds > 0) sb.append(diffSeconds + "с. ");
                lore.add(sb.toString());
            }

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name.replace("&", "§"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            slotname.put(slot, s);
            i.setItem(slot, item);


        }
        Network.donkmap.put(p, new DonatPropertis(slotname, i));


        ItemStack item;

        if(Network.c.getString("donate.history.id").contains(":")) {
            String[] id2 = Network.c.getString("donate.history.id").split(":");
            int id = Integer.parseInt(id2[0]);
            int id22 = Integer.parseInt(id2[1]);
            item = new ItemStack(id, 1,(short) id22);
        }else {
            int id = Integer.parseInt(Network.c.getString("donate.history.id"));
            item = new ItemStack(id, 1);
        }
        ItemMeta meta = item.getItemMeta();
        String name = Network.c.getString("donate.history.name");
        int sl = 18;
        ArrayList<String> lore1 = (ArrayList<String>) Network.c.getStringList("donate.history.lore");

        for(DonatHistory dh : Database.DonatHystory()){
            ArrayList<String> lore = new ArrayList<>();
            float bonus = dh.getAmount();
            bonus/=10000;
            if(bonus > 0.015) bonus = (float)0.015;
            bonus = bonus * 6 * 60;
            bonus = Precision.round(bonus, 3);
            meta.setDisplayName(name
                    .replace("$player", dh.getNick())
                    .replace("$money", dh.getAmount() + "")
                    .replace("$date", dh.getDate())
                    .replace("&", "§"));


            for(String s : lore1){
                lore.add(s
                    .replace("$money", dh.getAmount() + "")
                    .replace("$bonus", bonus + "")
                    .replace("&", "§")
                );
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            i.setItem(sl, item);
            sl++;
        }
        p.openInventory(i);
        return false;
    }
    public int progress(String s, int price){
        int bal = Database.getProgressDonate(s);
        if(bal == 0) return 0;
        int test = bal * 100 / price;
        if(test >99){
            int count = test/100;
            while (count-- >1 || bal >= price){
               Database.adddonate(s);
                bal-=price;
                test-=100;
            }
            Database.updateProgressDonate(s,bal);
        }

        return test;
    }

    public String secret(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
    public String geturl(String player, double price){
        if(price < 100 ){
            return  "https://site.hcgo.ru/donate.php?user="+player+"&amount="+price;
        }
        String o = System.currentTimeMillis() + "";
        o = o.substring(6);
        String secret = secret("16074:"+(int)price+":r_SiCnBTdytemD&:RUB:" + o);
        String url = "https://pay.freekassa.ru/?m=16074&oa="+(int)price+"&o="+o+"&s="+secret+"&currency=RUB&us_mail=" + player;
        return  url;
    }
    public happycraft.network.params.Discount discount (){
        Date discdate = new Date(Network.c.getInt("donate.y"),(Network.c.getInt("donate.m")-1),Network.c.getInt("donate.d"));
        Date date = new Date();
        long milliseconds = discdate.getTime() - date.getTime();
        int seconds = (int) (milliseconds / (1000));
        if(seconds <0)return null;
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
        String time = "";
        if(day >0)time+=day+"д.";
        if(hours >0)time+=hours+"ч.";
        if(minute >0)time+=minute+"м.";
        if(second >0)time+=second+"с.";
        happycraft.network.params.Discount desc = new happycraft.network.params.Discount(Network.c.getInt("donate.discount"), time);
        return desc;
    }
    @EventHandler
    public void click(InventoryClickEvent e) {
        try {
            if(!Network.donkmap.containsKey(e.getWhoClicked())) return;
            if(Network.donkmap.get(e.getWhoClicked()).getI() == null) return;
            if(!e.getClickedInventory().getType().toString().equalsIgnoreCase("CHEST")) return;

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getTypeId() == 0) return;
            if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
            e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta().getLore().size() <=0) return;

            Player p = (Player) e.getWhoClicked();
            if(!Network.donkmap.get(p).getSlotname().containsKey(e.getSlot()))return;


            String slot = Network.donkmap.get(p).getSlotname().get(e.getSlot());
            int discount = 0;
            String nd = Network.c.getString("donate.donats."+slot+".discount");//m
            long diff = 0;
            if(nd != null){
                try {
                    long timeUp  = format.parse(nd).getTime();
                    diff = timeUp - System.currentTimeMillis();
                    if(diff > 0)discount = Network.c.getInt("donate.donats."+slot+".count");
                }catch (Exception ignored){}

            }
            double price = getPrice(discount, Network.c.getInt("donate.donats."+slot+".price"), slot, p);
            if(!Network.c.getBoolean("donate.donats." + slot + ".doplata") &&
                    p.hasPermission("donate." + slot)
            ){
                //уже есть такой дон
                String msg2 = Network.c.getString("donate.downgrade");
                p.sendMessage(msg2.replace("&", "§"));
                p.closeInventory();
                return;
            }



            float money = Database.getMoney(p.getName().toLowerCase());


            if(money >= price){
                if(Network.c.getBoolean("donate.donats." + slot + ".limited")){
                    if(Database.donLimit(slot) <=0){
                        String msg2 = Network.c.getString("donate.limited");
                        p.sendMessage(msg2
                                .replace("$count", "0")
                                .replace("&", "§")

                        );
                        p.closeInventory();
                        return;
                    }
                    Database.donLimitRemove(slot);
                }
                money-=price;
                Database.updateMoney(p.getName().toLowerCase(), money);
                if(slot.equalsIgnoreCase("freecase")){
                    Network.push("givecase " + p.getName() + " free");
                }else if(slot.equalsIgnoreCase("donatcase")){
                    Network.push("givecase " + p.getName() + " donate");
                }
                else if(slot.equalsIgnoreCase("prefixcase")){
                    Network.push("givecase " + p.getName() + " prefix");
                }
                else if(slot.equalsIgnoreCase("megacase")){
                    Network.push("givecase " + p.getName() + " legend");
                }
                else if(slot.equalsIgnoreCase("money")){
                    Network.push("givecase " + p.getName() + " legend");
                }
                else if(slot.equalsIgnoreCase("megacase1")){
                    Network.push("givecase " + p.getName() + " legend");
                }
                //discord: true
                else if(slot.equalsIgnoreCase("op")){
                    Database.newProtect(p.getName().toLowerCase());
                }
                else if(Network.c.getBoolean("donate.donats."+slot+".discord")){
                    Database.addConsole(p.getName(), slot);
                }

                //Database.getMoneyID(e.getPlayer().getName().toLowerCase())
                else {
                    Network.push(Network.c.getString("donate.cmd").replace("$player", p.getName()).replace("$group", slot));
                }

            }
            else {

                int hav = (int) money;
                double count = price -hav;

                for(String s : Network.c.getStringList("donate.buydonate.geturl")){
                    String msg2 = Network.c.getString("donate.buydonate.hover");
                    String CHAT_FORMAT = "[\"\"";
                    String clickvalue = geturl(p.getName().toLowerCase(), count);
                    String click = "\"clickEvent\":{\"action\":\"OPEN_URL\",\"value\":\""+clickvalue+"\"},";
                    CHAT_FORMAT += ",{\"text\":\"§e" + s.replace("$count", count + "").replace("&", "§") + "\","+click
                            + "\"hoverEvent\":{\"action\":\"show_text\",\"value\":"
                            + "{\"text\":\"\",\"extra\":[{\"text\":\""+msg2.replace("&", "§")+"\",\"color\":\"gray\"}]}}}]";
                    p.sendMessage(new TextComponent(ComponentSerializer.parse(CHAT_FORMAT)));
                }
            }
            p.closeInventory();

        }catch (Exception ignored){

        }



    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            try {
            if(!Network.donkmap.containsKey(p))continue;
            Inventory i = p.getOpenInventory().getTopInventory();
                long timeUp = 0;
            for (Map.Entry<Integer, String> d : Network.discounditems.entrySet()) {
                //System.out.println("ID =  " + entry.getKey() + " День недели = " + entry.getValue()); 2022/06/21 22:00:00
                timeUp = format.parse(d.getValue()).getTime();
                long diff = timeUp - System.currentTimeMillis();
                if(timeUp > 1) {
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    StringBuilder sb = new StringBuilder("§cСкидка закончится через §f");
                    if (diff > 0) sb.append(diffDays + "д. ");
                    if (diffHours > 0) sb.append(diffHours + "ч. ");
                    if (diffMinutes > 0) sb.append(diffMinutes + "м. ");
                    if (diffSeconds > 0) sb.append(diffSeconds + "с. ");
                    ItemStack itemStack = i.getItem(d.getKey());
                    ArrayList<String> l = new ArrayList<>(itemStack.getItemMeta().getLore());
                    l.set(l.size()-1, sb.toString());
                    itemStack.setLore(l);
                    p.updateInventory();
                }
            }

            } catch (ParseException ignored) {
            }
        }
    }
}
