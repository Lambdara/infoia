package infoia;

import java.util.ArrayList;

public class CookingAgent {
	public static void main(String[] args) {
		Ingredient tomato = new Ingredient("Tomato");
		Ingredient pasta = new Ingredient("Pasta");
		Ingredient garlic = new Ingredient("Garlic");
		Ingredient onion = new Ingredient("Onion");
		Ingredient spanishPepper = new Ingredient("Spanish Pepper");

		Recipe pastaRabiata = new Recipe("Pasta Rabiata");
		pastaRabiata.add(tomato);
		pastaRabiata.add(pasta);
		pastaRabiata.add(garlic);
		pastaRabiata.add(onion);
		pastaRabiata.add(spanishPepper);

		CookingAgent ca = new CookingAgent();
		ca.recipeBook.add(pastaRabiata);
		System.out.println(ca.recipeBook.get(0).toString());
	}

	ArrayList<Ingredient> fridge;
	ArrayList<Recipe> recipeBook;

	CookingAgent () {
		fridge = new ArrayList<Ingredient>();
		recipeBook = new ArrayList<Recipe>();
	}

	private boolean hasIngredients(Recipe recipe) {
		for (Ingredient i : recipe)
			if (!fridge.contains(i))
				return false;
		return true;
	}

	ArrayList<Recipe> getAvailableRecipes() {
		ArrayList<Recipe> result = new ArrayList<Recipe>();
		for (Recipe r : recipeBook)
			if(hasIngredients(r))
				result.add(r);
		return result;
	}
}
