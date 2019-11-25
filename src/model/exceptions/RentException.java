package model.exceptions;

public class RentException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private static final String ERROR_MESSAGE = "Invalid rental details were entered, rental has been declined";
	
	public RentException() {
		super(ERROR_MESSAGE);
	}

}
