package happycraft.network;

public class BanPropertis {
    int id;
    String admin;
    String date;
    String reason;
    String time;
    String punish;
    String ptiority;
    String statusclear;

    public BanPropertis(int id, String admin, String date, String reason, String time, String punish, String ptiority, String statusclear) {
        this.id = id;
        this.admin = admin;
        this.date = date;
        this.reason = reason;
        this.time = time;
        this.punish = punish;
        this.ptiority = ptiority;
        this.statusclear = statusclear;
    }

    public int getId() {
        return id;
    }

    public String getAdmin() {
        return admin;
    }

    public String getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public String getTime() {
        return time;
    }

    public String getPunish() {
        return punish;
    }

    public String getPtiority() {
        return ptiority;
    }

    public String getStatusclear() {
        return statusclear;
    }
}
