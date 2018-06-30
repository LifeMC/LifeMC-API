package com.lifemcserver.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HttpsURLConnection;

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
			
			jsonResponse = connectTo("https://www.lifemcserver.com/API.php?" + this.name + "&password=" + this.password);		
			
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
	 */
	public String formatValue(double d) {
		
		try {
			
			String val;
			
			val = String.format("%,.2f", d);
			
			if(val.endsWith(",00") || val.endsWith(".00")) {
				
				val = val.substring(0, val.length() - 3);
				
			}
			
			val = val.replace(".", ",");
			
			return val;
			
		} catch(NumberFormatException ex) {
			
			ex.printStackTrace();
			return "0";
			
		} catch(Exception ex) {
			
			ex.printStackTrace();
			return "0";
			
		}
		
	}
	
	/**
	 * Converts any object to integer. If object doesn't represent an int, throws a IllegalArgumentException.
	 * @param Object obj - Any object to convert.
	 * @return Integer - The integer value of the given object.
	 */
	public Integer convertToInteger(Object obj) {
		
		  try {
				
			  if(obj == null) {
				  
				  return -1;
				  
			  }
			  
			  if (obj instanceof String) {
				  
				  return Integer.parseInt((String) obj);
				  
			  } else if (obj instanceof Number) {
				  
				  return ((Number) obj).intValue();
				  
			  } else if (obj instanceof Double) {
				  
				  return ((Double) obj).intValue();
				  
			  } else {
				  
				  String toString = obj.toString();
				  
			      if (toString.matches("-?\\d+")) {
			    	  
			    	  return Integer.parseInt(toString);
			    	  
			      }
			      
			      throw new IllegalArgumentException("This object doesn't represent an int (" + obj + ")");
			      
			  }
			  
		  } catch(NumberFormatException ex) {
			  
			  ex.printStackTrace();
			  return 0;
			  
		  } catch(Exception ex) {
			  
			  ex.printStackTrace();
			  return 0;
			  
		  }
		
	}
	
	/**
	 * Converts any object to double. If object doesn't represent a double, throws a IllegalArgumentException.
	 * @param Object obj - Any object to convert.
	 * @return Double - The double value of the given object.
	 */
	public Double convertToDouble(Object obj) {
		
		  try {
				
			  if(obj == null) {
				  
				  return 0.0;
				  
			  }
			  
			  if (obj instanceof String) {

				  return Double.parseDouble((String) obj);
				  
			  } else if (obj instanceof Number) {
				  
				  return ((Number) obj).doubleValue();
				  
			  } else if (obj instanceof Double) {
				  
				  return ((Double) obj).doubleValue();
				  
			  } else if(obj instanceof Integer) {
				  
				  return ((Integer) obj).doubleValue();
				  
			  } else {
				  
				  String toString = obj.toString();
				  
			      if (toString.matches("-?\\d+")) {
			    	  
			    	  return ((Integer) Integer.parseInt(toString)).doubleValue();
			      }
			      
			      throw new IllegalArgumentException("This object doesn't represent a double (" + obj + ")");
			  }
			  
		  } catch(NumberFormatException ex) {
			  
			  ex.printStackTrace();
			  return 0.0;
			  
		  } catch(Exception ex) {
			  
			  ex.printStackTrace();
			  return 0.0;
			  
		  }
		
	}
	
	/**
	 * Connects a URL and gets the response. The url must be starting with https://.
	 * 
	 * @param final String address - The URL of the web server.
	 * @return String response - The response from the web server.
	 * 
	 * @throws Exception - If any exceptions occured.
	 * @throws Throwable - If any exceptions occured.
	 */
	public static String connectTo(final String address) throws Exception, Throwable {
		
		Future<String> resp = LifeAPI.apiThread.submit(new Callable<String>() {
			
			public String call() throws Exception {
				
				URL url = new URL(address);
				
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

				con.setAllowUserInteraction(false);
				con.setDoOutput(false);
				con.setUseCaches(true);
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
			    con.setRequestProperty("Referer", "https://www.lifemcserver.com/");
				
			    InputStream error = con.getErrorStream();
			    
			    BufferedInputStream in = null;
			    
			    if (error == null) {
			    	
			    	in = new BufferedInputStream(con.getInputStream());
			    	
			    } else {
			    	
					error.close();
			    	throw new Exception("An internal error occured when connecting to web server. Response code: " + con.getResponseCode());
					
			    }
			
			    String encoding = con.getContentEncoding();
			    
			    if (encoding != null) {
			    	
			    	if (encoding.equalsIgnoreCase("gzip")) {
			    		
			          in = new BufferedInputStream(new GZIPInputStream(in));
			          
			        } else if (encoding.equalsIgnoreCase("deflate")) {
			        	
			          in = new BufferedInputStream(new InflaterInputStream(in, new Inflater(true)));
			          
			        }
				}
			
				StringBuilder responseBody = new StringBuilder();
			    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			    
			    String line;
			    
			    while ((line = br.readLine()) != null) {
			    	
			    	responseBody.append(line.toString().trim());
			    	responseBody.append("\n");
			    	
			    }
				
				in.close();
				
				String response = responseBody.toString().trim();
				
				//con.disconnect(); - commented out for caching the connection
				
				return response;
				
			}
				
		});
		
		return resp.get();
		
	}
	
	/**
	 * Gets the current user object's instance.
	 * @return User - The current user object instance.
	 */
	public User getInstance() {
		
		return this;
		
	}
	
}