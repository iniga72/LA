package happycraft.network;

import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Arrays;

public class FreeCase implements CommandExecutor {
/*
    @EventHandler
    public void join(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(Database.getTimeID(p.getName().toLowerCase()) == 0)Database.loadFree(p.getName().toLowerCase());
    }
    @EventHandler
    public void click(InventoryClickEvent e) throws SQLException {

        Inventory i = Network.holders.get(e.getWhoClicked());
        if (e.getCurrentItem() == null) return;
        if (i == null)return;
        if (e.getCurrentItem().getTypeId() == 0)return;
        if (e.getCurrentItem().getItemMeta().getDisplayName() == null)return;
        e.setCancelled(true);
        Player p = (Player)e.getWhoClicked();

        FileConfiguration c = Network.c;
        if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§a\u041d\u0430\u0447\u0430\u0442\u044c")) {
            String name = p.getName();
            int time = p.getStatistic(Statistic.PLAY_ONE_TICK) / 60;
            int free = Database.getFreeCount(p.getName().toLowerCase() ) * 600;
            if (time >= free) {
                final ItemMeta meta = e.getCurrentItem().getItemMeta();
                final ItemStack item = e.getCurrentItem();
                final int r = (int)(Math.random() * 100.0);
                meta.setDisplayName("§c" + r);
                if (r % 2 == 1) {
                    p.sendMessage("§aВы проиграли. Попробуйте ещё раз через 10 минут");
                    int a1 = Database.getFreeCount(p.getName().toLowerCase()) ;
                    a1++;
                    Database.updateFree(p.getName().toLowerCase(), a1);
                    p.closeInventory();
                    return;
                }
                else {
                    int i2 = 0;
                    if(Network.win.containsKey(p))i2 = Network.win.get(p);
                    i2++;
                    Network.win.put(p, i2);
                    ItemStack item2 = new ItemStack(264, 1);
                    ItemMeta meta2 = item2.getItemMeta();
                    String names = "";
                    names += c.getString("freecase.sit.wins." + i2 + "").replace("&", "§");

                    meta2.setDisplayName("§e\u0422\u0432\u043e\u0439 \u043f\u0440\u0438\u0437: " + names);

                    String[] te = {"§e\u0414\u043e\u043d\u0430\u0442 \u043d\u0438\u0436\u0435, \u0447\u0435\u043c \u0443 \u0432\u0430\u0441 \u0435\u0441\u0442\u044c \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043d\u0435\u043b\u044c\u0437\u044f.", "§e\u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0430\u0439 \u043e\u0442\u043a\u0440\u044b\u0432\u0430\u0442\u044c. \u0423 \u0442\u0435\u0431\u044f \u0432\u0441\u0451 \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u0441\u044f.", "§a\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0435 \u043f\u0440\u0438\u0437\u044b: ", "§8[§bFly§8]", "§8[§6\u0412\u0438\u043f§8]", "§8[§5\u041f\u0440\u0435\u043c\u0438\u0443\u043c§8]", "§8[§a\u041a\u0440\u0435\u0430\u0442\u0438\u0432§8]", "§8[§e\u041c\u043e\u0434\u0435\u0440§8]", "§8[§c\u0410\u0434\u043c\u0438\u043d§8]", "§8[§2\u041e\u0441\u043d\u043e\u0432\u0430\u0442\u0435\u043b\u044c§8]", "§8[§4You§fTube§8]", "§8[§4§l\u0412§6§l\u043b§e§l\u0430§2§l\u0434§3§l\u0435§1§l\u043b§5§l\u0435§4§l\u0446§8]" };
                    meta2.setLore(Arrays.asList(te));
                    item2.setItemMeta(meta2);
                    i.setItem(13, item2);
                }
                item.setItemMeta(meta);
                i.setItem(e.getSlot(), item);
            }
            else {
                int sec = free - time;
                int min = 0;
                if(sec >= 60){
                    min = sec/60;
                }
                sec = sec - (min * 60);
                p.sendMessage("§eИгра будет доступна через " + min + "м. " + sec + "с.");
                p.closeInventory();
            }
        }
    }
    @EventHandler
    public void onclose(InventoryCloseEvent e) {
        Network.holders.remove(e.getPlayer());
        Player p = (Player)e.getPlayer();
        if (Network.win.containsKey(p)) {
            final int a2 = Network.win.get(p);
            FileConfiguration c = Network.c;
            final String s = c.getString("freecase.sit.wins." + a2);
            //Network.push("money give " + p.getName() + " " + s);
            Network.win.remove(p);
        }
    }
*/
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only for players");
            return true;
        }
        Player p = (Player)sender;
        CasinoGame.openInventory(p, 0.4);
        /*Inventory i;
        i = Bukkit.createInventory(null, 18, "§0         \u0411\u0435\u0441\u043f\u043b\u0430\u0442\u043d\u044b\u0439 \u0434\u043e\u043d\u0430\u0442!");
        Network.holders.put(p, i);
        for (int in = 0; in <= 8; ++in) {
            final ItemStack item = new ItemStack(160, 1);
            final ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a\u041d\u0430\u0447\u0430\u0442\u044c");
            item.setItemMeta(meta);
            i.setItem(in, item);
        }
        final ItemStack item2 = new ItemStack(340, 1);
        final ItemMeta meta2 = item2.getItemMeta();
        meta2.setDisplayName("§a\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f.");

        String[] asfd = { "§a\u041d\u0430\u0436\u0438\u043c\u0430\u0439 \u043d\u0430 \u0441\u0442\u0435\u043a\u043b\u043e \u0438 \u043f\u043e\u0431\u0435\u0436\u0434\u0430\u0439.", "§a\u0412\u044b\u043f\u0430\u0434\u0430\u0435\u0442 \u0440\u0430\u043d\u0434\u043e\u043c\u043d\u043e\u0435 \u0447\u0438\u0441\u043b\u043e, \u0435\u0441\u043b\u0438 \u043e\u043d\u043e \u0447\u0451\u0442\u043d\u043e\u0435 \u0442\u043e \u0442\u044b §c\u0432\u044b\u0438\u0433\u0440\u0430\u043b", "§a\u0438 \u043c\u043e\u0436\u0435\u0448\u044c \u043f\u0440\u043e\u0434\u043e\u043b\u0436\u0430\u0442\u044c \u043e\u0442\u043a\u0440\u044b\u0432\u0430\u0442\u044c. \u0427\u0435\u043c \u0431\u043e\u043b\u044c\u0448\u0435 \u043f\u043e\u0431\u0435\u0434 ", "§a- \u0442\u0435\u043c \u0432\u044b\u0448\u0435 \u0434\u043e\u043d\u0430\u0442 \u043c\u043e\u0436\u043d\u043e \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c.", "§a\u041a\u0430\u0436\u0434\u044b\u0439 \u043d\u0430\u0438\u0433\u0440\u0430\u043d\u043d\u044b\u0439 \u0447\u0430\u0441 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435 \u0434\u0430\u0451\u0442 \u0432\u0430\u043c 1 \u043f\u043e\u043f\u044b\u0442\u043a\u0443.", "§a\u0415\u0441\u043b\u0438 \u0447\u0438\u0441\u043b\u043e \u043d\u0435\u0447\u0451\u0442\u043d\u043e\u0435 \u0442\u043e \u0432\u044b \u043f\u0440\u043e\u0438\u0433\u0440\u0430\u043b\u0438.", "§a\u0421\u043e\u0445\u0440\u0430\u043d\u044f\u0435\u0442\u0441\u044f \u043c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u044b\u0439 \u0434\u043e\u043d\u0430\u0442, \u0434\u043e\u043d\u0430\u0442 \u043d\u0438\u0436\u0435 \u0432\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043d\u0435 \u0441\u043c\u043e\u0436\u0435\u0442\u0435.§e\u0423\u0434\u0430\u0447\u0438" };
        meta2.setLore(Arrays.asList(asfd));
        item2.setItemMeta(meta2);
        i.setItem(9, item2);
        p.openInventory(i);*/
        return true;
    }
}
