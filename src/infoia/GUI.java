package infoia;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GUI extends Application {
	CookingAgent cookingAgent;
	ListView<String> guiFridge;
	ListView<String> guiBestRecipe;
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
	    guiBestRecipe.setPrefSize(200, 500);
	    
		// Update GUI    
		updateGUIFridge(guiFridge);	
		
		bPane.setTop(new TextField("Top")); 
	    bPane.setBottom(new TextField("Bottom")); 
	    bPane.setLeft(guiFridge); 
	    bPane.setRight(guiBestRecipe); 
	    bPane.setCenter(new TextField("Center")); 
		
		// Setup GUI stage and show
		guiStage.setTitle("My First JavaFX App");
		guiStage.setScene(guiScene);
		guiScene.setFill(Color.LIGHTGREY);
		guiStage.show();
	}
	
	private void updateGUIFridge(ListView<String> fridge) {
		ObservableList<String> ingredients = FXCollections.observableArrayList ();
		
		for (Ingredient ingredient : cookingAgent.fridge) {
			ingredients.add(ingredient.getName());
		}
		fridge.setItems(ingredients);
	}
	
	private void updateGUIBestRecipe(ListView<String> bestRecipe) {
		ObservableList<String> ingredients = FXCollections.observableArrayList ();
		
		for (Ingredient ingredient : cookingAgent.fridge) {
			ingredients.add(ingredient.getName());
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
