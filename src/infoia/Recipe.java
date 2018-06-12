package infoia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class Recipe extends ArrayList<Portion> {
    private static final long serialVersionUID = 1L;

    String name;
    private HashMap<Portion, Double> leaveOutWeights;
    private HashMap<Portion, Pair> replacements;
    private ArrayList<Portion> shoppingList;

    Recipe(String name) {
        super();
        this.name = name;
        this.leaveOutWeights = new HashMap<Portion, Double>();
        for (Portion key : this.leaveOutWeights.keySet()) {
            this.leaveOutWeights.put(key, 1.0);
        }
        this.replacements = new HashMap<Portion, Pair>();
        this.shoppingList = new ArrayList<Portion>();
    }

    @Override
    public String toString() {
        String output = name + ":\n";

        if (this.size() == 0) {
            output += "No ingredients";
        } else {
            for (Portion p : this) {
                output += p;
                if (replacements.containsKey(p)) {
                    if (replacements.get(p).getPortion() != null) {
                        output += " ---> " + replacements.get(p).getPortion() + ", value:"
                                + replacements.get(p).getValue() + "\n";
                    } else {
                        output += " ---> REMOVED value:" + replacements.get(p).getValue() + "\n";
                    }
                } else {
                    output += ", value:1.0";
                    if (shoppingList.stream().anyMatch(q -> q.getIngredient() == p.getIngredient())) {
                        Optional<Integer> missingAmount = shoppingList.stream()
                                .filter(q -> q.getIngredient() == p.getIngredient()).map(q -> q.getAmount()).findAny();
                        output += " [Shopping List: " + missingAmount.get() + "g]";
                    }
                    output += "\n";
                }
            }
        }

        return output;
    }

    public void addWeightToPortion(Portion portion, Double weight) {
        leaveOutWeights.put(portion, weight);
    }

    public Double getWeightByPortion(Portion portion) {
        return leaveOutWeights.get(portion);
    }

    public void setReplacements(HashMap<Portion, Pair> replacements) {
        this.replacements = replacements;
    }

    public HashMap<Portion, Pair> getReplacements() {
        return replacements;
    }

    public double getLeaveOutWeight(Portion p) {
        return leaveOutWeights.get(p);
    }

    public void replace(Portion i, Pair p) {
        this.replacements.put(i, p);
    }

    public void putOnShoppingList(Portion i) {
        this.shoppingList.add(i);
    }

    public void replacementToShoppingList(Portion i) {
        this.replacements.remove(i);
        this.shoppingList.add(i);
    }

    public ArrayList<Portion> getShoppingList() {
        return this.shoppingList;
    }

    public Recipe getCopy() {
        Recipe r = new Recipe(name);
        HashMap<Portion,Portion> copies = new HashMap<>();
        stream().forEach(p -> copies.put(p,p.getCopy()));
        leaveOutWeights.keySet().stream()
        .forEach(k -> r.leaveOutWeights.put(copies.get(k), leaveOutWeights.get(k)));
        copies.values().stream().forEach(x -> r.add(x));
        return r;
    }
}
