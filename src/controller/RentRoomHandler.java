package controller;

import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.HotelRoom;
import view.RentRoomPane;

public class RentRoomHandler implements EventHandler<ActionEvent> {

	private Stage primaryStage;
	private Scene previousScene;
	private CityLodge lodge;
	private CityLodgeDatabase CLDB;
	private HotelRoom room;

	public RentRoomHandler(Stage primaryStage, Scene previousScene, CityLodge lodge, CityLodgeDatabase CLDB, HotelRoom room) {
		this.primaryStage = primaryStage;
		this.previousScene = previousScene;
		this.lodge = lodge;
		this.CLDB = CLDB;
		this.room = room;
	}

	@Override
	public void handle(ActionEvent event) {
		RentRoomPane rentRoomPane = new RentRoomPane(primaryStage, previousScene, lodge, CLDB, room);
		
		Scene rentRoomScene;
		try {
			rentRoomScene = new Scene(rentRoomPane.getPane(), 1024, 768);
			primaryStage.setScene(rentRoomScene);
			primaryStage.show();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
