package model.exceptions;

public class InvalidRoomDetailsException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "Invalid room details were entered";

	public InvalidRoomDetailsException() {
		super(ERROR_MESSAGE);
	}

}
