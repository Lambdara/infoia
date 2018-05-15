package infoia;

public class Portion {
	Ingredient ingredient;
	String unit;
	double amount;
	
	Portion (Ingredient ingredient, double amount, String unit) {
		this.ingredient = ingredient;
		this.amount = amount;
		this.unit = unit;
	}
}
