package model.exceptions;

public class RentClashWithMaintenanceException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "Rental clashes with next schedulded Maintenance";

	
	public RentClashWithMaintenanceException() {
		super(ERROR_MESSAGE);
	}

}
