package happycraft.network;

import org.bukkit.entity.Player;

public class CInvite {
    Player p;
    String tag;

    public CInvite(Player p, String tag) {
        this.p = p;
        this.tag = tag;
    }

    public Player getP() {
        return p;
    }

    public String getTag() {
        return tag;
    }
}
