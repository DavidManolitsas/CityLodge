package model;

import model.exceptions.ConfirmMaintenanceException;
import model.exceptions.InvalidRentDaysException;
import model.exceptions.MaintenanceException;
import model.exceptions.RentClashWithMaintenanceException;
import model.exceptions.RentException;
import model.exceptions.ReturnException;

public abstract class HotelRoom {

	private String roomID;
	private int beds;
	private String features;
	private String type;
	private String status;
	private String image;
	
	protected int rentCount;
	protected int returnCount;
	protected boolean isRented;
	protected boolean underMaintenance;
	
	private final int MAX_RECORD_SIZE = 10;

	protected HiringRecord[] records = new HiringRecord[MAX_RECORD_SIZE];

	public HotelRoom(String roomID, int beds, String features, String type, String status, String image) {
		this.roomID = roomID;
		this.beds = beds;
		this.features = features;
		this.type = type;
		this.status = status;
		this.image = image;
		this.isRented = false;
		this.underMaintenance = false;
		this.rentCount = 0;
		this.returnCount = 0;
	}

	public String getRoomID() {
		return roomID;
	}

	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}

	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setStatusRented() {
		status = "Rented";
		isRented = true;
	}

	public void setStatusMaintenance() {
		status = "Maintenance";
		underMaintenance = true;
	}

	public void setStatusAvailable() {
		status = "Available";
		underMaintenance = false;
		isRented = false;
	}

	public boolean isRented() {
		return isRented;
	}

	public boolean isUnderMaintenance() {
		return underMaintenance;
	}

	public int getRentCount() {
		return rentCount;
	}

	public void setRentCount() {
		this.rentCount++;
	}

	public void addToRentCount(int num) {
		this.rentCount += num;
	}

	public int getReturnCount() {
		return returnCount;
	}

	public void setReturnCount() {
		this.returnCount++;
	}

	public void addToReturnCount(int num) {
		this.returnCount += num;
	}


	public HiringRecord[] getRecords() {
		return records;
	}

	public void setRecords(HiringRecord[] records) {
		this.records = records;
	}
	
	//abstract methods
	public abstract double getRoomFee();

	public abstract void rent(String customerID, DateTime rentDate, int days) throws RentException, InvalidRentDaysException, RentClashWithMaintenanceException;

	public abstract void returnRoom(DateTime returnDate) throws ReturnException;

	public abstract void performMaintenance() throws MaintenanceException;

	public abstract void completeMaintenance(DateTime completionDate) throws ConfirmMaintenanceException;

	public abstract String toString();

	public abstract String getDetails();


	//methods to manipulate Hiring Record array
	public int getActualRecordsLength(HiringRecord[] record) {
		//this method returns the number or records in the Hiring Record array
		//Since the max size is always set to 10, the array length will always return 10
		int length = 0;

		for(int i = 0; i < record.length; i ++) {
			if(record[i] != null) {
				length++;
			}
		}
		return length;
	}


	public void addRecord(HiringRecord record) {

		//Once a hiring records has reached 10 records
		//Remove the oldest record and add the new record into the array
		if (getActualRecordsLength(records) >= MAX_RECORD_SIZE) {
			HiringRecord[] newRecord = new HiringRecord[MAX_RECORD_SIZE];

			for (int i = 1; i < MAX_RECORD_SIZE; i ++) {
				newRecord[i-1] = records[i];
				if(i == MAX_RECORD_SIZE-1) {
					newRecord[i] = record;
				}
			}
			records = newRecord;

		} else {
			if (getActualRecordsLength(records) < MAX_RECORD_SIZE) {
				records[getActualRecordsLength(records)] = record;
			} 
			else {

			}
		}

	}


	//When importing from a .txt file the records will be put into the records array backwards
	//This method reverses the order of the records array
	public void reverseRecords() {
		int actLength = getActualRecordsLength(records);

		for (int i = 0; i < actLength/2; i++) {
			HiringRecord temp = records[i];
			records[i] = records[actLength - 1 - i];
			records[actLength - 1 - i] = temp;
		}

	}

}


