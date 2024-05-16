package happycraft.network;

public class SelParams {
    String donate;
    String player;

    public String getDonate() {
        return donate;
    }

    public String getPlayer() {
        return player;
    }

    public double getPrice() {
        return price;
    }

    public SelParams(String donate, String player, double price) {
        this.donate = donate;
        this.player = player;
        this.price = price;
    }

    double price;
}
