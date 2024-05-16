package happycraft.network;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Rank implements Listener, CommandExecutor {


    @Override
    public boolean onCommand(CommandSender senderd, Command cmd, String cmdlabel, String[] args) {
        if (!(senderd instanceof Player)) {
            senderd.sendMessage("Only for players");
            return true;
        }
        Player p = (Player)senderd;



            Inventory i;
            int rows = Network.c.getInt("ranksit.rows");
            String name = Network.c.getString("ranksit.name");
            i = Bukkit.createInventory(null, rows * 9, name.replace("&", "§"));
            Network.rankmap.put(p, i);
            ConfigurationSection ranks = Network.c.getConfigurationSection("rank");

            int player_kills = Database.getKillCount(p.getName());

            for(String s : ranks.getKeys(false)) {
                int rant_amount = Network.c.getInt("rank." + s + ".kills");
                String accsess = Network.c.getString("ranksit.access.not_available");
                if(player_kills >= rant_amount) {
                    accsess = Network.c.getString("ranksit.access.available");
                }
                int slot = Network.c.getInt("rank." + s + ".ID");
                List<String> disc = new ArrayList<String>();
                for(String ss : Network.c.getStringList("rank." + s + ".disk")) {
                    disc.add(ss.replace("$access", accsess).replace("&", "§"));
                }
                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1 , (short)3);
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                String skin = Network.c.getString("rank." + s + ".skin");
                meta.setOwner(skin);
                meta.setDisplayName(s.replace("&", "§"));
                meta.setLore(disc);
                item.setItemMeta(meta);
                i.setItem(slot, item);

            }
            p.openInventory(i);


        return true;
    }
    @EventHandler
    public void onclose(InventoryCloseEvent e) {
        if(Network.rankmap.containsKey(e.getPlayer()))Network.rankmap.remove(e.getPlayer());
    }

    @EventHandler
    public void kill(EntityDeathEvent e) {
        if(!(e.getEntity().getKiller() instanceof Player))return;
        try {
                if( PlaceholderAPI.setPlaceholders(e.getEntity().getKiller(), "%essentials_godmode%").equals("yes"))return;
                if( PlaceholderAPI.setPlaceholders(e.getEntity().getKiller(), "%essentials_vanished%").equals("yes"))return;
                for (final ProtectedRegion r : WGBukkit.getRegionManager(e.getEntity().getKiller().getWorld()).getApplicableRegions(e.getEntity().getKiller().getLocation())) {
                    if(r.getId().equalsIgnoreCase("mob")){
                        if(e.getEntity().getKiller().getGameMode() != GameMode.CREATIVE){
                            if(Network.antifarm.containsKey(e.getEntity().getKiller())){
                                if(Network.antifarm.get(e.getEntity().getKiller()) < 3){
                                    Network.antifarm.put(e.getEntity().getKiller(),(Network.antifarm.get(e.getEntity().getKiller()) + 1));
                                    Network.push("money give " + e.getEntity().getKiller().getName() + " 0.008");
                                }
                            } else {
                                Network.antifarm.put(e.getEntity().getKiller(),1);
                                Network.push("money give " + e.getEntity().getKiller().getName() + " 0.008");
                            }

                        }
                        return;
                    }

            }

            if(!(e.getEntity() instanceof Player))return;
            if(e.getEntity() instanceof Player && e.getEntity().getKiller().getGameMode() != GameMode.CREATIVE
                    && !e.getEntity().getKiller().getAddress().getAddress().getHostAddress().equals(((Player) e.getEntity()).getPlayer().getAddress().getAddress().getHostAddress())) Network.push("money give " + e.getEntity().getKiller().getName() + " 0.013");
            Player p = e.getEntity().getKiller(); //кто убил
            int kills = Database.getKillCount(p.getName()) + 1;
            Database.updateKillCount(p.getName().toLowerCase(), kills);

                String don = PlaceholderAPI.setPlaceholders(e.getEntity().getKiller(), "%vault_group%");
                Database.addlog(e.getEntity().getKiller().getName(), e.getEntity().getName(), "kill", don);

        }catch(Exception ignored){

        }
    }
    @EventHandler
    public void click(InventoryClickEvent e) throws SQLException {
        Inventory i = Network.rankmap.get(e.getWhoClicked());
        if (i == null) return;
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getTypeId() == 0) return;
        if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        int kill_players = Database.getKillCount(p.getName().toLowerCase());
        int kill_count = Network.c.getInt("rank." + e.getCurrentItem().getItemMeta().getDisplayName().replace("§", "&") + ".kills");
        if(kill_players >= kill_count) {
            Database.updateRank(p.getName().toLowerCase(), e.getCurrentItem().getItemMeta().getDisplayName().replace("§", "&"));
        }else {
            int a11 = kill_count - kill_players;
            p.sendMessage(Network.c.getString("sit.not_received_the_rank").replace("$kills", "" + a11));
        }
        p.closeInventory();
    }
    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        try {
            if(e.isCancelled()) return;
            if(e.getDamager() instanceof Player){
                if(((Player) e.getDamager()).getGameMode() == GameMode.CREATIVE){
                    e.setCancelled(true);
                    return;
                }
            }
            double damege = e.getDamage();
            if(e.getEntity() instanceof Player) {
                Player p = (Player) e.getDamager();
                if(e.getEntity() instanceof Player){
                    Player p2 = (Player) e.getEntity();
                    String mc = Database.getClan(p.getName());
                    String uc = Database.getClan(p2.getName());
                    if(mc != null && uc != null && mc.equals(uc)){
                        e.setCancelled(true);
                        p.sendMessage(Network.c.getString("clan.msg.damage").replace("&", "§"));
                        return;
                    }
                }
                if(Database.getRank(p.getName()) != null) { ///урон
                    int change= Network.c.getInt("rank." + Database.getRank(p.getName()) + ".change.damage");
                    String random = "no";
                    int r = (int)(Math.random() * 100);
                    r = r - change;
                    if(r <= 0) {
                        random = "yes";
                    }

                    if(random.equals("yes")) {
                        int def = Network.c.getInt("rank." + Database.getRank(p.getName()) + ".up.damage");
                        damege = damege + damege * def / 100;
                        p.sendMessage(Network.c.getString("sit.critical").replace("$damage", damege + "").replace("&", "§"));

                    }
                }

            }

            if(e.getEntity() instanceof Player) {
                if(Database.getRank(e.getEntity().getName()) != null) { //защита
                    String rank = Database.getRank(e.getEntity().getName());
                    int change= Network.c.getInt("rank." + rank + ".change.def");
                    String random = "no";
                    int r = (int)(Math.random() * 100);
                    r = r - change;
                    if(r <= 0) {
                        random = "yes";
                    }
                    if(random.equals("yes")) {
                        int def = Network.c.getInt("rank." + rank + ".up.def");

                        double damege1 = damege * def / 100;

                        damege = damege - damege1;
                        e.getEntity().sendMessage(Network.c.getString("sit.defence").replace("$defence", damege1 + "").replace("&", "§"));

                    }
                }
            }
            e.setDamage(damege);
        }catch (Exception ignored){

        }

    }
}
