package infoia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;
import java.util.Optional;
import java.util.Scanner;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceDepth;
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
	
	String uriPrefix = "http://www.semanticweb.org/jordi/ontologies/2018/4/pasta#";

	CookingAgent () {
		fridge = new ArrayList<Ingredient>();
		recipeBook = new ArrayList<Recipe>();
		ingredients = new ArrayList<Ingredient>();
		
		manager = OWLManager.createOWLOntologyManager();
        
        try {
            String location = fixSeperators("file:///" + System.getProperty("user.dir") +"/ontologies/PastaOntology.owl");
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
						}

						recipe.add(ingredient);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		System.out.println(similarity(new Ingredient("Steak"), new Ingredient("Pepper")));
		
		
		for(Recipe r : recipeBook) {
			
		}
	}
	
	private double similarity(Ingredient i, Ingredient j) {
	    OWLClass c1 = dataFactory.getOWLClass(uriPrefix + i.getName());
	    OWLClass c2 = dataFactory.getOWLClass(uriPrefix + j.getName());
        OWLClass thing = dataFactory.getOWLClass("owl:Thing");
	    
	    int steps = 0;
	    
	    while (true) {
    	    if (reasoner.subClasses(c1).anyMatch(x -> x == c2)) {
    	        return Math.pow(0.75,steps);
    	    } else {
    	        
    	        steps++; 
    	        c1 = reasoner.superClasses(c1,true).filter(x -> x != thing).findAny().get();
    	        System.out.println("Class is now " + c1.toString());
    	    }
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
	
	private String fixSeperators(String path) {
		return path.replace("\\", "/");
	}
	
	private HashMap<Ingredient, Pair<Ingredient, Double>> getReplacements(Recipe r) {
		ArrayList<Ingredient> available = new ArrayList<Ingredient>();
		ArrayList<Ingredient> unavailable = new ArrayList<Ingredient>();
		
		for(Ingredient i : r) {
			if(fridge.contains(i)) {
				available.add(i);
			} else {
				unavailable.add(i);
			}
		}
		
		HashMap<Ingredient, Pair<Ingredient, Double>> similarIngredients = new HashMap<Ingredient, Pair<Ingredient, Double>>();
		for(Ingredient i : unavailable) {
			double bestSimilarity = 0.0;
			Ingredient bestIngredient = null;
			for(Ingredient j : fridge) {
				double similarity = similarity(i, j);
				if(bestSimilarity < similarity) {
					bestSimilarity = similarity;
					bestIngredient = j;
				}
			}
			similarIngredients.put(i, new Pair<Ingredient, Double>(bestIngredient, bestSimilarity));
		}
		return similarIngredients;
	}
	
	private double utility(Recipe r, HashMap<Ingredient, Pair<Ingredient, Double>> replacements) {
		double utility = 0.0;
		for(Ingredient i : r) {
			if(replacements.containsKey(i)) {
				utility += replacements.get(i).getValue();
			} else {
				utility += 1.0;
			}
		}
		return utility / r.size();
	}
}
