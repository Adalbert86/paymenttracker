package com.padoli.paymenttracker;

import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.util.TimerTask;

import com.padoli.paymenttracker.model.Transaction;

/**
 * 
 * @author vojtech
 *
 */
public class PaymentTrackerPrintJob extends TimerTask {

	private PaymentTracker ptr;
	private Timer timer;

	// mute print for unit testing purposes
	private boolean mutePrint;

	/**
	 * 
	 * @param interval - hold milliseconds value to know how ofter a user wants to
	 *                 run this job
	 */
	public PaymentTrackerPrintJob(PaymentTracker ptr, int interval, boolean mutePrint) {

		init(ptr, interval, mutePrint);
	}

	public PaymentTrackerPrintJob(PaymentTracker ptr, int interval) {

		init(ptr, interval, false);
	}

	private void init(PaymentTracker ptr, int interval, boolean mutePrint) {

		this.ptr = ptr;
		this.mutePrint = mutePrint;

		timer = new Timer();
		timer.schedule(this, 0, interval);
	
	}
	
	public void terminate() {
		
		this.cancel();
		
		if (timer != null) {
			
			timer.cancel();
			timer.purge();	
		}	
	}

	/**
	 * In a more complex app it would make sense to pass a list of callback
	 * functions which would be executed from a run method. Here we are only
	 * interested in running/printing consolidated transactions statement once every
	 * minute.
	 */
	@Override
	public void run() {

		printConsolidatedTransactions();
	}
	
	/**
	 * Prints out the consolidated list of transactions. In a more complex app I
	 * would choose to use a separate service for the information logging and
	 * printing. Here it does make sense to delegate the printing functionality onto
	 * PaymentTracker itself.
	 */
	public void printConsolidatedTransactions() {

		if (!this.mutePrint)
			System.out.println();

		try {
			for (Transaction t : ptr.getConsolidatedTransactions()) {

				if (!this.mutePrint)
					System.out.println(t.toString());
			}

		} catch (ConcurrentModificationException ex) {

			// see AppTest, we should never get here using sync collections and synchronized
			// block of code
			throw new RuntimeException("This application is not thread safe");

		}
		
		if (!this.mutePrint)
			System.out.println();
	}

}
