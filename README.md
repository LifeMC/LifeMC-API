# LifeMC-API
The LifeMC API Library for Getting a User's Info.

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
```java
package com.lifemcserver.test;

import com.lifemcserver.api.LifeAPI;
import com.lifemcserver.api.User;

public class Main {

	public static LifeAPI API = new LifeAPI();
	
  	public static void main(String[] args) {
  	
    	Object o = API.getUser("demo", "demo");
    	
  	}
  	
}
```
- Check if any errors occured, print the infos and terminate the program and the LifeAPI Background Thread. Remember, if you don't use System.exit, Your program continues to run after execution. Because LifeAPI Background Thread always run in background. A full example below:
```java
package com.lifemcserver.test;

import java.util.Scanner;

import com.lifemcserver.api.LifeAPI;
import com.lifemcserver.api.ResponseType;
import com.lifemcserver.api.User;
import com.lifemcserver.api.Utils;

public class Main {

	public static LifeAPI API = new LifeAPI();
	
	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		
		System.out.print("Enter your username (you can enter \"demo\" for testing): ");
		
		String userName = scan.next();
		
		System.out.print("Enter your password (you can enter \"demo\" for testing): ");
		
		String password = scan.next();
		
		scan.close();
		
		Object o = API.getUser(userName, password);
		
		if(o instanceof ResponseType) {
			
			System.out.println("An error occured when validating your account. The web server response is: " + ((ResponseType) o).toString());
			
		} else if(o instanceof User) {
			
			User u = (User) o;
			ResponseType resp = u.updateInfos();
			
			if(resp.equals(ResponseType.SUCCESS)) {
				
				String registeredPlayerCount = String.valueOf(API.getRegisteredPlayerCount());
				
				String islandLevel = Utils.formatValue(u.getIslandLevel());
				String money = Utils.formatValue(u.getMoney());
				String credit = Utils.formatValue(u.getCreditAmount());
				String ironsps = Utils.formatValue(u.getIronSPCount());
				String diasps = Utils.formatValue(u.getDiamondBlockSPCount());
				String profileLikes = Utils.formatValue(u.getProfileLikes());
				String profileFollowers = Utils.formatValue(u.getProfileFollowers());	
								
				System.out.println("");
				
				System.out.println("Total Registered Players: " + registeredPlayerCount);
				
				System.out.println("");
				
				System.out.println("Island Level: " + islandLevel);
				System.out.println("Money: " + money);
				System.out.println("Credit: " + credit);
				System.out.println("Iron SP Count: " + ironsps);
				System.out.println("Diamond Block SP Count: " + diasps);
				System.out.println("Profile Likes: " + profileLikes);
				System.out.println("Profile Followers: " + profileFollowers); // if it's -1, the user's profile not upgraded to ProfilV2.
				
				System.out.println("");
				
			} else {
				
				System.out.println("An error occured when getting infos from web server. The web server response is: " + resp.toString());
				
			}	
			
		}
		
		System.exit(0); // don't forget this at end of your program
		
	}
	
}
```