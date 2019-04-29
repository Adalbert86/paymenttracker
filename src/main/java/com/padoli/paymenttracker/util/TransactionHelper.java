package com.padoli.paymenttracker.util;

import org.apache.commons.lang3.math.NumberUtils;

import com.padoli.paymenttracker.model.Transaction;

/**
 * 
 * @author vojtech
 *
 */
public class TransactionHelper {

	public static boolean isCodeValid(String code) {

		if (code == null || code.length() != 3 || !code.matches("[a-zA-Z]+"))
			return false;

		return true;
	}

	public static boolean isAmountValid(Double amount) {

		return amount == null ? false : true;
	}

	public static boolean isAmountValid(String amount) {

		if (amount == null || amount.isEmpty() || !NumberUtils.isNumber(amount))
			return false;

		// assume a reasonable length
		if (amount.length() > 100)
			return false;

		return true;
	}

	public static boolean isTransactionValid(Transaction t) {

		if (t == null)
			return false;

		String code = t.getCurrencyCode();
		Double amount = t.getAmount();

		if (!isCodeValid(code) || !isAmountValid(amount))
			return false;

		return true;

	}

}
