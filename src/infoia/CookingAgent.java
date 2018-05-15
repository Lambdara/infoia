package infoia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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
	}

	ArrayList<Ingredient> fridge;
	ArrayList<Recipe> recipeBook;
	ArrayList<Ingredient> ingredients;

	CookingAgent () {
		fridge = new ArrayList<Ingredient>();
		recipeBook = new ArrayList<Recipe>();
		ingredients = new ArrayList<Ingredient>();

		File folder = new File("recipes/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				System.out.println("Read " + file.getName());
				String fileName = "recipes/" + file.getName();

				try (Scanner scanner = new Scanner(new File(fileName))) {

					Recipe recipe = new Recipe(fileName);

					while (scanner.hasNext()){
						String ingredientName = scanner.nextLine();

						Ingredient ingredient = null;
						for (Ingredient i : ingredients) {
							if(i.getName().equals(ingredientName)) {
								ingredient = i;
							}
						}

						if (ingredient == null) {
							ingredient = new Ingredient(ingredientName);
							ingredients.add(ingredient);
						}

						recipe.add(ingredient);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println(ingredients.size());
		for(Ingredient i : ingredients) {
			System.out.println(i.getName());
		}
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
