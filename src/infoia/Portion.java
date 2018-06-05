package infoia;

public class Portion {

    private int amount;
    private Ingredient ingredient;

    Portion(Ingredient ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void remove(int amount) {
        if (amount <= this.amount)
            this.amount -= amount;
        else
            throw new RuntimeException("Using more than available of ingredient " + ingredient.getName());
    }

    public void add(int amount) {
        this.amount += amount;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public String toString() {
        return amount + "g of " + ingredient;
    }
}
