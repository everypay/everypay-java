package com.everypay.exceptions;

import com.bugsense.trace.BugSenseHandler;
import com.everypay.api.Server;

public class ServerConnectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerConnectionException() {
		super();
	}

	public ServerConnectionException(String message) {
		super(message);
		if (Server.DEVELOPMENT_MODE)
			System.out.println("ServerConnectionException " + message);
	}

	public ServerConnectionException(Exception e) {
		super((e != null) ? e.getMessage() : "");
		if (e != null) {
			if (Server.DEVELOPMENT_MODE)
				System.out.println("ServerConnectionException "
						+ e.getMessage());
			if (Server.BUGSENSEON)
				BugSenseHandler.sendException(e);
		}

	}

}
