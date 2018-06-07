package infoia;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.event.ChangeEvent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GUI extends Application {
	CookingAgent cookingAgent;
	ListView<String> guiFridge;
	ListView<String> guiBestRecipe;
	ListView<String> guiShoppingList;
	ObservableList<String> guiAllIngredients;
	Text guiBestRecipeName;
	Scene guiScene;
	Stage guiStage;
	BorderPane bPane;
	
	
	@Override
	public void start(Stage stage) throws Exception {
		guiStage = stage;
		bPane = new BorderPane();
		guiScene = new Scene(bPane, 1000, 600);
	    cookingAgent = new CookingAgent(); 
	    guiFridge = new ListView<String>();
	    guiFridge.setPrefSize(200, 500);
	    guiBestRecipe = new ListView<String>();
	    guiBestRecipe.setPrefSize(400, 350);
	    guiBestRecipeName = boldText("");
	    guiShoppingList = new ListView<String>();
	    guiShoppingList.setPrefSize(400, 150);
	    guiAllIngredients = FXCollections.observableArrayList();

	    for (Ingredient i : cookingAgent.ingredients) {
	    	guiAllIngredients.add(i.getName());
	    }
		
	    updateGUIFridge(guiFridge);	
	    
		setupLayout();
		guiStage.show();
	}
	
	private void setupLayout() {
//		bPane.setTop(); 
//	    bPane.setBottom(new TextField("Bottom")); 
	    bPane.setLeft(layoutLeft());
	    bPane.setRight(layoutRight()); 
	    bPane.setCenter(layoutCenter());
		
		// Setup GUI stage and show
		guiStage.setTitle("INFOIA: Cooking Agent");
		guiStage.setScene(guiScene);
		guiScene.setFill(Color.LIGHTGREY);
	}
	
	private Node layoutLeft() {
		Insets padding = new Insets(10);
		VBox leftVBox = new VBox();
	    leftVBox.setPadding(padding);
	    leftVBox.setSpacing(5.0);
	    
	    // Fridge buttons section
	    HBox fridgeHBox = new HBox();
	    fridgeHBox.setSpacing(5.0);
	    Button randomFridgeButton = new Button("Random");
	    randomFridgeButton.setOnAction(value ->  {
	    	cookingAgent.fillFridgeRandomly();
			updateGUIFridge(guiFridge);	
	        });
	    Button clearFridgeButton = new Button("Clear");
	    clearFridgeButton.setOnAction(value ->  {
	    	cookingAgent.clearFridge();
	    	updateGUIFridge(guiFridge);
	        });
	    fridgeHBox.getChildren().add(randomFridgeButton);	
	    fridgeHBox.getChildren().add(clearFridgeButton);
	    
	    // Fridge add ingredient fields
	    HBox addIngredientHBox = new HBox();
	    addIngredientHBox.setSpacing(5.0);
	    ComboBox<String> ingredientBox = new ComboBox<String>();
	    ingredientBox.setItems(guiAllIngredients);
	    ingredientBox.setEditable(true);
	    ingredientBox.setPromptText("Enter ingredient name...");
	    ingredientBox.getEditor().focusedProperty().addListener(observable -> {
            if (ingredientBox.getSelectionModel().getSelectedIndex() < 0) {
            	ingredientBox.getEditor().setText(null);
            }
        });
	    ingredientBox.addEventHandler(KeyEvent.KEY_PRESSED, t -> ingredientBox.hide());
	    ingredientBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String oldVal, String newVal) {
				handleSearchByKey(oldVal, newVal, guiAllIngredients, ingredientBox);
				System.out.println("It's continuing mission,..."); 
			}
	    });
	    
	    Button addIngredientButton = new Button("Add");
	    addIngredientButton.setOnAction(value ->  {
	    	System.out.println(ingredientBox.getValue());
	    	cookingAgent.addIngredientToFridge(ingredientBox.getValue());
	    	System.out.println("--- I wanna be the very best..."); 
			updateGUIFridge(guiFridge);
			System.out.println("--- like no one ever was.");
			ingredientBox.valueProperty().setValue("");		
			System.out.println("--- Teach Pokemon to understand..."); 
	        });
	    addIngredientHBox.getChildren().add(ingredientBox);	
	    addIngredientHBox.getChildren().add(addIngredientButton);
	    
	    leftVBox.getChildren().add(boldText("Fridge"));
	    leftVBox.getChildren().add(fridgeHBox);
	    leftVBox.getChildren().add(addIngredientHBox);
	    leftVBox.getChildren().add(guiFridge);
	    
	    return leftVBox;
	}

	private Node layoutRight() {
		Insets padding = new Insets(10);
	    VBox rightVBox = new VBox();
	    rightVBox.setPadding(padding);
	    rightVBox.setSpacing(5.0);
	    HBox recipeHBox = new HBox();
	    recipeHBox.getChildren().add(boldText("Best recipe: "));
	    recipeHBox.getChildren().add(guiBestRecipeName);
	    rightVBox.getChildren().add(recipeHBox);
	    rightVBox.getChildren().add(guiBestRecipe);
	    rightVBox.getChildren().add(boldText("Shopping list"));
	    rightVBox.getChildren().add(guiShoppingList);
	    
	    return rightVBox;
	}
	
	private Node layoutCenter() {
		Insets padding = new Insets(10);
		VBox centerVBox = new VBox();
	    centerVBox.setPadding(padding);
	    centerVBox.setSpacing(5.0);
	    Button findRecipeButton = new Button("Find Recipe");
	    findRecipeButton.setOnAction(value ->  {
	    	updateGUIBestRecipe(guiBestRecipe, guiShoppingList);
	        });
	    centerVBox.getChildren().add(findRecipeButton);
	    
	    return centerVBox;
	}
	
	private Text boldText(String string) {
		Text text = new Text(string);
		text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		return text;
	}
	
	// Source: http://www.drdobbs.com/jvm/simple-searching-in-java/232700121
	private void handleSearchByKey(String oldVal, String newVal, ObservableList<String> totalList, ComboBox<String> box) {
		if (totalList == null || box == null || newVal == null) {
			System.out.println("Warning: parameter is null in handleSearchByKey()");
			return;
		}
		System.out.println("Space."); 	
		// If the number of characters in the text box is less than last time
	    // it must be because the user pressed delete
	    if (oldVal != null && (newVal.length() < oldVal.length())) {
	        // Restore the lists original set of entries 
	        box.setItems( totalList );
	    }
	    System.out.println("The final frontier."); 
	    // Break out all of the parts of the search text by splitting on white space
	    String[] parts = newVal.toUpperCase().split(" ");
	 
	    // Filter out the entries that don't contain the entered text
	    ObservableList<String> subentries = FXCollections.observableArrayList();
	    for (Object entry : box.getItems()) {
	        boolean match = true;
	        String entryText = (String)entry;
	        for (String part : parts) {
	            // The entry needs to contain all portions of the
	            // search string *but* in any order
	            if (!entryText.toUpperCase().contains(part)) {
	                match = false;
	                break;
	            }
	        }
	 
	        if (match) {
	            subentries.add(entryText);
	        }
	    }
	    System.out.println("These are the voyages..."); 
	    box.setItems(subentries);
	    System.out.println("of the starship Enterprise."); 
	}
	
	private void updateGUIFridge(ListView<String> fridge) {
		ObservableList<String> ingredients = FXCollections.observableArrayList();
		
		if (cookingAgent.fridge == null) {
			System.out.println("Warning: cookingAgent.fridge is null in updateGUIFridge()");
			return;
		}
		
		if (cookingAgent.fridge.size() < 1) {
			ingredients.clear();
		} else {
			for (Ingredient ingredient : cookingAgent.fridge) {
				ingredients.add(ingredient.getName());
			}
		}
		fridge.setItems(ingredients);
	}
	
	private void updateGUIBestRecipe(ListView<String> bestRecipe, ListView<String> shoppingList) {
		ObservableList<String> ingredients = FXCollections.observableArrayList();
		ObservableList<String> missingIngredients = FXCollections.observableArrayList();
		String recipeName = "";
		
		if (!guiFridge.getItems().isEmpty()) {
			Recipe recipe = cookingAgent.getBestRecipe();
			if (recipe != null) {
				recipeName = recipe.name;
				for (Ingredient i : recipe) {
					if (recipe.replacements.containsKey(i)) {
						if(recipe.replacements.get(i).getIngredient() != null) {
							ingredients.add(i.getName() + " REPLACED BY " + recipe.replacements.get(i).getIngredient().getName());
						} else {
							ingredients.add("REMOVED " + i.getName());
						}
					} else {
						if (recipe.shoppingList.contains(i))
							missingIngredients.add(i.getName());
	                    else
	                    	ingredients.add(i.getName());
					}
				}
			}
		}
		guiBestRecipeName.setText(recipeName);
		bestRecipe.setItems(ingredients);
		shoppingList.setItems(missingIngredients);
	}
	
}
