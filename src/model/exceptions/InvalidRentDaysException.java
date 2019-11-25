package model.exceptions;

public class InvalidRentDaysException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "Invalid number of rental days were entered";

	public InvalidRentDaysException() {
		super(ERROR_MESSAGE);
	}

}
