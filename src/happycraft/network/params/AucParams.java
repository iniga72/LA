package happycraft.network.params;

import java.util.HashMap;
import java.util.Map;

public class  AucParams{
    public AucParams() {
    }

    String user;
    String win;
    Float money;
    String donate;
    Map<String, Float> donats = new HashMap<>();

    public String getDonate() {
        return donate;
    }

    public String getWin() {
        return win;
    }

    public Float getStep(String p) {
        if(donats.containsKey(p)){
            return donats.get(p);

        }
        return(float) 0;
    }
    public void setStep(String p, float step) {
        donats.put(p, step);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Float getMoney() {
        return money;
    }

    public String getUser() {
        return user;
    }

    public void setMoney(Float money) {
        this.money = money;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public AucParams(String user, String win, Float money, String donate) {
        this.user = user;
        this.win = win;
        this.money = money;
        this.donate = donate;
    }
}
