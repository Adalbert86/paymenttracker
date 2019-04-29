package paymenttracker;

import static org.junit.Assert.*;

import java.util.ConcurrentModificationException;

import org.junit.Test;

import com.padoli.paymenttracker.App;
import com.padoli.paymenttracker.PaymentTrackerPrintJob;
import com.padoli.paymenttracker.exception.AppUserInputException;
import com.padoli.paymenttracker.model.UserInputReturnCode;

public class AppTest {

	@Test
	public void testProcessUserInputNormal() {

		// this is how the normal valid input should look like
		// shall not throw any exceptions
		App app = new App();

		assertNotNull(app);
		assertTrue(app.processUserInput("USD 100") == UserInputReturnCode.TRANSACTION_PROCESS);
		assertTrue(app.processUserInput("czk 100") == UserInputReturnCode.TRANSACTION_PROCESS);
		assertTrue(app.processUserInput("HkD 50.12") == UserInputReturnCode.TRANSACTION_PROCESS);
		assertTrue(app.processUserInput("HKD 50.120987") == UserInputReturnCode.TRANSACTION_PROCESS);
		assertTrue(app.processUserInput("WED 2134.323") == UserInputReturnCode.TRANSACTION_PROCESS);
		assertTrue(app.processUserInput("quit") == UserInputReturnCode.QUIT);
		assertTrue(app.processUserInput("QUIT") == UserInputReturnCode.QUIT);
	}

	@Test(expected = AppUserInputException.class)
	public void testProcessUserInputTooLongNumber() {

		// user input should be reasonably long

		App app = new App();

		assertNotNull(app);
		assertTrue(app.processUserInput(
				"USDD 100987997809897989890989809809808098086976757544564664654.098450934803485094092728745") == UserInputReturnCode.TRANSACTION_PROCESS);
	}

	@Test(expected = AppUserInputException.class)
	public void testProcessUserInputInvalidCurrencyCode1() {

		// although the test is covered by PaymentTrackerTest
		// it makes sense to do some additional testing on user input and telling the
		// user what went wrong even before submitting the input to payment tracker

		// I assume the currency is a valid 3 letter string

		App app = new App();

		assertNotNull(app);
		app.processUserInput("USDEDS 100.6");
	}

	@Test(expected = AppUserInputException.class)
	public void testProcessUserInputInvalidCurrencyCode2() {

		// I assume the currency is a valid 3 letter string
		// see testProcessUserInputInvalidCurrencyCode1

		App app = new App();

		assertNotNull(app);
		app.processUserInput("US 100");
	}

	@Test(expected = AppUserInputException.class)
	public void testProcessUserInputInvalidCurrencyCode3() {

		// I assume the currency is a valid 3 letter string
		// see testProcessUserInputInvalidCurrencyCode1

		App app = new App();

		assertNotNull(app);
		app.processUserInput("U1D 100");
	}

	@Test(expected = AppUserInputException.class)
	public void testProcessUserInputInvalidCurrencyCode4() {

		// I assume the currency is a valid 3 letter string
		// see testProcessUserInputInvalidCurrencyCode1

		App app = new App();

		assertNotNull(app);
		app.processUserInput("123 100");
	}

	@Test(expected = AppUserInputException.class)
	public void testProcessUserInputNonsense1() {

		App app = new App();
		String input = "";
		app.processUserInput(input);
	}

	@Test(expected = AppUserInputException.class)
	public void testProcessUserInputNonsense2() {

		App app = new App();
		String input = "DES 120 00";
		app.processUserInput(input);
	}

	@Test(expected = AppUserInputException.class)
	public void testProcessUserInputNonsense3() {

		App app = new App();
		String input = "DESy0";
		app.processUserInput(input);
	}

	/**
	 * !!! IMPORTANT !!!
	 * 
	 * I am aware this is something this homework is about. It is important to use a
	 * thread safe collections (in wrapper objects here) and synchronized blocks of
	 * code. If you change the PaymentTracker map to normal unwrapped map and try to
	 * run this code below, it will fail and you will get a
	 * java.util.ConcurrentModificationException from either thread. In this case,
	 * this test should pass with flying colors.
	 * 
	 * 
	 */
	@Test
	public void testConcurentAccessToTransactions() {

		PaymentTrackerPrintJob ptrPrintJob = null;
		
		try {

			App app = new App();
			// run the job every 10ms and turn off console printing
			// for unit testing purposes
			 ptrPrintJob = new PaymentTrackerPrintJob(app.getPaymentTracker(), 10, true);

			for (int x = 0; x < 5_000; x++) {
				for (int i = 65; i <= 90; i++) {
					for (int j = 65; j <= 90; j++) {

						String code = String.valueOf((char) i) + String.valueOf((char) j) + "A";
						app.processUserInput(code + " 100.10");
					}
				}
			}

		} catch (ConcurrentModificationException ex) {

			fail("Thread safe test has failed!");
		} finally {
			
			if(ptrPrintJob != null)
				ptrPrintJob.cancel();
		}

	}

}