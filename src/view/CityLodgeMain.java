package view;

/* 
 * Advanced Programming Assignment 2
 * @Student   David Manolitsas
 * @studentID s3779380
 */  

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;

public class CityLodgeMain extends Application {

	private final static CityLodge LODGE = new CityLodge();
	private final static CityLodgeDatabase CLDB = new CityLodgeDatabase(LODGE);

	public static void main(String[] args) {
		//Refer to README to find out more about this uncommented code
//		CLDB.runConnection();
//		CLDB.dropAllTables();
//		CLDB.createRoomTable();
//		CLDB.createHiringRecordTable();
//		CLDB.addPresetRooms();
//		CLDB.addPresetRecords();

		//Add Database to HashMaps and Arrays
		CLDB.addStandardRoomsToHashmap();
		CLDB.addSuitesToHashmap();
		CLDB.addHiringRecordsToArray();
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("City Lodge");
		MainScene mScene = new MainScene(primaryStage, LODGE, CLDB); 
		Scene scene = mScene.getScene();
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
