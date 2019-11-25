package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.exceptions.InvalidRoomDetailsException;
import model.exceptions.InvalidRoomIDException;
import view.AddStandardPane;

public class AddStandardRoomHandler implements EventHandler<ActionEvent> {

	private Stage primaryStage;
	private Scene previousScene;
	private CityLodge lodge;
	private CityLodgeDatabase CLDB;


	public AddStandardRoomHandler(Stage primaryStage, Scene previousScene, CityLodge lodge, CityLodgeDatabase CLDB) {
		this.primaryStage = primaryStage;
		this.previousScene = previousScene;
		this.lodge = lodge;
		this.CLDB = CLDB;
	}

	@Override
	public void handle(ActionEvent event) {
		AddStandardPane addStandardPane = new AddStandardPane(primaryStage, previousScene, lodge, CLDB);

		Scene addStandardScene;
		try {
			addStandardScene = new Scene(addStandardPane.getPane(), 1024, 768);
			primaryStage.setScene(addStandardScene);
			primaryStage.show();
		} catch (InvalidRoomDetailsException | InvalidRoomIDException e) {
			e.printStackTrace();
		}
		
	}
	
}

