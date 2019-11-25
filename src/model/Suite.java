package model;

import model.exceptions.ConfirmMaintenanceException;
import model.exceptions.InvalidRentDaysException;
import model.exceptions.MaintenanceException;
import model.exceptions.RentClashWithMaintenanceException;
import model.exceptions.RentException;
import model.exceptions.ReturnException;

public class Suite extends HotelRoom {

	private DateTime lastMaintenanceDate;

	public static final int BEDROOMS = 6;
	public static final double RENT_RATE = 999;
	public static final double SUITE_LATE_FEE = 1099;

	public Suite(String roomID, int beds, String features, String type, String status, String image, DateTime lastMaintenanceDate) {
		super(roomID, BEDROOMS, features, "Suite", status, image);
		this.lastMaintenanceDate = lastMaintenanceDate;
	}


	public DateTime getLastMaintenanceDate() {
		return lastMaintenanceDate;
	}

	public void setLastMaintenanceDate(DateTime lastMaintenanceDate) {
		this.lastMaintenanceDate = lastMaintenanceDate;
	}
	
	@Override
	public double getRoomFee() {
		return RENT_RATE;
	}


	@Override
	public void rent(String customerID, DateTime rentDate, int days) throws RentException, InvalidRentDaysException, RentClashWithMaintenanceException {
		DateTime estimatedReturnDate = new DateTime(rentDate, days);
		DateTime nextMaintenance = new DateTime (lastMaintenanceDate, 10);

		//Check that room is available to be rented
		if (isRented() == true || isUnderMaintenance() == true) {
			throw new RentException();
		}
		else {
			//check to see if rental period will clash with maintenance schedule
			if(DateTime.diffDays(estimatedReturnDate, rentDate) > DateTime.diffDays(nextMaintenance, rentDate)) {
				throw new RentClashWithMaintenanceException();
			}
			else {
				//Create a new hiring record object and add it to the hiring record array
				HiringRecord record = new HiringRecord(getRoomID() + "_" + customerID + "_" + rentDate.getEightDigitDate(),
						rentDate, estimatedReturnDate, SUITE_LATE_FEE);
				addRecord(record);
				setRentCount();
				setStatusRented();
			}
			
		}

	}

	@Override
	public void returnRoom(DateTime returnDate) throws ReturnException {
		//check to see if room has been rented
		if (isRented() == true) {
		//get estimated return date and rent date from latest HiringRecord
		DateTime estReturn = records[getActualRecordsLength(records)-1].getEstimatedReturnDate();
		DateTime rentDate = records[getActualRecordsLength(records)-1].getRentDate();
		
			//Check if room is returned before rental date
			if (DateTime.diffDays(returnDate, rentDate) < DateTime.diffDays(rentDate, rentDate)) {
				throw new ReturnException();
			}
			else {
				//Set Hiring Record Actual Return Date
				records[getActualRecordsLength(records)-1].setActualReturnDate(returnDate);
				
				//check to see if a room is returned on time or late
				//if room is returned on a day before it was rented return false
				//returned room on time or early
				if(DateTime.diffDays(returnDate, rentDate) <= DateTime.diffDays(estReturn, rentDate)) {
					records[getActualRecordsLength(records)-1].setRentalFee(RENT_RATE * DateTime.diffDays(returnDate, rentDate));
					records[getActualRecordsLength(records)-1].setLateFee(0.0);
					setReturnCount();
					setStatusAvailable();
				}
				//room was returned late
				//if the difference between return date and rent date is greater than the difference between estimated return date and rent date there is a late fee
				else if (DateTime.diffDays(returnDate, rentDate) > DateTime.diffDays(estReturn, rentDate)) {
					records[getActualRecordsLength(records)-1].setRentalFee(RENT_RATE * DateTime.diffDays(estReturn, rentDate));
					records[getActualRecordsLength(records)-1].setLateFee(SUITE_LATE_FEE * DateTime.diffDays(returnDate, estReturn));
					setReturnCount();
					setStatusAvailable();	
				}
				else
					throw new ReturnException();
			}
		}
		else 
			throw new ReturnException();
	}


	@Override
	public void performMaintenance() throws MaintenanceException {
		//Check the the room is available for maintenance
		if(isUnderMaintenance() == false && isRented() == false) {
			setStatusMaintenance();
		}
		else {
			throw new MaintenanceException();
		}
	}


	@Override
	public void completeMaintenance(DateTime completionDate) throws ConfirmMaintenanceException {
		if (isUnderMaintenance() == true) {
			//complete maintenance, and update the last maintenance date
			setLastMaintenanceDate(completionDate);
			setStatusAvailable();
		}
		else {	
			throw new ConfirmMaintenanceException();
		}
	}


	@Override
	public String toString() {
			return getRoomID() + ":" + getBeds() + ":" + getType() + ":" + getStatus() + ":" + lastMaintenanceDate + ":" + getFeatures() + ":" + getImage();
	}


	@Override
	public String getDetails() {

		String record1 = "";
		String record2 = "";
		String record3 = "";
		String roomDetails = "Room ID:\t\t\t\t" + getRoomID() +
				"\nBedrooms:\t\t\t\t" + getBeds() +
				"\nRoom type:\t\t\t\t" + getType() +
				"\nRoom Status:\t\t\t" + getStatus() +
				"\nLast Maintenance date:\t" + lastMaintenanceDate +
				"\nFeature Summary:\t\t" + getFeatures(); 

		//If the room has never been rented before display an empty hiring record
		if (rentCount == 0 && returnCount == 0) {
			return roomDetails + "\nRENTAL RECORD:\t\tempty";
		} 
		else {
			record1 = "Record ID: \t\t\t\t" + records[getActualRecordsLength(records)-1].getRecordID() + 
					"\nRent Date: \t\t\t\t" + records[getActualRecordsLength(records)-1].getRentDate()+ 
					"\nEstimated Return Date: \t" + records[getActualRecordsLength(records)-1].getEstimatedReturnDate().getFormattedDate();

			//if the room has only been rented once display the most recent hiring record
			if (rentCount == 1 && returnCount == 0) {
				return roomDetails + "\nRENTAL RECORD:\n" + record1;
			}
			else {
				for(int i = getActualRecordsLength(records)-2; i >= 0; i--) {
					record2 += "Record ID: \t\t\t\t" + records[i].getRecordID() + 
							"\nRent Date: \t\t\t\t" + records[i].getRentDate().getFormattedDate() + 
							"\nEstimated Return Date: \t" + records[i].getEstimatedReturnDate().getFormattedDate() + 
							"\nActual Return Date: \t" + records[i].getActualReturnDate().getFormattedDate() +
							"\nRental Fee: \t\t\t\t" + String.format("%.2f",records[i].getRentalFee()) + 
							"\nLate Fee: \t\t\t\t" + String.format("%.2f",records[i].getLateFee()) +
							"\n--------------------------------------\n";
				}
				
				//if the room has been rented more than once show the current hiring record and the all previous hiring records
				if(isRented() == true) {
					return roomDetails + "\nRENTAL RECORD:\n" + record1 + 
							"\n--------------------------------------\n" + record2;
				} 
				else {
					record3 = "Record ID: \t\t\t\t" + records[getActualRecordsLength(records)-1].getRecordID() + 
							"\nRent Date: \t\t\t\t" + records[getActualRecordsLength(records)-1].getRentDate()+ 
							"\nEstimated Return Date: \t"+ records[getActualRecordsLength(records)-1].getEstimatedReturnDate().getFormattedDate() +  
							"\nActual Return Date: \t" + records[getActualRecordsLength(records)-1].getActualReturnDate().getFormattedDate() +		  
							"\nRental Fee: \t\t\t\t" + String.format("%.2f",records[getActualRecordsLength(records)-1].getRentalFee()) + 
							"\nLate Fee: \t\t\t\t" + String.format("%.2f",records[getActualRecordsLength(records)-1].getLateFee());

					return roomDetails + "\nRENTAL RECORD:\n" + record3 + 
							"\n--------------------------------------\n" + record2;
				}
			}
		}
		
	}
	
}
