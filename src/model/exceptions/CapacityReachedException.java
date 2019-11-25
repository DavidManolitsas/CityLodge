package model.exceptions;

public class CapacityReachedException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "City Lodge has reached its maximum room capacity";

	
	public CapacityReachedException() {
		super(ERROR_MESSAGE);
	}

}
