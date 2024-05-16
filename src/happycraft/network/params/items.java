package happycraft.network.params;

public class items {
    int id;
    int time;
    String item;
    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public String getItem() {
        return item;
    }

    public items(int id, int time, String item) {
        this.id = id;
        this.time = time;
        this.item = item;
    }
}
