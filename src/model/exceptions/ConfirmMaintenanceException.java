package model.exceptions;

public class ConfirmMaintenanceException extends Exception{

	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "Room maintenance can not be completed at this time";

	public ConfirmMaintenanceException() {
		super(ERROR_MESSAGE);
	}

}
