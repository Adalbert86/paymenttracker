package com.padoli.paymenttracker.exception;

/**
 * 
 * @author vojtech
 *
 * A custom exception to show what I think about in terms of design:)
 */
public class PaymentTrackerException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8405748813708611714L;

	public PaymentTrackerException(String message) {
		
		super(message);
	}
	
}
