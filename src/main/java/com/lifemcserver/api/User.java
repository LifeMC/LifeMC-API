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
	
	public User(String name, String password) {
		
		this.name = name;
		this.password = password;
		
	}
	
	public String getName() {
		
		return name;
		
	}
	
	public User setName(String newName) {
		
		this.name = newName;
		return this;
		
	}
	
	public User setPassword(String newPassword) {
		
		this.password = newPassword;
		return this;
		
	}
	
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
	
	public Integer getIslandLevel() {
		
		return this.islandLevel;
		
	}
	
	public Double getMoney() {
		
		return convertToDouble(this.money);
		
	}
	
	public Integer getCreditAmount() {
		
		return this.credit;
		
	}
	
	public Integer getIronSPCount() {
		
		return this.ironsps;
		
	}
	
	public Integer getDiamondBlockSPCount() {
		
		return this.diasps;
		
	}
	
	public Integer getProfileLikes() {
		
		return this.profileLike;
		
	}
	
	public Integer getProfileFollowers() {
		
		return convertToInteger(this.profileFollow);
		
	}
	
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
	
	protected static String connectTo(final String address) throws Exception, Throwable {
		
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
				
				con.disconnect();
				
				return response;	
				
			}
				
		});
		
		return resp.get();
		
	}
	
}