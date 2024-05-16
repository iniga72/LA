package happycraft.network;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.Random;

public class MegaJail  implements CommandExecutor, Listener, Runnable {
    //z -3079
    //x 3658
    // 64
    //s 8
    //r 70
    /*public void move(PlayerMoveEvent e){


    }*/
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public static void breack(BlockBreakEvent e) throws SQLException {
        int count = Database.getJail(e.getPlayer().getName());
        if(count <=0)return;
        e.setCancelled(true);
        //if(e.isCancelled()) return;
        if(e.getPlayer().getItemInHand().getType() != Material.STONE_PICKAXE &&
                !(Database.isJaildonat(e.getPlayer().getName()) && e.getPlayer().getItemInHand().getType() == Material.IRON_PICKAXE)

        )return;
        if(e.getPlayer().getItemInHand().getEnchantments().size() > 0)return;
        if(e.getPlayer().getGameMode() != GameMode.SURVIVAL)return;
        if(e.getPlayer().getActivePotionEffects().size() >0)return;
        String block = Database.getJailBlock(e.getPlayer().getName());
        e.setCancelled(true);
        if(!e.getBlock().getType().toString().replace("_", "").toLowerCase().equalsIgnoreCase(block.split("_")[0])){
            e.getPlayer().sendMessage(Network.c.getString("jail.last")
                    .replace("$count", count + "")
                    .replace("$block", block.split("_")[1])
                    .replace("&", "§")
            );
            //e.getPlayer().sendMessage(e.getBlock().getType().toString().toLowerCase());
            return;
        }

        String block2 = Network.c.getStringList("jail.blocks").get(new Random().nextInt( Network.c.getStringList("jail.blocks").size()));
        Database.updatejail(e.getPlayer().getName(), block2);
        count--;
        if(count <=0){
            e.getPlayer().sendMessage(Network.c.getString("jail.unjail").replace("&", "§"));

        }
        else e.getPlayer().sendMessage(Network.c.getString("jail.last")
                .replace("$count", count + "")
                .replace("$block", block2.split("_")[1])
                .replace("&", "§")
        );

    }
    World world = Bukkit.getWorld("world");
    double x = 3657;
    double y = 64d;
    double z = -3075;
    Location jail =  new Location(world, x, y, z);
    @Override
    public void run() {

                for (Player p : Bukkit.getServer().getOnlinePlayers()){
                    int count = Database.getJail(p.getName());
                    if(count >0){
                        Location player = p.getLocation();


                        if(jail.distance(player) > 20){
                            ItemStack axe = new ItemStack(Material.STONE_PICKAXE, 1);
                            p.getInventory().addItem(axe);
                            p.teleport(jail);
                            p.sendMessage(Network.c.getString("jail.info").replace("$count", count + "").replace("&", "§"));
                        }
                    }else{
                        if (offset(p) > 10)
                            continue;
                        if(p.getGameMode() == GameMode.SPECTATOR)
                            return;
                        Entity bottom = p;
                        while (bottom.getVehicle() != null)
                            bottom = bottom.getVehicle();
                        velocity(bottom, getTrajectory2d(bottom), 1.6, true, 0.8, 0, 10);
                    }

                }
    }
    public double offset(Entity a) {
        return a.getLocation().toVector().subtract(Network.jailloc.toVector()).length();
    }
    public Vector getTrajectory2d(Entity to){
        return to.getLocation().toVector().subtract(Network.jailloc.toVector()).setY(0).normalize();
    }
    public void velocity(Entity ent, Vector vec, double str, boolean ySet, double yBase, double yAdd, double yMax){
        if (Double.isNaN(vec.getX()) || Double.isNaN(vec.getY()) || Double.isNaN(vec.getZ()) || vec.length() == 0)
            return;
        if (ySet)
            vec.setY(yBase);
        vec.normalize();
        vec.multiply(str);
        vec.setY(vec.getY() + yAdd);
        if (vec.getY() > yMax)
            vec.setY(yMax);
        ent.setFallDistance(0);
        ent.setVelocity(vec);
    }
        @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(!p.hasPermission("network.jail")) {
                p.sendMessage(Network.c.getString("jail.perm").replace("&", "§"));
                return true;
            }
        }
        if(args.length < 2) {
            md.help(sender, "jail");
            return true;
        }
        int count = 0;
        try {
            count = Integer.parseInt(args[1]);
        }catch (Exception e){
            md.help(sender, "jail");
            return true;
        }

        try {
            //REPLACE INTO `jail`(`id`, `user`, `count`, `admin`) VALUES (NULL,'user',12,'admin');
            String block = Network.c.getStringList("jail.blocks").get(new Random().nextInt( Network.c.getStringList("jail.blocks").size()));
            Database.setjail(args[0].toLowerCase(), count,sender.getName(), block);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String reason = "";
        if(args.length == 2){
            reason = Network.c.getString("bans.messages.no_reason");

        }else {
            for(int i = 2; i < args.length; i++) {
                reason += args[i] + " ";
            }
        }
        md.sendServer(sender.getName(), args[0], "jail", "permanent", reason);
        return false;
    }
}

