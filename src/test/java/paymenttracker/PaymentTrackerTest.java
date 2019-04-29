package paymenttracker;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.padoli.paymenttracker.PaymentTracker;
import com.padoli.paymenttracker.exception.PaymentTrackerException;
import com.padoli.paymenttracker.model.Transaction;


public class PaymentTrackerTest {

	private List<Transaction> sampleInput1() {

		List<Transaction> res = new ArrayList<>();
		res.add(new Transaction("USD", 1000.0));
		res.add(new Transaction("HKD", 100.0));
		res.add(new Transaction("USD", -100.0));
		res.add(new Transaction("RMB", 2000.0));
		res.add(new Transaction("HKD", 200.0));
		return res;
	}

	/**
	 * 
	 * Will need to correlate with sampleInput1
	 */
	private Map<String, Transaction> sampleExpectedOutput1() {
		Map<String, Transaction> mapRes = new HashMap<>();
		mapRes.put("USD", new Transaction("USD", 900.0));
		mapRes.put("RMB", new Transaction("RMB", 2000.0));
		mapRes.put("HKD", new Transaction("HKD", 300.0));
		return mapRes;
	}

	/**
	 * This is a helper method to compare two collections. The list is expected to
	 * have a representation in the map. Map is better to lookup values in a
	 * constant time.
	 * 
	 * @return boolean
	 */
	private boolean compareTransCollections(List<Transaction> list, Map<String, Transaction> refMap) {

		for (Transaction t : list) {

			if (!refMap.containsKey(t.getCurrencyCode()) || !refMap.get(t.getCurrencyCode()).equals(t))
				return false;
		}

		return true;
	}

	@Test
	public void testInsertTransaction() {

		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction("USD", 100.0));
		ptr.insertTransaction(new Transaction("USD", 100.0));
		ptr.insertTransaction(new Transaction("AUS", 8.5));

		List<Transaction> consTransactions = ptr.getConsolidatedTransactions();

		assertNotNull(consTransactions);
		assertEquals(consTransactions.size(), 2);
	}

	@Test
	public void testInsertNothing() {

		PaymentTracker ptr = new PaymentTracker();

		List<Transaction> consTransactions = ptr.getConsolidatedTransactions();

		assertNotNull(consTransactions);
		assertEquals(consTransactions.size(), 0);
	}

	@Test
	public void testInsertSampleTransactions() {

		PaymentTracker ptr = new PaymentTracker();
		for (Transaction tSample : sampleInput1()) {

			ptr.insertTransaction(tSample);
		}

		List<Transaction> consTransactions = ptr.getConsolidatedTransactions();

		assertNotNull(consTransactions);
		assertEquals(consTransactions.size(), sampleExpectedOutput1().size());
		assertTrue(compareTransCollections(consTransactions, sampleExpectedOutput1()));
	}

	@Test(expected = PaymentTrackerException.class)
	public void testInsertInvalidTransaction1() {

		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction("USD", null));
	}

	@Test(expected = PaymentTrackerException.class)
	public void testInsertInvalidTransaction2() {

		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction(null, null));
	}

	@Test(expected = PaymentTrackerException.class)
	public void testInsertInvalidTransaction3() {

		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction(null, 12.0));
	}

	@Test(expected = PaymentTrackerException.class)
	public void testInsertInvalidTransaction4() {

		// submitting invalid Currency Code
		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction("", 12.0));
	}

	@Test(expected = PaymentTrackerException.class)
	public void testInsertInvalidTransaction5() {

		// submitting invalid Currency Code digits
		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction("123", 12.0));
	}

	@Test(expected = PaymentTrackerException.class)
	public void testInsertInvalidTransaction6() {

		// submitting invalid Currency Code
		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction("U3D", 12.0));
	}

	@Test(expected = PaymentTrackerException.class)
	public void testInsertInvalidTransaction7() {

		// submitting invalid too long
		// I assume the longest currency code may be 3 letters
		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction("AMERICKYDOLAR", 1.0));
	}

	@Test
	public void testInsertUpperAndLowerCaseCode() {

		// the payment tracker needs to support insert of of the currency code in both
		// upper and lower case
		// the output needs to be in upper case

		PaymentTracker ptr = new PaymentTracker();
		ptr.insertTransaction(new Transaction("USD", 50.1));
		ptr.insertTransaction(new Transaction("usd", 80.1));

		ptr.insertTransaction(new Transaction("czk", 90.0));

		ptr.insertTransaction(new Transaction("EuR", 100.0));

		ptr.insertTransaction(new Transaction("CHF", 200.0));

		List<Transaction> consTransactions = ptr.getConsolidatedTransactions();

		assertNotNull(consTransactions);
		assertEquals(4, consTransactions.size());

		Map<String, Transaction> mapRes = new HashMap<>();
		mapRes.put("USD", new Transaction("USD", 130.2));
		mapRes.put("CZK", new Transaction("CZK", 90.0));
		mapRes.put("CHF", new Transaction("CHR", 200.0));
		mapRes.put("EUR", new Transaction("EUR", 100.0));

		for (Transaction t : consTransactions) {

			if (t.getCurrencyCode().equals("czk") || t.getCurrencyCode().equals("usd"))
				fail("Contains lowercase letters!");

		}
	}


}
