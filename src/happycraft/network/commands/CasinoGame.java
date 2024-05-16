package happycraft.network.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CasinoGame {
    float stavka;
    int step = 0;
    int lvl = 3;
    Inventory i;
    boolean status = true;

    public CasinoGame(float stavka, Inventory i) {
        this.stavka = stavka;
        this.i = i;
    }

    public float getStavka() {
        return stavka;
    }

    public void setStavka(float stavka) {
        this.stavka = stavka;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public Inventory getI() {
        return i;
    }

    public void setI(Inventory i) {
        this.i = i;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
