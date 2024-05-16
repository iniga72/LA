package happycraft.network.commands;

import happycraft.network.Database;
import happycraft.network.Network;
import happycraft.network.params.AucParams;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

import static happycraft.network.Network.aucParams;

public class Auc implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))return true;
        String prefix = Network.c.getString("auction.prefix").replace("&", "§");
        if(args.length == 0){
            for(String msg : Network.c.getStringList("auction.help.sel")){
                commandSender.sendMessage(msg.replace("&", "§"));
            }
            return true;
        }
        if(args.length == 1){
            if(aucParams == null){
                for(String msg : Network.c.getStringList("auction.help.sel")){
                    commandSender.sendMessage(msg.replace("&", "§"));
                }
                return true;
            }
            if(aucParams.getUser().equals(commandSender.getName()) || Bukkit.getPlayer(aucParams.getUser()).getAddress().getAddress().getHostAddress().equals(((Player) commandSender).getAddress().getAddress().getHostAddress())){
                commandSender.sendMessage(prefix +Network.c.getString("auction.self").replace("&", "§"));
                return true;
            }
            try {
                float money = Float.parseFloat(args[0]);
                if(money < 0.001) money = (float) 0.001;
                money = Precision.round(money, 3);
                float my = Database.getMoney(commandSender.getName().toLowerCase());
                if(money > my){
                    //нужно больше денег
                    commandSender.sendMessage(prefix +Network.c.getString("auction.money").replace("&", "§"));
                    return true;
                }
                float step = aucParams.getStep(commandSender.getName());
                if(aucParams.getMoney() <(money + step)){


                    my-=money;
                    Database.updateMoney(commandSender.getName().toLowerCase(), my);



                    Network.push("money give "+aucParams.getUser()+" " + money);
                    aucParams.setMoney(step + money);
                    aucParams.setMoney(step + money);
                    aucParams.setWin(commandSender.getName());
                    aucParams.setStep(commandSender.getName(),step + money);
                    //новый фаворит
                    step =  Precision.round(money, 3);
                    Bukkit.broadcastMessage(prefix +Network.c.getString("auction.newstep")
                            .replace("$player",  aucParams.getWin())
                            .replace("$step", step + "")
                            .replace("&", "§")
                    );

                    return true;
                }else {
                    float newstep = (float) (aucParams.getMoney() - step + 0.001);
                    commandSender.sendMessage(prefix +Network.c.getString("auction.moneystep")
                            .replace("$step", newstep + "")
                            .replace("&", "§")
                    );
                    return true;
                }
            }catch (Exception ignored){
                String name = Network.c.getString("case.items." + aucParams.getDonate() + ".name").replace("&", "§");
                for(String msg : Network.c.getStringList("auction.help.step")){
                    commandSender.sendMessage(msg
                            .replace("$player", aucParams.getWin())
                            .replace("$step", aucParams.getMoney() + "")
                            .replace("$donate", name)
                            .replace("&", "§")
                    );
                }
                return true;
            }
        }
        if(args.length == 2){
            if(aucParams != null){
                commandSender.sendMessage(prefix +Network.c.getString("auction.wait").replace("&", "§"));
                //игра идёт
                return true;
            }
            float money = 0;
            try {
                money = Float.parseFloat(args[1]);
                if(money < 0.001) money = (float)0.001;
                money = Precision.round(money, 3);;
                if(money < 0 || money > 2000){
                    for(String msg : Network.c.getStringList("auction.help.sel")){
                        commandSender.sendMessage(msg.replace("&", "§"));
                    }
                    return true;
                }
            }catch (Exception ignored){
                for(String msg : Network.c.getStringList("auction.help.sel")){
                    commandSender.sendMessage(msg.replace("&", "§"));
                }
                return true;
            }
            int don = Database.getItemId(commandSender.getName().toLowerCase(), args[0]);
            if(don == 0){
                commandSender.sendMessage(prefix +Network.c.getString("auction.don")
                        .replace("&", "§")
                );
                return true;
            }
            try {
                String name = Network.c.getString("case.items." + args[0] + ".name").replace("&", "§");
                Database.removeItem(don);
                aucParams = new AucParams(commandSender.getName(), commandSender.getName(), money, args[0]);
                Plugin pl = Bukkit.getPluginManager().getPlugin("Network");
                new BukkitRunnable(){
                    int time = 6;
                    public void run() {
                        time--;
                        if(time <=0){
                            //Database.setInventoryItem(aucParams.getWin(), aucParams.getDonate());
                            String name = Network.c.getString("case.items." + aucParams.getDonate() + ".name").replace("&", "§");

                            Bukkit.broadcastMessage(prefix +Network.c.getString("auction.win")
                                    .replace("$player", aucParams.getWin())
                                    .replace("$step", aucParams.getMoney() + "")
                                    .replace("$donate", name)
                                    .replace("&", "§")
                            );
                            aucParams = null;
                            this.cancel();
                            return;
                        }else {
                            for(String msg : Network.c.getStringList("auction.help.step")){
                                Bukkit.broadcastMessage(msg
                                        .replace("$player", aucParams.getWin())
                                        .replace("$step", aucParams.getMoney() + "")
                                        .replace("$donate", name)
                                        .replace("&", "§")
                                );
                            }
                            Bukkit.broadcastMessage(prefix + Network.c.getString("auction.time")
                                    .replace("$time", time + "")
                                    .replace("&", "§")
                            );
                        }


                    }
                }.runTaskTimer(pl, 1, 60*20);
                return true;
            } catch (SQLException ignored) {
            }
        }
        for(String msg : Network.c.getStringList("auction.help.sel")){
            commandSender.sendMessage(msg.replace("&", "§"));
        }

        return true;
    }
}

