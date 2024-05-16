package happycraft.network.commands;


import happycraft.network.Database;
import happycraft.network.Network;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.ArrayList;

public class casinonew implements CommandExecutor, Listener {

    @EventHandler
    public void clear(InventoryCloseEvent e){
        Network.newcasino.remove(e.getPlayer());
    }


    @EventHandler
    public void game(InventoryClickEvent e){
        if(!Network.newcasino.containsKey(e.getWhoClicked())) return;
        if(!e.getClickedInventory().getType().toString().equalsIgnoreCase("CHEST")) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getTypeId() == 0) return;
        e.setCancelled(true);
        CasinoGame game = Network.newcasino.get(e.getWhoClicked());
        if(!game.isStatus()) return;


    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player))return true;
        FileConfiguration c = Network.c;
        String prefix = c.getString("casinoplus.prefix");
        if(args.length != 1){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix +c.getString("casinoplus.msg.help")));
            return true;
        }
        float stavka = 0;
        try {
            stavka = Float.parseFloat(args[0]);
            stavka = Precision.round(stavka, 3);
        }catch (Exception e){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix +c.getString("casinoplus.msg.help")));
            return true;
        }
        if(stavka < 0.001){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix +c.getString("casinoplus.msg.help")));
            return true;
        }
        float money = Database.getMoney(sender.getName());
        if(stavka > money){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix +c.getString("casinoplus.msg.money")));
            return true;
        }
        money-=stavka;
        try {
            Database.updateMoney(sender.getName(), money);
        } catch (SQLException ignored) {
        }
        Player p = (Player) sender;
        Inventory i = Bukkit.createInventory(null, 6*9,ChatColor.translateAlternateColorCodes('&', c.getString("casinoplus.menu.name")));
        ItemStack ec = new ItemStack(Material.ENDER_CHEST, 1);
        ItemMeta ecmeta = ec.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§dБилет для участия купить тут");
        lore.add("§aсайт");
        lore.add("§cусем удачи");
        lore.add("§eсливайте деньги");
        ecmeta.setLore(lore);
        int lvl = 0;
        int lvl2 = 0;
        lvl = 0;
        for(int f = 0; f <= 3; f++){
            lvl2++;
            String name = "x" + x(lvl2, 3);
            ecmeta.setDisplayName(name);

            ec.setItemMeta(ecmeta);
            i.setItem((lvl * 9) + f, ec);
        }
        lvl = 1;
        for(int f = 0; f <= 3; f++){
            lvl2++;
            String name = "x" + x(lvl2, 3);
            ecmeta.setDisplayName(name);
            ec.setItemMeta(ecmeta);
            i.setItem((lvl * 9) + f, ec);
        }
        lvl = 2;
        for(int f = 0; f <= 3; f++){
            lvl2++;
            String name = "x" + x(lvl2, 3);
            ecmeta.setDisplayName(name);
            ec.setItemMeta(ecmeta);
            i.setItem((lvl * 9) + f, ec);
        }
        lvl = 3;
        for(int f = 0; f <= 3; f++){
            lvl2++;
            String name = "x" + x(lvl2, 3);
            ecmeta.setDisplayName(name);
            ec.setItemMeta(ecmeta);
            i.setItem((lvl * 9) + f, ec);
        }
        lvl = 4;
        for(int f = 0; f <= 3; f++){
            lvl2++;
            String name = "x" + x(lvl2, 3);
            ecmeta.setDisplayName(name);
            ec.setItemMeta(ecmeta);
            i.setItem((lvl * 9) + f, ec);
        }
        ec = new ItemStack(Material.CHEST, 1);
        ecmeta = ec.getItemMeta();
        ecmeta.setDisplayName("§eСтарт");
        ec.setItemMeta(ecmeta);
        for(int z = 0; z <= 4; z++){
            for(int f = 0; f <= 4; f++)
                i.setItem(4+(z*9)+f, ec);
        }

        ec = new ItemStack(Material.WOOL, 1, (short)14);
        ecmeta = ec.getItemMeta();
        ecmeta.setDisplayName("§cБольше бомб");
        ec.setItemMeta(ecmeta);
        i.setItem(9*5, ec);

        ec = new ItemStack(Material.WOOL, 1, (short)5);
        ecmeta = ec.getItemMeta();
        ecmeta.setDisplayName("§aМеньше бомб");
        ec.setItemMeta(ecmeta);
        i.setItem(9*5 +8, ec);

        ec = new ItemStack(Material.BEACON, 1);
        ecmeta = ec.getItemMeta();
        ecmeta.setDisplayName("§eЗабрать &a" + stavka);
        ec.setItemMeta(ecmeta);
        i.setItem(9*5 +4, ec);


        p.openInventory(i);
        CasinoGame game = new CasinoGame(stavka, i);
        Network.newcasino.put(p, game);
        return true;
    }
    public float x(int x, int lvl){
        if(lvl == 3){
            if (x == 0)return (float) 1;
            if (x == 1)return (float) 1.07;
            if (x == 2)return (float) 1.26;
            if (x == 3)return (float) 1.41;
            if (x == 4)return (float) 1.64;
            if (x == 5)return (float) 1.91;
            if (x == 6)return (float) 2.25;
            if (x == 7)return (float) 2.67;
            if (x == 8)return (float) 3.21;
            if (x == 9)return (float) 3.9;
            if (x == 10)return (float) 4.8;
            if (x == 11)return (float) 6;
            if (x == 12)return (float) 7.63;
            if (x == 13)return (float) 9.93;
            if (x == 14)return (float) 13.24;
            if (x == 15)return (float) 18.2;
            if (x == 16)return (float) 26.01;
            if (x == 17)return (float) 39.01;
            if (x == 18)return (float) 62.42;
            if (x == 19)return (float) 109.24;
            if (x == 20)return (float) 215.49;
            if (x == 21)return (float) 546.24;
            if (x == 22)return (float) 2180;
            if (x == 23)return (float) 2180;
        }
        if(lvl == 2){
            if (x == 0)return (float) 1;
            if (x == 1)return (float) 1.07;
            if (x == 2)return (float) 1.26;
            if (x == 3)return (float) 1.41;
            if (x == 4)return (float) 1.64;
            if (x == 5)return (float) 1.91;
            if (x == 6)return (float) 2.25;
            if (x == 7)return (float) 2.67;
            if (x == 8)return (float) 3.21;
            if (x == 9)return (float) 3.9;
            if (x == 10)return (float) 4.8;
            if (x == 11)return (float) 6;
            if (x == 12)return (float) 7.63;
            if (x == 13)return (float) 9.93;
            if (x == 14)return (float) 13.24;
            if (x == 15)return (float) 18.2;
            if (x == 16)return (float) 26.01;
            if (x == 17)return (float) 39.01;
            if (x == 18)return (float) 62.42;
            if (x == 19)return (float) 109.24;
            if (x == 20)return (float) 215.49;
            if (x == 21)return (float) 546.24;
            if (x == 22)return (float) 2180;
            if (x == 23)return (float) 2180;
        }
        return 1;
    }
}
