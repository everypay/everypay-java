package com.everypay.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.everypay.exceptions.ServerConnectionException;
import com.everypay.model.Token.TokenType;

/**
 * 
 * @author EveryPay, 2013
 * 
 */

public class Customer {

	private final static String JSON_DESCRIPTION = "description";
	private final static String JSON_EMAIL = "email";
	private final static String JSON_DATE_CREATED = "date_created";
	private final static String JSON_FULL_NAME = "full_name";
	private final static String JSON_TOKEN = "token";
	private final static String JSON_IS_ACTIVE = "is_active";
	private final static String JSON_DATE_MODIFIED = "date_modified";
	private final static String JSON_CARD = "card";
	private final static String JSON_ERROR = "error";
	private final static String JSON_MESSAGE = "message";

	private String name = "";
	private String description = "";
	private Date dateCreated;
	private String dateCreatedString;
	private String dateModifiedString;
	private boolean active;
	private Date dateModified;
	private Card card;
	private Token token;
	private String email = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public String getDateCreatedString() {
		return dateCreatedString;
	}

	public void setDateCreatedString(String dateCreatedString) {
		this.dateCreatedString = dateCreatedString;
	}

	public String getDateModifiedString() {
		return dateModifiedString;
	}

	public void setDateModifiedString(String dateModifiedString) {
		this.dateModifiedString = dateModifiedString;
	}

	/**
	 * De-serialization of JSON response to Customer object
	 * 
	 * @param response
	 *            JSON response
	 * @param customer
	 *            object link to de-serialize
	 * @param card
	 * @return
	 * @throws JSONException
	 * @throws ServerConnectionException
	 */

	public static boolean deserialize(String response, Customer customer,
			Card card) throws JSONException, ServerConnectionException {
		String jsonDescription, jsonEmail, jsonDateCreated, jsonFullName, jsonToken, jsonDateModified;
		boolean jsonIsActive, res = false;
		;

		if (customer == null)
			return false;

		JSONObject jsonCustomer = new JSONObject(response);

		if (jsonCustomer.has(JSON_ERROR))
			if (!jsonCustomer.isNull(JSON_ERROR)) {
				// String message="";
				// JSONObject jsonError =
				// jsonCustomer.getJSONObject(JSON_ERROR);
				// if (jsonError.has(JSON_MESSAGE))
				// message = jsonError.getString(JSON_MESSAGE);
				//
				// throw new ServerConnectionException(message);
				return false;
			}

		if (jsonCustomer.has(JSON_DESCRIPTION))
			if (!jsonCustomer.isNull(JSON_DESCRIPTION)) {
				jsonDescription = jsonCustomer.optString(JSON_DESCRIPTION, "");
				try {
					customer.setDescription(URLDecoder.decode(jsonDescription,
							"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

		if (jsonCustomer.has(JSON_EMAIL))
			if (!jsonCustomer.isNull(JSON_EMAIL)) {
				jsonEmail = jsonCustomer.optString(JSON_EMAIL, "");
				customer.setEmail(jsonEmail);
			}

		if (jsonCustomer.has(JSON_DATE_CREATED))
			if (!jsonCustomer.isNull(JSON_DATE_CREATED)) {
				jsonDateCreated = jsonCustomer.optString(JSON_DATE_CREATED, "");
				customer.setDateCreatedString(jsonDateCreated);
			}

		if (jsonCustomer.has(JSON_FULL_NAME))
			if (!jsonCustomer.isNull(JSON_FULL_NAME)) {
				jsonFullName = jsonCustomer.optString(JSON_FULL_NAME, "");
				try {
					customer.setName(URLDecoder.decode(jsonFullName, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

		if (jsonCustomer.has(JSON_TOKEN))
			if (!jsonCustomer.isNull(JSON_TOKEN)) {
				jsonToken = jsonCustomer.optString(JSON_TOKEN, "");
				if (customer.getToken() == null) {
					Token token = new Token(jsonToken, TokenType.Customer);
					customer.setToken(token);
				} else {
					customer.getToken().setValue(jsonToken);
				}
			}

		if (jsonCustomer.has(JSON_IS_ACTIVE))
			if (!jsonCustomer.isNull(JSON_IS_ACTIVE)) {
				jsonIsActive = jsonCustomer.getBoolean(JSON_IS_ACTIVE);
				customer.setActive(jsonIsActive);
			}

		if (jsonCustomer.has(JSON_DATE_MODIFIED))
			if (!jsonCustomer.isNull(JSON_DATE_MODIFIED)) {
				jsonDateModified = jsonCustomer.optString(JSON_DATE_MODIFIED,
						"");
				customer.setDateModifiedString(jsonDateModified);
			}

		if (jsonCustomer.has(JSON_CARD))
			if (!jsonCustomer.isNull(JSON_CARD)) {
				if (card == null) {
					card = new Card();
				}
				Card.deserialize(jsonCustomer.optString(JSON_CARD, ""), card);
				customer.setCard(card);
			}

		if (jsonCustomer.has(JSON_ERROR))
			if (!jsonCustomer.isNull(JSON_ERROR)) {
				JSONObject error = jsonCustomer.getJSONObject(JSON_ERROR);
				String description = error.getString(JSON_MESSAGE);
				ServerConnectionException exception = new ServerConnectionException(
						description);
				throw exception;
			}

		return true;
	}

	/**
	 * De-serialization of JSON response to credit Card object
	 * 
	 * @param response
	 * @param card
	 * @return
	 * @throws JSONException
	 * @throws ServerConnectionException
	 */

	public static Customer deserialize(String response, Card card)
			throws JSONException, ServerConnectionException {
		Customer customer = new Customer();
		deserialize(response, customer, card);
		return customer;
	}

}
