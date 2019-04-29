package com.padoli.paymenttracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.padoli.paymenttracker.exception.PaymentTrackerException;
import com.padoli.paymenttracker.model.Transaction;
import com.padoli.paymenttracker.util.TransactionHelper;

/**
 * 
 * @author vojtech
 * 
 *         PaymentTracker has all the core methods for dealing with the
 *         requirements of this homework. It will work great as a singleton
 *         object for our purposes.
 *
 */
public class PaymentTracker {

	// consolidated map of transactions
	private Map<String, Double> mapTransactions = Collections.synchronizedMap(new HashMap<String, Double>());

	public void insertTransaction(Transaction transaction) {

		if (!TransactionHelper.isTransactionValid(transaction))
			throw new PaymentTrackerException("Invalid transaction");

		synchronized (mapTransactions) {

			String currencyCode = transaction.getCurrencyCode().toUpperCase();
			double curAmount = mapTransactions.getOrDefault(currencyCode, (double) 0);
			curAmount += transaction.getAmount();

			mapTransactions.put(currencyCode, curAmount);

		}
	}

	public void insertTransaction(String code, double amount) {

		insertTransaction(new Transaction(code, amount));
	}

	public List<Transaction> getConsolidatedTransactions() {

		List<Transaction> res = new ArrayList<Transaction>();

		synchronized (mapTransactions) {
			if (mapTransactions != null) {

				Iterator<Map.Entry<String, Double>> entries = mapTransactions.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry<String, Double> entry = entries.next();

					String currencyCode = entry.getKey();
					double amount = entry.getValue();

					final double threshold = 0.001;

					if (Math.abs(amount) > threshold)
						res.add(new Transaction(currencyCode, amount));
				}
			}
		}

		return res;
	}

}
