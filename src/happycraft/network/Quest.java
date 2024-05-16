package happycraft.network;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Quest implements Listener, CommandExecutor {


    @EventHandler
    public void	breack(BlockBreakEvent e) throws SQLException {
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Player p = e.getPlayer();
        int questid = Database.getQuestEnableId(p.getName().toLowerCase());
        if(questid == 0)return;
        FileConfiguration c = Network.c;
        String l = Database.getQuestName(questid);
        if(!c.getString("quest.quest." + l + ".type").equals("BREAK"))return;
        if(p.getItemInHand().getTypeId() == c.getInt("quest.quest." + l + ".id") && e.getBlock().getTypeId() == c.getInt("quest.quest."+l + ".block")) {
            int amount = c.getInt("quest.quest." + Database.getQuestName(questid) + ".amount");
            int count = Database.getQuestCount(questid);
            count++;
            Database.updateQuestCount(questid, count);
            if(count >= amount) {
                p.sendMessage("§aКвест был выполнен.");
                float am = c.getInt("quest.quest." + l + ".ball") + Database.getMoney(p.getName().toLowerCase());
                Database.updateMoney(p.getName().toLowerCase(), am);
                Database.updateQuest(questid, "end");
            }
        }else {

        }
    }
    @EventHandler
    public void killers(EntityDeathEvent e) throws SQLException {
        if(e.getEntity().getKiller() == null)return;
        if(e.getEntity().getKiller().getGameMode() == GameMode.CREATIVE) return;
        String p = e.getEntity().getKiller().getName();
        int questid = Database.getQuestEnableId(p.toLowerCase());
        if(questid == 0)return;
        String l = Database.getQuestName(questid);
        FileConfiguration c = Network.c;
        if(c.getString("quest.quest." + l + ".type").equals("PLAYER")) {
            if(e.getEntity().getType().toString().equals("PLAYER")){
                if(e.getEntity().getKiller().getItemInHand().getTypeId() == c.getInt("quest.quest." + l + ".id")) {
                    int amount = c.getInt("quest.quest." + Database.getQuestName(questid) + ".amount");
                    int count = Database.getQuestCount(questid);
                    count++;
                    Database.updateQuestCount(questid, count);
                    if(count >= amount) {
                        e.getEntity().getKiller().sendMessage("§aКвест был выполнен.");
                        float am = c.getInt("quest.quest." + l + ".ball") + Database.getMoney(p.toLowerCase());
                        Database.updateMoney(p.toLowerCase(), am);
                        Database.updateQuest(questid, "end");
                    }

                }
            }

        }
        else if(c.getString("quest.quest." + l + ".type").equals("MOB")){
            if(e.getEntity().getType().toString().toLowerCase().equals(c.getString("quest.quest." + l + ".mob"))){
                if(e.getEntity().getKiller().getItemInHand().getTypeId() == c.getInt("quest.quest." + l + ".id")) {

                    int amount = c.getInt("quest.quest." + Database.getQuestName(questid) + ".amount");
                    int count = Database.getQuestCount(questid);
                    count++;
                    Database.updateQuestCount(questid, count);
                    if(count >= amount) {
                            e.getEntity().getKiller().sendMessage("§aКвест был выполнен.");
                        float am = c.getInt("quest.quest." + l + ".ball") + Database.getMoney(p.toLowerCase());
                            Database.updateMoney(p.toLowerCase(), am);
                            Database.updateQuest(questid, "end");
                        }
                    }
                }
            }



    }

    @EventHandler
    public void click(InventoryClickEvent e) throws SQLException {
        Inventory i = Network.questmenu.get(e.getWhoClicked());
        if(e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getTypeId() == 0) return;
        if(e.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        if(i == null) return;
        e.setCancelled(true);
        if(Network.questslot.get(e.getWhoClicked().getName().toLowerCase() + "_" + e.getSlot()) == null) return;

        int questid = Database.getQuestEnableId(e.getWhoClicked().getName().toLowerCase());//активированный квест
        if(questid == 0){
            Database.addQuestNew(e.getWhoClicked().getName().toLowerCase(), Network.questslot.get(e.getWhoClicked().getName().toLowerCase() + "_" + e.getSlot()));
        }else {
            int id = Database.getQuestId(e.getWhoClicked().getName().toLowerCase(), Network.questslot.get(e.getWhoClicked().getName().toLowerCase() + "_" + e.getSlot()));
            Database.updateQuest(questid, "disable");
            if(id == 0){
                Database.addQuestNew(e.getWhoClicked().getName().toLowerCase(), Network.questslot.get(e.getWhoClicked().getName().toLowerCase() + "_" + e.getSlot()));
                Database.updateQuest(questid, "disable");
            }else {
                Database.updateQuest(id, "enable");
            }
        }

        Player p = (Player) e.getWhoClicked();
        p.sendMessage("§e" + e.getWhoClicked().getName() + " взял квест " + Network.questslot.get(e.getWhoClicked().getName().toLowerCase() + "_" + e.getSlot()));
        p.closeInventory();
    }
    @EventHandler
    public void onclose(InventoryCloseEvent e) {
        Network.questmenu.remove(e.getPlayer());
    }
    @Override
    public boolean onCommand(CommandSender sender, final Command cmd, final String cmdlabel, final String[] args) {
        if (!(sender instanceof Player)) {
            if(args.length == 1){
                String user = args[0];
                try {
                    Database.clearQuest(user);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sender.sendMessage("good");
            }
            return true;
        }
        Player p = (Player)sender;
        FileConfiguration c = Network.c;
        ConfigurationSection quests = c.getConfigurationSection("quest.quest");
        Inventory i;
        int only = quests.getKeys(false).size();
        int only1 = 0;
        only1 = only / 9 + 1;
        only1*=9;
        only1 +=9;
        String title = c.getString("quest.name").replace("&", "§");
        i = Bukkit.createInventory(null, only1, title);
        Network.questmenu.put(p, i);
        ItemStack items1 = new ItemStack(340, 1);
        ItemMeta meta = items1.getItemMeta();
        meta.setDisplayName("§aИнформация.");
        List<String> info = c.getStringList("quest.info");
        meta.setLore(info);
        items1.setItemMeta(meta);
        i.setItem(3, items1);
        i.setItem(5, items1);

        items1 = new ItemStack(384, 1);
        meta = items1.getItemMeta();
        meta.setDisplayName("§aТекущий квест:");
        int questid = Database.getQuestEnableId(p.getName().toLowerCase());
        ArrayList<String> lore = new ArrayList<>();
        if(questid == 0) lore.add("§aКвест не выбран");
        else {
            int amount = c.getInt("quest.quest." + Database.getQuestName(questid) + ".amount");
            int count = Database.getQuestCount(questid);
            lore.add("§eНазвание: " + Database.getQuestName(questid));
            lore.add("§eВыполнено : §f" + count + " §eиз §f" + amount);
        }
        meta.setLore(lore);
        items1.setItemMeta(meta);
        i.setItem(4, items1);
        int is = 9;
        for(String s : quests.getKeys(false)) {
            String perm = c.getString("quest.quest." + s + ".permission");
            if(c.getString("quest.quest." + s + ".type") != null &&
                    c.getString("quest.quest." + s + ".type") != null &&
                    c.getString("quest.quest." + s + ".permission") != null &&
                    c.getString("quest.quest." + s + ".id") != null &&
                    c.getString("quest.quest." + s + ".item") != null &&
                    c.getString("quest.quest." + s + ".amount") != null &&
                    c.getString("quest.quest." + s + ".ball") != null &&
                    c.getString("quest.quest." + s + ".stats") != null &&
                    c.getString("quest.quest." + s) != null
            ) {
                int amount = c.getInt("quest.quest." + s + ".amount");
                int count = Database.getQuestCount(Database.getQuestId(p.getName().toLowerCase(), s));
                if(amount > count && p.hasPermission(perm)){
                    List<String> disc = c.getStringList("quest.quest." + s + ".disc");
                    int item = c.getInt("quest.quest." + s + ".item");
                    ItemStack items = new ItemStack(item, 1);
                    ItemMeta meta1 = items.getItemMeta();
                    meta1.setDisplayName(s);
                    meta1.setLore(disc);
                    items.setItemMeta(meta1);
                    i.setItem(is, items);
                    Network.questslot.put(p.getName().toLowerCase() + "_" + is, s);
                    is++;

                }
            }
        }
        if(Network.questslot.size() >150){
            ArrayList<String> values = new ArrayList<>(Network.questslot.keySet());
            Network.questslot.remove(values.get(0));
            Network.questslot.remove(values.get(1));
            Network.questslot.remove(values.get(2));
            Network.questslot.remove(values.get(3));
            Network.questslot.remove(values.get(4));
        }
        p.openInventory(i);
        return true;
    }
}
