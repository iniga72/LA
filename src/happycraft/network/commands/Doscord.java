package happycraft.network.commands;
import happycraft.network.Database;
import happycraft.network.Network;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class Doscord implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;
        if(args.length == 1){
            if(Network.dsaccept.containsKey(args[0])){
                if(!Database.getDiscord(p.getName()).equals("")){
                    p.sendMessage(Network.c.getString("discord.title") +
                            Network.c.getString("discord.badacceptplayer").replace("&", "§"));
                    Network.dsaccept.remove(args[0]);
                    return true;
                }
            }else {
                return true;
            }
            GuildMessageReceivedEvent e = Network.dsaccept.get(args[0]);
            if(!Database.getDiscordPlayer(e.getAuthor().getAsTag()).equals("")){
                p.sendMessage(Network.c.getString("discord.title") +
                        Network.c.getString("discord.badacceptplayer").replace("&", "§"));
                Network.dsaccept.remove(args[0]);
                return true;
            }
            try {
                Database.updateDiscord(p.getName(), e.getAuthor().getAsTag().split("#")[1]);
                e.getChannel().sendMessage("Запрос принят. Привязка подключена.").queue();
                p.sendMessage(Network.c.getString("discord.title") +
                        Network.c.getString("discord.good").replace("&", "§"));
                Network.dsaccept.remove(args[0]);
            } catch (SQLException ignored) {
            }
        }
        return true;
    }
}
