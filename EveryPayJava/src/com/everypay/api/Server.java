package com.everypay.api;

import it.sauronsoftware.base64.Base64;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.text.html.HTML;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.everypay.exceptions.ServerConnectionException;
import com.everypay.model.Card;
import com.everypay.model.Card.CardType;
import com.everypay.model.Company;
import com.everypay.model.Customer;
import com.everypay.model.Payment;
import com.everypay.model.Payment.Status;
import com.everypay.model.Token;
import com.everypay.model.Token.TokenType;

/**
 * Communication class between java application and EveryPay service REST API
 * 
 * @author EveryPay
 * @version 1.1
 */

public class Server {

	/** EveryPay API server URL */

	// public static final String HOST = "api.testing.everypay.gr";
	// public static final String HOST = "176.9.114.244";

	 public static final String HOST = "https://api.everypay.gr:443/";
	 public static final String HOST_ = "api.everypay.gr";
	 public static final int PORT = 443;

//	public static final String HOST = "https://api-v1-test.everypay.gr:55555/";
//	public static final String HOST_ = "api-v1-test.everypay.gr";
//	public static final int PORT = 55555;

	 public HttpsURLConnection connection = null;

	/** JSON server response field names */

	public static final String API_SESSIONS = "sessions";
	public static final String API_TOKENS = "tokens";
	public static final String API_CUSTOMERS = "customers";
	public static final String API_PAYMENTS = "payments";
	public static final String API_PAYMENTS_REFUND = "payments/refund";
	public static final String API_RECEIPTS = "receipts";
	public static final String API_RECEIPTS_SEND = "receipts/send";
	public static final String PAR_CARD_NUMBER = "card_number";
	public static final String PAR_EXPIRATION_YEAR = "expiration_year";
	public static final String PAR_EXPIRATION_MONTH = "expiration_month";
	public static final String PAR_CVV = "cvv";
	public static final String PAR_FULL_NAME = "full_name";
	public static final String PAR_TOKEN = "token";
	public static final String PAR_EMAIL = "email";
	public static final String PAR_AMOUNT = "amount";
	public static final String PAR_CURRENCY = "currency";
	public static final String PAR_DESCRIPTION = "description";
	public static final String PAR_HOLDER_NAME = "holder_name";
	public static final String PAR_COUNTRY = "country";
	public static final String PAR_COUNT = "count";
	public static final String PAR_STATUS = "status";
	public static final String PAR_DATE_FROM = "date_from";
	public static final String PAR_DATE_TO = "date_to";
	public static final String PAR_OFFSET = "offset";
	public static final String PAR_USERNAME = "username";
	public static final String PAR_PASSWORD = "password";
	private final static String JSON_REQUEST_TIME = "request_time_sec";
	private final static String JSON_EMAIL_SENT = "send";

	/** Bugsens monitoring status */
	public static final boolean BUGSENSEON = true;

	/** Singleton class instance variable */
	private static Server helper;
	private Company defaultCompany;
	public static final boolean DEVELOPMENT_MODE = true;
	private double lastDelay=0;
	public String delayLog="";
	public double startTime = 0; 
	public java.security.cert.X509Certificate sert;

	/** REST API methods */
	private enum RequestMethod {
		GET, POST, PUT, DELETE
	};

	private TrustManager[] trustAllCerts;
	private SSLContext sc;	
	private HostnameVerifier allHostsValid;
	/**
	 * 
	 * @return singleton Server instance
	 */

	public static Server get() {

		if (helper == null)
			helper = new Server();

		return helper;
	}

	/**
	 * Signs HTTP request
	 * 
	 * @param connection
	 * @param company
	 */
	
	

	private void signRequest(HttpsURLConnection connection, Company company) {
		String credentials = company.getKey() + ":";
		String base64EncodedCredentials = Base64.encode(credentials);
		connection.addRequestProperty("Authorization", "Basic "
				+ base64EncodedCredentials);
	}

	public java.security.cert.X509Certificate getSert() {
		return sert;
	}

	public void setSert(java.security.cert.X509Certificate sert) {
		this.sert = sert;
	}

	public X509Certificate getServerSert()
	{
		try {
			 InputStream inStream =  new FileInputStream(new File("-.everypay.gr.crt"));
			 CertificateFactory cf;
			cf = CertificateFactory.getInstance("X.509");
			 X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
			 inStream.close();
			 return cert;
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	
	
	public Company authorize(String userName, String password, ResultCallback resultCallback)
			throws ServerConnectionException {

		Company company = null;

		HttpsURLConnection connection = null;
		try {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(PAR_USERNAME, userName);
			map.put(PAR_PASSWORD, password);

			connection = createSecureConnection(HOST + API_SESSIONS, company);
			preparePostQuery(connection, map, RequestMethod.POST);

			int serverResponseCode = connection.getResponseCode();

			for (Certificate certificate : connection.getServerCertificates()){
				System.out.println("certificate "+sert.getSigAlgName());
				System.out.println("certificate "+certificate.getType()+"  "+certificate.getPublicKey().getAlgorithm());
			}

			String responseString = getResponse(serverResponseCode, connection);
			if (DEVELOPMENT_MODE)
				System.out.println("Authorization:\n" + responseString);
			if (DEVELOPMENT_MODE)
				System.out.println("Content-encoding:\n"
						+ connection.getContentEncoding());

			company = Company.deserialize(responseString);
			boolean res = false;
			if (company != null)
				if (company.getSessionToken() != null
						&& company.getKey() != null)
					if ((company.getSessionToken().getValue().length() > 0)
							&& (company.getKey().length() > 0)) {
						res = true;
					}
			if (resultCallback!=null)
			resultCallback.onRequestCompleted(res);
		} catch (IOException e) {
			e.printStackTrace();
			resultCallback.onRequestCompleted(false);
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			resultCallback.onRequestCompleted(false);
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return company;

	}

	public boolean isAuthorized(Company company)
			throws ServerConnectionException {

		boolean res = false;

		HttpsURLConnection connection = null;
		try {

			connection = createSecureConnection(HOST + API_SESSIONS + "/"
					+ company.getSessionToken().getValue(), company);
			connection.setRequestMethod("GET");

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);
			if (DEVELOPMENT_MODE)
				System.out.println("is authorized:\n" + responseString);
			if (DEVELOPMENT_MODE)
				System.out.println("Content-encoding:\n"
						+ connection.getContentEncoding());

			res = !Company.isError(responseString);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return res;
	}


	public void logout(Company company, ResultCallback resultCallback)
			throws ServerConnectionException {

		boolean res = false;

		HttpsURLConnection connection = null;
		try {

			connection = createSecureConnection(HOST + API_SESSIONS + "/"
					+ company.getSessionToken().getValue(), company);
			connection.setRequestMethod("DELETE");

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);
			if (DEVELOPMENT_MODE)
				System.out.println("is authorized:\n" + responseString);
			if (DEVELOPMENT_MODE)
				System.out.println("Content-encoding:\n"
						+ connection.getContentEncoding());

			if (resultCallback!=null)
				resultCallback.onRequestCompleted(false);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

	}

	
	
//	/**
//	 * Creates credit card token and stores it in the card object
//	 * 
//	 * @param company
//	 * @param card
//	 * @throws ServerConnectionException
//	 */
//
//	public void createCardToken(Company company, Card card, Payment payment)
//			throws ServerConnectionException {
//		defaultCompany = company;
//		HttpsURLConnection connection = null;
//		try {
//
//			int year = card.getExpirationYear();
//			int month = card.getExpirationMonth();
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put(PAR_CARD_NUMBER, card.getNumber());
//			map.put(PAR_EXPIRATION_YEAR, Integer.toString(year - 1900));
//			map.put(PAR_EXPIRATION_MONTH, Integer.toString(month + 1));
//			map.put(PAR_CVV, card.getCvv());
//			map.put(PAR_HOLDER_NAME, "Test");
//			map.put(PAR_AMOUNT, payment.getAmountString());
//			// if (card.getHolderName() != null)
//			// map.put(PAR_HOLDER_NAME, card.getHolderName());
//			// if (card.getCountry() != null)
//			// map.put(PAR_COUNTRY, card.getCountry());
//			// System.out.println(year-1900+" year "+month +" month");
//
//			connection = createSecureConnection(HOST + API_TOKENS, company);
//			preparePostQuery(connection, map, RequestMethod.POST);
//
//			int serverResponseCode = connection.getResponseCode();
//			String responseString = getResponse(serverResponseCode, connection);
//			// if (DEVELOPMENT_MODE)
//			System.out.println(responseString);
//			if (serverResponseCode < 400) {
//				Token tok = Token.deserialized(responseString, TokenType.Card);
//				card.setToken(tok);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new ServerConnectionException(e);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			throw new ServerConnectionException(e);
//		} finally {
//			if (connection != null)
//				connection.disconnect();
//		}
//	}

	/**
	 * Looks for existed credit card token
	 * 
	 * @param company
	 * @param card
	 * @return returns {@link #com.everypay.model.Token}
	 * @throws ServerConnectionException
	 */

	public Token findCardToken(Company company, Card card)
			throws ServerConnectionException {
		defaultCompany = company;
		Token tok = null;
		HttpsURLConnection connection = null;
		try {

			connection = createSecureConnection(HOST + API_TOKENS + "/"
					+ card.getToken().getValue(), company);
			connection.setRequestMethod("GET");

			int serverResponseCode = connection.getResponseCode();
			String responseString = getResponse(serverResponseCode, connection);

			if (DEVELOPMENT_MODE)
				System.out.println(responseString);

			if (serverResponseCode < 400)
				try {
					JSONObject resp = new JSONObject(responseString);
					String token = resp.optString("token", "");
					Boolean used = resp.getBoolean("is_used");
					Boolean expired = resp.getBoolean("has_expired");
					String requested = resp.optString("date_created", "");
					JSONObject cardJson = resp.getJSONObject("card");
					String lastFour = cardJson.optString("last_four", "");
					String holderName = cardJson.optString("holder_name", "");
					String country = cardJson.optString("country", "");
					String type = cardJson.optString("type", "");
					card.setLastFour(lastFour);
					card.setHolderName(holderName);
					card.setCountry(country);
					if (type.equalsIgnoreCase("Visa"))
						card.setCardType(CardType.Visa);
					else if (type.equalsIgnoreCase("Mastercard"))
						card.setCardType(CardType.MasterCard);

					// if (token != null) {
					tok = new Token(token, TokenType.Card);
					tok.setUsed(used);
					tok.setExpired(expired);

//					tok.setDateRequested(new SimpleDateFormat(
//							"dd-MM-yyyy hh:mm:ss", Locale.ENGLISH)
//							.parse(requested));
					tok.setDateRequested(new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
							.parse(requested));

					card.setToken(tok);
					// }
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServerConnectionException(e);
				} catch (ParseException e) {
					e.printStackTrace();
					throw new ServerConnectionException(e);
				}

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return tok;

	}

	/**
	 * Requests for a list of customers associated with current company
	 * 
	 * @param company
	 * @param query
	 *            GET request query string
	 * @return ArrayList of {@link com.everypay.model.Customer} objects
	 * @throws ServerConnectionException
	 */

	private List<Customer> listCustomers(Company company, String query)
			throws ServerConnectionException {
		defaultCompany = company;
		ArrayList<Customer> customers = new ArrayList<Customer>();

		HttpsURLConnection connection = null;
		try {

			connection = createSecureConnection(HOST + API_CUSTOMERS + query,
					company);
			connection.setRequestMethod("GET");

			int serverResponseCode = connection.getResponseCode();
			String responseString = getResponse(serverResponseCode, connection);

			if (DEVELOPMENT_MODE)
				System.out.println(responseString);

			JSONObject jsonObject = new JSONObject(responseString);

			JSONArray array = jsonObject.getJSONArray("items");
			for (int i = 0; i < array.length(); i++) {
				String jsonCustomer = array.optString(i);
				customers.add(Customer.deserialize(jsonCustomer, null));
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return customers;

	}

	/**
	 * Default request for a list of customers, associated with current company
	 * 
	 * @param company
	 * @return ArrayList of {@link com.everypay.model.Customer} objects
	 * @throws ServerConnectionException
	 */

	public List<Customer> listCustomers(Company company)
			throws ServerConnectionException {
		return listCustomers(company, "");
	}

	/**
	 * Request for a list of customers, associated with current company using
	 * parameters
	 * 
	 * @param company
	 * @param count
	 *            is the number of records returned
	 * @param offset
	 *            offset from the beginning of the list
	 * @return ArrayList of {@link com.everypay.model.Customer} objects
	 * @throws ServerConnectionException
	 */

	public List<Customer> listCustomers(Company company, int count, int offset)
			throws ServerConnectionException {
		String countValue = "", offsetValue = "";
		try {
			countValue = (count == 0) ? null : URLEncoder.encode(
					Integer.toString(count), "UTF-8");
			offsetValue = (offset == 0) ? null : URLEncoder.encode(
					Integer.toString(offset), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		}

		String query = "";
		if (countValue == null && offsetValue != null)
			query = "?offset=" + offsetValue;
		else if (countValue != null && offsetValue == null)
			query = "?count=" + countValue;
		else if (countValue != null && offsetValue != null)
			query = "?count=" + countValue + "&offset" + offsetValue;

		return listCustomers(company, query);
	}

	/**
	 * Creates a new customer of current company using credit card data
	 * 
	 * @param company
	 * @param card
	 * @param customer
	 * @throws ServerConnectionException
	 */

	public void createCustomerWithCard(Company company, Card card,
			Customer customer) throws ServerConnectionException {
		defaultCompany = company;
		HttpsURLConnection connection = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(card.getExpiration());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(PAR_CARD_NUMBER, card.getNumber());
			map.put(PAR_EXPIRATION_YEAR, Integer.toString(year - 1900));
			map.put(PAR_EXPIRATION_MONTH, Integer.toString(month + 1));
			map.put(PAR_CVV, card.getCvv());
			map.put(PAR_FULL_NAME, customer.getName());
			if (customer.getDescription().length() > 0)
				map.put(PAR_DESCRIPTION, customer.getDescription());
			if (customer.getEmail().length() > 0)
				map.put(PAR_EMAIL, customer.getEmail());

			connection = createSecureConnection(HOST + API_CUSTOMERS, company);

			preparePostQuery(connection, map, RequestMethod.POST);

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);

			if (DEVELOPMENT_MODE)
				System.out.println("Create customer:\n" + responseString);
			Customer.deserialize(responseString, customer, null);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

	}

	/**
	 * Creates a new customer of current company using credit card token
	 * 
	 * @param company
	 * @param card
	 * @param customer
	 * @throws ServerConnectionException
	 */

	public void createCustomerWithCardToken(Company company, Card card,
			Customer customer) throws ServerConnectionException {
		defaultCompany = company;

		HttpsURLConnection connection = null;
		try {

			HashMap<String, String> map = new HashMap<String, String>();
			if (card.getToken() != null)
				map.put(PAR_TOKEN, card.getToken().getValue());
			map.put(PAR_FULL_NAME, customer.getName());
			if (customer.getDescription().length() > 0)
				map.put(PAR_DESCRIPTION, customer.getDescription());
			if (customer.getEmail().length() > 0)
				map.put(PAR_EMAIL, customer.getEmail());

			connection = createSecureConnection(HOST + API_CUSTOMERS, company);
			preparePostQuery(connection, map, RequestMethod.POST);

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);
			if (DEVELOPMENT_MODE)
				System.out.println("Create customer with card token:\n"
						+ responseString);
			Customer.deserialize(responseString, customer, null);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

	}

	/**
	 * Seeking for a customer associated with current company
	 * 
	 * @param company
	 * @param customer
	 * @return true if customer is exists otherwise returns false
	 * @throws ServerConnectionException
	 */

	public boolean findCustomer(Company company, Customer customer)
			throws ServerConnectionException {
		boolean res = false;
		defaultCompany = company;

		HttpsURLConnection connection = null;
		try {

			if (customer.getToken() == null)
				throw new ServerConnectionException();

			connection = createSecureConnection(HOST + API_CUSTOMERS + "/"
					+ customer.getToken().getValue(), company);
			connection.setRequestMethod("GET");

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);

			if (DEVELOPMENT_MODE)
				System.out.println("Find customer:\n" + responseString);
			res = Customer.deserialize(responseString, customer, null);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return res;
	}

	/**
	 * Updates customer data using current state of the customer and card
	 * objects
	 * 
	 * @param company
	 * @param customer
	 * @param card
	 * @throws ServerConnectionException
	 */

	public void updateCustomer(Company company, Customer customer, Card card)
			throws ServerConnectionException {
		defaultCompany = company;
		HttpsURLConnection connection = null;
		try {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(PAR_DESCRIPTION, customer.getDescription());
			if (customer.getName().length() > 0)
				map.put(PAR_FULL_NAME, customer.getName());
			if (customer.getEmail().length() > 0)
				map.put(PAR_EMAIL, customer.getEmail());

			if (card != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(card.getExpiration());
				int year = cal.get(Calendar.YEAR);
				int month = cal.get(Calendar.MONTH);

				map.put(PAR_CARD_NUMBER, card.getNumber());
				map.put(PAR_EXPIRATION_YEAR, Integer.toString(year - 1900));
				map.put(PAR_EXPIRATION_MONTH, Integer.toString(month + 1));
				map.put(PAR_CVV, card.getCvv());
			}

			connection = createSecureConnection(HOST + API_CUSTOMERS + "/"
					+ customer.getToken().getValue(), company);
			preparePostQuery(connection, map, RequestMethod.PUT);

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);
			if (DEVELOPMENT_MODE)
				System.out.println("Update customer:\n" + responseString);
			Customer.deserialize(responseString, customer, card);

		} catch (IOException e) {
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

	}

	/**
	 * Updates token of customer's credit card
	 * 
	 * @param company
	 * @param customer
	 * @param token
	 * @throws ServerConnectionException
	 */

	public void updateCustomerCardToken(Company company, Customer customer,
			Token token) throws ServerConnectionException {
		defaultCompany = company;
		customer.getCard().setToken(token);
		HttpsURLConnection connection = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(PAR_TOKEN, token.getValue());

			connection = createSecureConnection(HOST + API_CUSTOMERS + "/"
					+ customer.getToken().getValue(), company);
			preparePostQuery(connection, map, RequestMethod.PUT);
			// connection.setRequestMethod("PUT");

			// connection.setRequestProperty(PAR_TOKEN, token.getValue());

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);
			if (DEVELOPMENT_MODE)
				System.out.println("Update customer card token:\n"
						+ responseString);
			Customer.deserialize(responseString, customer.getCard());

		} catch (IOException e) {
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

	}

	/**
	 * Deletes current customer
	 * 
	 * @param company
	 * @param customer
	 * @throws ServerConnectionException
	 */

	public void deleteCustomer(Company company, Customer customer)
			throws ServerConnectionException {
		defaultCompany = company;
		HttpsURLConnection connection = null;
		try {

			connection = createSecureConnection(HOST + API_CUSTOMERS + "/"
					+ customer.getToken().getValue(), company);
			connection.setRequestMethod("DELETE");

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);
			if (DEVELOPMENT_MODE)
				System.out.println("Delete customer:\n" + responseString);
			customer.setActive(false);

		} catch (IOException e) {
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

	}

	/**
	 * Requests list of payments
	 * 
	 * 
	 * @param company
	 * @param count
	 *            number of payments requested
	 * @param offset
	 *            sets the offset for the beginning of list
	 * @param dateFrom
	 *            start date
	 * @param dateTo
	 *            last date
	 * @param status
	 *            payments status {@link com.everypay.model.Payment.Status} (all
	 *            if null, Paid or Refunded)
	 * @return ArrayList of {@link com.everypay.model.Payment} objects
	 * @throws ServerConnectionException
	 */

	public List<Payment> listPayments(Company company, int count, int offset,
			Date dateFrom, Date dateTo, Status status)
			throws ServerConnectionException {
		defaultCompany = company;
		ArrayList<Payment> payments = new ArrayList<Payment>();

//		HttpsURLConnection 
		connection = null;
		try {

			StringBuilder params = new StringBuilder("?");

			boolean first = true;
			if (status != Status.all && status != null) {
				String statusValue = URLEncoder.encode(status.toString(),
						"UTF-8");
				params.append(PAR_STATUS + "=" + statusValue);
				first = false;
			}

			if (count != 0) {
				String countValue = URLEncoder.encode(count + "", "UTF-8");
				if (!first) {
					params.append("&");
				} else {
					first = false;
				}
				params.append(PAR_COUNT + "=" + countValue);
			}

			if (offset != 0) {
				String offsetValue = URLEncoder.encode(offset + "", "UTF-8");
				if (!first) {
					params.append("&");
				} else {
					first = false;
				}
				params.append(PAR_OFFSET + "=" + offsetValue);
			}

			if (dateFrom != null) {
				String dateFromString = new SimpleDateFormat("yyyy-MM-dd")
				.format(dateFrom);
				
//				String fromValue = URLEncoder.encode(
//						"" + (int) Math.floor(dateFrom.getTime() / 1000),
//						"UTF-8");
				if (!first) {
					params.append("&");
				} else {
					first = false;
				}
				params.append(PAR_DATE_FROM + "=" + dateFromString);
			}

			if (dateTo != null) {
				String dateToString = new SimpleDateFormat("yyyy-MM-dd")
				.format(dateTo);
//				String toValue = URLEncoder.encode(
//						"" + (int) Math.ceil(dateTo.getTime() / 1000), "UTF-8");
				if (!first) {
					params.append("&");
				} else {
					first = false;
				}
				params.append(PAR_DATE_TO + "=" + dateToString);
			}

			String paramsString = (first) ? "" : params.toString();

			connection = createSecureConnection(HOST + API_PAYMENTS
					+ paramsString, company);
			connection.addRequestProperty("Accept-Encoding", "gzip,deflate");
			connection.setRequestMethod("GET");

//			if (DEVELOPMENT_MODE)
//				System.out.println("List of payments in "+status.toString()+": ===============================================\n");

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);
			if (DEVELOPMENT_MODE)
				System.out.println("List of payments:\n" + responseString);
			if (DEVELOPMENT_MODE)
				System.out.println("Content-encoding:\n"
						+ connection.getContentEncoding());

			if (responseString.length()!=0){
			JSONObject jsonResponse = new JSONObject(responseString);
			if (jsonResponse.has("items")) {
				JSONArray jsonItems = jsonResponse.getJSONArray("items");
				for (int i = 0; i < jsonItems.length(); i++) {
					String jsonItem = jsonItems.optString(i);
					payments.add(Payment.deserialize(jsonItem, null));
				}
			}}

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return payments;

	}

	/**
	 * Requests list of payments with default parameters: 20 records with 0
	 * offset
	 * 
	 * @see #listPayments(Company, int, int, Date, Date, Status)
	 * @param company
	 * @return ArrayList of {@link com.everypay.model.Payment} objects
	 * @throws ServerConnectionException
	 */

	public List<Payment> listPayments(Company company)
			throws ServerConnectionException {
		return listPayments(company, 0, 0, null, null, null);
	}

	public boolean findPayment(Company company, Payment payment)
			throws ServerConnectionException {
		boolean res = false;
		if (payment.getToken() == null)
			return res;
		HttpsURLConnection connection = null;
		try {

			connection = createSecureConnection(HOST + API_PAYMENTS + "/"
					+ payment.getToken().getValue(), company);
			connection.setRequestMethod("GET");

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);

			if (DEVELOPMENT_MODE)
				System.out.println("Find payment:\n" + responseString);

			Payment.deserialize(responseString, payment, null);

			if (payment.getDateString().length() > 0)
				res = true;

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return res;

	}

	public boolean sendReceipt(Company company, Payment payment, String email, ResultCallback resultCallback)
			throws ServerConnectionException {
		boolean res = false;
		if (payment.getToken() == null)
			return res;
		HttpsURLConnection connection = null;
		try {

//			connection = createSecureConnection(HOST + API_RECEIPTS_SEND + "/"
//					+ payment.getToken().getValue()+"?email="+email, company);
//			connection.setRequestMethod("GET");

			
			connection = createSecureConnection(HOST + API_RECEIPTS_SEND + "/"
					+ payment.getToken().getValue(), company);

			HashMap<String, String> map = new HashMap<String, String>();
				map.put(PAR_EMAIL, email);

			// map.put("test", "test");

			preparePostQuery(connection, map, RequestMethod.POST);
			
			
			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);
			System.out.println("response "+responseString);
			JSONObject jsonEmailResponse = new JSONObject(responseString);
			res = jsonEmailResponse.getBoolean(JSON_EMAIL_SENT);


			if (DEVELOPMENT_MODE)
				System.out.println("Send receipt for token "+payment.getToken().getValue()+":\n" + responseString);

//			if (payment.getDateString().length() > 0)
//				res = true;

			if (resultCallback != null)
			resultCallback.onRequestCompleted(res);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (Exception e){
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} 
		finally {
			if (connection != null)
				connection.disconnect();
		}

		
		
		
		return res;

	}
	
	
	
	/**
	 * Creates new payment with current credit card and stores token to this
	 * payment object
	 * 
	 * @param company
	 * @param card
	 * @param payment
	 * @throws ServerConnectionException
	 */

	public void newPaymentWithCard(Company company, Card card, Payment payment)
			throws ServerConnectionException {
		if (card == null || payment == null || company == null) throw new ServerConnectionException("NULL method parameter.");
		
		
		HttpsURLConnection connection = null;
		try {
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(card.getExpiration());
			int year = card.getExpirationYear();
			int month = card.getExpirationMonth();

			connection = createSecureConnection(HOST + API_PAYMENTS, company);
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(PAR_CARD_NUMBER, card.getNumber());
			map.put(PAR_EXPIRATION_YEAR, Integer.toString(year - 1900));
			map.put(PAR_EXPIRATION_MONTH, Integer.toString(month + 1));
			map.put(PAR_CVV, card.getCvv());
			map.put(PAR_AMOUNT, payment.getAmountString());
			map.put(PAR_CURRENCY, "eur");
			map.put(PAR_DESCRIPTION, payment.getDescription());
			map.put(PAR_HOLDER_NAME, card.getHolderName());

			preparePostQuery(connection, map, RequestMethod.POST);

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);

			delayLog += "Response received "+ String.format("%1$,.3f", (new Date()).getTime()/1000.0 - startTime) +"\n"; 
			if (DEVELOPMENT_MODE)
				System.out.println("New payment with card:\n"
						+ responseString);

			Payment.deserialize(responseString, payment, null);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	/**
	 * Creates new payment with credit card token and stores payment token to
	 * this payment object
	 * 
	 * @param company
	 * @param token
	 * @param payment
	 * @return true if payment succeeds and false otherwise
	 * @throws ServerConnectionException
	 */

	public boolean newPaymentWithCardToken(Company company, String token,
			Payment payment) throws ServerConnectionException {
		boolean res = false;
		HttpsURLConnection connection = null;
		try {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(PAR_TOKEN, token);
			map.put(PAR_AMOUNT, payment.getAmountString());
			if ("eur".length() > 0)
				map.put(PAR_CURRENCY, "eur");
			if (payment.getDescription().length() > 0)
				map.put(PAR_DESCRIPTION, payment.getDescription());

			// httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
			// "ISO-8859-2"));

			connection = createSecureConnection(HOST + API_PAYMENTS, company);
			connection.setRequestProperty("Accept-Charset", "ISO-8859-2");

			preparePostQuery(connection, map, RequestMethod.POST);

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);

			if (DEVELOPMENT_MODE)
				System.out.println("New payment with card token:\n"
						+ responseString);
			res = Payment.deserialize(responseString, payment, null);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		return res;
	}

	/**
	 * Creates new payment with customer token and stores payment token to this
	 * payment object
	 * 
	 * @param company
	 * @param customer
	 * @param payment
	 * @throws ServerConnectionException
	 */

	public void newPaymentWithCustomerToken(Company company, Customer customer,
			Payment payment) throws ServerConnectionException {

		HttpsURLConnection connection = null;
		try {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(PAR_TOKEN, customer.getToken().getValue());
			map.put(PAR_AMOUNT, payment.getAmountString());
			if ("eur".length() > 0)
				map.put(PAR_CURRENCY, "eur");
			if (payment.getDescription().length() > 0)
				map.put(PAR_DESCRIPTION, payment.getDescription());

			connection = createSecureConnection(HOST + API_PAYMENTS, company);
			connection.setRequestProperty("Accept-Charset", "ISO-8859-2");

			preparePostQuery(connection, map, RequestMethod.POST);

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);

			if (DEVELOPMENT_MODE)
				System.out.println("New payment with customer token:\n"
						+ responseString);
			Payment.deserialize(responseString, payment, null);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

	}

	/**
	 * Refunds payment
	 * 
	 * @param company
	 * @param payment
	 * @param amount
	 *            is refund amount
	 * @throws ServerConnectionException
	 */

	public void refundPayment(Company company, Payment payment, double amount, String description)
			throws ServerConnectionException {

		HttpsURLConnection connection = null;
		try {

			connection = createSecureConnection(HOST + API_PAYMENTS_REFUND
					+ "/" + payment.getToken().getValue(), company);

			HashMap<String, String> map = new HashMap<String, String>();
			if (amount != 0.0)
				map.put(PAR_AMOUNT, Integer.toString((int) (amount * 100)));
			if (description!=null)
				if (description.length()>0)
					map.put(PAR_DESCRIPTION, description);
			
			// map.put("test", "test");

			preparePostQuery(connection, map, RequestMethod.POST);

			int serverResponseCode = connection.getResponseCode();

			String responseString = getResponse(serverResponseCode, connection);

			if (DEVELOPMENT_MODE)
				System.out.println("Refund payment:\n" + responseString);
			Payment.deserialize(responseString, payment, null);

		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		}

	}

	/**
	 * Refund payment with a default amount equal to the amount of the payment
	 * 
	 * @param company
	 * @param payment
	 * @throws ServerConnectionException
	 */

	public void refundPayment(Company company, Payment payment, String description)
			throws ServerConnectionException {

		refundPayment(company, payment, 0, description);
	}

	/**
	 * Returns the default company
	 * 
	 * @return default {@link com.everypay.model.Company}
	 */

	public Company getDefaultCompany() {
		return defaultCompany;
	}

	/*
	 * Creates secure HTTPS connection
	 */

	private HttpsURLConnection createSecureConnection(String urlString,
			final Company company) throws ServerConnectionException {

		if (trustAllCerts==null || sc == null){
		trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				if (sert!=null){
					System.out.println("Our certificate is "+sert.getSigAlgName());
					return new java.security.cert.X509Certificate[] {sert};
					}
				else
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
		} };
		// Install the all-trusting trust manager
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		}
		// Create all-trusting host name verifier
//		if (allHostsValid == null){
//		allHostsValid = new HostnameVerifier() {
//
//			@Override
//			public boolean verify(String arg0, SSLSession session) {
////				 HostnameVerifier hv =
////				            HttpsURLConnection.getDefaultHostnameVerifier();
////				        return hv.verify(HOST_, session);				
//				return true;
//			}
//		};
//		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//		}
		HttpsURLConnection connection = null;
		try {
			connection = (HttpsURLConnection) (new URL(urlString))
					.openConnection();
				
			connection.setReadTimeout(60000);
			connection.setConnectTimeout(60000);
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// connection.setRequestProperty("User-Agent",
			// "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");

			// connection.setRequestProperty("http.protocol.expect-continue",
			// "false");
			connection.setUseCaches(false);
			if (company != null)
				signRequest(connection, company);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerConnectionException(e);
		}

		return connection;
	}

	private String getResponse(int serverResponseCode,
			HttpsURLConnection connection) {
		InputStream is = null;
		InputStream gzipStream = null;

		if (serverResponseCode >= 400) {
			is = connection.getErrorStream();
		} else {
			try {
				is = connection.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}

		StringBuilder sb = new StringBuilder();
		Scanner scanner = null;

		if (connection.getContentEncoding() != null) {
			// System.out.println(connection.getContentEncoding());
			if (connection.getContentEncoding().equals("gzip")) {
				try {
					gzipStream = new GZIPInputStream(is);
				} catch (IOException e) {
					e.printStackTrace();
				}
				scanner = new Scanner(gzipStream, "UTF-8");
			} else {
				scanner = new Scanner(is, "UTF-8");
			}
		} else {
			scanner = new Scanner(is, "UTF-8");
		}

		scanner.useDelimiter("\\A");
		while (scanner.hasNext())
			sb.append(scanner.next());

		scanner.close();

		
		setLastDelay(getDelay(sb.toString()));

//		try {
//			is.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return sb.toString();
	}
	
	private double getDelay(String response)
	{
		try {
			JSONObject json = new JSONObject(response);
			if (json.has(JSON_REQUEST_TIME)) 
			if (!json.isNull(JSON_REQUEST_TIME))
			{
//				JSONObject error = new JSONObject(jsonCard.getString(JSON_REQUEST_TIME));
				double delay = json.getDouble(JSON_REQUEST_TIME);
				System.out.printf("Last delay %e\n", delay);
				return delay;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void preparePostQuery(HttpsURLConnection connection,
			Map<String, String> pars, RequestMethod method) {

		StringBuilder res = new StringBuilder();
		for (String key : pars.keySet())
			try {
				res.append(((res.length() != 0) ? "&" : "") + key + "="
						+ URLEncoder.encode(pars.get(key), "UTF-8"));
				// System.out.println("-----"+key+"-----");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		String queryString = res.toString();

		connection.setRequestProperty("Content-Length", "" + queryString.length());
		
		System.out.println(queryString);
		DataOutputStream output = null;
		OutputStream os = null;
		try {
			connection.setRequestMethod(method.name());
			connection.setDoOutput(true);
//			connection.setUseCaches(true);
			 os = connection.getOutputStream();
			output = new DataOutputStream(os);
			output.writeBytes(queryString);
			 if (((new Date()).getTime()/1000.0 - startTime) <100)
			delayLog += "Request sent "+ String.format("%1$,.3f", (new Date()).getTime()/1000.0 - startTime) +"\n"; 
		} catch (IOException e) {
			System.out.println("ups... ");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ups... ");
			e.printStackTrace();
		} finally {
				try {
					if (output!=null)
					output.close();
					if (os!=null)
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}

	}

	public double getLastDelay() {
		return lastDelay;
	}

	public void setLastDelay(double lastDelay) {
		this.lastDelay = lastDelay;
	}

	public class NullHostNameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {
			// Log.i("RestUtilImpl", "Approving certificate for " + hostname);
			return true;
		}
	}



}
