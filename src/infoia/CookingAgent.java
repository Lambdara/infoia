package infoia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.InferenceDepth;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class CookingAgent {
	
	public static Random random = new Random();
	public static final Double FLAVOUR_WEIGHT = 1.0;
	public static final Double SIMILARITY_WEIGHT = 5.0;
	public static final Double SIMILARITY_THRESHOLD = 0.7;
	public static final Double[] LABEL_WEIGHTS = {0.5, 0.5}; // {SimilarityWeight, FlavourWeight}	
	
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
				String fileName = folder.getPath() + "/" + file.getName();

				try (Scanner scanner = new Scanner(new File(fileName))) {

					Recipe recipe = new Recipe(fileName);

					while (scanner.hasNext()){
						String ingredientString = scanner.nextLine();
						String[] splittedIngredient = ingredientString.split(";");
						String ingredientName = splittedIngredient[0];
						Double ingredientReplacableWeight = Double.parseDouble(splittedIngredient[1]);

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
						recipe.addWeightToIngredient(ingredient, ingredientReplacableWeight);
					}
					recipeBook.add(recipe);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
		
		addFlavoursToIngredients();
		
		ArrayList<String> inFridge = new ArrayList<String>();
		inFridge.add("ChickenMeat");
		inFridge.add("Cream");
		inFridge.add("Milk");
		inFridge.add("Cannelloni");
		inFridge.add("Champignon");
		inFridge.add("SaltSeasoning");		
		addIngredientsToFridge(inFridge);
		
		
		
		Recipe best = getBestRecipe();
		System.out.println("Fridge: " + fridge);
		System.out.println("Best Recipe: " + best);
		
		
	}
	
	private void addFlavoursToIngredients() {
		for(Ingredient.Flavour flavour : java.util.Arrays.asList(Ingredient.Flavour.values())) {
//			System.out.println(flavour);
			OWLClass query = dataFactory.getOWLClass(uriPrefix + "Get" + flavour.toString());
			ArrayList<String> ins = new ArrayList<String>();
			reasoner.subClasses(query).forEach(x -> ins.add(x.getIRI().getFragment()));
			for(Ingredient i : ingredients) {
				if(ins.contains(i.getName())) {
					i.addFlavour(flavour);
//					System.out.println(i.getName() + " added :" + flavour);
				}
			}
		}
	}
	
	private double flavourSimilarity(Ingredient i, Ingredient j) {
		ArrayList<Ingredient.Flavour> fi = i.getFlavours();
		ArrayList<Ingredient.Flavour> fj = i.getFlavours();
//		fi.addAll(j.getFlavours());
//		long total = fi.stream().distinct().count();
//		long similar = fi.stream().count() - total;
//		double result = (double) similar / total;
		double total = 0.0;
		double similar = 0.0;
		for(Ingredient.Flavour f : fi) {
			if(fj.contains(f)) similar+= 1.0; 
			total+= 1.0;
		}
		for(Ingredient.Flavour f : fj) {
			if(!fi.contains(f)) total+= 1.0;
		}
		if(total == 0) {
			return -1.0;
		}
		return similar / total;
//		return 1.0;
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
        }
        
//        System.out.println("Found!");
        
        while(!reasoner.superClasses(cur).allMatch(x -> x == thing)) {
            stepsToEnd++;
            cur = reasoner.superClasses(cur,true).filter(x -> x != thing).findAny().get();
        }
        
//        System.out.println("From start: " + stepsFromStart + "; To end: " + stepsToEnd);
        
        return (double) Math.pow(stepsToEnd,2) / (Math.pow(stepsFromStart,2) + Math.pow(stepsToEnd,2));
	}
	
	private double ingredientSimilarity(Ingredient i, Ingredient j) {
		double simWeight = SIMILARITY_WEIGHT;
		double flavourWeight = FLAVOUR_WEIGHT;
		
		double flavourSimilarity = flavourSimilarity(i, j);
		double similarity = (ingredientSimilarityAssymetric(i, j) + ingredientSimilarityAssymetric(j, i))/2;
		if(flavourSimilarity == -1.0) flavourWeight = 0.0;
		
	    return ((similarity * simWeight)
	     			+ (flavourSimilarity * flavourWeight))
	    			/ (simWeight + flavourWeight);
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
	
	private Recipe getBestRecipe() {
		double bestUtil = 0.0;
		Recipe bestRecipe = null;
		for(Recipe r : recipeBook) {
			HashMap<Ingredient, Pair> replacements = getReplacements(r);
			Recipe recipe = createRecipe(r, replacements);
			double util = recipeUtility2(recipe);
			if(bestUtil < util) {
				bestUtil = util;
				bestRecipe = recipe;
			}
			System.out.println("Utility: " + util);
			System.out.println("Fridge: " + fridge);
			System.out.println(recipe);
			System.out.println("\n----------------------\n");
		}
		System.out.println("Best Utility: " + bestUtil);
		return bestRecipe;
	}
	
	private HashMap<Ingredient, Pair> getReplacements(Recipe r) {
		ArrayList<Ingredient> available = new ArrayList<Ingredient>();
		ArrayList<Ingredient> unavailable = new ArrayList<Ingredient>();
		
		for(Ingredient i : r) {
			if(fridge.contains(i)) {
				available.add(i);
			} else {
				unavailable.add(i);
			}
		}
		
		HashMap<Ingredient, Pair> replacements = new HashMap<Ingredient, Pair>();
		for(Ingredient i : unavailable) {
			double bestSimilarity = 0.0;
			Ingredient bestIngredient = null;
			for(Ingredient j : fridge) {
				double similarity = ingredientSimilarity(i, j);
				if(bestSimilarity < similarity) {
					bestSimilarity = similarity;
					bestIngredient = j;
				}
			}
			System.out.println("Best alternative ingredient for " + i.getName() + " --> " + bestIngredient.getName() + ", similarity:"+ bestSimilarity);
			if(bestSimilarity > SIMILARITY_THRESHOLD) {
				replacements.put(i, new Pair(bestIngredient, bestSimilarity));
			} else {
				replacements.put(i, new Pair(null, 1.0 - r.getWeightByIngredient(i)));
			}
		}
		return replacements;
	}
	
	private double recipeUtility(Recipe r) {
		HashMap<Ingredient, Pair> replacements = r.getReplacements();
		double utility = 1.0;
		for(Ingredient i : r) {
			if(replacements.containsKey(i)) {
				utility *= replacements.get(i).getValue();
			}
		}
		return utility;
	}
	
	private double recipeUtility2(Recipe r) {
		HashMap<Ingredient, Pair> replacements = r.getReplacements();
		double utility = 0.0;
		for(Ingredient i : r) {
			if(replacements.containsKey(i)) {
				utility += replacements.get(i).getValue();
			} else {
				utility += 1;
			}
		}
		return utility/r.size();
	}
	
	private Recipe createRecipe(Recipe r, HashMap<Ingredient, Pair> replacements) {
		Recipe newRecipe = new Recipe("modified " + r.name);
		for(Ingredient i : r) {
			newRecipe.add(i);
		}
		newRecipe.setReplacements(replacements);
		return newRecipe;
	}
	
	private void addIngredientsToFridge(ArrayList<String> ingredientNames) {
		for(Ingredient i : ingredients) {
			if(ingredientNames.contains(i.getName())) {
				fridge.add(i);
			}
		}
	}
}
