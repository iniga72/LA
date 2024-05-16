package happycraft.network.params;

public class Case {
    boolean donate;
    int id;

    public boolean isDonate() {
        return donate;
    }

    public int getId() {
        return id;
    }

    public Case(boolean donate, int id) {
        this.donate = donate;
        this.id = id;
    }

    public void setDonate(boolean donate) {
        this.donate = donate;
    }
}
