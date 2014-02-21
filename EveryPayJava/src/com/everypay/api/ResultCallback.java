package com.everypay.api;

/**
 * Callback interface to use when request is completed. 
 * 
 * 
 * @author alex
 */
public interface ResultCallback {

	/**
	 * @param res true if result is positive, false if result is negative
	 */
	public void onRequestCompleted(boolean res);
	
}
