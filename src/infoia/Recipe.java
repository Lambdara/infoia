package infoia;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe extends ArrayList<Ingredient> {
	private static final long serialVersionUID = 1L;

	String name;
	private HashMap<Ingredient, Double> replacableWeights;

	Recipe(String name) {
		super();
		this.name = name;
		this.replacableWeights = new HashMap<Ingredient, Double>();
		for (Ingredient key : this.replacableWeights.keySet()) {
			this.replacableWeights.put(key, 1.0);
		}
	}

	public String toString() {
		String output = name + ":\n";

		if (this.size() == 0) {
			output += "No ingredients";
		} else {
			for (Ingredient i : this) {
				output += i.getName() + "\n";
			}
		}

		return output;
	}

	public void addWeightToIngredient(Ingredient ingredient, Double weight){
		replacableWeights.put(ingredient, weight);
	}

	public Double getWeightByIngredient(Ingredient ingredient){
		return replacableWeights.get(ingredient);
	}
}
