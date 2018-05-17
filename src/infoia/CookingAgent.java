package infoia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class CookingAgent {
	public static void main(String[] args) {
		new CookingAgent();
	}

	ArrayList<Ingredient> fridge;
	ArrayList<Recipe> recipeBook;
	ArrayList<Ingredient> ingredients;
	OWLOntology ontology;
	OWLReasoner reasoner;
	OWLOntologyManager manager;
	OWLDataFactory dataFactory;

	CookingAgent () {
		fridge = new ArrayList<Ingredient>();
		recipeBook = new ArrayList<Recipe>();
		ingredients = new ArrayList<Ingredient>();
		
		manager = OWLManager.createOWLOntologyManager();
        
        try {
            String location = "file://" + System.getProperty("user.dir") + "/ontologies/PastaOntology.owl";
            ontology = manager.loadOntology(IRI.create(location));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        OWLReasonerFactory rf = new ReasonerFactory();
        reasoner = rf.createReasoner(ontology);
        dataFactory = manager.getOWLDataFactory();
		
		File folder = new File("pasta_recipes/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
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
//						}

						recipe.add(ingredient);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		reasoner.getSubClasses(dataFactory.getOWLClass("http://www.semanticweb.org/jordi/ontologies/2018/4/untitled-ontology-6#Cheese"), false).forEach(System.out::println);;
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
