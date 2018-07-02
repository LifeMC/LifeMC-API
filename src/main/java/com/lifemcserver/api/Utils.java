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

import javax.annotation.CheckReturnValue;
import javax.net.ssl.HttpsURLConnection;

public abstract class Utils {

	/**
	 * Formats a number to in-game format.
	 * @param double d - Any number to format. (you can convert to double using .doubleValue() or convertToDouble(object).)
	 * @return String val - The formatted value of the given number.
	 */
	@CheckReturnValue
	public static String formatValue(final double d) {
		
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
	@CheckReturnValue
	public static Integer convertToInteger(final Object obj) {
		
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
	@CheckReturnValue
	public static Double convertToDouble(final Object obj) {
		
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
	@CheckReturnValue
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
	
}