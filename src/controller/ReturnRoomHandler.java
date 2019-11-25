package controller;

import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.HotelRoom;
import view.ReturnRoomPane;

public class ReturnRoomHandler implements EventHandler<ActionEvent> {

	private Stage primaryStage;
	private Scene previousScene;
	private CityLodge lodge;
	private CityLodgeDatabase CLDB;
	private HotelRoom room;

	public ReturnRoomHandler(Stage primaryStage, Scene previousScene, CityLodge lodge, CityLodgeDatabase CLDB, HotelRoom room) {
		this.primaryStage = primaryStage;
		this.previousScene = previousScene;
		this.lodge = lodge;
		this.CLDB = CLDB;
		this.room = room;
	}

	@Override
	public void handle(ActionEvent event) {
		ReturnRoomPane returnRoomPane = new ReturnRoomPane(primaryStage, previousScene, lodge, CLDB, room);
		
		Scene returnRoomScene;
		try {
			returnRoomScene = new Scene(returnRoomPane.getPane(), 1024, 768);
			primaryStage.setScene(returnRoomScene);
			primaryStage.show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
