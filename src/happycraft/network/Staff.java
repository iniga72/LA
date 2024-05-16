package happycraft.network;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Staff implements Listener, CommandExecutor {
    @EventHandler
    public void onclose(InventoryCloseEvent e) {
        Network.stuffinv.remove(e.getPlayer());
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        Inventory i = Network.stuffinv.get(e.getWhoClicked());
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getTypeId() == 0) return;
        if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if(i == null) return;
        e.setCancelled(true);

        String color = Network.c.getString("staff.sittings.colorname").replace("&", "§");
        String nick = e.getCurrentItem().getItemMeta().getDisplayName().replace(color, "");
        if(Network.c.getString("staff.stuff." + nick) == null)return;
        Player p = (Player) e.getWhoClicked();
        if(Network.c.getString("staff.stuff." + nick + ".url") == null) {
            p.sendMessage(Network.c.getString("staff.msg.nourl").replace("$name", nick)); return;
        }
        p.closeInventory();
        p.sendMessage(Network.c.getString("staff.stuff." + nick + ".url").replace("&", "§"));
    }
    @Override
    public boolean onCommand(CommandSender senderd, final Command cmd, final String cmdlabel, final String[] args) {
        if (!(senderd instanceof Player)) {
            senderd.sendMessage("Only for players");
            return true;
        }
        Player p = (Player)senderd;

        if(args.length == 0) {
            Inventory i;
            ConfigurationSection helpers = Network.c.getConfigurationSection("staff.stuff");
            int only = helpers.getKeys(false).size();
            int only1 = 0;
            only1 = only / 9 + 1;
            String name = " " + Network.c.getString("staff.sittings.name").replace("&", "§");
            i = Bukkit.createInventory(null, only1 * 9, name);
            Network.stuffinv.put(p, i);
            int is = 0;
            String status  = "";
            for(String s : helpers.getKeys(false)) {
                Player z = Bukkit.getPlayer(s);
                String online = "";
                String time = "";
                if(Network.c.getString("staff.stuff." + s + ".status") == null) {
                    status  = "пусто";
                }else {
                    status = Network.c.getString("staff.stuff." + s + ".status").replace("&", "§");
                }
                if(z == null) {
                    try {
                        online = "§aБыл в сети: " + Database.getJoin(s);
                    } catch (SQLException e) {

                    }
                }else {
                    online = "§aOnline";
                }
                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1 , (short)3);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                String skin = s;
                if(Network.c.getString("staff.stuff." + s + ".skin") != null) {
                    skin = Network.c.getString("staff.stuff." + s + ".skin");
                }
                List<String> d = Lists.newArrayList();
                List<String> disc = Network.c.getStringList("staff.sittings.lore");
                for(String sz : disc) {
                    sz = sz.replace("$online", online).replace("$time", time).replace("$status", status).replace("&", "§");
                    d.add(sz);
                }
                meta.setOwner(skin);
                meta.setDisplayName(Network.c.getString("staff.sittings.colorname").replace("&", "§") + s);
                meta.setLore(d);
                item.setItemMeta(meta);
                i.setItem(is, item);
                is = is + 1;
            }
            p.openInventory(i);
            return true;
        }
        if(!p.hasPermission("stuff.admin")) {
            p.sendMessage(Network.c.getString("staff.msg.error").replace("&", "§"));
            return true;
        }
        return true;
    }
}
