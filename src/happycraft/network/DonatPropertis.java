package happycraft.network;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class DonatPropertis {
    HashMap<Integer, String> slotname;
    Inventory i;

    public HashMap<Integer, String> getSlotname() {
        return slotname;
    }

    public Inventory getI() {
        return i;
    }

    public DonatPropertis(HashMap<Integer, String> slotname, Inventory i) {
        this.slotname = slotname;
        this.i = i;
    }
}
