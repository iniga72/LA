package happycraft.network;

import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;

import static happycraft.network.Network.*;

public class Casino implements CommandExecutor, Listener {
    @EventHandler
    public void onclose(InventoryCloseEvent e) {
        if(casino.containsKey(e.getPlayer())){
            CasinoParams cp = Network.casino.get(e.getPlayer());
            if(cp.isGame()){
                String rg = (cp.getStavka() * x(cp.getWin()-1)) + "";
                String result = rg.replace(".", "_");
                if(result.split("_").length >= 2){
                    if(result.split("_")[1].length() > 3){
                        result =result.substring(0,(result.split("_")[0].length() +4));
                    }
                }
                result = result.replace("_", ".");
                Network.push("money give "+e.getPlayer().getName() + " " + result);
                Bukkit.broadcastMessage(Network.c.getString("casino.broadcast")
                        .replace("$player", e.getPlayer().getName())
                        .replace("$win",result)
                        .replace("&", "§"));
                cp.setGame(false);
            }

        }
        casino.remove(e.getPlayer());

    }
    @EventHandler
    public void click(InventoryClickEvent e) {
        if(e.isCancelled()) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if(!Network.casino.containsKey(e.getWhoClicked()))return;
        if(!e.getClickedInventory().getType().toString().equalsIgnoreCase("CHEST")) return;
        e.setCancelled(true);
        if (e.getSlot() < 0 || e.getSlot() > 36) return;
        CasinoParams cp = Network.casino.get(e.getWhoClicked());
        if(e.getCurrentItem().getType() != Material.BEACON){
            if(e.getCurrentItem().getType() == Material.ENDER_CHEST){
                if(!cp.isGame())return;
                String rg = (cp.getStavka() * x(cp.getWin()-1)) + "";
                String result = rg.replace(".", "_");
                if(result.split("_").length >= 2){
                    if(result.split("_")[1].length() > 3){
                        result =result.substring(0,(result.split("_")[0].length() +4));
                    }
                }
                result = result.replace("_", ".");
                Network.push("money give "+e.getWhoClicked().getName() + " " + result);
                Bukkit.broadcastMessage(Network.c.getString("casino.broadcast")
                        .replace("$player", e.getWhoClicked().getName())
                        .replace("$win",result)
                .replace("&", "§"));
                cp.setGame(false);
            }
            return;
        }

        if(!cp.isGame())return;
        ItemStack start = new ItemStack(Material.BEACON, 1);
        ItemStack win = new ItemStack(Material.DIAMOND, 1);
        ItemStack bomb = new ItemStack(Material.TNT, 1);
        ItemMeta mm = win.getItemMeta();
        mm.setDisplayName("§f");
        win.setItemMeta(mm);
        bomb.setItemMeta(mm);
        if(cp.getBombs().contains(e.getSlot())){
            FileConfiguration c = Network.c;
            String msg = c.getString("casino.louse").replace("&", "§");
            e.getWhoClicked().sendMessage(msg);
            cp.setGame(false);
             int slot = e.getSlot() > 8 ?  e.getSlot()-9: e.getSlot();
            Inventory i = cp.getInv();
            for (int nslot = 0; nslot <=26; nslot++) {
                if(cp.getBombs().contains(nslot)){
                    i.setItem(nslot, bomb);
                }else {
                    i.setItem(nslot, win);
                }

            }
            return;
        }
        cp.getInv().setItem(e.getSlot(), win);
        cp.setWin(cp.getWin()+1);

        ItemMeta meta  = start.getItemMeta();
        meta.setDisplayName(c.getString("casino.start").replace("&", "§"));
        start.setItemMeta(meta);



        ItemStack chest = cp.getInv().getItem(cp.win+26);
        chest.setType(Material.ENDER_CHEST);

        String rg = (cp.getStavka() * x(cp.getWin()-1)) + "";
        String result = rg.replace(".", "_");
        if(result.split("_").length >= 2){
            if(result.split("_")[1].length() > 3){
                result =result.substring(0,(result.split("_")[0].length() +4));
            }
        }

        String getx = c.getString("casino.give").replace("$win", result.replace("_", ".")).replace("&", "§");
        meta = chest.getItemMeta();
        meta.setDisplayName(getx);
        meta.setLore(null);
        chest.setItemMeta(meta);
        Player player = (Player) e.getWhoClicked();
        player.updateInventory();



    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            if(args.length != 1){
                commandSender.sendMessage(Network.c.getString("casino.help").replace("&", "§"));
                return true;
            }
            float amount;
            try {
                amount =Float.parseFloat(args[0]);
            }catch (Exception ign){
                commandSender.sendMessage(Network.c.getString("casino.help").replace("&", "§"));
                return true;
            }
            amount = Precision.round(amount, 3);;
            float money = Database.getMoney(p.getName().toLowerCase());
            if(amount > money){
                p.sendMessage(c.getString("money.enough").replace("&", "§"));
                return true;
            }
            money-=amount;
            try {
                if(amount < 0.001) return true;
                Database.updateMoney(p.getName().toLowerCase(), money);
                CasinoGame.openInventory(p, amount);
            } catch (SQLException e) {
            }


        }
        return false;
    }
}
