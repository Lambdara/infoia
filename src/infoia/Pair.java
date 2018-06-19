package infoia;

/**
 * This is a pair of a Portion and its attached value. 
 * The value is used to calculate the utility of a recipe. 
 * This value is either the similarity or a punishment value for leaving the ingredient out of the recipe.
 * 
 * @author Intelligent Agents Group 6
 */
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
