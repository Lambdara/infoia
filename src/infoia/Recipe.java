package infoia;

import java.util.ArrayList;

public class Recipe extends ArrayList<Portion> {
	private static final long serialVersionUID = 1L;

	String name;

	Recipe(String name) {
		super();
		this.name = name;
	}

	public String toString() {
		String output = name + ":\n";

		if (this.size() == 0) {
			output += "No ingredients";
		} else {
			for (Portion p : this) {
				output += p.amount + " " + p.unit + " " + p.ingredient.getName() + "\n";
			}
		}

		return output;
	}
}
