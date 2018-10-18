package com.lifemcserver.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Utility class
 */
public final class Utils {
	
	private Utils() { throw new UnsupportedOperationException("Utils class contains static methods, do not create instances of it!"); }
	
	protected static final Cache<String, URL> urlCache = CacheBuilder.newBuilder()
			.build();
	
    private static final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols( Locale.ENGLISH );
	
	static {
		
		URLConnection.setDefaultAllowUserInteraction(false);
		HttpURLConnection.setFollowRedirects(true);
		
	}
	
	/**
	 * Formats a number to in-game format.
	 * 
	 * @param d - Any number to format. (you can convert to double using .doubleValue() or convertToDouble(object).)
	 * @return val - The formatted value of the given number.
	 */
    public static final String formatValue(final double d) {
    	
        final boolean isWholeNumber = d == Math.round( d );
                
        formatSymbols.setDecimalSeparator( '.' );
        
        final String pattern = isWholeNumber ? "###,###.###" : "###,##0.00";
        
        final DecimalFormat df = new DecimalFormat( pattern, formatSymbols );
        
        return df.format( d );
        
    }
	
	/**
	 * Converts any object to integer. If object doesn't represent an int, throws a IllegalArgumentException.
	 * 
	 * @param obj - Any object to convert.
	 * @return Integer - The integer value of the given object.
	 * 
	 * @throws IllegalArgumentException - If given object doesn't represents an Integer.
	 */
	public static final int convertToInteger(final Object obj) {
		
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
				  
				  final String toString = obj.toString();
				  
			      if (toString.matches("-?\\d+")) {
			    	  
			    	  return Integer.parseInt(toString);
			    	  
			      }
			      
			      throw new IllegalArgumentException("This object doesn't represent an int (" + obj + ")");
			      
			  }
			  
		  } catch(final NumberFormatException ex) {
			  
			  ex.printStackTrace();
			  return 0;
			  
		  } catch(final Exception ex) {
			  
			  ex.printStackTrace();
			  return 0;
			  
		  }
		
	}
	
	/**
	 * Converts any object to double. If object doesn't represent a double, throws a IllegalArgumentException.
	 * 
	 * @param obj - Any object to convert.
	 * @return Double - The double value of the given object.
	 * 
	 * @throws IllegalArgumentException - If given object doesn't represents an Double.
	 */
	public static final double convertToDouble(final Object obj) {
		  
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
			  
		  } catch(final NumberFormatException ex) {
			  
			  ex.printStackTrace();
			  return 0.0;
			  
		  } catch(final Exception ex) {
			  
			  ex.printStackTrace();
			  return 0.0;
			  
		  }
		
	}
	
	/**
	 * Connects a URL and gets the response. The url must be starting with https://.
	 * 
	 * @param address - The URL of the web server.
	 * @return response - The response from the web server.
	 */
	public static final String connectTo(final String address) {
		
		String response = null;
		BufferedInputStream in = null;
		BufferedReader br = null;
		
		try {
			
			URL url = null;
			final URL cachedURL = urlCache.getIfPresent(address);
			
			if(cachedURL != null) {
				
				url = cachedURL;
				
			}
			
			if(url == null) {
				
				url =  new URL(address);
				urlCache.put(address, url);
				
			}
			
			final URLConnection con = url.openConnection();
			
			con.setAllowUserInteraction(false);
			con.setDoOutput(false);
			con.setUseCaches(true);
			con.setRequestProperty("Method", "GET");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Encoding", "UTF-8");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
		    con.setRequestProperty("Referer", "https://www.lifemcserver.com/forum/");	
		    
		    in = new BufferedInputStream(con.getInputStream());
		    final String encoding = con.getContentEncoding();
		    
		    if (encoding != null) {
		    	
		    	if (encoding.equalsIgnoreCase("gzip")) {
		    	  
		          in = new BufferedInputStream(new GZIPInputStream(in));
		          
		        } else if (encoding.equalsIgnoreCase("deflate")) {
		          
		          in = new BufferedInputStream(new InflaterInputStream(in, new Inflater(true)));
		          
		        }
		    	
			}
		    
			final StringBuilder responseBody = new StringBuilder();
		    br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		    
		    String line = "";
		    
		    while ((line = br.readLine()) != null) {
		    	
		    	responseBody.append(line.trim());
		    	responseBody.append("\n");
		    	
		    }
		    
			in.close();
			br.close();
			
			response = responseBody.toString().trim();
			
			//con.disconnect(); - commented out for caching the connection
			
			return response;
			
		} catch(final Throwable throwable) { throwable.printStackTrace(); return response; }
		finally {
			
			if(in != null) {
				
				try {
					
					in.close();
					
				} catch (final IOException e) {
					
					e.printStackTrace();
					
				}
				
			}
			
			if(br != null) {
				
				try {
					
					br.close();
					
				} catch (final IOException e) {
					
					e.printStackTrace();
					
				}
				
			}
			
		}
		
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
  	public static final long diff(final Date dateOne, final Date dateTwo) {
  		
  		long diff;
  		
  		if(dateOne.getTime() > dateTwo.getTime()) {
  			
  			diff = Math.abs(dateOne.getTime() - dateTwo.getTime());	
  			
  		} else {
  			
  			diff = Math.abs(dateTwo.getTime() - dateOne.getTime());
  			
  		}
  		
  	    return diff;
  	    
  	}
  	
}