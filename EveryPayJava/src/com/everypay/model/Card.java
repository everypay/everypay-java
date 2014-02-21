package com.everypay.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.everypay.exceptions.ServerConnectionException;
import com.everypay.model.Token.TokenType;


/**
 *	Credit card class
 * 
 * @author EveryPay
 */


public class Card {

	private final static String JSON_LAST_FOUR = "last_four";
	private final static String JSON_HOLDER_NAME = "holder_name";
	private final static String JSON_TYPE = "type";
	private final static String JSON_EXPIRATION_MONTH = "expiration_month";
	private final static String JSON_EXPIRATION_YEAR = "expiration_year";
	private final static String JSON_TOKEN = "token";
	private final static String JSON_ERROR = "error";
	private final static String JSON_MESSAGE = "message";
	private final static String JSON_COUNTRY = "country";
	

	/** Types of credit cards */
	
	public static enum CardType {
		Visa, MasterCard, Other
	};

	private Token token;
	private String number="";
	private Date expiration;
	private String cvv="";
	private Customer customer;
	private CardType cardType;
	private String holderName="";
	private String country="";
	private String lastFour="";
	private int expirationMonth=1;
	private int expirationYear=1;

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLastFour() {
		return lastFour;
	}

	public void setLastFour(String lastFour) {
		this.lastFour = lastFour;
	}

	public int getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(int expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public int getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(int expirationYear) {
		this.expirationYear = expirationYear;
	}

	
	/**
	 * De-serialization of JSON response to credit Card object 
	 * 
	 * @param response
	 * @param card
	 * @throws JSONException
	 * @throws ServerConnectionException 
	 */
	
	public static double deserialize(String response, Card card)
			throws JSONException, ServerConnectionException {
		int jsonExpirationMonth, jsonExpirationYear;
		String jsonLastFour, jsonType, jsonHolderName, jsonCountry, jsonToken;

		if (card==null) return 0;

		JSONObject jsonCard = new JSONObject(response);

		if (jsonCard.has(JSON_EXPIRATION_YEAR)) 
		if (!jsonCard.isNull(JSON_EXPIRATION_YEAR))
		{
			jsonExpirationYear = jsonCard.getInt(JSON_EXPIRATION_YEAR);
			card.setExpirationYear(jsonExpirationYear);
		}

		if (jsonCard.has(JSON_LAST_FOUR)) 
		if (!jsonCard.isNull(JSON_LAST_FOUR))
		{
			jsonLastFour = jsonCard.optString(JSON_LAST_FOUR,"");
			card.setLastFour(jsonLastFour);
		}

		if (jsonCard.has(JSON_HOLDER_NAME)) 
		{
			jsonHolderName = jsonCard.optString(JSON_HOLDER_NAME,"");
			if (jsonHolderName.equalsIgnoreCase("null")) jsonHolderName="";
			try {
				card.setHolderName(URLDecoder.decode(jsonHolderName, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		if (jsonCard.has(JSON_EXPIRATION_MONTH)) 
		if (!jsonCard.isNull(JSON_EXPIRATION_YEAR))
		{
			jsonExpirationMonth = jsonCard.getInt(JSON_EXPIRATION_MONTH);
			card.setExpirationMonth(jsonExpirationMonth);
		}
		
		if (jsonCard.has(JSON_TYPE)) 
		if (!jsonCard.isNull(JSON_TYPE))
		{
			jsonType = jsonCard.optString(JSON_TYPE);
			if (jsonType.equalsIgnoreCase("Visa"))
				card.setCardType(CardType.Visa);
			else if (jsonType.equalsIgnoreCase("MasterCard"))
				card.setCardType(CardType.MasterCard);
		}

		if (jsonCard.has(JSON_COUNTRY)) 
		if (!jsonCard.isNull(JSON_COUNTRY))
		{
			jsonCountry = jsonCard.optString(JSON_COUNTRY);
			card.setCountry(jsonCountry);
		}
		
		if (jsonCard.has(JSON_TOKEN)) 
		if (!jsonCard.isNull(JSON_TOKEN))
		{
			jsonToken = jsonCard.optString(JSON_TOKEN);
			Token token = new Token(jsonToken, TokenType.Customer);
			card.setToken(token);
		}
		
		if (jsonCard.has(JSON_ERROR)) 
		if (!jsonCard.isNull(JSON_ERROR))
		{
			JSONObject error = jsonCard.getJSONObject(JSON_ERROR);
			String description = error.getString(JSON_MESSAGE);
			ServerConnectionException exception = new ServerConnectionException(description);
			throw exception;
		}

		

		return 0;
	}

}
