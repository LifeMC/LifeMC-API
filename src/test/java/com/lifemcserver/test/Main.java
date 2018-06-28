package com.lifemcserver.test;

import java.util.Scanner;

import com.lifemcserver.api.LifeAPI;
import com.lifemcserver.api.ResponseType;
import com.lifemcserver.api.User;

public class Main {

	public static LifeAPI API = new LifeAPI();
	
	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		
		System.out.print("Enter your username: ");
		
		String userName = scan.next();
		
		System.out.print("Enter your password: ");
		
		String password = scan.next();
		
		scan.close();
		
		Object o = API.getUser(userName, password);
		
		if(o instanceof ResponseType) {
			
			throw new IllegalArgumentException("An error occured when validating your account. The web server response is: " + ((ResponseType) o).toString());
			
		} else if(o instanceof User) {
			
			User u = (User) o;
			ResponseType resp = u.updateInfos();
			
			if(resp.equals(ResponseType.SUCCESS)) {
				
				String islandLevel = u.formatValue(u.getIslandLevel());
				String money = u.formatValue(u.getMoney());
				String credit = u.formatValue(u.getCreditAmount());
				String ironsps = u.formatValue(u.getIronSPCount());
				String diasps = u.formatValue(u.getDiamondBlockSPCount());
				String profileLikes = u.formatValue(u.getProfileLikes());
				String profileFollowers = u.formatValue(u.getProfileFollowers());	
				
				System.out.println("Island Level: " + islandLevel);
				System.out.println("Money: " + money);
				System.out.println("Credit: " + credit);
				System.out.println("Iron SP Count: " + ironsps);
				System.out.println("Diamond Block SP Count: " + diasps);
				System.out.println("Profile Likes: " + profileLikes);
				System.out.println("Profile Followers: " + profileFollowers);
				
			} else {
				
				System.out.println("An error occured when getting infos from web server. The web server response is: " + resp.toString());
				
			}	
			
		}
		
		System.exit(0); // don't forget this at end of your program
		
	}
	
}