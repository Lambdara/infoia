package infoia;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

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
        this.replacements = new HashMap();
        this.shoppingList = new ArrayList();
    }

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
                    if (shoppingList.contains(p))
                        output += " [Shopping List]";
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

    public double getLeaveOutWeight(Ingredient i) {
        return leaveOutWeights.get(i);
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
}
