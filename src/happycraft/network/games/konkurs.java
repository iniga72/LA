package happycraft.network.games;

import happycraft.network.Database;
import happycraft.network.Network;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class konkurs implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lekjrg, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(Database.isKonkurs(p.getName())){
            p.sendMessage("§cКонкурс §6> §fВы уже участвуете в конкурсе!");
            return true;
        }
        float money = Database.getMoney(p.getName().toLowerCase());
        if(10 > money){
            p.sendMessage(Network.c.getString("money.enough").replace("&", "§"));
            return true;
        }
        return true;

    }
}
