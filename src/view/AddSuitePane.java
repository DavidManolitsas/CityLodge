package view;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.StringTokenizer;

import controller.ExportDataHandler;
import controller.ImportDataHandler;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.DateTime;
import model.HotelRoom;
import model.Suite;
import model.exceptions.CapacityReachedException;
import model.exceptions.InvalidRoomDetailsException;
import model.exceptions.InvalidRoomIDException;

public class AddSuitePane extends BorderPane {

	private Stage primaryStage;
	private Scene previousScene;
	private CityLodge lodge;
	private CityLodgeDatabase CLDB;


	public AddSuitePane(Stage primaryStage, Scene previousScene, CityLodge lodge, CityLodgeDatabase CLDB) {
		this.primaryStage = primaryStage;
		this.previousScene = previousScene;
		this.lodge = lodge;
		this.CLDB = CLDB;
	}


	public BorderPane getPane() throws InvalidRoomDetailsException {

		BorderPane root = new BorderPane();

		BorderPane topRoot = new BorderPane();
		//City Lodge menu bar with drop down menu for import/export data and quit
		MenuBar menuBar = new MenuBar();
		Menu CLMenu = new Menu("City Lodge");
		MenuItem importMenu = new MenuItem("Import Data");
		MenuItem exportMenu = new MenuItem("Export Data");
		MenuItem quitMenu = new MenuItem("Quit City Lodge");
		CLMenu.getItems().addAll(importMenu, exportMenu, quitMenu);
		menuBar.getMenus().add(CLMenu);
		topRoot.setTop(menuBar);

		ImportDataHandler IDH = new ImportDataHandler(primaryStage, lodge, CLDB);
		importMenu.setOnAction(IDH);

		ExportDataHandler EDH = new ExportDataHandler(primaryStage, lodge);
		exportMenu.setOnAction(EDH);

		//Handlers in the menu bar 
		quitMenu.setOnAction(e -> {
			if (CLDB.isSaved() == true) {
				Platform.exit();
			}
			else {	
				ButtonType save = new ButtonType("Save and Quit", ButtonBar.ButtonData.OK_DONE);
				ButtonType quit = new ButtonType("Quit", ButtonBar.ButtonData.CANCEL_CLOSE);
				Alert a = new Alert(AlertType.CONFIRMATION,"Do you want to save your changes to the database?\nYour changes will be lost if you dont save them",save,quit);
				a.setHeaderText("Database has not been saved");

				Optional<ButtonType> result = a.showAndWait();

				if (result.orElse(quit) == save) {
					CLDB.saveAllRooms(lodge);
					CLDB.saveAllRecords(lodge);
					Platform.exit();
				}
				else {
					Platform.exit();
				}	
			}
		});
		
		BorderPane toolbar = new BorderPane();
		Text logoText = new Text("City Lodge");
		logoText.setFont(Font.font("Impact", 36));
		logoText.setFill(Color.WHITESMOKE);		
		toolbar.setStyle("-fx-background-color: #4682B4;");
		BorderPane.setMargin(logoText, new Insets(0,0,0,15));
		
		Button backBt = new Button("Back");
		BorderPane.setMargin(backBt, new Insets(7,15,5,0));
		toolbar.setLeft(logoText);
		toolbar.setRight(backBt);
		topRoot.setCenter(toolbar);
		root.setTop(topRoot);
		backBt.setOnAction(e -> primaryStage.setScene(previousScene));
		
		
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(15, 15, 15, 15));
		pane.setHgap(5);
		pane.setVgap(10);

		Label roomDetails = new Label("Enter the details of the suite:");
		Label roomIDLabel = new Label("Room ID:");
		TextField roomIDText = new TextField();
		Label featureLabel  = new Label("Feature Summary:");
		TextField featureText = new TextField();
		Label lastMaintenanceLabel = new Label("Last Maintenance Date: ");
		DatePicker maintenanceDatePick = new DatePicker();

		pane.add(roomDetails, 0, 0);
		pane.add(roomIDLabel, 0, 1);
		pane.add(roomIDText, 1, 1);
		pane.add(featureLabel, 0, 2);
		pane.add(featureText, 1, 2);
		pane.add(lastMaintenanceLabel, 0, 3);
		pane.add(maintenanceDatePick, 1, 3);
		Button addBt = new Button("Add Room");
		pane.add(addBt, 1, 4);


		addBt.setOnAction(e -> {
			if(CLDB.isSaved() == true) {
				CLDB.setSaved(false);
			}

			Alert a = new Alert(AlertType.NONE);
			try {

				if(roomIDText.getText() == null || featureText.getText() == null || maintenanceDatePick.getValue() == null) {
					throw new InvalidRoomDetailsException();
				}

				String roomID = roomIDText.getText();
				String features = featureText.getText();
				String date = maintenanceDatePick.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

				String[] split = date.split("/");
				int day = Integer.parseInt(split[0]);					
				int month = Integer.parseInt(split[1]);
				int year = Integer.parseInt(split[2]);
				DateTime lastMaintenanceDate = new DateTime(day, month, year);


				StringTokenizer tokens = new StringTokenizer(features);
				if(lodge.getRoomMap().containsKey(roomID)) {
					throw new InvalidRoomIDException();
				}
				else {
					if (roomID.startsWith("S_") && tokens.countTokens() <= 20) {
						HotelRoom room = new Suite(roomID, 0, features, null, "Available", null, lastMaintenanceDate);
						lodge.addRoomMap(room);
						CLDB.insertRoom(roomID, room.getBeds(), features, room.getType(), room.getStatus(), lastMaintenanceDate, room.getImage());

						a.setHeaderText("Suite Added");
						a.setContentText("Room " + roomID + " has been added to City Lodge");
						a.setAlertType(AlertType.INFORMATION);
						MainScene newMain = new MainScene(primaryStage, lodge, CLDB);
						Scene scene = newMain.getScene();
						primaryStage.setScene(scene);
						a.show();
					}
					else {
						throw new InvalidRoomDetailsException();
					}
				}
			} catch (InvalidRoomDetailsException | InvalidRoomIDException | CapacityReachedException e2) {
				String message = e2.getMessage();
				a.setHeaderText("Add Suite Error");
				a.setContentText(message);
				a.setAlertType(AlertType.ERROR);
				a.show();
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.setCenter(pane);
		setCenter(root);
		return this;
	}
}
