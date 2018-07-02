package com.lifemcserver.api;

import javax.annotation.CheckReturnValue;

import org.json.JSONObject;

public class User {

	private String name;
	private String password;
	
	private Integer islandLevel;
	private String money;
	private Integer credit;
	private Integer ironsps;
	private Integer diasps;
	private Integer profileLike;
	private String profileFollow;
	
	/**
	 * Creates a user object with the given user name and password.
	 * You should use the LifeAPI.getUser(String name, String password) instead of this constructor.
	 * 
	 * @param String name - The user name of the user.
	 * @param String password - The password of the user.
	 */
	public User(String name, String password) {
		
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
	public User(String name, String password, Integer islandLevel, String money, Integer credit, Integer ironsps, Integer diasps, Integer profileLike, String profileFollow) {
		
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
	public String getName() {
		
		return name;
		
	}
	
	/**
	 * Sets the user name of the user.
	 * @param String newName - The new user name of the user.
	 * @return User - Current user instance.
	 */
	public User setName(String newName) {
		
		this.name = newName;
		return this;
		
	}
	
	/**
	 * Sets the password of the user.
	 * @param String newPassword - The new password of the user.
	 * @return User - Current user instance.
	 */
	public User setPassword(String newPassword) {
		
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
	public ResponseType updateInfos() {
		
		String jsonResponse = "";
		
		try {
			
			jsonResponse = Utils.connectTo("https://www.lifemcserver.com/API.php?" + this.name + "&password=" + this.password);		
			
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
		
		else {
			
			if(jsonResponse.contains("userInfo")) {
				
				response = ResponseType.SUCCESS;
				
			} else {
				
				response = ResponseType.ERROR;
				return response;
				
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
		
		return response;
		
	}
	
	/**
	 * Gets the island level of the user.
	 * @return Integer islandLevel - The island level of the user.
	 */
	public Integer getIslandLevel() {
		
		return this.islandLevel;
		
	}
	
	/**
	 * Gets the money of the user.
	 * @return Double money - The money of the user.
	 */
	public Double getMoney() {
		
		return convertToDouble(this.money);
		
	}
	
	/**
	 * Gets the credit amount of the user.
	 * @return Integer credit - The credit amount of the user.
	 */
	public Integer getCreditAmount() {
		
		return this.credit;
		
	}
	
	/**
	 * Gets the Iron Spawner Count of the user.
	 * @return Integer ironsps - The iron spawner count of the user.
	 */
	public Integer getIronSPCount() {
		
		return this.ironsps;
		
	}
	
	/**
	 * Gets the Diamond Block Spawner Count of the user.
	 * @return Integer diasps - The diamond block spawner count of the user.
	 */
	public Integer getDiamondBlockSPCount() {
		
		return this.diasps;
		
	}
	
	/**
	 * Gets the profile like count of the user.
	 * @return Integer profileLike - The profile like count of the user.
	 */
	public Integer getProfileLikes() {
		
		return this.profileLike;
		
	}
	
	/**
	 * Gets the profile follower count of the user.
	 * @return Integer profileFollow - The profile follower count of the user.
	 */
	public Integer getProfileFollowers() {
		
		return convertToInteger(this.profileFollow);
		
	}
	
	/**
	 * Formats a number to in-game format.
	 * @param double d - Any number to format. (you can convert to double using .doubleValue() or convertToDouble(object).)
	 * @return String val - The formatted value of the given number.
	 * 
	 * Use Utils.formatValue(final double d) instead of this.
	 * This keeps for backwards compability.
	 */
	@CheckReturnValue
	@Deprecated
	public static String formatValue(double d) {
		
		return Utils.formatValue(d);
		
	}
	
	/**
	 * Converts any object to integer. If object doesn't represent an int, throws a IllegalArgumentException.
	 * @param Object obj - Any object to convert.
	 * @return Integer - The integer value of the given object.
	 * 
	 * Use Utils.convertToInteger(final Object obj) instead of this.
	 * This keeps for backwards compability.
	 */
	@CheckReturnValue
	@Deprecated
	public static Integer convertToInteger(Object obj) {
		
		return Utils.convertToInteger(obj);
		
	}
	
	/**
	 * Converts any object to double. If object doesn't represent a double, throws a IllegalArgumentException.
	 * @param Object obj - Any object to convert.
	 * @return Double - The double value of the given object.
	 * 
	 * Use Utils.convertToDouble(final Object obj) instead of this.
	 * This keeps for backwards compability.
	 */
	@CheckReturnValue
	@Deprecated
	public static Double convertToDouble(Object obj) {
		
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
	 * Use Utils.connectTo(final String address) instead of this.
	 * This keeps for backwards compability.
	 */
	@CheckReturnValue
	@Deprecated
	public static String connectTo(final String address) throws Exception, Throwable {
		
		return Utils.connectTo(address);
		
	}
	
	/**
	 * Gets the current user object's instance.
	 * @return User - The current user object instance.
	 */
	public User getInstance() {
		
		return this;
		
	}
	
}