package com.lifemcserver.test;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Scanner;

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
		
		API.getUser(userName, password, (o) -> {

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
				
				API.getRegisteredPlayerCount( (registeredPlayerCount) -> {
					
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
					
					user.updateInfos( (response) -> {
						
						Date updatedTwo = new Date();
						
						Long diffThree = Utils.diff(updatedFirst, updatedTwo);
						
						System.out.println("Getted account infos successfully. It tooked " + diffThree + " ms.");
						System.out.println();
						
						for(Entry<String, String> entry : user.getAllInfos().entrySet()) {
							
							System.out.println(entry.getKey() + " : " + entry.getValue());
							
						}
						
						Main.main(new String[0]);
						
					});
					
				});
				
			} else {
				
				System.out.println("An error occured when validating account credentials you entered. The web server's response is: " + String.valueOf(o));
				
			}
			
		});
		
    }
	
}