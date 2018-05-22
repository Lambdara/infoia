package infoia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
	
	String uriPrefix = "http://www.semanticweb.org/jordi/ontologies/2018/4/Pasta#";

	CookingAgent () {
		fridge = new ArrayList<Ingredient>();
		recipeBook = new ArrayList<Recipe>();
		ingredients = new ArrayList<Ingredient>();
		
		manager = OWLManager.createOWLOntologyManager();
        
        try {
            String location = fixSeperators("file:///" + System.getProperty("user.dir") +"/ontologies/PastaOntologyRDF.owl");
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
		System.out.println(ingredientSimilarity(new Ingredient("Salmon"), new Ingredient("OliveOil")));
	}
	
	private double ingredientSimilarityAssymetric(Ingredient i, Ingredient j) {
	    OWLClass c1 = dataFactory.getOWLClass(uriPrefix + i.getName());
        OWLClass c2 = dataFactory.getOWLClass(uriPrefix + j.getName());
        OWLClass thing = dataFactory.getOWLClass("owl:Thing");
        OWLClass cur = c1;
        
        int stepsFromStart = 0;
        int stepsToEnd = 0;
        
        while (!reasoner.subClasses(cur).anyMatch(x -> x == c2) && cur != c2) {
            stepsFromStart++; 
            cur = reasoner.superClasses(cur,true).filter(x -> x != thing).findAny().get();
            System.out.println("Class is now " + cur.toString());
        }
        
        while(!reasoner.superClasses(cur).allMatch(x -> x == thing)) {
            stepsToEnd++;
            cur = reasoner.superClasses(cur,true).filter(x -> x != thing).findAny().get();
            System.out.println("Class is now " + cur.toString());
        }
        
        System.out.println("From start: " + stepsFromStart + "; To end: " + stepsToEnd);
        
        return (double) Math.pow(stepsToEnd,2) / (stepsFromStart + Math.pow(stepsToEnd,2));
	}
	
	private double ingredientSimilarity(Ingredient i, Ingredient j) {
	    
	    return (ingredientSimilarityAssymetric(i, j) + ingredientSimilarityAssymetric(j, i))/2;
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
}
