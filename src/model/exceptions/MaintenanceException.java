package model.exceptions;

public class MaintenanceException extends Exception{

	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "Room is unavailable for maintenance, it is already under maintenance or is being rented";
	
	public MaintenanceException() {
		super(ERROR_MESSAGE);
	}

}
