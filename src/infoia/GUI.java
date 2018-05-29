package infoia;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
	Text guiBestRecipeName;
	Scene guiScene;
	Stage guiStage;
	BorderPane bPane;
	
	
	@Override
	public void start(Stage stage) throws Exception {
		guiStage = stage;
		bPane = new BorderPane();
		guiScene = new Scene(bPane, 800, 600);
	    cookingAgent = new CookingAgent(); 
	    guiFridge = new ListView<String>();
	    guiFridge.setPrefSize(200, 500);
	    guiBestRecipe = new ListView<String>();
	    guiBestRecipe.setPrefSize(300, 300);
	    guiBestRecipeName = boldText("");
	    guiShoppingList = new ListView<String>();
	    guiShoppingList.setPrefSize(300, 200);
		
	    updateGUIFridge(guiFridge);	
	    
		setupLayout();
		guiStage.show();
	}
	
	private void setupLayout() {
		Insets padding = new Insets(10);
		// Left side of UI
	    VBox leftVBox = new VBox();
	    leftVBox.setPadding(padding);
	    leftVBox.setSpacing(5.0);
	    HBox fridgeHBox = new HBox();
	    fridgeHBox.setSpacing(5.0);
	    
	    Button randomFridgeButton = new Button("Random");
	    randomFridgeButton.setOnAction(value ->  {
	    	ArrayList<String> inFridge = new ArrayList<String>();
			inFridge.add("OliveOil");
			cookingAgent.addIngredientsToFridge(inFridge);
			updateGUIFridge(guiFridge);	
	        });
	    Button clearFridgeButton = new Button("Clear");
	    fridgeHBox.getChildren().add(randomFridgeButton);	
	    fridgeHBox.getChildren().add(clearFridgeButton);
	    
	    leftVBox.getChildren().add(boldText("Fridge"));
	    leftVBox.getChildren().add(fridgeHBox);
	    leftVBox.getChildren().add(guiFridge);

	    // Center UI
	    VBox centerVBox = new VBox();
	    centerVBox.setPadding(padding);
	    centerVBox.setSpacing(5.0);
	    Button findRecipeButton = new Button("Find Recipe");
	    findRecipeButton.setOnAction(value ->  {
	    	updateGUIBestRecipe(guiBestRecipe, guiShoppingList);
	        });
	    centerVBox.getChildren().add(findRecipeButton);
	    
	    // Right side of GUI
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
		
//		bPane.setTop(); 
//	    bPane.setBottom(new TextField("Bottom")); 
	    bPane.setLeft(leftVBox);
	    bPane.setRight(rightVBox); 
	    bPane.setCenter(centerVBox);
		
		// Setup GUI stage and show
		guiStage.setTitle("INFOIA: Cooking Agent");
		guiStage.setScene(guiScene);
		guiScene.setFill(Color.LIGHTGREY);
	}
	
	private Text boldText(String string) {
		Text text = new Text(string);
		text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		
		return text;
	}
	
	private void updateGUIFridge(ListView<String> fridge) {
		ObservableList<String> ingredients = FXCollections.observableArrayList();
		
		if (cookingAgent.fridge.size() == 0) {
			ingredients.add("empty");
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
		Recipe recipe = cookingAgent.getBestRecipe();
		guiBestRecipeName.setText(recipe.name);
		System.out.println(guiBestRecipeName);
		
		if (recipe.size() == 0) {
			ingredients.add("<empty>");
		} else {
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
		bestRecipe.setItems(ingredients);
		shoppingList.setItems(missingIngredients);
	}
	
}
