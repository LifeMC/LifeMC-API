package com.lifemcserver.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.annotation.CheckReturnValue;

import org.json.JSONObject;

public final class User {

	private volatile String name = "demo";
	private volatile String password = "demo";
	
	private volatile int islandLevel;
	private volatile String money = "0.00";
	private volatile int credit;
	private volatile int ironsps;
	private volatile int diasps;
	private volatile int profileLike;
	private volatile String profileFollow = "-1";
	
	/**
	 * Creates a user object with the given user name and password.
	 * You should use the LifeAPI.getUser(String name, String password) instead of this constructor.
	 * 
	 * @param String name - The user name of the user.
	 * @param String password - The password of the user.
	 */
	public User(final String name, final String password) {
		
		this.name = name;
		this.password = password;
		
	}
	
	/**
	 * 
	 * Creates a user object with the given user name, password an the default values.
	 * You should use the LifeAPI.getUser(String name, String password) instead of this constructor.
	 * 
	 * @param name - The user name of the user.
	 * @param password - The password of the user.
	 * @param islandLevel - The island level of the user.
	 * @param money - The money of the user.
	 * @param credit - The credit of the user.
	 * @param ironsps - The iron spawner count of the user.
	 * @param diasps - The diamond block spawner count of the user.
	 * @param profileLike - The profile like count of the user.
	 * @param profileFollow - The profile follower count of the user.
	 */
	public User(final String name, final String password, final int islandLevel, String money, final int credit, final int ironsps, final int diasps, final int profileLike, String profileFollow) {
		
		this.name = name;
		this.password = password;
		
		this.islandLevel = islandLevel;
		this.money = money;
		this.credit = credit;
		this.ironsps = ironsps;
		this.diasps = diasps;
		this.profileLike = profileLike;
		this.profileFollow = profileFollow;
		
	}
	
	/**
	 * Gets the user name of the user.
	 * @return String name - The user name of the user.
	 */
	public final String getName() {
		
		return name;
		
	}
	
	/**
	 * Sets the user name of the user.
	 * @param String newName - The new user name of the user.
	 * @return User - Current user instance.
	 */
	public final User setName(final String newName) {
		
		this.name = newName;
		return this;
		
	}
	
	/**
	 * Sets the password of the user.
	 * @param String newPassword - The new password of the user.
	 * @return User - Current user instance.
	 */
	public final User setPassword(final String newPassword) {
		
		this.password = newPassword;
		return this;
		
	}
	
	/**
	 * Updates all infos of the user.
	 * You MUST call this method before getting any values.
	 * Otherwise, values are not updated, wrong or may be null.
	 * 
	 * @return ResponseType - The response of the API request.
	 */
	public final void updateInfos(final Consumer<ResponseType> consumer) {
		
		LifeAPI.apiThread.execute( () -> {
			
			String jsonResponse = "";
			
			try {
				
				jsonResponse = Utils.connectTo("https://www.lifemcserver.com/API.php?" + name + "&password=" + password);		
				
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
			
			else {
				
				if(jsonResponse.contains("userInfo")) {
					
					response = ResponseType.SUCCESS;
					
				} else {
					
					response = ResponseType.UNKNOWN;
					consumer.accept(response);
					
				}
				
			}
			
			JSONObject userInfos = new JSONObject(jsonResponse).getJSONObject("userInfo");
			
			islandLevel = userInfos.getInt("islandLevel");
			money = userInfos.getString("money");
			credit = userInfos.getInt("credit");
			ironsps = userInfos.getInt("ironsps");
			diasps = userInfos.getInt("diasps");
			profileLike = userInfos.getInt("profileLike");
			profileFollow = userInfos.getString("profileFollow");
			
			consumer.accept(response);
			
		});
		
	}
	
	/**
	 * Gets all infos as ConcurrentHashMap<String, String>.
	 */
	public final ConcurrentHashMap<String, String> getAllInfos() {
		
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
		
		map.clear();
		
		map.put("islandLevel", Utils.formatValue(getIslandLevel()));
		map.put("money", Utils.formatValue(getMoney()));
		map.put("credit", Utils.formatValue(getCreditAmount()));
		map.put("ironsps", Utils.formatValue(getIronSPCount()));
		map.put("diasps", Utils.formatValue(getDiamondBlockSPCount()));
		map.put("profileLikes", Utils.formatValue(getProfileLikes()));
		map.put("profileFollowers", Utils.formatValue(getProfileFollowers()));
		
		return map;
		
	}
	
	/**
	 * Gets the island level of the user.
	 * @return Integer islandLevel - The island level of the user.
	 */
	public final int getIslandLevel() {
		
		return this.islandLevel;
		
	}
	
	/**
	 * Gets the money of the user.
	 * @return Double money - The money of the user.
	 */
	public final double getMoney() {
		
		return Utils.convertToDouble(this.money);
		
	}
	
	/**
	 * Gets the credit amount of the user.
	 * @return Integer credit - The credit amount of the user.
	 */
	public final int getCreditAmount() {
		
		return this.credit;
		
	}
	
	/**
	 * Gets the Iron Spawner Count of the user.
	 * @return Integer ironsps - The iron spawner count of the user.
	 */
	public final int getIronSPCount() {
		
		return this.ironsps;
		
	}
	
	/**
	 * Gets the Diamond Block Spawner Count of the user.
	 * @return Integer diasps - The diamond block spawner count of the user.
	 */
	public final int getDiamondBlockSPCount() {
		
		return this.diasps;
		
	}
	
	/**
	 * Gets the profile like count of the user.
	 * @return Integer profileLike - The profile like count of the user.
	 */
	public final int getProfileLikes() {
		
		return this.profileLike;
		
	}
	
	/**
	 * Checks if the user has updated to ProfileV2.
	 * 
	 * @return true if user is updated & using profile v2.
	 * false if user is NOT updated / NOT using profile v2.
	 */
	public final boolean isUsingProfileV2() {
		
		return getProfileFollowers() != -1;
		
	}
	
	/**
	 * Gets the profile follower count of the user.
	 * 
	 * WARNING
	 * This method returns -1 as an integer value if the user has not updated to ProfileV2.
	 * @see User#isUsingProfileV2
	 * 
	 * @return Integer profileFollow - The profile follower count of the user.
	 */
	public final int getProfileFollowers() {
		
		return Utils.convertToInteger(this.profileFollow);
		
	}
	
	/**
	 * Formats a number to in-game format.
	 * @param double d - Any number to format. (you can convert to double using .doubleValue() or convertToDouble(object).)
	 * @return String val - The formatted value of the given number.
	 * 
	 * @deprecated
	 * Use Utils.formatValue(final double d) instead of this.
	 * This keeps for backwards compability.
	 */
	@CheckReturnValue
	@Deprecated
	public static final String formatValue(double d) {
		
		return Utils.formatValue(d);
		
	}
	
	/**
	 * Converts any object to integer. If object doesn't represent an int, throws a IllegalArgumentException.
	 * @param Object obj - Any object to convert.
	 * @return Integer - The integer value of the given object.
	 * 
	 * @deprecated
	 * Use Utils.convertToInteger(final Object obj) instead of this.
	 * This keeps for backwards compability.
	 */
	@CheckReturnValue
	@Deprecated
	public static final Integer convertToInteger(Object obj) {
		
		return Utils.convertToInteger(obj);
		
	}
	
	/**
	 * Converts any object to double. If object doesn't represent a double, throws a IllegalArgumentException.
	 * @param Object obj - Any object to convert.
	 * @return Double - The double value of the given object.
	 * 
	 * @deprecated
	 * Use Utils.convertToDouble(final Object obj) instead of this.
	 * This keeps for backwards compability.
	 */
	@CheckReturnValue
	@Deprecated
	public static final Double convertToDouble(Object obj) {
		
		return Utils.convertToDouble(obj);
		
	}
	
	/**
	 * Connects a URL and gets the response. The url must be starting with https://.
	 * 
	 * @param final String address - The URL of the web server.
	 * @return String response - The response from the web server.
	 * 
	 * @throws Exception - If any exceptions occured.
	 * @throws Throwable - If any exceptions occured.
	 * 
	 * @deprecated
	 * Use Utils.connectTo(final String address) instead of this.
	 * This keeps for backwards compability.
	 */
	@CheckReturnValue
	@Deprecated
	public static final String connectTo(final String address) throws Exception, Throwable {
		
		return Utils.connectTo(address);
		
	}
	
	/**
	 * Gets the current user object's instance.
	 * @return User - The current user object instance.
	 */
	public final User getInstance() {
		
		return this;
		
	}
	
}