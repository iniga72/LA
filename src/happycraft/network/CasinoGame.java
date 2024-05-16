package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static happycraft.network.Network.casino;
import static happycraft.network.Network.x;

public class CasinoGame {


    public static void openInventory(Player p, double count){
        Player pp = p;

        FileConfiguration c = Network.c;
        String name = c.getString("casino.name").replace("&", "§");
        Inventory i = Bukkit.createInventory(null, 4 * 9, name);

        //х1
        //х1,2
        //х1,5
        //х1,8
        //х2
        //х2,4
        //х2,8
        //х3
        //х5
        ArrayList<Integer> bombs = new ArrayList<>();
        ItemStack chest = new ItemStack(Material.CHEST, 1);
        String getx = c.getString("casino.info.defaultname").replace("&", "§");
        String getx2 = c.getString("casino.info.defaultlore").replace("&", "§");
        ItemStack start = new ItemStack(Material.BEACON, 1);
        ItemMeta meta  = start.getItemMeta();
        meta.setDisplayName(c.getString("casino.start").replace("&", "§"));
        start.setItemMeta(meta);
        i.setItem(0, start);
        i.setItem(9, start);
        i.setItem(18, start);

        start = new ItemStack(Material.BEACON, 1);
        meta.setDisplayName("§1");
        start.setItemMeta(meta);
        i.setItem(1, start);
        i.setItem(2, start);
        i.setItem(3, start);
        i.setItem(4, start);
        i.setItem(5, start);
        i.setItem(6, start);
        i.setItem(7, start);
        i.setItem(8, start);
        i.setItem(10, start);
        i.setItem(11, start);
        i.setItem(12, start);
        i.setItem(13, start);
        i.setItem(14, start);
        i.setItem(15, start);
        i.setItem(16, start);
        i.setItem(17, start);

        i.setItem(19, start);
        i.setItem(20, start);
        i.setItem(21, start);
        i.setItem(22, start);
        i.setItem(23, start);
        i.setItem(24, start);
        i.setItem(25, start);
        i.setItem(26, start);

        for (int nslot = 0; nslot <=8; nslot++) {
            int r = (int) (Math.random() * 3);
            if(r == 0)bombs.add((nslot));
            else if (r == 1)bombs.add(nslot+9);
            else if (r == 2)bombs.add(nslot+18);
            meta  = chest.getItemMeta();
            meta.setDisplayName(getx.replace("$x", x(nslot) + ""));
            ArrayList<String> lore = new ArrayList<>();
            String rg = (count * x(nslot)) + "";
            String result = rg.replace(".", "_");
            if(result.split("_").length >= 2){
                if(result.split("_")[1].length() > 3){
                    result =result.substring(0,(result.split("_")[0].length() +4));
                }
            }
            lore.add(getx2.replace("$win",  result.replace("_", ".")));
            meta.setLore(lore);
            chest.setItemMeta(meta);
            i.setItem(nslot+27, chest);

            /*ItemStack ItemUse1 = new ItemStack(Material.TNT, 1);
            ItemStack ItemUse2 = new ItemStack(Material.DIAMOND, 1);
            if(otvet()){
                bombs.add((nslot));
                ItemUse1 = new ItemStack(Material.DIAMOND, 1);
                ItemUse2 = new ItemStack(Material.TNT, 1);
            }else {
                bombs.add(nslot+9);
            }

            i.setItem(nslot, ItemUse2);
            i.setItem(nslot+9, ItemUse1);*/

        }
        CasinoParams cp = new CasinoParams(count, i, 0, bombs);
        casino.put(p, cp);
        p.openInventory(i);
       /* ItemStack item;
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
        i.setItem(9, item);*/

        //getAllItems

    }
}
