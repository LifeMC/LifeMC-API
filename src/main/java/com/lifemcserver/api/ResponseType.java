package com.lifemcserver.api;

public enum ResponseType {
	
	/**
	 * NO_USER - The user is not found on the database.
	 * WRONG_PASSWORD - The given password is wrong.
	 * MAX_TRIES - The rate limit was reached. Wait 3 minutes and retry.
	 * ERROR - A database error occured. Possible maintenance.
	 * SUCCESS - No errors occured and the process is finished successfully.
	 * UNKNOWN - Unknown response received from server, possibly a connection issue, or maintenance.
	 */
	NO_USER, WRONG_PASSWORD, MAX_TRIES, ERROR, SUCCESS, UNKNOWN;
	
}