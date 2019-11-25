package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.exceptions.InvalidRoomDetailsException;
import view.AddSuitePane;

public class AddSuiteHandler implements EventHandler<ActionEvent>{

	private Stage primaryStage;
	private Scene previousScene;
	private CityLodge lodge;
	private CityLodgeDatabase CLDB;


	public AddSuiteHandler(Stage primaryStage, Scene previousScene, CityLodge lodge, CityLodgeDatabase CLDB) {
		this.primaryStage = primaryStage;
		this.previousScene = previousScene;
		this.lodge = lodge;
		this.CLDB = CLDB;
	}

	@Override
	public void handle(ActionEvent event) {
		AddSuitePane addSuitePane = new AddSuitePane(primaryStage, previousScene, lodge, CLDB);

		Scene addSuiteScene;
		try {
			addSuiteScene = new Scene(addSuitePane.getPane(), 1024, 768);
			primaryStage.setScene(addSuiteScene);
			primaryStage.show();
		} catch (InvalidRoomDetailsException e) {
			e.printStackTrace();
		}
		
	}
	
}
