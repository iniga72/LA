package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static happycraft.network.Network.*;
import static happycraft.network.Network.invItems;

public class ClanEvents {
   /* public static void openInventory1(Player p){
        Player pp = p;
        FileConfiguration c = Network.c;
        String name = c.getString("case.inventory.name").replace("&", "§");
        Inventory i = Bukkit.createInventory(null, 4 * 9, name);
        int slot = 9;

        //ArrayList<String> params = Database.getAllItems(p.getName().toLowerCase());
        String sell = c.getString("case.inventory.sell").replace("&", "§");
        String use = c.getString("case.inventory.use").replace("&", "§");
        String sellplus = c.getString("case.inventory.sellplus").replace("&", "§");
        for (String s : params) {
            if(slot >= 9 && slot <= 17){
                *//*ItemStack ItemSell = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (short) 14);
                ItemMeta ItemSellMeta = ItemSell.getItemMeta();
                ItemSellMeta.setDisplayName(sell);
                ItemSell.setItemMeta(ItemSellMeta);
                i.setItem(slot-9, ItemSell);
                sellmenu.put(p.getName() + "_._" + (slot-9), s);*//*
                ItemStack ItemUse = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (short) 5);
                ItemMeta ItemUseMeta = ItemUse.getItemMeta();
                ItemUseMeta.setDisplayName(use);
                ItemUse.setItemMeta(ItemUseMeta);
                i.setItem(slot+9, ItemUse);
                usemenu.put(p.getName() + "_._" +(slot+9), s);

                ItemStack ItemSellServer = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (short) 15);
                ItemMeta ItemSellMetaServer = ItemUse.getItemMeta();
                ItemSellMetaServer.setDisplayName(sellplus);
                ItemSellServer.setItemMeta(ItemSellMetaServer);
                i.setItem(slot+18, ItemSellServer);
                sellplusmenu.put(p.getName() + "_._" +(slot+18), s);


                ItemStack item = new ItemStack(Material.DIAMOND, 1);
                ItemMeta meta = item.getItemMeta();
                String nameitem = c.getString("case.items." + s +  ".name").replace("&", "§");
                meta.setDisplayName(nameitem);
                item.setItemMeta(meta);
                i.setItem(slot, item);
            }
            slot++;
        }
       *//* ItemStack item;
        ItemMeta meta;
        item = new ItemStack(Material.ARROW, 1);
        meta = item.getItemMeta();
        meta.setDisplayName("§eДалее");
        item.setItemMeta(meta);
        i.setItem(17, item);
        item = new ItemStack(Material.ARROW, 1);
        meta = item.getItemMeta();
        meta.setDisplayName("§eНазад");
        item.setItemMeta(meta);
        i.setItem(9, item);*//*
        p.openInventory(i);
        invItems.put(p, i);
        //getAllItems

    }*/
}
