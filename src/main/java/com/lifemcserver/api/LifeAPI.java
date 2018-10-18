package com.lifemcserver.api;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
	protected Cache<String, User> userCache = CacheBuilder.newBuilder()
			.build();
	
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
			} catch(final Throwable throwable) { throwable.printStackTrace(); }
			
			try {
				
				// initialize the most used URL's for caching.
				
				Utils.urlCache.put("https://www.lifemcserver.com/registeredPlayerCount.php",
								   new URL("https://www.lifemcserver.com/registeredPlayerCount.php"));
				
				Utils.urlCache.put("https://www.lifemcserver.com/loginAPI.php",
						   new URL("https://www.lifemcserver.com/loginAPI.php"));
				
				Utils.urlCache.put("https://www.lifemcserver.com/loginAPI.php?demo&password=demo",
						   new URL("https://www.lifemcserver.com/loginAPI.php?demo&password=demo"));
				
				Utils.urlCache.put("https://www.lifemcserver.com/API.php",
						   new URL("https://www.lifemcserver.com/API.php"));
				
				Utils.urlCache.put("https://www.lifemcserver.com/API.php?demo&password=demo",
						   new URL("https://www.lifemcserver.com/API.php?demo&password=demo"));
							
				apiThread.execute( () -> {
					try {
						// for initializing http client for caching. - makes it faster when using first time.
						Utils.connectTo("https://www.lifemcserver.com/registeredPlayerCount.php");
						
						// initialize the default demo account for caching.
						this.getUser("demo", "demo", (user) -> {
							
							user.updateInfos((response) -> {
								
								if(response.equals(ResponseType.SUCCESS)) {
									
									final User u = this.getUser("demo");
									
									if(u == null) {
										
										throw new NullPointerException("Error occured when creating demo account!");
										
									}
									
								}
								
							});
							
						}, (error) -> {
							
							error.getError().printStackTrace();
							
						});
					} catch(final Throwable throwable) { throwable.printStackTrace(); }
				});
			} catch(final Throwable throwable) { throwable.printStackTrace(); }
			
			instance = this;
			
		}
		
	}
	
	/**
	 * Initializes the LifeAPI
	 */
	public LifeAPI() {
		
		this(Runtime.getRuntime().availableProcessors());
		
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
	 * @return (Object) ResponseType response - if any errors occured.
	 * @return (Object) User u - if validation successed and user object created successfully.
	 * 
	 * @deprecated Please use {@link com.lifemcserver.api.LifeAPI#getUser(String, String, Consumer, Consumer) getUser(Username, Password, Consumer success, Consumer error)}.
	 */
	@Deprecated
	public final void getUser(final String name, final String password, final Consumer<Object> consumer) {
		
		apiThread.execute( () -> {
			
			final User cachedUser = this.userCache.getIfPresent(name);
			
			if(cachedUser != null) {
				
				consumer.accept(cachedUser);
				
			} else {
				
				String jsonResponse = "";
				
				try {
					
					jsonResponse = Utils.connectTo("https://www.lifemcserver.com/loginAPI.php?" + name + "&password=" + password);
					
				} catch(final Exception ex) {
					
					ex.printStackTrace();
					
				} catch(final Throwable tw) {
					
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
				
				final User u = new User(name, password);
				this.userCache.put(name, u);
				
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
	 * 
	 * @param String name - The user name.
	 * @param String password - The password of the user.
	 * 
	 * @param Consumer<User> success - Callback, called when non-null user is returned.
	 * @param Consumer<ApiResponse> error - Callback, called when any errors occur.
	 * 
	 */
	public final void getUser(final String name, final String password, final Consumer<User> success, final Consumer<ApiResponse> error) {
		
		apiThread.execute( () -> {
			
			String jsonResponse = "";
			
			try {
				
				final User cachedUser = this.userCache.getIfPresent(name);
				
				if(cachedUser != null) {
					
					success.accept(cachedUser);
					
				} else {
										
					try {
						
						jsonResponse = Utils.connectTo("https://www.lifemcserver.com/loginAPI.php?" + name + "&password=" + password);
						
					} catch(final Exception ex) {
						
						ex.printStackTrace();
						
					} catch(final Throwable tw) {
						
						tw.printStackTrace();
						
					}
					
					ResponseType response = null;
					
					if(jsonResponse.equalsIgnoreCase("NO_USER")) {
						
						response = ResponseType.NO_USER;
						error.accept(new ApiResponse(jsonResponse, response, new IllegalStateException("No user found with the given name!")));
						
					}
					
					else if(jsonResponse.equalsIgnoreCase("WRONG_PASSWORD")) {
						
						response = ResponseType.WRONG_PASSWORD;
						error.accept(new ApiResponse(jsonResponse, response, new IllegalStateException("Wrong password!")));
						
					}
					
					else if(jsonResponse.equalsIgnoreCase("MAX_TRIES")) {
						
						response = ResponseType.MAX_TRIES;
						error.accept(new ApiResponse(jsonResponse, response, new IllegalStateException("Max tries reached!")));
						
					}
					
					else if(jsonResponse.equalsIgnoreCase("ERROR")) {
						
						response = ResponseType.ERROR;
						error.accept(new ApiResponse(jsonResponse, response, new IllegalStateException("Error response from API! Error: " + response)));
						
					}
					
					else if(jsonResponse.equalsIgnoreCase("SUCCESS")) {
						
						response = ResponseType.SUCCESS;
						
					}
					
					else {
						
						response = ResponseType.UNKNOWN;
						error.accept(new ApiResponse(jsonResponse, response, new IllegalStateException("Unknown response from API!")));
						
					}
					
					final User u = new User(name, password);
					this.userCache.put(name, u);
					
					success.accept(u);
					
				}
				
			} catch(final Throwable throwable) {
				
				error.accept(new ApiResponse(jsonResponse, ResponseType.UNKNOWN, throwable));
				
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
	 * @return User - if success
	 * @return null - if user not found in the cache
	 */
	public final User getUser(final String name) {
		
		return this.userCache.getIfPresent(name);
		
	}
	
	/**
	 * Gets the current registered player count of the LifeMC.
	 * May be null, so you should always check != null.
	 * 
	 * @return Integer - Registered player count
	 * @return null - If any connection issues occured
	 * 
	 * @deprecated Use {@link com.lifemcserver.api.LifeAPI#getRegisteredPlayerCount(Consumer, Consumer) getRegisteredPlayerCount(Consumer<Integer>, Consumer<Throwable>)}.
	 */
	@Deprecated
	public final void getRegisteredPlayerCount(final Consumer<Integer> consumer) {
		
		apiThread.execute( () -> {
			
			String response = null;
			
			try {
				
				response = Utils.connectTo("https://www.lifemcserver.com/registeredPlayerCount.php");
				
			} catch(final Exception ex) {
				
				ex.printStackTrace();
				
			} catch(final Throwable tw) {
				
				tw.printStackTrace();
				
			}
			
			if(response != null) {
				
				final Integer playerCount = Utils.convertToInteger(response);
				consumer.accept(playerCount);
				
			} else {
				
				consumer.accept(null);
				
			}
			
		});
		
	}
	
	/**
	 * Gets the current registered player count of the LifeMC.
	 * May be null, so you should always check != null.
	 * 
	 * @return Integer - Registered player count
	 * @return null - If any connection issues occured
	 * 
	 */
	public final void getRegisteredPlayerCount(final Consumer<Integer> consumer, final Consumer<Throwable> error) {
		
		apiThread.execute( () -> {
			
			try {
				
				final String response = Utils.connectTo("https://www.lifemcserver.com/registeredPlayerCount.php");
				
				final Integer playerCount = Utils.convertToInteger(response);
				consumer.accept(playerCount);
				
			} catch(final Throwable tw) {
				
				error.accept(tw);
				
			}
			
		});
		
	}
	
}