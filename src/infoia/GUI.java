package infoia;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GUI extends Application {
	CookingAgent cookingAgent;
	ListView<String> guiFridge;
	ListView<String> guiBestRecipe;
	String guiBestRecipeName;
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
	    guiBestRecipe.setPrefSize(300, 500);
	    guiBestRecipeName = new String();
	    
		// Update GUI    
		updateGUIFridge(guiFridge);	
		updateGUIBestRecipe(guiBestRecipe);
		
		setupLayout();
		guiStage.show();
	}
	
	private void setupLayout() {
		Insets padding = new Insets(10);
		// Left side of UI
	    VBox leftVBox = new VBox();
	    leftVBox.setPadding(padding);
	    leftVBox.getChildren().add(boldText("Fridge"));
	    leftVBox.getChildren().add(guiFridge);
	    
	    // Right side of GUI
	    VBox rightVBox = new VBox();
	    rightVBox.setPadding(padding);
	    rightVBox.getChildren().add(boldText("Best Recipe: " + guiBestRecipeName));
	    rightVBox.getChildren().add(guiBestRecipe);
		
		bPane.setTop(new TextField("Top")); 
	    bPane.setBottom(new TextField("Bottom")); 
	    bPane.setLeft(leftVBox);
	    bPane.setRight(rightVBox); 
	    bPane.setCenter(new TextField("Center")); 
		
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
		
		for (Ingredient ingredient : cookingAgent.fridge) {
			ingredients.add(ingredient.getName());
		}
		fridge.setItems(ingredients);
	}
	
	private void updateGUIBestRecipe(ListView<String> bestRecipe) {
		ObservableList<String> ingredients = FXCollections.observableArrayList();
		Recipe recipe = cookingAgent.getBestRecipe();
		guiBestRecipeName = recipe.name;
		
		if (recipe.size() == 0) {
			ingredients.add("<empty>");
		} else {
			for (Ingredient i : recipe) {
				if (recipe.replacements.containsKey(i)) {
					if(recipe.replacements.get(i).getIngredient() != null) {
						ingredients.add(i.getName() + " -> " + recipe.replacements.get(i).getIngredient().getName());
					} else {
						ingredients.add("REMOVED " + i.getName());
					}
				} else {
					ingredients.add(i.getName());
				}
			}
		}
		
		bestRecipe.setItems(ingredients);
	}
	
	
	private Text addText() {
		Text text = new Text();      
	      
		//Setting the text to be added. 
		text.setText("Hello how are you"); 
		   
		//setting the position of the text 
		text.setX(50); 
		text.setY(50);
		
		return text;
	}
//	
//	public static void main(String[] args) {
//		launch(args);
//		CookingAgent cookingAgent = new CookingAgent();
//	}
}
