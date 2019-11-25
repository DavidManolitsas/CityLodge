package model;

public class HiringRecord {

	private String recordID;
	private DateTime rentDate;
	private DateTime estimatedReturnDate;
	private DateTime actualReturnDate;
	private double rentalFee;
	private double lateFee;


	public HiringRecord(String recordID, DateTime rentDate, DateTime estimatedReturnDate, double rentalFee) {
		this.recordID = recordID;
		this.rentDate = rentDate;
		this.estimatedReturnDate = estimatedReturnDate;
		this.rentalFee = rentalFee;
	}

	public String toString() {	
		if (actualReturnDate == null) {
			return recordID + ":" + rentDate + ":" + estimatedReturnDate + ":none:none:none";
		}
		else {
			return recordID + ":" + rentDate + ":" + estimatedReturnDate + ":" + actualReturnDate + ":" + rentalFee + ":" + lateFee;
		}
	}

	public String getDetails(String recordID, DateTime rentDate, DateTime estimatedReturnDate) {
		return  "Record ID: \t\t" + recordID +
				"\nRent Date: \t\t" + rentDate +
				"\nEstimated Return Date: \t" + estimatedReturnDate;
	}

	public String getDetails(String recordID, DateTime rentDate, DateTime estimatedReturnDate, DateTime actualReturnDate, double rentalFee, double lateFee) {
		return "Record ID: \t\t" + recordID + 
				"\nRent Date: \t\t" + rentDate +
				"\nEstimated Return Date: \t" + estimatedReturnDate +
				"\nActual Return Date: \t" + actualReturnDate +
				"\nRental Fee: \t\t" + String.format("%.2f",rentalFee) +
				"\nLate Fee: \t\t" + String.format("%.2f",lateFee);
	}

	public String getRecordID() {
		return recordID;
	}

	public void setRecordID(String roomID, String customerID, DateTime rentDate) {
		this.recordID = roomID + "_" + customerID + "_" + rentDate.getFormattedDate(); 
	}

	public DateTime getRentDate() {
		return rentDate;
	}

	public void setRentDate(DateTime rentDate) {
		this.rentDate = rentDate;
	}

	public DateTime getEstimatedReturnDate() {
		return estimatedReturnDate;
	}

	public void setEstimatedReturnDate(DateTime estimatedReturnDate) {
		this.estimatedReturnDate = estimatedReturnDate;
	}

	public DateTime getActualReturnDate() {
		return actualReturnDate;
	}

	public void setActualReturnDate(DateTime actualReturnDate) {
		this.actualReturnDate = actualReturnDate;
	}

	public double getRentalFee() {
		return rentalFee;
	}

	public void setRentalFee(double rentalFee) {
		this.rentalFee = rentalFee;
	}

	public double getLateFee() {
		return lateFee;
	}

	public void setLateFee(double lateFee) {
		this.lateFee = lateFee;
	}

}
