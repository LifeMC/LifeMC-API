package com.lifemcserver.api;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ApiResponse {
	
	private final String actualResponse;
	private final ResponseType responseType;
	private final Throwable[] errors;
	
	public ApiResponse(final String actualResponse, final ResponseType responseType, final Throwable... errors) {
		
		if(actualResponse == null) {
			
			throw new NullPointerException("Api response was null, but it should be != null");
			
		}
		
		this.actualResponse = actualResponse;
		
		if(responseType == null) {
			
			throw new NullPointerException("Response type must be != null");
			
		}
		
		this.responseType = responseType;
		
		if(errors != null && errors.length > 0) {
			
			this.errors = errors;
			
		} else {
			
			this.errors = null;
			
		}
		
	}
	
	public final String getAPIResponse() {
		
		return this.actualResponse;
		
	}
	
	public final ResponseType getResponseType() {
		
		return this.responseType;
		
	}
	
	public final Throwable[] getErrors() {
		
		return this.errors;
		
	}
	
	public final Throwable getError() {
		
		if(this.errors == null) {
			
			return null;
			
		}
		
		if(this.errors.length > 0) {
			
			return this.errors[0];
			
		} else {
			
			return null;
			
		}
		
	}
	
	public final void handleError() {
		
		if(this.getError() != null) {
			
			this.getError().printStackTrace();
			
		}
		
	}
	
	public final void handleError(final PrintWriter pw) {
		
		if(this.getError() != null) {
			
			this.getError().printStackTrace(pw);
			
		}
		
	}
	
	public final void handleError(final PrintStream ps) {
		
		if(this.getError() != null) {
			
			this.getError().printStackTrace(ps);
			
		}
		
	}
	
	public final void handleError(final Logger logger) {
		
		if(this.getError() != null) {
			
			logger.log(Level.SEVERE, this.actualResponse, this.getError());
			
		}
		
	}
	
}