package infoia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;


public class CookingAgent {
	public static void main(String[] args) {
		new CookingAgent();
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
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology;
        try {
            String location = "file://" + System.getProperty("user.dir") + "/ontologies/IngredientsOntology.owl";
            System.out.println(location);
            ontology = manager.loadOntology(IRI.create(location));
            ontology.saveOntology(new FunctionalSyntaxDocumentFormat(), System.out);
        } catch (Exception e) {
            e.printStackTrace();
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
