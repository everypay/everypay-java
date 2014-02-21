package com.everypay.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.everypay.exceptions.ServerConnectionException;
import com.everypay.model.Token.TokenType;

/**
 * Company class
 * 
 * @author EveryPay
 * 
 */

public class Company {

	private final static String JSON_SESSION_TOKEN = "session_token";
	private final static String JSON_TERMINAL = "card_terminal";
	private final static String JSON_DESCRIPTION = "description";
	private final static String JSON_SECRET_KEY = "secret_key";
	// private final static String JSON_SERIAL_NUMBER = "serial_number";
	private final static String JSON_COMPANY = "company";
	private final static String JSON_AFM = "afm";
	private final static String JSON_ADDRESS = "address";
	private final static String JSON_TITLE = "title";
	private final static String JSON_ADDRESS_AREA = "address_area";
	private final static String JSON_ADDRESS_NUM = "address_num";
	private final static String JSON_OCCUPATION = "occupation";
	private final static String JSON_FIXED_PHONE = "fixed_phone";
	private final static String JSON_POSTAL_CODE = "postal_code";
	private final static String JSON_CONTACT_PHONE = "contact_phone";
	private final static String JSON_WEBSITE = "website";
	private final static String JSON_REQUEST_TIME = "request_time_sec";
	private final static String JSON_ERROR = "error";
	private final static String JSON_MESSAGE = "message";

	private String name;
	private String key;
	private Token sessionToken;
	private String afm, address, title, area, companyDescription,
			addressNumber, occupation, fixedPhone, postalCode, contactPhone,
			webSIte;
	private double requestTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Token getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(Token token) {
		this.sessionToken = token;
	}

	public String getAfm() {
		return afm;
	}

	public void setAfm(String afm) {
		this.afm = afm;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCompanyDescription() {
		return companyDescription;
	}

	public void setCompanyDescription(String companyDescription) {
		this.companyDescription = companyDescription;
	}

	public String getAddressNumber() {
		return addressNumber;
	}

	public void setAddressNumber(String addressNumber) {
		this.addressNumber = addressNumber;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getFixedPhone() {
		return fixedPhone;
	}

	public void setFixedPhone(String fixedPhone) {
		this.fixedPhone = fixedPhone;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getWebSIte() {
		return webSIte;
	}

	public void setWebSIte(String webSIte) {
		this.webSIte = webSIte;
	}

	public double getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(double requestTime) {
		this.requestTime = requestTime;
	}

	public static Company deserialize(String response) throws JSONException,
			ServerConnectionException {
		Company company = new Company();

		JSONObject jsonCompany = new JSONObject(response);

		if (jsonCompany.has(JSON_SESSION_TOKEN))
			if (!jsonCompany.isNull(JSON_SESSION_TOKEN)) {
				company.setSessionToken(new Token(jsonCompany
						.getString(JSON_SESSION_TOKEN), TokenType.Company));
			}

		if (jsonCompany.has(JSON_TERMINAL))
			if (!jsonCompany.isNull(JSON_TERMINAL)) {
				JSONObject jsonTerminal = jsonCompany
						.getJSONObject(JSON_TERMINAL);

				if (jsonTerminal.has(JSON_SECRET_KEY))
					if (!jsonTerminal.isNull(JSON_SECRET_KEY)) {
						company.setKey(jsonTerminal.getString(JSON_SECRET_KEY));
					}

				if (jsonTerminal.has(JSON_COMPANY))
					if (!jsonTerminal.isNull(JSON_COMPANY)) {
						JSONObject jsonCompanyData = jsonTerminal
								.getJSONObject(JSON_COMPANY);

						if (jsonCompanyData.has(JSON_AFM))
							if (!jsonCompanyData.isNull(JSON_AFM)) {
								company.setAfm(jsonCompanyData
										.getString(JSON_AFM));
							}

						if (jsonCompanyData.has(JSON_ADDRESS))
							if (!jsonCompanyData.isNull(JSON_ADDRESS)) {
								company.setAddress(jsonCompanyData
										.getString(JSON_ADDRESS));
							}

						if (jsonCompanyData.has(JSON_TITLE))
							if (!jsonCompanyData.isNull(JSON_TITLE)) {
								company.setTitle(jsonCompanyData
										.getString(JSON_TITLE));
							}

						if (jsonCompanyData.has(JSON_ADDRESS_AREA))
							if (!jsonCompanyData.isNull(JSON_ADDRESS_AREA)) {
								company.setArea(jsonCompanyData
										.getString(JSON_ADDRESS_AREA));
							}

						if (jsonCompanyData.has(JSON_DESCRIPTION))
							if (!jsonCompanyData.isNull(JSON_DESCRIPTION)) {
								company.setCompanyDescription(jsonCompanyData
										.getString(JSON_DESCRIPTION));
							}

						if (jsonCompanyData.has(JSON_ADDRESS_NUM))
							if (!jsonCompanyData.isNull(JSON_ADDRESS_NUM)) {
								company.setAddressNumber(jsonCompanyData
										.getString(JSON_ADDRESS_NUM));
							}

						if (jsonCompanyData.has(JSON_OCCUPATION))
							if (!jsonCompanyData.isNull(JSON_OCCUPATION)) {
								company.setOccupation(jsonCompanyData
										.getString(JSON_OCCUPATION));
							}

						if (jsonCompanyData.has(JSON_FIXED_PHONE))
							if (!jsonCompanyData.isNull(JSON_FIXED_PHONE)) {
								company.setFixedPhone(jsonCompanyData
										.getString(JSON_FIXED_PHONE));
							}

						if (jsonCompanyData.has(JSON_POSTAL_CODE))
							if (!jsonCompanyData.isNull(JSON_POSTAL_CODE)) {
								company.setPostalCode(jsonCompanyData
										.getString(JSON_POSTAL_CODE));
							}

						if (jsonCompanyData.has(JSON_CONTACT_PHONE))
							if (!jsonCompanyData.isNull(JSON_CONTACT_PHONE)) {
								company.setContactPhone(jsonCompanyData
										.getString(JSON_CONTACT_PHONE));
							}

						if (jsonCompanyData.has(JSON_WEBSITE))
							if (!jsonCompanyData.isNull(JSON_WEBSITE)) {
								company.setWebSIte(jsonCompanyData
										.getString(JSON_WEBSITE));
							}

					}
			}

		if (jsonCompany.has(JSON_REQUEST_TIME))
			if (!jsonCompany.isNull(JSON_REQUEST_TIME)) {
				company.setRequestTime(jsonCompany.getDouble(JSON_REQUEST_TIME));
			}

		if (jsonCompany.has(JSON_ERROR))
			if (!jsonCompany.isNull(JSON_ERROR)) {
				JSONObject error = jsonCompany.getJSONObject(JSON_ERROR);
				String description = error.getString(JSON_MESSAGE);
				ServerConnectionException exception = new ServerConnectionException(
						description);
				throw exception;
			}

		return company;
	}

	public static boolean isError(String response) throws JSONException {

		JSONObject jsonCompany = new JSONObject(response);

		if (jsonCompany.has(JSON_ERROR))
			if (!jsonCompany.isNull(JSON_SESSION_TOKEN)) {
				return true;
			}

		return false;
	}

}
