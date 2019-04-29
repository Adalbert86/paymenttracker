package com.padoli.paymenttracker.exception;

/**
 * 
 * @author vojtech
 * 
 *         Custom exception for the App
 *
 */
public class AppUserInputException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8294602180947533551L;

	public AppUserInputException(String message) {
		super(message);

	}

}
