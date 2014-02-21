package com.everypay.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.everypay.exceptions.ServerConnectionException;
import com.everypay.model.Token.TokenType;

/**
 * Class represents single payment
 * 
 * @author EveryPay
 *
 */

public class Payment {
	public static enum Status {
		Paid, Refunded, PartiallyRefunded, All
	}

	private final static String JSON_DESCRIPTION = "description";
	private final static String JSON_DATE_CREATED = "date_created";
	private final static String JSON_TOKEN = "token";
	private final static String JSON_AMOUNT = "amount";
	private final static String JSON_FEE_AMOUNT = "fee_amount";
	private final static String JSON_CURRENCY = "currency";
	private final static String JSON_STATUS = "status";
	private final static String JSON_CARD = "card";
	private final static String JSON_CUSTOMER = "customer";
	private final static String JSON_REFUNDED = "refunded";
	private final static String JSON_REFUND_AMOUNT = "refund_amount";
	private final static String JSON_ERROR = "error";
	private final static String JSON_MESSAGE = "message";

	private Date date;
	private String dateString;
	private int amount;
	private int feeAmount;
	private Card card;
	private Token token;
	private String description;
	private String currency;
	private Status status;
	private boolean refunded = false;
	private int refundAmount;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getAmountString() {
		return Integer.toString(amount);
	}

	public String getAmountPresentation() {
		return String.format("%.2f", amount / 100.0);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFeeAmount() {
		return feeAmount;
	}

	public String getFeeAmountString() {
		return Integer.toString(feeAmount);
	}

	public void setFeeAmount(int feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getFeeAmountPresentation() {
		return String.format("%.2f", feeAmount / 100.0);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isRefunded() {
		return refunded;
	}

	public void setRefunded(boolean refunded) {
		this.refunded = refunded;
	}

	public int getRefundAmount() {
		return refundAmount;
	}

	public String getRefundAmountString() {
		return Integer.toString(refundAmount);
	}

	public String getRefundAmountPresentation() {
		return String.format("%.2f", refundAmount / 100.0);
	}

	public void setRefundAmount(int refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	/**
	 * De-serialization of JSON response to payment object
	 * 
	 * @param response
	 * @param payment
	 * @param card
	 * @return
	 * @throws JSONException
	 * @throws ServerConnectionException 
	 */
	
	public static boolean deserialize(String response, Payment payment,
			Card card) throws JSONException, ServerConnectionException {
		Integer jsonAmount, jsonFeeAmount, jsonRefundAmount;
		Boolean jsonRefunded;
		String jsonDescription, jsonDateCreated, jsonStatus, jsonCard, jsonCurrency, jsonToken, jsonCustomer;

		boolean res = true;

		JSONObject jsonPayment = new JSONObject(response);

		if (jsonPayment.has(JSON_DESCRIPTION)) {
			jsonDescription = jsonPayment.optString(JSON_DESCRIPTION);
			if (jsonDescription.equalsIgnoreCase("null"))
				jsonDescription = "";
			try {
				payment.setDescription(URLDecoder.decode(jsonDescription, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		if (jsonPayment.has(JSON_DATE_CREATED)) {
			jsonDateCreated = jsonPayment.optString(JSON_DATE_CREATED);
			payment.setDateString(jsonDateCreated);
			
			String format = "dd-MM-yyyy hh:mm:ss";
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(format);
			    Date date = formatter.parse(jsonDateCreated);
				payment.setDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		    
		}

		if (jsonPayment.has(JSON_AMOUNT)) {
			jsonAmount = jsonPayment.getInt(JSON_AMOUNT);
			payment.setAmount(jsonAmount);
		}

		if (jsonPayment.has(JSON_FEE_AMOUNT)) {
			jsonFeeAmount = jsonPayment.getInt(JSON_FEE_AMOUNT);
			payment.setFeeAmount(jsonFeeAmount);
		}

		if (jsonPayment.has(JSON_TOKEN)) {
			jsonToken = jsonPayment.optString(JSON_TOKEN);
			if (payment.getToken() != null)
				payment.getToken().setValue(jsonToken);
			else {
				Token token = new Token(jsonToken, TokenType.Payment);
				payment.setToken(token);

			}
		}

		if (jsonPayment.has(JSON_STATUS)) {
			jsonStatus = jsonPayment.optString(JSON_STATUS);
			if (jsonStatus.equalsIgnoreCase("Paid"))
				payment.setStatus(Status.Paid);
			if (jsonStatus.equalsIgnoreCase("Refunded"))
				payment.setStatus(Status.Refunded);
			if (jsonStatus.equalsIgnoreCase("Partially Refunded"))
				payment.setStatus(Status.PartiallyRefunded);
		}

		if (jsonPayment.has(JSON_REFUNDED)) {
			jsonRefunded = jsonPayment.getBoolean(JSON_REFUNDED);
			payment.setRefunded(jsonRefunded);
		}

		if (jsonPayment.has(JSON_CURRENCY)) {
			jsonCurrency = jsonPayment.optString(JSON_CURRENCY);
			payment.setCurrency(jsonCurrency);
		}

		if (jsonPayment.has(JSON_REFUND_AMOUNT)) {
			jsonRefundAmount = jsonPayment.getInt(JSON_REFUND_AMOUNT);
			payment.setRefundAmount(jsonRefundAmount);
		}

		if (jsonPayment.has(JSON_CARD)) {
			jsonCard = jsonPayment.optString(JSON_CARD,"");
			card = payment.getCard();
			if (card == null) {
				card = new Card();
				payment.setCard(card);
			}
			Card.deserialize(jsonCard, card);
		}

		if (jsonPayment.has(JSON_CUSTOMER)) {
			jsonCustomer = jsonPayment.optString(JSON_CUSTOMER,"");
				card = new Card();
				Customer.deserialize(jsonCustomer, card);
				payment.setCard(card);
		}
		
		
		if (jsonPayment.has(JSON_ERROR)) {
			JSONObject error = jsonPayment.getJSONObject(JSON_ERROR);
			String description = error.getString(JSON_MESSAGE);
			ServerConnectionException exception = new ServerConnectionException(description);
			res = false;
			throw exception;
		}

		return res;
	}

	/**
	 * De-serialization of JSON response
	 * 
	 * 
	 * @param response
	 * @param card
	 * @return de-serialized Payment object
	 * @throws JSONException
	 * @throws ServerConnectionException 
	 */
	
	public static Payment deserialize(String response, Card card)
			throws JSONException, ServerConnectionException {
		Payment payment = new Payment();
		boolean res = deserialize(response, payment, card);
		return (res) ? payment : null;
	}

}
