package com.lifemcserver.api;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class LifeAPI {
	
	/**
	 * API Thread to execute some heavy tasks.
	 * (Such as web connections. Causes some spikes and performance losses.)
	 */
	protected static ExecutorService apiThread = null;
	
	/**
	 * The current LifeAPI Instance
	 */
	protected static LifeAPI instance = null;
	
	/**
	 * Is initializing LifeAPI first time?
	 */
	private static boolean firstTime = true;
	
	/**
	 * The user cache map to store users.
	 */
	protected ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<String, User>();
	
	/**
	 * Initializes the LifeAPI
	 */
	public LifeAPI() {
		
		if(instance != null) {
			throw new IllegalStateException("Only one instance allowed for LifeAPI.");
		}
		
		if(firstTime) {
			
			apiThread = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setPriority(Thread.MAX_PRIORITY).setNameFormat("LifeAPI - API Thread").build());
			
			firstTime = false;
			
			try {
				// for loading classes at startup - makes it faster when requesting first time.
				Class.forName("java.lang.String");
				Class.forName("com.lifemcserver.api.LifeAPI");
				Class.forName("com.lifemcserver.api.ResponseType");
				Class.forName("com.lifemcserver.api.User");
				Class.forName("java.net.URL");
				Class.forName("javax.net.ssl.HttpsURLConnection");
				Class.forName("java.io.InputStream");
				Class.forName("java.io.BufferedInputStream");
				Class.forName("java.lang.StringBuilder");
				Class.forName("java.io.BufferedReader");
				Class.forName("java.lang.Integer");
				Class.forName("java.lang.Double");
				Class.forName("java.lang.Math");
				Class.forName("java.text.DecimalFormat");
				Class.forName("java.text.DecimalFormatSymbols");
				Class.forName("java.util.Locale");
				Class.forName("com.lifemcserver.api.Utils");
			} catch(Throwable throwable) { throwable.printStackTrace(); }
			
			try {
				
				// initialize the most used URL's for caching.
				ArrayList<URL> uriList = new ArrayList<URL>();
				
				uriList.add(new URL("https://www.lifemcserver.com/registeredPlayerCount.php"));
				uriList.add(new URL("https://www.lifemcserver.com/loginAPI.php"));
				uriList.add(new URL("https://www.lifemcserver.com/loginAPI.php?demo&password=demo"));
				uriList.add(new URL("https://www.lifemcserver.com/API.php"));
				uriList.add(new URL("https://www.lifemcserver.com/API.php?demo&password=demo"));
				
				int addedIndex = 0;
				for(URL url : uriList) {
					
					Utils.urlCache.put(url.toString(), url);
					addedIndex++;
					
				}
				
				if(addedIndex < 5 || Utils.urlCache.size() < 5) {
					
					throw new IllegalStateException("Failed to initialize URI's for caching!");
					
				}
				
				apiThread.execute( () -> {
					try {
						// for initializing http client for caching. - makes it faster when using first time.
						Utils.connectTo("https://www.lifemcserver.com/registeredPlayerCount.php");
						
						// initialize the default demo account for caching.
						this.getUserBlocking("demo", "demo");
					} catch(Throwable throwable) { throwable.printStackTrace(); }
				});
			} catch(Throwable throwable) { throwable.printStackTrace(); }
			
			instance = this;
			
		}
		
	}
	
	/**
	 * Initializes the LifeAPI with the given thread pool size.
	 * @deprecated The recommended value is the default value of available cpu cores.
	 * @see {@link Runtime#getRuntime.availableProcessors()}
	 */
	@Deprecated
	public LifeAPI(final int poolSize) {
		
		if(instance != null) {
			throw new IllegalStateException("Only one instance allowed for LifeAPI.");
		}
		
		if(firstTime) {
			
			apiThread = Executors.newFixedThreadPool(poolSize, new ThreadFactoryBuilder().setPriority(Thread.MAX_PRIORITY).setNameFormat("LifeAPI - API Thread").build());
			
			firstTime = false;
			
			try {
				// for loading classes at startup - makes it faster when requesting first time.
				Class.forName("java.lang.String");
				Class.forName("com.lifemcserver.api.LifeAPI");
				Class.forName("com.lifemcserver.api.ResponseType");
				Class.forName("com.lifemcserver.api.User");
				Class.forName("java.net.URL");
				Class.forName("javax.net.ssl.HttpsURLConnection");
				Class.forName("java.io.InputStream");
				Class.forName("java.io.BufferedInputStream");
				Class.forName("java.lang.StringBuilder");
				Class.forName("java.io.BufferedReader");
				Class.forName("java.lang.Integer");
				Class.forName("java.lang.Double");
				Class.forName("java.lang.Math");
				Class.forName("java.text.DecimalFormat");
				Class.forName("java.text.DecimalFormatSymbols");
				Class.forName("java.util.Locale");
				Class.forName("com.lifemcserver.api.Utils");
			} catch(Throwable throwable) { throwable.printStackTrace(); }
			
			try {
				
				// initialize the most used URL's for caching.
				ArrayList<URL> uriList = new ArrayList<URL>();
				
				uriList.add(new URL("https://www.lifemcserver.com/registeredPlayerCount.php"));
				uriList.add(new URL("https://www.lifemcserver.com/loginAPI.php"));
				uriList.add(new URL("https://www.lifemcserver.com/loginAPI.php?demo&password=demo"));
				uriList.add(new URL("https://www.lifemcserver.com/API.php"));
				uriList.add(new URL("https://www.lifemcserver.com/API.php?demo&password=demo"));
				
				int addedIndex = 0;
				for(URL url : uriList) {
					
					Utils.urlCache.put(url.toString(), url);
					addedIndex++;
					
				}
				
				if(addedIndex < 5 || Utils.urlCache.size() < 5) {
					
					throw new IllegalStateException("Failed to initialize URI's for caching!");
					
				}
				
				apiThread.execute( () -> {
					try {
						// for initializing http client for caching. - makes it faster when using first time.
						Utils.connectTo("https://www.lifemcserver.com/registeredPlayerCount.php");
						
						// initialize the default demo account for caching.
						this.getUserBlocking("demo", "demo");
					} catch(Throwable throwable) { throwable.printStackTrace(); }
				});
			} catch(Throwable throwable) { throwable.printStackTrace(); }
			
			instance = this;
			
		}
		
	}
		
	/**
	 * Gets the current LifeAPI Instance.
	 * 
	 * @return LifeAPI Instance - The LifeAPI Instance
	 */
	public final LifeAPI getInstance() {
		
		return instance;
		
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
	public final void getUser(final String name, final String password, final Consumer<Object> consumer) {
		
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
					
					response = ResponseType.UNKNOWN;
					consumer.accept(response);
					
				}
				
				User u = new User(name, password);
				userMap.put(name, u);
				
				consumer.accept(u);
				
			}
			
		});
		
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
	private final Object getUserBlocking(final String name, final String password) {
		
		if(userMap.containsKey(name)) {
			
			return userMap.get(name);
			
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
				
				response = ResponseType.UNKNOWN;
				return response;
				
			}
			
			User u = new User(name, password);
			userMap.put(name, u);
			
			return u;
			
		}
		
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
	public final User getUser(final String name) {

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
	public final void getRegisteredPlayerCount(final Consumer<Integer> consumer) {
		
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