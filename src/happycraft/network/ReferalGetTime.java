package happycraft.network;

public class ReferalGetTime {
    int time;
    int count;

    public ReferalGetTime(int time, int count) {
        this.time = time;
        this.count = count;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
