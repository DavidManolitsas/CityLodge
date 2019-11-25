package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import controller.AddStandardRoomHandler;
import controller.AddSuiteHandler;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.DateTime;
import model.HotelRoom;
import model.exceptions.ConfirmMaintenanceException;

public class CompleteMaintenancePane extends BorderPane {

	private Stage primaryStage;
	private Scene mainScene;
	private CityLodge lodge;
	private CityLodgeDatabase CLDB;
	private HotelRoom room;

	public CompleteMaintenancePane(Stage primaryStage, Scene mainScene, CityLodge lodge, CityLodgeDatabase CLDB, HotelRoom room) {
		this.primaryStage = primaryStage;
		this.mainScene = mainScene;
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

		AddStandardRoomHandler handler1 = new AddStandardRoomHandler(primaryStage, mainScene ,lodge, CLDB);
		standardMenu.setOnAction(handler1);

		AddSuiteHandler handler2 = new AddSuiteHandler(primaryStage, mainScene, lodge, CLDB);
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

		Button mainMenuBt = new Button("Main Menu");
		BorderPane.setMargin(mainMenuBt, new Insets(7,15,5,0));
		toolbar.setLeft(logoText);
		toolbar.setRight(mainMenuBt);
		topRoot.setCenter(toolbar);
		root.setTop(topRoot);
		mainMenuBt.setOnAction(e -> primaryStage.setScene(mainScene));


		VBox leftPaneRoot = new VBox();
		//room image
		if(room.getImage() == null) {
			FileInputStream input = new FileInputStream("images/No-Image-Large.jpg");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);			
			leftPaneRoot.getChildren().add(imageView);
		}
		else if(room.getImage().equalsIgnoreCase("null")) {
			FileInputStream input = new FileInputStream("images/No-Image-Large.jpg");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);			
			leftPaneRoot.getChildren().add(imageView);
		}
		else {
			FileInputStream input = new FileInputStream("images/" + room.getRoomID()+"-Large.jpg");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);			
			leftPaneRoot.getChildren().add(imageView);
		}


		//Get details scroll pane
		String roomDetails = room.getDetails();
		TextArea roomDetailsTextArea = new TextArea(roomDetails);
		ScrollPane scrollPane = new ScrollPane(roomDetailsTextArea);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		root.setCenter(scrollPane);

		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(15, 15, 15, 15));
		pane.setHgap(5);
		pane.setVgap(10);

		Label enterDetails = new Label("Enter details:");
		Label completeDateLabel = new Label("Maintenance Completion Date:");
		DatePicker completeDatePick = new DatePicker();
		Button completeDateBt = new Button("Complete Room Maintenance");
		

		pane.add(enterDetails, 0, 0);
		pane.add(completeDateLabel, 0, 1);
		pane.add(completeDatePick, 1, 1);
		pane.add(completeDateBt, 1, 2);
		leftPaneRoot.getChildren().add(pane);

		//Confirm Return
		completeDateBt.setOnAction(e -> {
			if(CLDB.isSaved() == true) {
				CLDB.setSaved(false);
			}

			Alert a = new Alert(AlertType.NONE);
			try {
				if(completeDatePick.getValue() == null) {
					throw new ConfirmMaintenanceException();
				}

				String completeDateString = completeDatePick.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
				String[] split = completeDateString.split("/");
				int day = Integer.parseInt(split[0]);					
				int month = Integer.parseInt(split[1]);
				int year = Integer.parseInt(split[2]);
				DateTime completeMaintenanceDate = new DateTime(day, month, year);

				a.setHeaderText("Room Maintenance Complete");
				a.setContentText(room.getRoomID() + " has completed its maintenance");
				a.setAlertType(AlertType.INFORMATION);
				room.completeMaintenance(completeMaintenanceDate);
				MainScene newMain = new MainScene(primaryStage, lodge, CLDB);
				Scene scene = newMain.getScene();
				primaryStage.setScene(scene);
				a.show();

			} catch (ConfirmMaintenanceException e2) {
				String message = e2.getMessage();
				a.setHeaderText("Room Maintenance Error");
				a.setContentText(message);
				a.setAlertType(AlertType.ERROR);
				a.show();

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		root.setLeft(leftPaneRoot);
		BorderPane.setAlignment(leftPaneRoot, Pos.BOTTOM_LEFT);

		setCenter(root);
		return this;
	}

}
