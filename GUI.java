import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application{
	
	private final int NUM_HORSES = 5;
	private HorseRace race;
	
	@Override
	public void start(Stage stage) throws Exception {
		
		VBox root = new VBox();
		GridPane horseTrack =  new GridPane();
		HBox buttonRow = new HBox(20);
		
		race = new HorseRace();
		
				
		horseTrack.setPadding(new Insets(20, 0, 30, 10));
	    horseTrack.setHgap(10);
	    horseTrack.setVgap(35);						
	    
		//Buttons
		Button run = createRun();
		Button reset = createReset();
		Button quit = createQuit();
			
		buttonRow.getChildren().addAll(run, reset, quit);	
		root.getChildren().addAll(horseTrack, buttonRow);

		Canvas[] horses = race.getCanvasArr();
		for(int i = 0; i < NUM_HORSES; i++) {
			
			horseTrack.add(horses[i], 0, i);
		}
		
		stage.setScene(new Scene(root, 1000, 600));
		stage.show();		
	}
	
	private Button createRun() {
		Button btn = new Button("Run race");
		
		btn.setOnAction(new EventHandler<ActionEvent>() {

		    @Override
		    public void handle(ActionEvent actionEvent) {
		    	
		    	race.startRace();
		    }
		});
		return btn;
	}
	private Button createReset() {
		Button btn = new Button("Reset race");
		btn.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent actionEvent) {
		        
		    	race.resetRace();
		    }
		});
		return btn;
	}
	private Button createQuit() {
		Button btn = new Button("Quit race");
		btn.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent actionEvent) {
		        race.handleQuit();
		    	System.exit(0);
		    }
		});
		return btn;
	} 
}
