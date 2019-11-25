package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.CityLodge;
import model.HiringRecord;
import model.HotelRoom;

public class ExportDataHandler implements EventHandler<ActionEvent> {

	private Stage primaryStage;
	private CityLodge lodge;

	public ExportDataHandler(Stage primaryStage, CityLodge lodge) {
		this.primaryStage = primaryStage;
		this.lodge = lodge;
	}

	@Override
	public void handle(ActionEvent event) {

		try {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File selectedDirectory = directoryChooser.showDialog(primaryStage);

			if(selectedDirectory != null) {

				File file = new File (selectedDirectory.getAbsolutePath() + "/export_data.txt");
				directoryChooser.setInitialDirectory(file);

				PrintWriter out = new PrintWriter(file);

				for(HotelRoom room : lodge.getRoomMap().values()) {
					out.println(room.toString());

					HiringRecord[] records = room.getRecords();

					int actualLength = room.getActualRecordsLength(room.getRecords());

					for (int i = actualLength-1; i >= 0; i--) {
						out.println(records[i].toString());
					}
				}
				out.close();
				
				Alert a = new Alert(AlertType.NONE);
				a.setHeaderText("Data Exported");
				a.setContentText("The City Lodge database has been successfully exported");
				a.setAlertType(AlertType.CONFIRMATION);
				a.show();
			}

		} catch (FileNotFoundException | NullPointerException e) {
			
		}

	}

}
