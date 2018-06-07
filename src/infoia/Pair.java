package infoia;

public class Pair {
    private Portion p;
    private Double value;

    public Pair(Portion p, Double value) {
        this.p = p;
        this.value = value;
    }

    public Portion getPortion() {
        return p;
    }

    public Double getValue() {
        return value;
    }

    public void setPortion(Portion p) {
        this.p = p;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
