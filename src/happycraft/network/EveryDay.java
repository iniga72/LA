package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EveryDay implements Listener, CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(p.getName().equalsIgnoreCase("featurehack")){
            if(args.length == 1){
                
                    String playerlast = Database.getDate(p.getName().toLowerCase());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = new Date();
                    String now = sdf.format(date1);
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(sdf.parse(now));
                    } catch (ParseException ignored) {
                    }
                    
                    String tomorrow = sdf.format(c.getTime());
                if(args[0].equalsIgnoreCase("+")){
                    c.add(Calendar.DATE, +1);
                }
                if(args[0].equalsIgnoreCase("-")){
                    c.add(Calendar.DATE, -1);
                }
                FileConfiguration co = Network.c;
                if(playerlast.equalsIgnoreCase("")){
                    //Database.setDate(p.getName().toLowerCase(), now);
                    give(p, 1);
                    try {
                        Database.updateEveryDay(p.getName().toLowerCase(), tomorrow, 2);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage(co.getString("everyday.new").replace("&", "§"));
                    return true;
                }
                if(now.equals(playerlast)){
                    int day = Database.getDay(p.getName().toLowerCase());
                    give(p, day);
                    day++;
                    ConfigurationSection cases = co.getConfigurationSection("everyday.days.");
                    if(day > cases.getKeys(false).size())day = 1;
                    try {
                        Database.updateEveryDay(p.getName().toLowerCase(), tomorrow, day);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage(co.getString("everyday.new").replace("&", "§"));
                    return true;
                }
                if(!playerlast.equals(tomorrow)){
                    try {
                        Database.updateEveryDay(p.getName().toLowerCase(), tomorrow, 2);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    give(p, 1);
                    p.sendMessage(co.getString("everyday.new").replace("&", "§"));
                }
            }
        }
        open(p);
        return true;
    }
    @EventHandler
    public void onclose(InventoryCloseEvent e) {
        Network.inventory.remove(e.getPlayer());
    }
    @EventHandler
    public void click(InventoryClickEvent e) {
        Inventory i = Network.inventory.get(e.getWhoClicked());
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if (i != null)e.setCancelled(true);

    }
    @EventHandler
    public void join(PlayerJoinEvent e) throws ParseException, SQLException {
        String playerlast = Database.getDate(e.getPlayer().getName().toLowerCase());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        String now = sdf.format(date1);
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(now));
        c.add(Calendar.DATE, +1);
        String tomorrow = sdf.format(c.getTime());

        FileConfiguration co = Network.c;
        if(playerlast.equalsIgnoreCase("")){
            //Database.setDate(e.getPlayer().getName().toLowerCase(), now);
            give(e.getPlayer(), 1);
            Database.updateEveryDay(e.getPlayer().getName().toLowerCase(), tomorrow, 2);
            e.getPlayer().sendMessage(co.getString("everyday.new").replace("&", "§"));
            return;
        }
        if(now.equals(playerlast)){
            int day = Database.getDay(e.getPlayer().getName().toLowerCase());
            give(e.getPlayer(), day);
            day++;
            ConfigurationSection cases = co.getConfigurationSection("everyday.days.");
            if(day > cases.getKeys(false).size())day = 1;
            Database.updateEveryDay(e.getPlayer().getName().toLowerCase(), tomorrow, day);
            e.getPlayer().sendMessage(co.getString("everyday.new").replace("&", "§"));
            return;
        }
        if(!playerlast.equals(tomorrow)){
            Database.updateEveryDay(e.getPlayer().getName().toLowerCase(), tomorrow, 2);
            give(e.getPlayer(), 1);
            e.getPlayer().sendMessage(co.getString("everyday.new").replace("&", "§"));
        }
    }
    public void give(Player p, int day){
        FileConfiguration c = Network.c;
        Network.push(c.getString("everyday.days." + day + ".cmd").replace("$player", p.getName()));
    }
    public void open(Player p){
        FileConfiguration c = Network.c;
        String name = c.getString("everyday.name").replace("&", "§");
        Inventory i = Bukkit.createInventory(null, 3 * 9, name);
        int day = Database.getDay(p.getName().toLowerCase());
        day--;
        ConfigurationSection cases = c.getConfigurationSection("everyday.days");

        for(String s : cases.getKeys(false)){
            int d = Integer.parseInt(s);

            ItemStack item = new ItemStack(Material.GLOWSTONE_DUST, d);
            if(day+1 > d)item = new ItemStack(Material.REDSTONE, d);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(c.getString("everyday.item.name").replace("$day", s).replace("&", "§"));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(c.getString("everyday.item.lore").replace("$item", c.getString("everyday.days." + s + ".name")).replace("&", "§"));

            if(day+1 > d)lore.add(c.getString("everyday.item.end").replace("&", "§"));
            meta.setLore(lore);
            item.setItemMeta(meta);
            d--;
            i.setItem(d, item);
        }
        Network.inventory.put(p, i);
        p.openInventory(i);
    }

}
