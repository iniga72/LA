package happycraft.network;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Protector implements Listener, CommandExecutor
{

    @EventHandler
    public void delete(PlayerCommandPreprocessEvent e){

        if(e.isCancelled()) return;
        String[] ms = e.getMessage().split(" ");
        if(ms.length != 3) return;
        if(e.getMessage().startsWith("/rg remove") || e.getMessage().startsWith("/rg delete") || e.getMessage().startsWith("/region remove") || e.getMessage().startsWith("/rg delete")){
            Player p = e.getPlayer();
            if(WGBukkit.getRegionManager(p.getWorld()).getRegion(ms[2]) == null) return;
            LocalPlayer localPlayer = Objects.requireNonNull(this.getWorldGuard()).wrapPlayer(p);
            if (WGBukkit.getRegionManager(p.getWorld()).getRegion(ms[2]).isOwner(localPlayer)) {
                Database.removeProtector(ms[2]);
            }
        }
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String cmdlabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player p = (Player)sender;
        if (!p.hasPermission("protector.protect")) {
            p.sendMessage(Network.c.getString("regoinprotect.messages.permission").replace("&", "§"));
            return true;
        }
        final ArrayList<String> regions = new ArrayList<String>();
        final LocalPlayer localPlayer = Objects.requireNonNull(this.getWorldGuard()).wrapPlayer(p);
        for (final String s : WGBukkit.getRegionManager(p.getWorld()).getRegions().keySet()) {
            if (WGBukkit.getRegionManager(p.getWorld()).getRegion(s).isOwner(localPlayer)) {
                regions.add(s);
            }
        }
        if (regions == null) {
            p.sendMessage(Network.c.getString("regoinprotect.messages.noregions").replace("&", "§"));
            return true;
        }
        Inventory i;
        final String name = Network.c.getString("regoinprotect.menu.name");
        final int itemssize = regions.size() / 9 + 1;
        i = Bukkit.createInventory(null, itemssize * 9, name);
        int is = 0;
        for (final String s2 : regions) {
            final List<String> dosc = Network.c.getStringList("regoinprotect.menu.discription");
            final ArrayList<String> desc = new ArrayList<String>();
            String status = Network.c.getString("regoinprotect.menu.status.disable");
            ItemStack item = new ItemStack(Material.WOOL, 1, (short)14);
            if (Database.getProtector(s2) != null && Database.getProtector(s2).equalsIgnoreCase(p.getName().toLowerCase())) {
                status = Network.c.getString("regoinprotect.menu.status.enable");
                item = new ItemStack(Material.WOOL, 1, (short)5);
            }
            for (final String l : dosc) {
                desc.add(l.replace("$status", status).replace("&", "§"));
            }
            final String itemname = Network.c.getString("regoinprotect.menu.itemname").replace("$region", s2).replace("&", "§");
            final ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(itemname);
            meta.setLore((List)desc);
            item.setItemMeta(meta);
            i.setItem(is, item);
            Network.ProtectorRG.put(p.getName() + "_" + is, s2);
            ++is;
        }
        Network.ProtectorInv.put(p, i);
        p.openInventory(i);
        if(Network.ProtectorRG.size() >50){
            ArrayList<String> values = new ArrayList<>(Network.ProtectorRG.keySet());
            Network.ProtectorRG.remove(values.get(0));
            Network.ProtectorRG.remove(values.get(1));
            Network.ProtectorRG.remove(values.get(2));
            Network.ProtectorRG.remove(values.get(3));
            Network.ProtectorRG.remove(values.get(4));
        }
        return true;
    }

    private WorldGuardPlugin getWorldGuard() {
        final Plugin plugin = Network.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin)plugin;
    }



    @EventHandler
    public void click(final InventoryClickEvent e) {
        final Inventory i = Network.ProtectorInv.get(e.getWhoClicked());
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getTypeId() == 0) {
            return;
        }
        if (e.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }
        if (i == null) {
            return;
        }
        e.setCancelled(true);
        final String region = Network.ProtectorRG.get(e.getWhoClicked().getName() + "_" + e.getSlot());
        ItemStack item = null;
        String status = Network.c.getString("regoinprotect.menu.status.enable");
        if (Database.getProtector(region) != null && Database.getProtector(region).equalsIgnoreCase(e.getWhoClicked().getName().toLowerCase())) {
            status = Network.c.getString("regoinprotect.menu.status.disable");
            item = new ItemStack(Material.WOOL, 1, (short)14);
            Database.removeProtector(region);
        }
        else {
            Database.setProtector(region, e.getWhoClicked().getName().toLowerCase());
            item = new ItemStack(Material.WOOL, 1, (short)5);
        }
        final List<String> dosc = (List<String>)Network.c.getStringList("regoinprotect.menu.discription");
        final ArrayList<String> desc = new ArrayList<String>();
        for (final String l : dosc) {
            desc.add(l.replace("$status", status).replace("&", "§"));
        }
        final String itemname = Network.c.getString("regoinprotect.menu.itemname").replace("$region", region).replace("&", "§");
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(itemname);
        meta.setLore((List)desc);
        item.setItemMeta(meta);
        i.setItem(e.getSlot(), item);
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    void wg(final BlockBreakEvent e) {
        if(e.isCancelled()) return;
        int count = Database.getJail(e.getPlayer().getName());
        if(count >0){
            e.setCancelled(true);
            return;
        }
        final Player p = e.getPlayer();
        LocalPlayer localPlayer = Objects.requireNonNull(this.getWorldGuard()).wrapPlayer(p);
        for (final ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(e.getBlock().getLocation())) {
            if(r.getId().equals("jail") && Database.getJail(e.getPlayer().getName()) > 0)return;
            if (Database.getProtector(r.getId()) != null ) {
                if (!Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isOwner(localPlayer) && !Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isMember(localPlayer) &&!(Network.c.getBoolean("regoinprotect.bypass") && p.hasPermission("protector.bypass"))) {
                    e.setCancelled(true);
                    p.sendMessage(Network.c.getString("regoinprotect.messages.pretect").replace("&", "§"));
                    return;
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    void wg(final BlockPlaceEvent e) {
        int count = Database.getJail(e.getPlayer().getName());
        if(count >0){
            e.setCancelled(true);
            return;
        }

        final Player p = e.getPlayer();
        if(e.isCancelled()) return;
        /*if(!e.getPlayer().hasPermission("antiblock.use") && Network.antiitem.contains(e.getBlock().getTypeId())){
            e.getPlayer().sendMessage(Network.c.getString("antiblock.msg").replace("&", "§"));
            e.setCancelled(true);
            return;
        }*/
        LocalPlayer localPlayer = Objects.requireNonNull(this.getWorldGuard()).wrapPlayer(p);
        for (final ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(e.getBlock().getLocation())) {
            if (Database.getProtector(r.getId()) != null ) {
                if (!Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isOwner(localPlayer) && !Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isMember(localPlayer) &&!(Network.c.getBoolean("regoinprotect.bypass") && p.hasPermission("protector.bypass"))) {
                    e.setCancelled(true);
                    p.sendMessage(Network.c.getString("regoinprotect.messages.pretect").replace("&", "§"));
                    return;
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    void wg(final PlayerBucketEmptyEvent e) {
        if(e.isCancelled()) return;
        int count = Database.getJail(e.getPlayer().getName());
        if(count >0){
            e.setCancelled(true);
            return;
        }
        final Player p = e.getPlayer();
        LocalPlayer localPlayer = Objects.requireNonNull(this.getWorldGuard()).wrapPlayer(p);
        for (final ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(e.getBlockClicked().getLocation())) {
            if(r.getId().equals("jail") && Database.getJail(e.getPlayer().getName()) > 0)return;
            if (Database.getProtector(r.getId()) != null ) {
                if (!Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isOwner(localPlayer) && !Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isMember(localPlayer) &&!(Network.c.getBoolean("regoinprotect.bypass") && p.hasPermission("protector.bypass"))) {
                    e.setCancelled(true);
                    p.sendMessage(Network.c.getString("regoinprotect.messages.pretect").replace("&", "§"));
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    void wg(final PlayerBucketFillEvent e) {
        if(e.isCancelled()) return;
        int count = Database.getJail(e.getPlayer().getName());
        if(count >0){
            e.setCancelled(true);
            return;
        }
        final Player p = e.getPlayer();
        LocalPlayer localPlayer = Objects.requireNonNull(this.getWorldGuard()).wrapPlayer(p);
        for (final ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(e.getBlockClicked().getLocation())) {
            if(r.getId().equals("jail") && Database.getJail(e.getPlayer().getName()) > 0)return;
            if (Database.getProtector(r.getId()) != null ) {
                if (!Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isOwner(localPlayer) && !Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isMember(localPlayer) &&!(Network.c.getBoolean("regoinprotect.bypass") && p.hasPermission("protector.bypass"))) {
                    e.setCancelled(true);
                    p.sendMessage(Network.c.getString("regoinprotect.messages.pretect").replace("&", "§"));
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    void wg(final HangingPlaceEvent e) {
        if(e.isCancelled()) return;
        int count = Database.getJail(e.getPlayer().getName());
        if(count >0){
            e.setCancelled(true);
            return;
        }
        final Player p = e.getPlayer();
        LocalPlayer localPlayer = Objects.requireNonNull(this.getWorldGuard()).wrapPlayer(p);
        for (final ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(e.getBlock().getLocation())) {
            if(r.getId().equals("jail") && Database.getJail(e.getPlayer().getName()) > 0)return;
            if (Database.getProtector(r.getId()) != null ) {
                if (!Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isOwner(localPlayer) && !Objects.requireNonNull(WGBukkit.getRegionManager(p.getWorld()).getRegion(r.getId())).isMember(localPlayer) &&!(Network.c.getBoolean("regoinprotect.bypass") && p.hasPermission("protector.bypass"))) {
                    e.setCancelled(true);
                    p.sendMessage(Network.c.getString("regoinprotect.messages.pretect").replace("&", "§"));
                    return;
                }
            }
        }
    }





    @EventHandler
    public void onclose(final InventoryCloseEvent e) {
        Network.ProtectorInv.remove(e.getPlayer());
    }
}
