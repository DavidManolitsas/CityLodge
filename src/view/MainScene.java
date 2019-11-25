package view;

import java.io.FileInputStream;
import java.util.Optional;

import controller.AddStandardRoomHandler;
import controller.AddSuiteHandler;
import controller.ExportDataHandler;
import controller.ImportDataHandler;
import controller.RoomDetailsHandler;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.HotelRoom;
import model.Suite;

public class MainScene {

	private Stage primaryStage;
	private CityLodge lodge;
	private CityLodgeDatabase CLDB;

	public MainScene(Stage primaryStage, CityLodge lodge, CityLodgeDatabase CLDB) {
		this.primaryStage = primaryStage;
		this.lodge = lodge;
		this.CLDB = CLDB;
	}


	public Scene getScene() throws Exception{

		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 1024, 768);
		
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

		AddStandardRoomHandler handler1 = new AddStandardRoomHandler(primaryStage, scene ,lodge, CLDB);
		standardMenu.setOnAction(handler1);

		AddSuiteHandler handler2 = new AddSuiteHandler(primaryStage, scene, lodge, CLDB);
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

		
		Button saveBt = new Button("Save");
		BorderPane.setMargin(saveBt, new Insets(7,15,5,0));
		toolbar.setLeft(logoText);
		toolbar.setRight(saveBt);
		topRoot.setCenter(toolbar);
		root.setTop(topRoot);
		
		saveBt.setOnAction(e -> {
			CLDB.saveAllRooms(lodge);
			CLDB.saveAllRecords(lodge);
			CLDB.setSaved(true);
			Alert a = new Alert(AlertType.NONE);
			a.setHeaderText("Database Saved");
			a.setContentText("Changes made to the database have been saved.");
			a.setAlertType(AlertType.INFORMATION);
			a.show();
		});
		

		//Loop through all the rooms in the HashMap and display the rooms and add to the roomBox
		VBox roomBox = new VBox();		
		for(HotelRoom room : lodge.getRoomMap().values()) {
			BorderPane roomBoxPane = new BorderPane();
			BorderPane.setAlignment(roomBoxPane, Pos.CENTER);

			if(room.getImage() == null) {
				FileInputStream input = new FileInputStream("images/No-Image-Available.jpg");
				Image image = new Image(input);
				ImageView imageView = new ImageView(image);			
				roomBoxPane.setLeft(imageView);
			}
			else if (room.getImage().equalsIgnoreCase("null")){
				FileInputStream input = new FileInputStream("images/No-Image-Available.jpg");
				Image image = new Image(input);
				ImageView imageView = new ImageView(image);			
				roomBoxPane.setLeft(imageView);
			}
			else {
				FileInputStream input = new FileInputStream("images/" + room.getImage());
				Image image = new Image(input);
				ImageView imageView = new ImageView(image);			
				roomBoxPane.setLeft(imageView);
			}


			VBox detailBox = new VBox();
			detailBox.setSpacing(15);
			roomBoxPane.setCenter(detailBox);
			BorderPane.setMargin(detailBox, new Insets(25,50,10,70));
			BorderPane.setAlignment(detailBox, Pos.CENTER_RIGHT);
			roomBoxPane.setStyle("-fx-border-width: 1px;-fx-border-color: #4682B4");

			if (room.getType().equalsIgnoreCase("Standard")) {	
				Label roomIDLabel = new Label("Room ID: " + room.getRoomID());
				Label bedroomsLabel = new Label("Bedrooms: " + room.getBeds());
				Label featuresLabel = new Label("Features: " + room.getFeatures());
				Label typeLabel = new Label("Room Type: " + room.getType());
				Label statusLabel = new Label("Status: " + room.getStatus());
				Button detailsBt = new Button("Details");

				RoomDetailsHandler standardRoomDetailsHandler = new RoomDetailsHandler(primaryStage, scene, lodge, CLDB, room);
				detailsBt.setOnAction(standardRoomDetailsHandler);

				detailBox.getChildren().addAll(roomIDLabel, bedroomsLabel, featuresLabel, typeLabel, statusLabel, detailsBt);
			}
			else {
				Label roomIDLabel = new Label("Room ID: " + room.getRoomID());
				Label bedroomsLabel = new Label("Bedrooms: " + room.getBeds());
				Label featuresLabel = new Label("Features: " + room.getFeatures());
				Label typeLabel = new Label("Room Type: " + room.getType());
				Label statusLabel = new Label("Status: " + room.getStatus());
				Label lastMaintenanceLabel = new Label("Last Maintenance Date: " + ((Suite) room).getLastMaintenanceDate());
				Button detailsBt = new Button("Details");

				RoomDetailsHandler suiteRoomDetailsHandler = new RoomDetailsHandler(primaryStage, scene, lodge, CLDB, room);
				detailsBt.setOnAction(suiteRoomDetailsHandler);

				detailBox.getChildren().addAll(roomIDLabel, bedroomsLabel, featuresLabel, typeLabel, statusLabel, lastMaintenanceLabel,  detailsBt);
			}

			roomBox.getChildren().add(roomBoxPane);
		
			Text price = new Text("$" + String.format("%.2f",room.getRoomFee()) + "\nper night");
			price.setFont(Font.font("Impact", 28));
			price.setFill(Color.STEELBLUE);
			roomBoxPane.setRight(price);
			BorderPane.setMargin(price, new Insets(25,25,0,0));
				
		}

		//Scroll pane
		ScrollPane scrollPane = new ScrollPane(roomBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		root.setCenter(scrollPane);

		return scene;
	}
}
