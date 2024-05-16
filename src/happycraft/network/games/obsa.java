package happycraft.network.games;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import happycraft.network.Database;
import happycraft.network.Network;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class obsa implements Listener, CommandExecutor {
    @Override
        public boolean onCommand(CommandSender sender, Command command, String lagel, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(!Database.isOwner(p.getName())){
            p.sendMessage("§cGame §6> §fВы не являетесь администратором сервера.");
            return true;
        }

        if(Network.game_obsa_check){
            p.sendMessage("§cGame §6> §fКонкурс уже идёт.");
            return true;
        }
        Network.game_obsa = p.getLocation().getWorld().getName() + "_"+
                p.getLocation().getBlockX() + "_"+
                p.getLocation().getBlockY() + "_"+
                p.getLocation().getBlockZ() + "_"

        ;
        p.getLocation().getBlock().setType(Material.OBSIDIAN);
        Network.game_obsa_check = true;

        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("§cGame §6> §fКонкурс начался");
        Bukkit.broadcastMessage("§cGame §6> §fВсем удачи");
        Bukkit.broadcastMessage(" ");
        return false;
    }
    @EventHandler
    public void game(BlockBreakEvent e){
        if(!Network.game_obsa_check)return;
        Player p = e.getPlayer();
        if(!p.getLocation().getWorld().getName().equalsIgnoreCase("mg"))return;

        for (final ProtectedRegion r : WGBukkit.getRegionManager(p.getWorld()).getApplicableRegions(e.getBlock().getLocation())) {
            if(r.getId().equals("game")){
                if(e.getBlock().getType() != Material.OBSIDIAN){
                    e.setCancelled(true);
                    p.sendMessage(" ");
                    p.sendMessage("§cGame §6> §fНужно искать обсидиан.");
                    p.sendMessage(" ");
                    return;
                }
                if(p.getGameMode() != GameMode.SURVIVAL){
                    e.setCancelled(true);
                    p.sendMessage(" ");
                    p.sendMessage("§cGame §6> §fПерейдитее в режим выживания.");
                    p.sendMessage(" ");
                    return;
                }

                if(p.getActivePotionEffects().size() > 0){
                    e.setCancelled(true);
                    p.sendMessage(" ");
                    p.sendMessage("§cGame §6> §fНеобходимо очистить все эффекты для участия.");
                    p.sendMessage(" ");
                    return;
                }
                if(p.getItemInHand().getType() != Material.STONE_PICKAXE){
                    e.setCancelled(true);
                    p.sendMessage(" ");
                    p.sendMessage("§cGame §6> §fМожно использовать только каменную кирку");
                    p.sendMessage(" ");
                    return;
                }
                if(p.getItemInHand().getEnchantments().size() >0){
                    e.setCancelled(true);
                    p.sendMessage(" ");
                    p.sendMessage("§cGame §6> §fМожно использовать только обычную кирку");
                    p.sendMessage(" ");
                    return;
                }
                if(Network.game_obsa.equals(e.getBlock().getLocation().getWorld().getName() + "_"+
                        e.getBlock().getLocation().getBlockX() + "_"+
                        e.getBlock().getLocation().getBlockY() + "_"+
                        e.getBlock().getLocation().getBlockZ() + "_")){
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§cGame §6> §fКонкурс завершен.");
                    Bukkit.broadcastMessage("§cGame §6> §fПобедил в конкурсе: §e" + p.getName() );
                    Bukkit.broadcastMessage("§cGame §6> §fИ получает: §a" + Bukkit.getOnlinePlayers().size());
                    Bukkit.broadcastMessage("");
                    Network.push("money give " + p.getName() + " " + Bukkit.getOnlinePlayers().size());
                    Network.game_obsa_check = false;
                }else{
                    e.setCancelled(true);
                    p.sendMessage(" ");
                    p.sendMessage("§cGame §6> §fВы не угадали. Ищите другой блок");
                    p.sendMessage(" ");
                    return;

                }
            }
        }
    }





















}
