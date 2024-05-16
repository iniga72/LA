package happycraft.network;

public class DonatHistory {
    int id;
    int amount;
    String nick;
    String date;

    public DonatHistory(int id, int amount, String nick, String date) {
        this.id = id;
        this.amount = amount;
        this.nick = nick;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getNick() {
        return nick;
    }

    public String getDate() {
        return date;
    }
}
