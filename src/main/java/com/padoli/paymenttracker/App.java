package com.padoli.paymenttracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.padoli.paymenttracker.exception.AppUserInputException;
import com.padoli.paymenttracker.model.UserInputReturnCode;
import com.padoli.paymenttracker.util.TransactionHelper;

/**
 * 
 * @author vojtech
 *
 */
public class App {

	private PaymentTracker ptr = null;
	private PaymentTrackerPrintJob ptrPrintJob = null;

	public App() {

		this.ptr = new PaymentTracker();
	}
	
	public PaymentTracker getPaymentTracker() {
		
		return this.ptr;
	}
	
	public void schedulePrintJob(int interval) {
		// initialize PaymentTrackePrintJob which is set to execute and spit out list of
		// consolidated transactions every one minute
		ptrPrintJob = new PaymentTrackerPrintJob(ptr, interval);	
	}
	
	public void cancelPrintJob() {
		
		if (this.ptrPrintJob != null)
			this.ptrPrintJob.terminate();
	}
	
	public UserInputReturnCode processUserInput(String strInput) {

		if(strInput == null || strInput.isEmpty())
			throw new AppUserInputException("Input cannot be empty!");
		
		// assume a reasonable length
		if (strInput.length() > 100)
			throw new AppUserInputException("Input unreasonably long!");
		
		
		strInput = strInput.trim().toLowerCase();
		if (strInput.equals("quit")) {

			return UserInputReturnCode.QUIT;
		}

		String words[] = strInput.split(" ");
		
		if (words.length != 2)
			throw new AppUserInputException("Input must contain exactly 2 values");

		
		String currencyCode = words[0];
		
		if (!TransactionHelper.isCodeValid(currencyCode))
			throw new AppUserInputException("Currency code invalid. Must be a 3 letter string.");
		
		if (!TransactionHelper.isAmountValid(words[1]))
			throw new AppUserInputException("Invalid number.");
		
		Double amount = Double.valueOf(words[1]);

		ptr.insertTransaction(currencyCode, amount);
		return UserInputReturnCode.TRANSACTION_PROCESS;
	}
	
	private static void inputScanner(Scanner reader, App app) {
		
		while (reader.hasNext()) {

			try {
			if (app.processUserInput(reader.nextLine()) == UserInputReturnCode.QUIT)
				break;
			
			} catch (AppUserInputException ex) {
				
				System.out.println(ex.getMessage());
			}
		}
		
		reader.close();
		
	}

	public static void main(String[] args) {

		System.out.println("Payment Tracker");

		App app = new App();
		
	
		if (args.length == 1) {
		try {
		File file = new File(args[0]);
		inputScanner(new Scanner(file), app);
		} catch (FileNotFoundException ex) {
			
			System.out.println("No input file found");
		}
		
		} else if (args.length > 1) {
			
			throw new RuntimeException("Invalid number of arguments. You are allowed only one argument (filename).");
		}
		
		app.schedulePrintJob(60000);	
	
		inputScanner(new Scanner(System.in), app);

		app.cancelPrintJob();
		
		System.out.println("End of program.");
	}

}
