package happycraft.network;

import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Loto implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player))return true;
        Player p = (Player) commandSender;
        if(args.length !=1){
            p.sendMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                    Network.c.getString("loto.help").replace("&", "§")
            );
            return true;
        }

        try {
            /*String am = args[0];
            if(am.contains(".")){
                if(am.split(".")[1].length() > 3){
                    p.sendMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                            Network.c.getString("loto.nomoney").replace("&", "§")
                    );
                }
            }*/
            float amount = Float.parseFloat(args[0]);
            amount = Precision.round(amount, 3);;
            if(amount > 1000 || amount < 0.001){
                p.sendMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                        Network.c.getString("loto.nomoney").replace("&", "§")
                );
                return true;
            }
            float money = Database.getMoney(p.getName().toLowerCase());
            if(amount > money){
                p.sendMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                        Network.c.getString("loto.nomoney").replace("&", "§")
                );
                return true;
            }
            money-=amount;
            Database.updateMoney(p.getName().toLowerCase(), money);
            Network.lotomoney+=amount;
            if(Network.loto.containsKey(p))amount += Network.loto.get(p);


            if(Network.loto.size() <=0){
                Bukkit.broadcastMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                        Network.c.getString("loto.start").replace("&", "§")
                );
                Plugin pl = Bukkit.getPluginManager().getPlugin("Network");

                new BukkitRunnable(){
                    int time = 7;
                    public void run(){
                        time--;


                        if(time <=0){
                            ArrayList<Player> playe = new ArrayList<>();
                            for(Map.Entry<Player, Float> entry: Network.loto.entrySet()) {
                                playe.add(entry.getKey());
                            }
                            while (true){
                                    // get key
                                    Player key = playe.get(new Random().nextInt(playe.size()));
                                    float value = Network.loto.get(key);
                                    int rand = (Math.random() * Network.lotomoney <value)?0:1;
                                    if(rand == 0){
                                        int res = (int)(Network.loto.get(key) / Network.lotomoney * 100);
                                        float win = Precision.round(Network.lotomoney, 3);
                                        Bukkit.broadcastMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                                                Network.c.getString("loto.win")
                                                        .replace("$player", key.getName() + "")
                                                        .replace("$win",  + win+"")
                                                        .replace("$chance", res+ "")
                                                        .replace("&", "§")
                                        );
                                        Network.loto.clear();
                                        Network.push("money give " + key.getName() + " " + win);
                                        Network.lotomoney=0;
                                        this.cancel();
                                        return;
                                    }
                            }
                        }else {
                            float win = Precision.round(Network.lotomoney, 3);
                            Bukkit.broadcastMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                                    Network.c.getString("loto.time")
                                            .replace("$time", time*10 + "")
                                            .replace("$win", win + "")
                                            .replace("&", "§")
                            );
                        }
                    }
                }.runTaskTimer(pl, 1, 10*20);

                }

            Network.loto.put(p, amount);
            int res = (int)(amount / Network.lotomoney * 100);
            Bukkit.broadcastMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                    Network.c.getString("loto.stavka")
                            .replace("$player", p.getName())
                            .replace("$count", amount+"")
                            .replace("$chance", res+"")
                            .replace("&", "§")
            );
        } catch (Exception e) {
            p.sendMessage(Network.c.getString("loto.prefix").replace("&", "§")+
                    Network.c.getString("loto.help").replace("&", "§")
            );

            //lotomoney
            //int rand = (Math.random() * 94 <50)?0:1;
        }

        return true;
    }
}
