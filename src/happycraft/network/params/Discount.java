package happycraft.network.params;

public class Discount {
    int Discount;
    String time;

    public int getDiscount() {
        return Discount;
    }

    public String getTime() {
        return time;
    }

    public Discount(int discount, String time) {
        Discount = discount;
        this.time = time;
    }
}
