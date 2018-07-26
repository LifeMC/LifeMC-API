package com.lifemcserver.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class LifeAPI {
	
	/**
	 * API Thread to execute some heavy tasks.
	 * (Such as web connections. Causes some spikes and performance losses.)
	 */
	protected static ExecutorService apiThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setPriority(Thread.MAX_PRIORITY).setNameFormat("LifeAPI - API Thread").build());
	
	/**
	 * The current LifeAPI Instance
	 */
	protected static LifeAPI Instance = null;
	
	/**
	 * The user cache map to store users.
	 */
	protected ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<String, User>();
	
	/**
	 * Initializes the LifeAPI
	 */
	public LifeAPI() {
		
		try {
			// for loading classes at startup - makes it faster when requesting first time.
			Class.forName("com.lifemcserver.api.LifeAPI");
			Class.forName("com.lifemcserver.api.ResponseType");
			Class.forName("com.lifemcserver.api.User");
			Class.forName("com.lifemcserver.api.Utils");	
		} catch(Throwable throwable) { throwable.printStackTrace(); }
		
		try {
			apiThread.execute( () -> {
				
				// for initializing http client for caching. - makes it faster when using first time.
				Utils.connectTo("https://www.lifemcserver.com/registeredPlayerCount.php");
				
			});
		} catch(Throwable throwable) { throwable.printStackTrace(); }
		
		Instance = this;
		
	}
	
	/**
	 * Gets the current LifeAPI Instance.
	 * 
	 * @return LifeAPI Instance - The LifeAPI Instance
	 */
	public LifeAPI getInstance() {
		
		return Instance;
		
	}
	
	/**
	 * Gets or creates a user by username and password.
	 * If user is already initialized / created by this method,
	 * The method returns the already initialized user from the cache.
	 * If user is not initialized / created or it's not found in the cache,
	 * The method creates a new user object and returns it.
	 * The method return type is Object.
	 * So, you MUST check if the response from method is instanceof User.
	 * 
	 * @param String name - The user name.
	 * @param String password - The password of the user.
	 * 
	 * @returns (Object) ResponseType response - if any errors occured.
	 * @returns (Object) User u - if validation successed and user object created successfully.
	 */
	public void getUser(final String name, final String password, final Consumer<Object> consumer) {
		
		apiThread.execute( () -> {
			
			if(userMap.containsKey(name)) {
				
				consumer.accept(userMap.get(name));
				
			} else {
				
				String jsonResponse = "";
				
				try {
					
					jsonResponse = Utils.connectTo("https://www.lifemcserver.com/loginAPI.php?" + name + "&password=" + password);
					
				} catch(Exception ex) {
					
					ex.printStackTrace();
					
				} catch(Throwable tw) {
					
					tw.printStackTrace();
					
				}
				
				ResponseType response = null;
				
				if(jsonResponse.equalsIgnoreCase("NO_USER")) {
					
					response = ResponseType.NO_USER;
					consumer.accept(response);
					
				}
				
				else if(jsonResponse.equalsIgnoreCase("WRONG_PASSWORD")) {
					
					response = ResponseType.WRONG_PASSWORD;
					consumer.accept(response);
					
				}
				
				else if(jsonResponse.equalsIgnoreCase("MAX_TRIES")) {
					
					response = ResponseType.MAX_TRIES;
					consumer.accept(response);
					
				}
				
				else if(jsonResponse.equalsIgnoreCase("ERROR")) {
					
					response = ResponseType.ERROR;
					consumer.accept(response);
					
				}
				
				else if(jsonResponse.equalsIgnoreCase("SUCCESS")) {
					
					response = ResponseType.SUCCESS;
					
				}
				
				else {
					
					response = ResponseType.ERROR;
					consumer.accept(response);
					
				}
				
				User u = new User(name, password);
				userMap.put(name, u);
				
				consumer.accept(u);
				
			}
			
		});
		
	}
	
	/**
	 * Gets a user from the user cache by user name.
	 * Password is not required for this method.
	 * The user MUST be ALREADY initialized with getUser(String name, String password) method.
	 * If its not initialized already with username and password, this method returns null.
	 * So, always check != null when using this method.
	 * If you store the passwords, you should use getUser(String name, String password) method instead of this.
	 * 
	 * @param String name - The user name of the cached user.
	 * 
	 * @returns User - if success
	 * @returns null - if user not found in the cache
	 */
	@CheckReturnValue
	@Nullable
	public User getUser(String name) {

		if(userMap.containsKey(name)) {
			
			return userMap.get(name);
			
		} else {
			
			return null;
			
		}
		
	}
	
	/**
	 * Gets the current registered player count of the LifeMC.
	 * May be null, so you should always check != null.
	 * 
	 * @return Integer - Registered player count
	 * @return null - If any connection issues occured
	 */
	@CheckReturnValue
	@Nullable
	public void getRegisteredPlayerCount(final Consumer<Integer> consumer) {
		
		apiThread.execute( () -> {
			
			String response = null;
			
			try {
				
				response = Utils.connectTo("https://www.lifemcserver.com/registeredPlayerCount.php");
				
			} catch(Exception ex) {
				
				ex.printStackTrace();
				
			} catch(Throwable tw) {
				
				tw.printStackTrace();
				
			}
			
			if(response != null) {
				
				Integer playerCount = Utils.convertToInteger(response);
				consumer.accept(playerCount);
				
			} else {
				
				consumer.accept(null);
				
			}
			
		});
		
	}
	
}