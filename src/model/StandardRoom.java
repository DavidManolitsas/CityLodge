package model;

import model.exceptions.ConfirmMaintenanceException;
import model.exceptions.InvalidRentDaysException;
import model.exceptions.MaintenanceException;
import model.exceptions.RentException;
import model.exceptions.ReturnException;

public class StandardRoom extends HotelRoom {

	public static final int BED_ROOM1 = 1;
	public static final int BED_ROOM2 = 2;
	public static final int BED_ROOM4 = 4;

	public static final double ONE_BEDROOM_RATE = 59;
	public static final double TWO_BEDROOM_RATE = 99;
	public static final double FOUR_BEDROOM_RATE = 199;


	public StandardRoom(String roomID, int beds, String features, String type, String status, String image) {
		super(roomID, beds, features, "Standard", status, image);
	}


	@Override
	public void rent(String customerID, DateTime rentDate, int days) throws RentException, InvalidRentDaysException {
		if (isRented() == true || isUnderMaintenance() == true) {
			throw new RentException();
			// if the its a weekday and the minimum rental days is 2
		} else if ((rentDate.getNameOfDay().equalsIgnoreCase("Monday") || 
				rentDate.getNameOfDay().equalsIgnoreCase("Tuesday") || 
				rentDate.getNameOfDay().equalsIgnoreCase("Wednesday") || 
				rentDate.getNameOfDay().equalsIgnoreCase("Thursday") || 
				rentDate.getNameOfDay().equalsIgnoreCase("Friday")) && days < 2) {
			throw new InvalidRentDaysException();
		}
		//if its a weekend the minimum rental days is 3
		else if ((rentDate.getNameOfDay().equalsIgnoreCase("Saturday") || rentDate.getNameOfDay().equalsIgnoreCase("Sunday")) && days < 3) {
			throw new InvalidRentDaysException();
		}
		//maximum rental days is 10
		else if(days > 10) {
			throw new InvalidRentDaysException();
		}
		else {
			DateTime estimatedReturnDate = new DateTime(rentDate, days);

			//create a new Hiring Record object and add it to the Hiring Record array
			HiringRecord record = new HiringRecord(getRoomID() + "_" + customerID + "_" + rentDate.getEightDigitDate(),rentDate, estimatedReturnDate, getRoomFee());
			setRentCount();
			setStatusRented();
			addRecord(record);
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
				//set Hiring Record return Date
				records[getActualRecordsLength(records)-1].setActualReturnDate(returnDate);

				//check to see if room is returned on time or late
				//if room is returned on a day before it was rented return false
				//returned room on time or early
				if(DateTime.diffDays(returnDate, rentDate) <= DateTime.diffDays(estReturn, rentDate)) {
					records[getActualRecordsLength(records)-1].setRentalFee(getRoomFee() * DateTime.diffDays(returnDate, rentDate));
					records[getActualRecordsLength(records)-1].setLateFee(0.0);
					setReturnCount();
					setStatusAvailable();
				}
				//room was returned late
				//if the difference between return date and rent date is greater than the difference between estimated return date and rent date there is a late fee
				else if (DateTime.diffDays(returnDate, rentDate) > DateTime.diffDays(estReturn, rentDate)) {
					records[getActualRecordsLength(records)-1].setRentalFee(getRoomFee() * DateTime.diffDays(estReturn, rentDate));
					records[getActualRecordsLength(records)-1].setLateFee((135/100 * getRoomFee()) * DateTime.diffDays(returnDate, estReturn));
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
		//check that the room is available to have maintenance
		if (isUnderMaintenance() == false && isRented() == false) {
			setStatusMaintenance();

		} else {
			throw new MaintenanceException();
		}
	}

	@Override
	public void completeMaintenance(DateTime completionDate) throws ConfirmMaintenanceException  {
		if (isUnderMaintenance() == true) {
			setStatusAvailable();
		} 
		else {
			throw new ConfirmMaintenanceException();
		}
	}


	@Override
	public double getRoomFee() {
		if (getBeds() == BED_ROOM1) {
			return ONE_BEDROOM_RATE;
		} else if (getBeds() == BED_ROOM2) {
			return TWO_BEDROOM_RATE;
		} else if (getBeds() == BED_ROOM4) {
			return FOUR_BEDROOM_RATE;
		}
		return 0.0;
	}


	@Override
	public String toString() {
		return getRoomID() + ":" + getBeds() + ":" + getType() + ":" + getStatus() + ":" + getFeatures() + ":" + getImage();
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
				"\nFeature Summary:\t\t" + getFeatures();

		//if the room has never been rented display an empty hiring record
		if (rentCount == 0 && returnCount == 0) {
			return  roomDetails + "\nRENTAL RECORD:\t\tempty";
		} else {
			record1 = "Record ID: \t\t\t\t" + records[getActualRecordsLength(records)-1].getRecordID() + 
					"\nRent Date: \t\t\t\t" + records[getActualRecordsLength(records)-1].getRentDate()+ 
					"\nEstimated Return Date: \t" + records[getActualRecordsLength(records)-1].getEstimatedReturnDate().getFormattedDate();

			//If a room has only been rented once return the current hiring record
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
							"\nLate Fee: \t\t\t\t" + String.format("%.2f",records[i].getLateFee())+
							"\n--------------------------------------\n";
				}
				
				//if the room has been rented more than once and show the current record and all previous hiring records
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

					return roomDetails +"\nRENTAL RECORD:\n" + record3 + 
							"\n--------------------------------------\n" + record2;
				}
			}
		}
	}
}
