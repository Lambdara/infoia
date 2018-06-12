package infoia;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javafx.application.Application;

public class CookingAgent {

    public static Random random = new Random();
    public static final Double FLAVOUR_WEIGHT = 1.0;
    public static final Double SIMILARITY_WEIGHT = 5.0;
    public static final Double STRUCTURE_WEIGHT = 3.0;

    public static final Double SIMILARITY_THRESHOLD = 0.7;
    public static final Double[] LABEL_WEIGHTS = { 0.5, 0.5 }; // {SimilarityWeight, FlavourWeight}
    public static final Double RECIPE_UTILITY_TRESHOLD = 0.90;

    public static void main(String[] args) {

        // new CookingAgent();
        Application.launch(GUI.class, args);
    }

    ArrayList<Portion> fridge;
    ArrayList<Recipe> recipes;
    ArrayList<Ingredient> ingredients;
    OWLOntology ontology;
    OWLReasoner reasoner;
    OWLOntologyManager manager;
    OWLDataFactory dataFactory;

    String uriPrefix = "http://www.semanticweb.org/jordi/ontologies/2018/4/Pasta#";

    CookingAgent() {
        fridge = new ArrayList<Portion>();
        recipes = new ArrayList<Recipe>();
        ingredients = new ArrayList<Ingredient>();

        manager = OWLManager.createOWLOntologyManager();

        try {
            String location = fixSeperators(
                    "file:///" + System.getProperty("user.dir") + "/ontologies/PastaOntologyRDF.owl");
            ontology = manager.loadOntology(IRI.create(location));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OWLReasonerFactory rf = new ReasonerFactory();
        reasoner = rf.createReasoner(ontology);
        dataFactory = manager.getOWLDataFactory();

        createIngredientsFromOntology();

        File folder = new File("pasta_recipes/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = folder.getPath() + "/" + file.getName();

                try (Scanner scanner = new Scanner(new File(fileName))) {

                    Recipe recipe = new Recipe(pathToName(fileName));

                    while (scanner.hasNext()) {
                        String ingredientString = scanner.nextLine();
                        String[] splittedIngredient = ingredientString.split(";");
                        Integer ingredientAmount = Integer.parseInt(splittedIngredient[0]);
                        String ingredientName = splittedIngredient[1];
                        Double ingredientReplacableWeight = Double.parseDouble(splittedIngredient[2]);

                        Ingredient ingredient = null;
                        for (Ingredient i : ingredients) {
                            if (i.getName().equals(ingredientName)) {
                                ingredient = i;
                            }
                        }

                        if (ingredient == null) {
                            System.err.println(ingredientName + " is not in the ontology!");
                        }

                        // TODO Get amount from recipe file instead
                        Portion p = new Portion(ingredient, ingredientAmount);
                        recipe.add(p);
                        recipe.addWeightToPortion(p, ingredientReplacableWeight);
                    }
                    recipes.add(recipe);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);

        HashMap<String, Integer> inFridge = new HashMap<String, Integer>();
        inFridge.put("SpanishPepper", 50);
        inFridge.put("Tomato", 200);
        inFridge.put("Penne", 500);
        inFridge.put("Shallot", 54);
        inFridge.put("Brocolli", 200);

        addFlavoursToIngredients();
        addStructureToIngredients();
        addIngredientsToFridge(inFridge);
    }

    private void addIngredientsToFridge(HashMap<String, Integer> ingredientsMap) {
        for (Map.Entry<Ingredient, Integer> pair : convertToIngredients(ingredientsMap).entrySet()) {
            fridge.add(new Portion(pair.getKey(), pair.getValue()));
        }
    }

    private HashMap<Ingredient, Integer> convertToIngredients(HashMap<String, Integer> ingredientsMap) {
        HashMap<Ingredient, Integer> resultMap = new HashMap<Ingredient, Integer>();
        for (Ingredient i : ingredients) {
            if (ingredientsMap.keySet().contains(i.getName())) {
                resultMap.put(i, ingredientsMap.get(i.getName()));
            }
        }
        return resultMap;
    }

    private void addFlavoursToIngredients() {
        for (Ingredient.Flavour flavour : java.util.Arrays.asList(Ingredient.Flavour.values())) {
            OWLClass query = dataFactory.getOWLClass(uriPrefix + "Get" + flavour.toString());
            ArrayList<String> ins = new ArrayList<String>();
            reasoner.subClasses(query).forEach(x -> ins.add(x.getIRI().getFragment()));
            for (Ingredient i : ingredients) {
                if (ins.contains(i.getName())) {
                    i.addFlavour(flavour);
                }
            }
        }
    }

    private void addStructureToIngredients() {
        for (Ingredient.Structure structure : java.util.Arrays.asList(Ingredient.Structure.values())) {
            OWLClass query = dataFactory.getOWLClass(uriPrefix + "Get" + structure.toString());

            ArrayList<String> ins = new ArrayList<String>();
            reasoner.subClasses(query).forEach(x -> ins.add(x.getIRI().getFragment()));
            for (Ingredient i : ingredients) {
                if (ins.contains(i.getName())) {
                    i.setStructure(structure);
                }
            }
        }
    }

    private double flavourSimilarity(Ingredient i, Ingredient j) {
        ArrayList<Ingredient.Flavour> fi = i.getFlavours();
        ArrayList<Ingredient.Flavour> fj = i.getFlavours();

        double total = 0.0;
        double similar = 0.0;
        for (Ingredient.Flavour f : fi) {
            if (fj.contains(f)) {
                similar += 1.0;
            }
            total += 1.0;
        }
        for (Ingredient.Flavour f : fj) {
            if (!fi.contains(f)) {
                total += 1.0;
            }
        }
        if (total == 0) {
            return -1.0;
        }
        return similar / total;
    }

    private double structureSimilarity(Ingredient i, Ingredient j) {
        if (i.getStructure() == null && j.getStructure() == null) {
            return -1.0;
        }
        if (i.getStructure() == j.getStructure()) {
            return 1.0;
        }
        return 0.0;
    }

    private void createIngredientsFromOntology() {
        for (OWLClass cls : ontology.getClassesInSignature()) {
            String ingredientName = cls.getIRI().getFragment();
            if (isLeaf(cls)) {
                Ingredient ingredient = new Ingredient(ingredientName);
                ingredients.add(ingredient);
            }
        }
    }

    private boolean isLeaf(OWLClass cls) {
        if (reasoner.subClasses(cls).count() > 1) {
            return false;
        }

        OWLClass ingredients = dataFactory.getOWLClass(uriPrefix + "Ingredients");
        return reasoner.superClasses(cls).anyMatch(x -> x == ingredients);
    }

    String pathToName(String path) {
        String[] splitPath = path.split("/");
        splitPath = splitPath[splitPath.length - 1].split(".txt");
        return splitPath[0];
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
            cur = reasoner.superClasses(cur, true).filter(x -> x != thing).findAny().get();
        }

        while (!reasoner.superClasses(cur).allMatch(x -> x == thing)) {
            stepsToEnd++;
            cur = reasoner.superClasses(cur, true).filter(x -> x != thing).findAny().get();
        }

        return (double) Math.pow(stepsToEnd, 2) / (Math.pow(stepsFromStart, 2) + Math.pow(stepsToEnd, 2));
    }

    private double ingredientSimilarity(Ingredient i, Ingredient j) {
        double simWeight = SIMILARITY_WEIGHT;
        double flavourWeight = FLAVOUR_WEIGHT;
        double structureWeight = STRUCTURE_WEIGHT;

        double flavourSimilarity = flavourSimilarity(i, j);
        double similarity = (ingredientSimilarityAssymetric(i, j) + ingredientSimilarityAssymetric(j, i)) / 2;

        double structureSimilarity = structureSimilarity(i, j);

        if (flavourSimilarity == -1.0) {
            flavourWeight = 0.0;
        }
        if (structureSimilarity == -1.0) {
            structureWeight = 0.0;
        }

        return ((similarity * simWeight) + (flavourSimilarity * flavourWeight)
                + (structureSimilarity * structureWeight)) / (simWeight + flavourWeight + structureWeight);
    }

    private boolean hasIngredients(Recipe recipe) {
        for (Portion i : recipe) {
            if (!fridge.contains(i)) {
                return false;
            }
        }
        return true;
    }

    ArrayList<Recipe> getAvailableRecipes() {
        ArrayList<Recipe> result = new ArrayList<Recipe>();
        for (Recipe r : recipes) {
            if (hasIngredients(r)) {
                result.add(r);
            }
        }
        return result;
    }

    private String fixSeperators(String path) {
        return path.replace("\\", "/");
    }

    public Recipe getBestRecipe(int users) {
        if (users < 1)
            throw new RuntimeException("Can't do users < 1");
        double bestUtil = 0.0;
        int smallestShoppingList = Integer.MAX_VALUE;
        Recipe bestRecipe = null;
        ArrayList<Recipe> localRecipes = new ArrayList<Recipe>();
        recipes.forEach(x -> localRecipes.add(x.getCopy()));
        localRecipes.stream()
        .forEach(recipe -> recipe.stream()
                .forEach(p -> p.setAmount(p.getAmount() * users)));

        for (Recipe r : localRecipes) {
            // Return recipe if completely available
            if (r.stream().mapToInt(i -> fridge.contains(i) ? 0 : 1).sum() == 0) {
                return r;
            }

            /*
             * If we get to this point then no recipe is completely available. We shall
             * replace ingredients until we hit the threshold; after that we add top
             * shopping list.
             */

            // First find all optimal replacements (or leave out if that's optimal)
            ArrayList<Portion> available = new ArrayList<Portion>();
            ArrayList<Portion> unavailable = new ArrayList<Portion>();

            for (Portion i : r) {
                if (fridge.stream()
                        .anyMatch(p -> p.getIngredient() == i.getIngredient() && p.getAmount() >= i.getAmount())) {
                    available.add(i);
                } else {
                    unavailable.add(i);
                }
            }

            HashMap<Portion, Pair> replacements = new HashMap<Portion, Pair>();

            // Calculate replacements initially
            for (Portion p : unavailable) {
                double bestSimilarity = 0.0;
                Portion bestPortion = null;
                for (Portion q : fridge) {
                    if (q.getAmount() >= p.getAmount()) {
                        double similarity = ingredientSimilarity(p.getIngredient(), q.getIngredient());
                        if (bestSimilarity < similarity) {
                            bestSimilarity = similarity;
                            bestPortion = q;
                        }
                    }
                }
                System.out.println("Best alternative ingredient for " + p.getIngredient() + " is "
                        + (bestPortion != null ? bestPortion.getIngredient() : "Removed") + ", similarity:"
                        + bestSimilarity);
                if (bestPortion != null && bestSimilarity > SIMILARITY_THRESHOLD) {
                    replacements.put(p,
                            new Pair(new Portion(bestPortion.getIngredient(), p.getAmount()), bestSimilarity));
                } else {
                    replacements.put(p, new Pair(null, 1.0 - r.getWeightByPortion(p)));
                }
            }

            // Now apply these replacements until we hit the threshold
            boolean thresholdNotHit = true;
            while (thresholdNotHit && !replacements.isEmpty()) {
                Portion optimalReplacement = replacements.keySet().stream()
                        .max((x, y) -> (replacements.get(x).getValue() < replacements.get(y).getValue() ? -1
                                : (replacements.get(x).getValue() == replacements.get(y).getValue()
                                        ? (x.getAmount() < y.getAmount() ? -1
                                                : (x.getAmount() == y.getAmount() ? 0 : 1))
                                        : 1)))
                        .get();
                for (Portion p : replacements.keySet()) {
                    if (replacements.get(p).getValue() > replacements.get(optimalReplacement).getValue()) {
                        optimalReplacement = p;
                    }
                }
                if (recipeUtility2WithReplacement(r, replacements.get(optimalReplacement)) >= RECIPE_UTILITY_TRESHOLD) {
                    r.replace(optimalReplacement, replacements.get(optimalReplacement));
                    replacements.remove(optimalReplacement);
                    unavailable.remove(optimalReplacement);
                } else {
                    thresholdNotHit = false;
                }

                // Recalculate replacements
                for (Portion p : unavailable) {
                    double bestSimilarity = 0.0;
                    Portion bestPortion = null;
                    for (Portion q : fridge) {
                        int totalInRecipe = r.stream().filter(x -> x != q)
                                .filter(x -> r.getReplacements().containsKey(x)
                                        || x.getIngredient() == q.getIngredient())
                                .filter(x -> !r.getReplacements().containsKey(x)
                                        || r.getReplacements().get(x).getPortion() == null || r.getReplacements().get(x)
                                                .getPortion().getIngredient() == q.getIngredient())
                                .mapToInt(
                                        x -> (r.getReplacements().containsKey(x)
                                                ? (r.getReplacements().get(x).getPortion() == null ? 0
                                                        : r.getReplacements().get(x).getPortion().getAmount())
                                                : x.getAmount()))
                                .sum();
                        if (q.getAmount() >= p.getAmount() + totalInRecipe) {
                            double similarity = ingredientSimilarity(p.getIngredient(), q.getIngredient());
                            if (bestSimilarity < similarity) {
                                bestSimilarity = similarity;
                                bestPortion = q;
                            }
                        }
                    }
                    if (replacements.get(p).getPortion() != null && (bestPortion == null
                            || (replacements.get(p).getPortion().getIngredient() != bestPortion.getIngredient()))) {
                        System.out.println("Changed best alternative ingredient for " + p.getIngredient() + " to "
                                + (bestPortion != null ? bestPortion.getIngredient() : "Removed") + ", similarity:"
                                + bestSimilarity);
                        if (bestPortion != null && bestSimilarity > SIMILARITY_THRESHOLD) {
                            replacements.put(p,
                                    new Pair(new Portion(bestPortion.getIngredient(), p.getAmount()), bestSimilarity));
                        } else {
                            replacements.put(p, new Pair(null, 1.0 - r.getWeightByPortion(p)));
                        }
                    }
                }
            }

            // If there are still replacements to be made but the threshold won't allow it,
            // then add them to the shopping list
            for (Portion p : replacements.keySet()) {
                Optional<Integer> fridgeAmount = fridge.stream().filter(q -> q.getIngredient() == p.getIngredient())
                        .map(q -> q.getAmount()).findAny();
                r.putOnShoppingList(new Portion(p.getIngredient(), p.getAmount() - fridgeAmount.orElse(0)));
            }

            int shoppingListSize = r.getShoppingList().size();
            double utility = recipeUtility2(r);

            // Now make note of utility and the like
            if (shoppingListSize < smallestShoppingList) {
                bestRecipe = r;
                bestUtil = utility;
                smallestShoppingList = shoppingListSize;
            } else if (r.getShoppingList().size() == smallestShoppingList && recipeUtility2(r) > bestUtil) {
                bestRecipe = r;
                bestUtil = utility;
            }

            System.out.println("Utility: " + utility);
            System.out.println("Fridge: " + fridge);
            System.out.println(r);
            System.out.println("\n----------------------\n");
        }
        System.out.println("Best Utility: " + bestUtil);

        if (bestUtil < RECIPE_UTILITY_TRESHOLD) {
            addShoppingList(bestRecipe, RECIPE_UTILITY_TRESHOLD);
        }

        return bestRecipe;
    }

    private void addShoppingList(Recipe recipe, Double targetUtility) {
        System.out.println("Creating shoppinglist for " + recipe.name);
        double utility;
        while ((utility = recipeUtility2(recipe)) < targetUtility) {
            System.out.println("Utility: " + utility + "/" + targetUtility);
            double worstValue = 1;
            Portion worstPenaltyPortion = null;
            for (Portion p : recipe.getReplacements().keySet()) {
                double value = recipe.getReplacements().get(p).getValue();
                if (value < worstValue) {
                    worstValue = value;
                    worstPenaltyPortion = p;
                }
            }
            recipe.replacementToShoppingList(worstPenaltyPortion);
            System.out.println("Moved " + worstPenaltyPortion + " with value " + worstValue + " to shopping list");
        }
        System.out.println("Utility: " + utility + "/" + targetUtility);
    }

    private double recipeUtility2(Recipe r) {
        HashMap<Portion, Pair> replacements = r.getReplacements();
        double utility = 0.0;
        for (Portion p : r) {
            if (replacements.containsKey(p)) {
                utility += replacements.get(p).getValue();
            } else {
                utility += 1;
            }
        }
        return utility / r.size();
    }

    private double recipeUtility2WithReplacement(Recipe r, Pair replacement) {
        return recipeUtility2(r) * (r.size() - 1) / (r.size()) + replacement.getValue() / r.size();
    }

    void addIngredientToFridge(String ingredientName, int amount) {
        if (ingredientName == null || ingredients == null) {
            System.out.println("Warning: ingredientName or ingredients is null in addIngredientToFridge()");
            return;
        }

        for (Ingredient i : ingredients) {
            if (ingredientName.equals(i.getName())) {
                Optional<Portion> fridgePortion = fridge.stream()
                        .filter(x -> x.getIngredient().getName() == ingredientName).findFirst();
                if (fridgePortion.isPresent()) {
                    fridgePortion.get().add(amount);
                } else {
                    fridge.add(new Portion(i, amount));
                }
            }
        }
    }

    void fillFridgeRandomly() {
        clearFridge();
        for (Ingredient i : ingredients) {
            if (random.nextFloat() < 0.1) {
                fridge.add(new Portion(i, 400 + random.nextInt(200)));
            }
        }
    }

    void clearFridge() {
        fridge.clear();
    }

    public void removeFromFridge(Portion p) {
        Optional<Portion> fridgePortion = fridge.stream().filter(x -> x.getIngredient() == p .getIngredient()).findAny();
        if(fridgePortion.isPresent()) {
            if (fridgePortion.get().getAmount() > p.getAmount())
                fridgePortion.get().add(-p.getAmount());
            else
                fridge.remove(fridgePortion.get());
        }
    }

    public void removeFromFridge(Recipe r) {
        r.stream()
            .filter(x -> !r.getShoppingList().contains(x))
            .map(x -> r.getReplacements().containsKey(x) ? r.getReplacements().get(x).getPortion() : x)
            .filter(x -> x != null)
            .forEach(this::removeFromFridge);
    }
}
