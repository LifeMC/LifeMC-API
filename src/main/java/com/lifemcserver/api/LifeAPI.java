package com.lifemcserver.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class LifeAPI {

	protected static ExecutorService apiThread = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("LifeAPI - API Thread").build());
	protected static LifeAPI Instance = null;
	
	protected ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<String, User>();
	
	public LifeAPI() {
		
		Instance = this;
		
	}
	
	@Nullable
	public Object getUser(String name, String password) {

		if(userMap.containsKey(name)) {
			
			return userMap.get(name);
			
		} else {
			
			String jsonResponse = "";
			
			try {
				
				jsonResponse = User.connectTo("https://www.lifemcserver.com/loginAPI.php?" + name + "&password=" + password);
				
			} catch(Exception ex) {
				
				ex.printStackTrace();
				
			} catch(Throwable tw) {
				
				tw.printStackTrace();
				
			}
			
			ResponseType response = null;
			
			if(jsonResponse.equalsIgnoreCase("NO_USER")) {
				response = ResponseType.NO_USER;
				return response;
			}
			
			else if(jsonResponse.equalsIgnoreCase("WRONG_PASSWORD")) {
				response = ResponseType.WRONG_PASSWORD;
				return response;
			}
			
			else if(jsonResponse.equalsIgnoreCase("MAX_TRIES")) {
				response = ResponseType.MAX_TRIES;
				return response;
			}
			
			else if(jsonResponse.equalsIgnoreCase("ERROR")) {
				response = ResponseType.ERROR;
				return response;
			}
			
			else if(jsonResponse.equalsIgnoreCase("SUCCESS")) {
				response = ResponseType.SUCCESS;
			}
			
			else {
				
				response = ResponseType.ERROR;
				return response;
				
			}
			
			User u = new User(name, password);
			userMap.put(name, u);
			
			return u;
			
		}
		
	}
	
	@Nullable
	public User getUser(String name) {

		if(userMap.containsKey(name)) {
			
			return userMap.get(name);
			
		} else {
			
			return null;
			
		}
		
	}
	
}