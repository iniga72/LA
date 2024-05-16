package happycraft.network.commands;

import happycraft.network.Database;
import happycraft.network.Network;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class reset implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player))return true;
        if(strings.length != 1) return true;
        //Network.push("pex user " + commandSender.getName() + " group set " + Database.reset(commandSender.getName(), strings[0]));
        commandSender.sendMessage("+");
        return true;
    }
}
