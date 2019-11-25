package model.exceptions;

public class InvalidRoomIDException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "The Room ID is already being used for another room";
	
	public InvalidRoomIDException() {
		super (ERROR_MESSAGE);
	}

}
