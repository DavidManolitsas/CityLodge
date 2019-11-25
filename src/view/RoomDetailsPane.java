package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

import controller.AddStandardRoomHandler;
import controller.AddSuiteHandler;
import controller.CompleteMaintenanceHandler;
import controller.ExportDataHandler;
import controller.ImportDataHandler;
import controller.RentRoomHandler;
import controller.ReturnRoomHandler;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.HotelRoom;
import model.Suite;
import model.exceptions.MaintenanceException;

public class RoomDetailsPane extends BorderPane {

	private Stage primaryStage;
	private Scene previousScene;
	private CityLodge lodge;
	private CityLodgeDatabase CLDB;
	private HotelRoom room;

	public RoomDetailsPane(Stage primaryStage, Scene previousScene, CityLodge lodge, CityLodgeDatabase CLDB, HotelRoom room) {
		this.primaryStage = primaryStage;
		this.previousScene = previousScene;
		this.lodge = lodge;
		this.CLDB = CLDB;
		this.room = room;
	}

	public BorderPane getPane() throws FileNotFoundException {
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

		//Add Room menu bar with drop down menu for standard room and Suite
		Menu addRoomMenu = new Menu("Add Room");
		MenuItem standardMenu = new MenuItem("Standard Room");
		MenuItem suiteMenu = new MenuItem("Suite");
		addRoomMenu.getItems().addAll(standardMenu, suiteMenu);
		menuBar.getMenus().add(addRoomMenu);
		topRoot.setTop(menuBar);

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

		AddStandardRoomHandler handler1 = new AddStandardRoomHandler(primaryStage, previousScene ,lodge, CLDB);
		standardMenu.setOnAction(handler1);

		AddSuiteHandler handler2 = new AddSuiteHandler(primaryStage, previousScene, lodge, CLDB);
		suiteMenu.setOnAction(handler2);

		ImportDataHandler IDH = new ImportDataHandler(primaryStage, lodge, CLDB);
		importMenu.setOnAction(IDH);

		ExportDataHandler EDH = new ExportDataHandler(primaryStage, lodge);
		exportMenu.setOnAction(EDH);

		//Toll bar with save button
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


		VBox leftPaneBox = new VBox();
		//room image
		if(room.getImage() == null) {
			FileInputStream input = new FileInputStream("images/No-Image-Large.jpg");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);			
			leftPaneBox.getChildren().add(imageView);
		}
		else if(room.getImage().equalsIgnoreCase("null")) {
			FileInputStream input = new FileInputStream("images/No-Image-Large.jpg");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);			
			leftPaneBox.getChildren().add(imageView);
		}
		else {
			FileInputStream input = new FileInputStream("images/" + room.getRoomID()+"-Large.jpg");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);			
			leftPaneBox.getChildren().add(imageView);
		}

		//Get details scroll pane

		String roomDetails = room.getDetails();
		TextArea roomDetailsTextArea = new TextArea(roomDetails);
		ScrollPane scrollPane = new ScrollPane(roomDetailsTextArea);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		root.setCenter(scrollPane);

		//Perform Actions on rooms	
		HBox roomActions = new HBox();
		Button rentBt = new Button("Rent Room");
		RentRoomHandler rentRoomHandler = new RentRoomHandler(primaryStage, previousScene, lodge, CLDB, room);
		rentBt.setOnAction(rentRoomHandler);

		Button returnBt = new Button("Return Room");
		ReturnRoomHandler returnRoomHandler = new ReturnRoomHandler(primaryStage, previousScene, lodge, CLDB, room);
		returnBt.setOnAction(returnRoomHandler);

		Button performMaintenanceBt = new Button("Perform Maintenance");
		performMaintenanceBt.setOnAction(e -> {
			if(CLDB.isSaved() == true) {
				CLDB.setSaved(false);
			}

			Alert a = new Alert(AlertType.NONE);
			try {
				room.performMaintenance();

				a.setHeaderText("Room Under Maintenance");
				a.setContentText(room.getRoomID() + " is now under maintenance");
				a.setAlertType(AlertType.INFORMATION);
				MainScene ms = new MainScene(primaryStage, lodge, CLDB);
				Scene scene = ms.getScene();
				primaryStage.setScene(scene);
				a.show();
			} 
			catch (MaintenanceException e2) {
				String message = e2.getMessage();
				a.setHeaderText("Room Maintenance Error");
				a.setContentText(message);
				a.setAlertType(AlertType.ERROR);
				a.show();
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		Button completeMaintenanceBt = new Button("Complete Maintenance");
		CompleteMaintenanceHandler completeMaintenanceHandler = new CompleteMaintenanceHandler(primaryStage, previousScene, lodge, CLDB, room);
		completeMaintenanceBt.setOnAction(completeMaintenanceHandler);

		double buttonWidth = 100;
		rentBt.setMinWidth(buttonWidth);
		returnBt.setMinWidth(buttonWidth);
		performMaintenanceBt.setMinWidth(buttonWidth);
		completeMaintenanceBt.setMinWidth(buttonWidth);


		roomActions.getChildren().addAll(rentBt, returnBt, performMaintenanceBt, completeMaintenanceBt);
		roomActions.setSpacing(5);
		roomActions.setPadding(new Insets(15, 12, 15, 60));
		leftPaneBox.getChildren().add(roomActions);

		GridPane pane = new GridPane();
		pane.setPadding(new Insets(5, 5, 25, 25));
		pane.setHgap(5);
		pane.setVgap(5);

		if (room.getType().equalsIgnoreCase("Standard")) {
			Label roomIDLabel = new Label("Room ID: " + room.getRoomID());
			Label bedroomsLabel = new Label("Bedrooms: " + room.getBeds());
			Label featuresLabel = new Label("Features: " + room.getFeatures());
			Label typeLabel = new Label("Room Type: " + room.getType());
			Label statusLabel = new Label("Status: " + room.getStatus());

			pane.add(roomIDLabel, 0, 0);
			pane.add(bedroomsLabel, 0, 1);
			pane.add(featuresLabel, 0, 2);
			pane.add(typeLabel, 0, 3);
			pane.add(statusLabel, 0, 4);

			leftPaneBox.getChildren().add(pane);
			leftPaneBox.setSpacing(5);
		}
		else {
			Label roomIDLabel = new Label("Room ID: " + room.getRoomID());
			Label bedroomsLabel = new Label("Bedrooms: " + room.getBeds());
			Label featuresLabel = new Label("Features: " + room.getFeatures());
			Label typeLabel = new Label("Room Type: " + room.getType());
			Label statusLabel = new Label("Status: " + room.getStatus());
			Label lastMaintenanceLabel = new Label("Last Maintenance Date: " + ((Suite) room).getLastMaintenanceDate());

			pane.add(roomIDLabel, 0, 0);
			pane.add(bedroomsLabel, 0, 1);
			pane.add(featuresLabel, 0, 2);
			pane.add(typeLabel, 0, 3);
			pane.add(statusLabel, 0, 4);
			pane.add(lastMaintenanceLabel, 0, 5);

			leftPaneBox.getChildren().addAll(pane);
			leftPaneBox.setSpacing(5);
		}
		root.setLeft(leftPaneBox);
		BorderPane.setAlignment(leftPaneBox, Pos.BOTTOM_LEFT);

		setCenter(root);
		return this;
	}
}
