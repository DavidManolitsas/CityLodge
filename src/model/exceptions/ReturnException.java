package model.exceptions;

public class ReturnException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "Invalid return details were entered, room can not be returned";

	public ReturnException() {
		super(ERROR_MESSAGE);
	}

}
