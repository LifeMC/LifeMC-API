package com.lifemcserver.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.json.JSONObject;

public final class User {
	
	private volatile String name = "demo";
	private volatile String password = "demo";
	
	private volatile int islandLevel = 0;
	private volatile String money = "0.00";
	private volatile int credit = 0;
	private volatile int ironsps = 0;
	private volatile int diasps = 0;
	private volatile int profileLike = 0;
	private volatile String profileFollow = "-1";
	
	private volatile boolean updatedInfos = false;
	
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
	public User(final String name, final String password, final int islandLevel, final String money, final int credit, final int ironsps, final int diasps, final int profileLike, final String profileFollow) {
		
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
		
		return this.name;
		
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
				
				jsonResponse = Utils.connectTo("https://www.lifemcserver.com/API.php?" + this.name + "&password=" + this.password);
				
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
			
			else {
				
				if(jsonResponse.contains("userInfo")) {
					
					response = ResponseType.SUCCESS;
					
				} else {
					
					response = ResponseType.UNKNOWN;
					consumer.accept(response);
					
				}
				
			}
			
			final JSONObject userInfos = new JSONObject(jsonResponse).getJSONObject("userInfo");
			
			this.islandLevel = userInfos.getInt("islandLevel");
			this.money = userInfos.getString("money");
			this.credit = userInfos.getInt("credit");
			this.ironsps = userInfos.getInt("ironsps");
			this.diasps = userInfos.getInt("diasps");
			this.profileLike = userInfos.getInt("profileLike");
			this.profileFollow = userInfos.getString("profileFollow");
			
			consumer.accept(response);
			
			if(!this.updatedInfos) {
				
				this.updatedInfos = true;
				
			}
			
		});
		
	}
	
	/**
	 * Gets all infos as ConcurrentHashMap<String, String>.
	 */
	public final ConcurrentHashMap<String, String> getAllInfos() {
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		
		map.clear();
		
		map.put("islandLevel", Utils.formatValue(this.getIslandLevel()));
		map.put("money", Utils.formatValue(this.getMoney()));
		map.put("credit", Utils.formatValue(this.getCreditAmount()));
		map.put("ironsps", Utils.formatValue(this.getIronSPCount()));
		map.put("diasps", Utils.formatValue(this.getDiamondBlockSPCount()));
		map.put("profileLikes", Utils.formatValue(this.getProfileLikes()));
		map.put("profileFollowers", Utils.formatValue(this.getProfileFollowers()));
		
		return map;
		
	}
	
	/**
	 * Gets the island level of the user.
	 * @return Integer islandLevel - The island level of the user.
	 */
	public final int getIslandLevel() {
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		return this.islandLevel;
		
	}
	
	/**
	 * Gets the money of the user.
	 * @return Double money - The money of the user.
	 */
	public final double getMoney() {
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		return Utils.convertToDouble(this.money);
		
	}
	
	/**
	 * Gets the credit amount of the user.
	 * @return Integer credit - The credit amount of the user.
	 */
	public final int getCreditAmount() {
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		return this.credit;
		
	}
	
	/**
	 * Gets the Iron Spawner Count of the user.
	 * @return Integer ironsps - The iron spawner count of the user.
	 */
	public final int getIronSPCount() {
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		return this.ironsps;
		
	}
	
	/**
	 * Gets the Diamond Block Spawner Count of the user.
	 * @return Integer diasps - The diamond block spawner count of the user.
	 */
	public final int getDiamondBlockSPCount() {
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		return this.diasps;
		
	}
	
	/**
	 * Gets the profile like count of the user.
	 * @return Integer profileLike - The profile like count of the user.
	 */
	public final int getProfileLikes() {
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		return this.profileLike;
		
	}
	
	/**
	 * Checks if the user has updated to ProfileV2.
	 * 
	 * @return true if user is updated & using profile v2.
	 * false if user is NOT updated / NOT using profile v2.
	 */
	public final boolean isUsingProfileV2() {
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		return this.getProfileFollowers() != -1;
		
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
		
		if(!this.updatedInfos) this.updateInfos((resp) -> {});
		
		return Utils.convertToInteger(this.profileFollow);
		
	}
	
	/**
	 * Gets the current user object's instance.
	 * @return User - The current user object instance.
	 */
	public final User getInstance() {
		
		return this;
		
	}
	
}