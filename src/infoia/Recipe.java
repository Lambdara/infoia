package infoia;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Recipe extends ArrayList<Ingredient> {
    private static final long serialVersionUID = 1L;

    String name;
    private HashMap<Ingredient, Double> leaveOutWeights;
    private HashMap<Ingredient, Pair> replacements;
    private ArrayList<Ingredient> shoppingList;

    Recipe(String name) {
        super();
        this.name = name;
        this.leaveOutWeights = new HashMap<Ingredient, Double>();
        for (Ingredient key : this.leaveOutWeights.keySet()) {
            this.leaveOutWeights.put(key, 1.0);
        }
        this.replacements = new HashMap();
        this.shoppingList = new ArrayList();
    }

    public String toString() {
        String output = name + ":\n";

        if (this.size() == 0) {
            output += "No ingredients";
        } else {
            for (Ingredient i : this) {
                output += i.getName();
                if (replacements.containsKey(i)) {
                    if (replacements.get(i).getIngredient() != null) {
                        output += " ---> " + replacements.get(i).getIngredient().getName() + ", value:"
                                + replacements.get(i).getValue() + "\n";
                    } else {
                        output += " ---> REMOVED value:" + replacements.get(i).getValue() + "\n";
                    }
                } else {
                    output += ", value:1.0";
                    if (shoppingList.contains(i))
                        output += " [Shopping List]";
                    output += "\n";
                }
            }
        }

        return output;
    }

    public void addWeightToIngredient(Ingredient ingredient, Double weight) {
        leaveOutWeights.put(ingredient, weight);
    }

    public Double getWeightByIngredient(Ingredient ingredient) {
        return leaveOutWeights.get(ingredient);
    }

    public void setReplacements(HashMap<Ingredient, Pair> replacements) {
        this.replacements = replacements;
    }

    public HashMap<Ingredient, Pair> getReplacements() {
        return replacements;
    }

    public double getLeaveOutWeight(Ingredient i) {
        return leaveOutWeights.get(i);
    }

    public void replace(Ingredient i, Pair p) {
        this.replacements.put(i, p);
    }

    public void putOnShoppingList(Ingredient i) {
        this.shoppingList.add(i);
    }

    public void replacementToShoppingList(Ingredient i) {
        this.replacements.remove(i);
        this.shoppingList.add(i);
    }

    public ArrayList<Ingredient> getShoppingList() {
        return this.shoppingList;
    }
}
