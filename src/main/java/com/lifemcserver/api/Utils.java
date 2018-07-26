package com.lifemcserver.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;

public final class Utils {

	/**
	 * Formats a number to in-game format.
	 * 
	 * @param double d - Any number to format. (you can convert to double using .doubleValue() or convertToDouble(object).)
	 * @return String val - The formatted value of the given number.
	 */
	@CheckReturnValue
    public static final String formatValue(final double d) {
    	
        boolean isWholeNumber = d == Math.round( d );
        
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols( Locale.ENGLISH );
        
        formatSymbols.setDecimalSeparator( '.' );
        
        String pattern = isWholeNumber ? "###,###.###" : "###,##0.00";
        
        DecimalFormat df = new DecimalFormat( pattern, formatSymbols );
        
        return df.format( d );
        
    }
	
	/**
	 * Converts any object to integer. If object doesn't represent an int, throws a IllegalArgumentException.
	 * 
	 * @param Object obj - Any object to convert.
	 * @return Integer - The integer value of the given object.
	 * 
	 * @throws IllegalArgumentException - If given object doesn't represents an Integer.
	 */
	@CheckReturnValue
	public static final Integer convertToInteger(final Object obj) {
		
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
	 * 
	 * @param Object obj - Any object to convert.
	 * @return Double - The double value of the given object.
	 * 
	 * @throws IllegalArgumentException - If given object doesn't represents an Double.
	 */
	@CheckReturnValue
	public static final Double convertToDouble(final Object obj) {
		  
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
				  
				  return Double.parseDouble(String.valueOf(obj));
				  
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
	 */
	@CheckReturnValue
	@Nullable
	public static final String connectTo(final String address) {
		
		try {
			
			URL url = new URL(address);
			
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			
			con.setAllowUserInteraction(false);
			con.setDoOutput(false);
			con.setUseCaches(true);
			con.setRequestMethod("GET");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Encoding", "UTF-8");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
		    con.setRequestProperty("Referer", "https://www.lifemcserver.com/forum/");
			
		    InputStream error = con.getErrorStream();
		    
		    BufferedInputStream in = null;
		    
		    if (error == null) {
		    	
		    	in = new BufferedInputStream(con.getInputStream());
		    	
		    } else {
		    	
				error.close();
		    	throw new IOException("An internal error occured when connecting to web server. Response code: " + con.getResponseCode());
				
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
		    
		    String line = "";
		    
		    while ((line = br.readLine()) != null) {
		    	
		    	responseBody.append(line.toString().trim());
		    	responseBody.append("\n");
		    	
		    }
		    
			in.close();
			
			String response = responseBody.toString().trim();
			
			//con.disconnect(); - commented out for caching the connection
			
			return response;
			
		} catch(Throwable throwable) { throwable.printStackTrace(); return null; }
		
	}
	
	/**
	 * Returns absolute time between specified two dates.
	 * 
	 * No matter if dateOne after dateTwo or vice verse,
	 * This method calculates everything and returns the true value
	 * in the {@link TimeUnit#MILLISECONDS} format. You can convert it to your value
	 * easily using the {@link TimeUnit} class.
	 * 
	 * @param dateOne - The first date object.
	 * @param dateTwo - The second date object.
	 * 
	 * @return The absolute time between specified two dates in {@link TimeUnit#MILLISECONDS} format.
	 */
  	public static Long diff(Date dateOne, Date dateTwo) {
  		
  		Long diff = null;
  		
  		if(dateOne.getTime() > dateTwo.getTime()) {
  			
  			diff = Math.abs(dateOne.getTime() - dateTwo.getTime());	
  			
  		} else {
  			
  			diff = Math.abs(dateTwo.getTime() - dateOne.getTime());
  			
  		}
  		
  	    return diff;
  	    
  	}
	
}