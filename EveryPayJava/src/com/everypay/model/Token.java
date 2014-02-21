package com.everypay.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.everypay.exceptions.ServerConnectionException;

/**
 * Token class
 * 
 *
 * @author EveryPay, 2013
 *
 */

public class Token {

	public static enum TokenType {
		Company, Card, Customer, Payment
	}

	private String value;
	private Date expiration;
	private Date dateRequested;
	private TokenType type;
	private boolean used = false;
	private boolean expired = false;

	private final static String JSON_IS_USED = "is_used";
	private final static String JSON_HAS_EXPIRED = "has_expired";
	private final static String JSON_TOKEN = "token";
	private final static String JSON_ERROR = "error";
	private final static String JSON_MESSAGE = "message";

	public Token(String value, TokenType type) {

		this.value = value;
		this.type = type;

	}

	/**
	 * De-serializes JSON response to Token object
	 * 
	 * @param response
	 * @param type
	 * @return
	 * @throws JSONException
	 * @throws ServerConnectionException 
	 */
	
	public static Token deserialized(String response, TokenType type)
			throws JSONException, ServerConnectionException {
		String token = "";
 		boolean used = false, expired = false;

		JSONObject resp = new JSONObject(response);
		if (resp.has(JSON_TOKEN)) {
			token = resp.getString(JSON_TOKEN);
		}
		if (resp.has(JSON_IS_USED)) {
			used = resp.getBoolean(JSON_IS_USED);
		}
		if (resp.has(JSON_HAS_EXPIRED)) {
			expired = resp.getBoolean(JSON_HAS_EXPIRED);
		}
		if (resp.has(JSON_ERROR)) {
			JSONObject error = resp.getJSONObject(JSON_ERROR);
			String description = error.getString(JSON_MESSAGE);
			ServerConnectionException exception = new ServerConnectionException(description);
			throw exception;
		}

		Token tok = new Token(token, type);
		tok.setUsed(used);
		tok.setExpired(expired);

		return tok;
	}

	/**
	 * 
	 * @return token value
	 */
	
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets token value
	 * 
	 * @param token
	 */

	public void setValue(String token) {
		this.value = token;
	}

	/**
	 * 
	 * @return token expiration date
	 */
	
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * Sets token expiration date
	 * 
	 * @param expiration
	 */
	
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	/**
	 * 
	 * @return token type
	 */
	
	public TokenType getType() {
		return type;
	}

	/**
	 * Sets token type
	 * 
	 * @param type
	 */
	
	public void setType(TokenType type) {
		this.type = type;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	/**
	 * 
	 * @return true if token is expired
	 */
	
	public boolean isExpired() {
		return expired;
	}

	/**
	 * Sets expired property
	 * 
	 * @param expired
	 */
	
	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	/**
	 * 
	 * @return date when token was requested
	 */
	
	public Date getDateRequested() {
		return dateRequested;
	}

	/**
	 * Sets data when token as requested
	 * 
	 * @param dateRequested
	 */
	
	public void setDateRequested(Date dateRequested) {
		this.dateRequested = dateRequested;
	}

}
