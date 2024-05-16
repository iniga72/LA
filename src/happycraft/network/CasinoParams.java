package happycraft.network;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class CasinoParams {
    double stavka;
    boolean game = true;
    Inventory inv;

    public double getStavka() {
        return stavka;
    }

    public Inventory getInv() {
        return inv;
    }

    public boolean isGame() {
        return game;
    }

    public void setGame(boolean game) {
        this.game = game;
    }

    public CasinoParams(double stavka, Inventory inv, int win, ArrayList<Integer> bombs) {
        this.stavka = stavka;
        this.inv = inv;
        this.win = win;
        this.bombs = bombs;
    }

    int win;
    ArrayList<Integer> bombs;

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public ArrayList<Integer> getBombs() {
        return bombs;
    }
}
