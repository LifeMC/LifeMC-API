# LifeMC-API
The LifeMC API Library for Getting a User's Info. Based on Java.

# Features
- **Async Execution.** This means your program is never blocks when making API requests.
- **Thread Safe.** This means you never get a ConcurrentModificationException.
- **Cache System.** Saves all users to a cache map. Second time when you use the user, the user is returned from cache map.
- **High Performance & Feature Rich.** Faster than you need! All features are available via easy methods.

# Requirements
- Java 8 is required for Life-API. If you are running Java 7, upgrade it. You should have no problems with it.
(Upgrading from Java 7 to Java 8 is not causes problems in general. Just upgrade and test it.)

# How to install?
- First, download the latest release <a href="https://github.com/LifeMC/LifeMC-API/releases/latest/">here</a>.
- After the download, create your Java Project in your IDE. And, add LifeAPI.jar to your Build Path (or classpath)
- Now you can use the LifeAPI! Write your code, organize your imports and export your program to a jar file.

# How to use?
- Create an instance of LifeAPI class and add a main method.

```java
package com.lifemcserver.test;

import com.lifemcserver.api.LifeAPI;

public class Main {
	
	public static LifeAPI API = new LifeAPI();
	
  	public static void main(String[] args) {
  		
  		
  		
  	}
  	
}
```

- Get a user from user name and password. (You should request this informations from the user)
(Consumers used for Async returning. LifeAPI works fully async & thread-safe.)

```java
package com.lifemcserver.test;

import com.lifemcserver.api.LifeAPI;
import com.lifemcserver.api.User;

public class Main {
	
	public static LifeAPI API = new LifeAPI();
	
  	public static void main(String[] args) {
  		
    	Object o = API.getUser("demo", "demo", new Consumer<Object>(){
    	
    		@Override
    		public void accept(Object o) {
    			
    			// o is the returned value from getUser method.
    			
    		}
    	
    	});
    	
  	}
  	
}
```

- Check if any errors occured, print the infos and terminate the program and the LifeAPI Background Thread. Remember, if you don't use System.exit, Your program continues to run after execution. Because LifeAPI Background Thread always run in background. A full example below:

```java
package com.lifemcserver.test;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.function.Consumer;

import com.lifemcserver.api.LifeAPI;
import com.lifemcserver.api.ResponseType;
import com.lifemcserver.api.User;
import com.lifemcserver.api.Utils;

public final class Main {
	
	public static final LifeAPI API = new LifeAPI();
	public static final Scanner scan = new Scanner(System.in, "UTF-8");
	
	public static final void main(final String[] args) {
		
		System.out.print("Enter your username (you can enter \"demo\" for testing): ");
		
		String userName = scan.next();
		
		System.out.print("Enter your password (you can enter \"demo\" for testing): ");
		
		String password = scan.next();

		System.out.println("Please wait when validating credentials you entered...");
		
		final Date validateFirst = new Date();
		
		API.getUser(userName, password, new Consumer<Object>() {
			
			@Override
			public void accept(final Object o) {
				
				if(o instanceof ResponseType) {
					
					ResponseType response = (ResponseType) o;
					
					if(response.equals(ResponseType.NO_USER)) {
						
						System.out.println("The user name you entered is not found on the database. Please re-check credentials you entered.");
						
					} else if(response.equals(ResponseType.WRONG_PASSWORD)) {
						
						System.out.println("The password you entered is wrong. Please re-check credentials you entered.");
						
					} else if(response.equals(ResponseType.MAX_TRIES)) {
						
						System.out.println("You have exceeded the maximum wrong password limit. You have to wait three minutes.");
						
					} else if(response.equals(ResponseType.ERROR)) {
						
						System.out.println("The web server returned a error status. Maybe the web server under maintenance. Please retry later.");
						
					} else {
						
						System.out.println("An error occured when validating your account from web server. Maybe the web server is down. Please retry later.");
						
					}
					
				} else if(o instanceof User) {
					
					Date validateTwo = new Date();
					
					Long diff = Utils.diff(validateFirst, validateTwo);
					
					System.out.println();
					System.out.println("Successfully validated your account. It tooked " + diff + " ms. Welcome! ;)");
					System.out.println("Now getting the total registered player count...");
					System.out.println();
					
					final Date registeredFirst = new Date();
					
					API.getRegisteredPlayerCount(new Consumer<Integer>() {
						
						@Override
						public void accept(final Integer registeredPlayerCount) {
							
							Date registeredTwo = new Date();
							
							Long diffTwo = Utils.diff(registeredFirst, registeredTwo);
							
							System.out.println("Getted the total registered player count from api was successful. It tooked " + diffTwo + " ms.");
							
							System.out.println();
							System.out.println("registeredPlayerCount : " + registeredPlayerCount);
							System.out.println();
							
							System.out.println("Now getting account infos...");
							System.out.println();
							
							final User user = (User) o;
							final Date updatedFirst = new Date();
							
							user.updateInfos(new Consumer<ResponseType>() {
								
								@Override
								public void accept(final ResponseType response) {
									
									Date updatedTwo = new Date();
									
									Long diffThree = Utils.diff(updatedFirst, updatedTwo);
									
									System.out.println("Getted account infos successfully. It tooked " + diffThree + " ms.");
									System.out.println();
									
									for(Entry<String, String> entry : user.getAllInfos().entrySet()) {
										
										System.out.println(entry.getKey() + " : " + entry.getValue());
										
									}
									
									Main.main(new String[0]);
									
								}
								
							});
							
						}
						
					});
					
				} else {
					
					System.out.println("An error occured when validating account credentials you entered. The web server's response is: " + String.valueOf(o));
					
				}
				
			}
			
		});
		
    }
	
}
```
