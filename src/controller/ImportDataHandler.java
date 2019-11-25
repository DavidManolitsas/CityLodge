package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.CityLodge;
import model.CityLodgeDatabase;
import model.DateTime;
import model.HiringRecord;
import model.HotelRoom;
import model.StandardRoom;
import model.Suite;
import model.exceptions.CapacityReachedException;
import view.MainScene;

public class ImportDataHandler implements EventHandler<ActionEvent> {

	Stage primaryStage;
	CityLodge lodge;
	CityLodgeDatabase CLDB;

	public ImportDataHandler(Stage primaryStage, CityLodge lodge, CityLodgeDatabase CLDB) {
		this.primaryStage = primaryStage;
		this.lodge = lodge;
		this.CLDB = CLDB;
	}

	@Override
	public void handle(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog(primaryStage);

		try (Scanner input = new Scanner(selectedFile)) {
			while(input.hasNextLine()) {

				String currentLine = input.nextLine();
				int count = 0;
				char underscore = '_';

				for (int i = 0; i < currentLine.length(); i++) {
					if (currentLine.charAt(i) == underscore) {
						count++;
					}
				}

				//Its a room
				if(count < 3) {
					//its a Suite
					if(currentLine.startsWith("S_")) { 
						importSuite(currentLine);
					}
					else {
						importStandardRoom(currentLine);
					}
				}
				//Its a record
				else {
					//its a suite record
					if(currentLine.startsWith("S_")) {
						importSuiteHiringRecord(currentLine);

					}
					//its a standard room record
					else {
						importStandardHiringRecord(currentLine);
					}
				}
			}

			//Rearrange Hiring Records into their correct order 
			for(HotelRoom room : lodge.getRoomMap().values()) {
				HiringRecord[] records = room.getRecords();
				if(room.getActualRecordsLength(records) > 1) {
					room.reverseRecords();
				}

			}

			MainScene newMain = new MainScene(primaryStage, lodge, CLDB);
			Scene scene = newMain.getScene();
			primaryStage.setScene(scene);

		} catch (FileNotFoundException | NullPointerException e) {

		} catch (Exception e) {
			e.printStackTrace();
		}	

	}

	
	private void importSuite(String currentLine) {
		HotelRoom room;
		Alert a = new Alert(AlertType.NONE);

		String[] split = currentLine.split(":");
		String roomID = split[0];					
		int bedrooms = Integer.parseInt(split[1]);
		String type = split[2];
		String status = split[3];
		String date = split[4];
		String[] splitDate = date.split("/");
		int day = Integer.parseInt(splitDate[0]);					
		int month = Integer.parseInt(splitDate[1]);
		int year = Integer.parseInt(splitDate[2]);
		DateTime lastMaintenanceDate = new DateTime(day, month, year);
		String features = split[5];
		String image = split[6];

		if (image.equalsIgnoreCase("null")) {
			room = new Suite(roomID, bedrooms, features, type, status, null, lastMaintenanceDate);
			CLDB.insertRoom(roomID, bedrooms, features, type, status, lastMaintenanceDate, null);
		}
		else {
			room = new Suite(roomID, bedrooms, features, type, status, image, lastMaintenanceDate);
			CLDB.insertRoom(roomID, bedrooms, features, type, status, lastMaintenanceDate, image);
		}
		try {
			lodge.addRoomMap(room);
		}
		catch (CapacityReachedException e3) {
			String message = e3.getMessage();
			a.setHeaderText("Add Suite Error");
			a.setContentText(message);
			a.setAlertType(AlertType.ERROR);
			a.show();
		}
		if (room.getStatus().equalsIgnoreCase("Available")) {
			room.setStatusAvailable();
		}
		else if(status.equalsIgnoreCase("Rented")) {
			room.setStatusRented();
		}
		else if (status.equalsIgnoreCase("Maintenance")) {
			room.setStatusMaintenance();
		}

	}

	
	private void importStandardRoom(String currentLine) {
		HotelRoom room;
		Alert a = new Alert(AlertType.NONE);

		String[] split = currentLine.split(":");
		String roomID = split[0];					
		int bedrooms = Integer.parseInt(split[1]);
		String type = split[2];
		String status = split[3];
		String features = split[4];
		String image = split[5];

		if (image.equalsIgnoreCase("null")) {
			room = new StandardRoom(roomID, bedrooms, features, type, status, null);
			CLDB.insertRoom(roomID, bedrooms, features, type, status, null, null);
		}
		else {
			room = new StandardRoom(roomID, bedrooms, features, type, status, image);
			CLDB.insertRoom(roomID, bedrooms, features, type, status, null, image);
		}
		try {
			lodge.addRoomMap(room);
		}
		catch (CapacityReachedException e3) {
			String message = e3.getMessage();
			a.setHeaderText("Add Standard Room Error");
			a.setContentText(message);
			a.setAlertType(AlertType.ERROR);
			a.show();
		}

		if (room.getStatus().equalsIgnoreCase("Available")) {
			room.setStatusAvailable();
		}
		else if(status.equalsIgnoreCase("Rented")) {
			room.setStatusRented();
		}
		else if (status.equalsIgnoreCase("Maintenance")) {
			room.setStatusMaintenance();
		}
		
	}

	
	private void importSuiteHiringRecord(String currentLine) {
		HiringRecord record;

		String roomID = currentLine.substring(0, 5);
		HotelRoom suite = lodge.getRoom(roomID);

		String[] split = currentLine.split(":");

		//record is in progress
		if(split[3].equalsIgnoreCase("none")) {
			String recordID = split[0];

			String rentDateString = split[1];
			String[] splitRentDate = rentDateString.split("/");
			int day = Integer.parseInt(splitRentDate[0]);					
			int month = Integer.parseInt(splitRentDate[1]);
			int year = Integer.parseInt(splitRentDate[2]);
			DateTime rentDate = new DateTime(day, month, year);

			String estReturnDateString = split[2];
			String[] splitEstReturnDate = estReturnDateString.split("/");
			int dd = Integer.parseInt(splitEstReturnDate[0]);					
			int mm = Integer.parseInt(splitEstReturnDate[1]);
			int yyyy = Integer.parseInt(splitEstReturnDate[2]);
			DateTime estimatedReturnDate = new DateTime(dd, mm, yyyy);

			record = new HiringRecord(recordID, rentDate, estimatedReturnDate, 0.0);
			suite.addRecord(record);
			suite.setRentCount();

			CLDB.insertRecord(roomID, recordID, rentDate, estimatedReturnDate, null, 0.0, 0.0);
		}	
		//record is complete
		else {
			String recordID = split[0];

			String rentDateString = split[1];
			String[] splitRentDate = rentDateString.split("/");
			int day = Integer.parseInt(splitRentDate[0]);					
			int month = Integer.parseInt(splitRentDate[1]);
			int year = Integer.parseInt(splitRentDate[2]);
			DateTime rentDate = new DateTime(day, month, year);

			String estReturnDateString = split[2];
			String[] splitEstReturnDate = estReturnDateString.split("/");
			int dd = Integer.parseInt(splitEstReturnDate[0]);					
			int mm = Integer.parseInt(splitEstReturnDate[1]);
			int yyyy = Integer.parseInt(splitEstReturnDate[2]);
			DateTime estimatedReturnDate = new DateTime(dd, mm, yyyy);

			String actReturnDateString = split[3];
			String[] splitActReturnDate = actReturnDateString.split("/");
			int d = Integer.parseInt(splitActReturnDate[0]);					
			int m = Integer.parseInt(splitActReturnDate[1]);
			int y = Integer.parseInt(splitActReturnDate[2]);
			DateTime actualReturnDate = new DateTime(d, m, y);

			double rentalFee = Double.parseDouble(split[4]);
			double lateFee = Double.parseDouble(split[5]);

			record = new HiringRecord(recordID, rentDate, estimatedReturnDate, rentalFee);
			record.setActualReturnDate(actualReturnDate);
			record.setLateFee(lateFee);
			suite.addRecord(record);
			suite.setRentCount();
			suite.setReturnCount();
			CLDB.insertRecord(roomID, recordID, rentDate, estimatedReturnDate, actualReturnDate, rentalFee, lateFee);
		}
		
	}

	
	private void importStandardHiringRecord(String currentLine) {
		HiringRecord record;

		String roomID = currentLine.substring(0, 5);
		HotelRoom standardRoom = lodge.getRoom(roomID);
		String[] split = currentLine.split(":");

		//record is in progress
		if(split[3].equalsIgnoreCase("none")) {
			String recordID = split[0];

			String rentDateString = split[1];
			String[] splitRentDate = rentDateString.split("/");
			int day = Integer.parseInt(splitRentDate[0]);					
			int month = Integer.parseInt(splitRentDate[1]);
			int year = Integer.parseInt(splitRentDate[2]);
			DateTime rentDate = new DateTime(day, month, year);

			String estReturnDateString = split[2];
			String[] splitEstReturnDate = estReturnDateString.split("/");
			int dd = Integer.parseInt(splitEstReturnDate[0]);					
			int mm = Integer.parseInt(splitEstReturnDate[1]);
			int yyyy = Integer.parseInt(splitEstReturnDate[2]);
			DateTime estimatedReturnDate = new DateTime(dd, mm, yyyy);

			record = new HiringRecord(recordID, rentDate, estimatedReturnDate, 0.0);
			standardRoom.addRecord(record);
			standardRoom.setRentCount();

			CLDB.insertRecord(roomID, recordID, rentDate, estimatedReturnDate, null, 0.0, 0.0);
		}	
		//record is complete
		else {
			String recordID = split[0];

			String rentDateString = split[1];
			String[] splitRentDate = rentDateString.split("/");
			int day = Integer.parseInt(splitRentDate[0]);					
			int month = Integer.parseInt(splitRentDate[1]);
			int year = Integer.parseInt(splitRentDate[2]);
			DateTime rentDate = new DateTime(day, month, year);

			String estReturnDateString = split[2];
			String[] splitEstReturnDate = estReturnDateString.split("/");
			int dd = Integer.parseInt(splitEstReturnDate[0]);					
			int mm = Integer.parseInt(splitEstReturnDate[1]);
			int yyyy = Integer.parseInt(splitEstReturnDate[2]);
			DateTime estimatedReturnDate = new DateTime(dd, mm, yyyy);

			String actReturnDateString = split[3];
			String[] splitActReturnDate = actReturnDateString.split("/");
			int d = Integer.parseInt(splitActReturnDate[0]);					
			int m = Integer.parseInt(splitActReturnDate[1]);
			int y = Integer.parseInt(splitActReturnDate[2]);
			DateTime actualReturnDate = new DateTime(d, m, y);

			double rentalFee = Double.parseDouble(split[4]);
			double lateFee = Double.parseDouble(split[5]);

			record = new HiringRecord(recordID, rentDate, estimatedReturnDate, rentalFee);
			record.setActualReturnDate(actualReturnDate);
			record.setLateFee(lateFee);
			standardRoom.addRecord(record);
			standardRoom.setRentCount();
			standardRoom.setReturnCount();
			CLDB.insertRecord(roomID, recordID, rentDate, estimatedReturnDate, actualReturnDate, rentalFee, lateFee);
		}
		
	}
	
}
