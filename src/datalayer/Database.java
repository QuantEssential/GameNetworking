package datalayer;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;



public class Database {
	
	public static Hashtable<String, GameID> GID;
	public static Hashtable<String, UserID> UID;
	public static ArrayList<GameID> activeGames;
	public static ArrayList<UserID> activeUsers;
	public static Integer sessCount;
	
	
	
	
	
	
	public static class GameID	{
		
		String gametype;
		ArrayList<UserID> users;
		String mapname;
		String sessID;
		boolean begin;
		
		GameID()
		{
			
		}
		GameID(String g)	{
			users = new ArrayList<UserID>();
			gametype = g;
			mapname = g;
			sessID = sessCount.toString();
			begin = false;
			sessCount++;
		}
	}
	
	public static class UserID	{
		
		SocketChannel sc;
		GameID gid;

		String username;
		String password;
		String name;
		String logid;
		
		boolean active;
		boolean playing;
		int highscore;

		ArrayList<Integer> prescores;
		
		
		String userx;
		String usery;
		
		
		UserID(String u, String p, String n)	{
			
			prescores = new ArrayList<Integer>();
			username = u;
			password = p;
			name = n;
			active = false;
			playing = false;
			highscore = 0;
			logid = "";
			userx="0";
			usery="0";
		}
	}
	
	


	public static String srchprocess(String[] s) {
		String r = "";
		UserID uid = UID.get(s[2]);
	//	GameID gid = GID.get(s[3]);
		if(s[1].equals("GAME"))	{
			if(s[3].equals("ALL")) {
				r = "OK";
				/*Enumeration<GameID> e = GID.elements();
				while(e.hasMoreElements())	{
					GameID temp = e.nextElement();
					r+="|"+temp.gametype+":"+temp.sessID+":";
					for(UserID U: temp.users) {
						r+=":"+U.username;
					}
				}*/
				for(GameID g:activeGames)	{
					r+="|Game ID: "+g.sessID+"|Type: "+g.gametype+"|Current Users: " ;
					for(UserID U: g.users) {
						r+=U.username+"; ";
					}
				}
				return r;
			}

			
		  }
		else if(s[1].equals("USR"))	{
			if(s[3].equals("ACTV"))	{
				r = "OK";
				for(int q = 0 ; q<activeUsers.size();q++) {
					r+="|" + activeUsers.get(q).username;
				}
				return r;
			}
			else if(s[3].equals("LOUT"))	{
				r = "OK";
				activeUsers.remove(uid);
				for(GameID g:activeGames) {
					if(g.users.contains(uid)){
						g.users.remove(uid);
					}
				}
				return r;
			}

			else if(s[3].equals("ALL")) {
				r = "OK";
				Enumeration<UserID> e = UID.elements();
				while(e.hasMoreElements())	{
					r+="|" +e.nextElement().username;
				}
				return r;
			}
			
			else if(UID.containsKey(s[2]))	{

				if(uid.password.equals(s[3]))	{
					if(!activeUsers.contains(uid))	{
						activeUsers.add(uid);
					}
					r = "OK|"+uid.username+"|"+uid.password+"|"+uid.name;
					return r;
				} else {
				  return	"ERROR|Invalid Password Username Combo";
				}

			} 
		}else {
			   return "ERROR|USER DOESNT EXIST";  
		}	
		
			
		return r;
	}
	

	
	public static String creaprocess(String[] s) {
		String r = "";
		UserID U = UID.get(s[2]);
		if(s[1].equals("USER")) {
			if(UID.containsKey(s[2]))	{
				return  "ERROR|USERNAME ALREADY EXISTS START OVER";
				
			}
			else {
				UserID u = new UserID(s[2], s[4], s[3]);
				UID.put(s[2], u);
				activeUsers.add(u);
				r = "OK|" +s[2] + "|" + s[3] + "|" + s[4];  
				return r;
			}
		} else if(s[1].equals("GAME"))	{
			GameID gid = new GameID(s[2]);
			String newuser = s[3];
			UserID user = UID.get(newuser);
			gid.users.add(user);
			activeGames.add(gid);
			GID.put(gid.sessID, gid);
			user.gid = gid;
			r = "OK|"+s[2]+"|"+gid.users.get(0).username + "|" +gid.sessID;

			for(int count = 4; count<s.length-1;count++) {
				newuser = s[count];
			    user = UID.get(newuser);
				gid.users.add(user);
				activeGames.add(gid);
				GID.put(gid.sessID, gid);
				user.gid = gid;
				r+=user.username;
			}
			return r;
			
		} else {
			return  "ERROR|Invalid Create Type Arg";
		}
	}
	
	
	public static String joinprocess(String[] s)	{
		String r = "";
		boolean found = false;
		int sid = Integer.parseInt(s[2]);
		UserID U = UID.get(s[1]);
		if(s[0].equals("JOIN")) {
			if(UID.containsKey(s[1]))	{
				if(sid<0 || sid>activeGames.size()) {
					return  "ERROR|Invalid Session ID";
				}
				for(int g = 0; g<activeGames.size();g++) {  //GameID gid: activeGames)	{
					GameID gid = activeGames.get(g);
					if(gid.sessID.equals(s[2]))	{
						System.out.println("FOUND");
						gid.users.add(U);
						U.gid = activeGames.get(g);
						found = true;
						r+="OK|"+U.username +"|JOINED|"+gid.sessID;
					}
					if(found) {
						return r;
					}
				}
			}
			else {
				return  "ERROR|USERNAME DOESNT EXIST";
			}
		}
		return "ERROR|DID NOT PROCESS";
	}
	
	
	public static boolean isInt(int n) {
		   return n % 1 == 0;
		}
	
	
	public static String updtprocess(String[] s)	{
		
		String r = "";
		UserID U = UID.get(s[1]);
		int x = Integer.parseInt(s[2]);
		int y = Integer.parseInt(s[3]);
		if(isInt(x) && isInt(y))	{
			U.userx = s[2];
			U.usery = s[3];
			r = "OK"+"|"+s[1]+"|"+s[2]+"|"+s[3];
		}
		else	{
			r = "ERROR|INVALID INTEGERS";
		}
		
		return r;
	}
	
	public static String getprocess(String[] s){
		
		String r = "OK";
		GameID G = GID.get(s[1]);
		Integer numusers = G.users.size()-1;
		r+="|" + numusers.toString();
		for(int i = 0; i < G.users.size();i++ )	{
			UserID U = G.users.get(i);
			if(!U.username.equals(s[2]))	{
				r += "|" + U.userx.toString() + "|";
				r += U.usery.toString();
			}
		}
		
		return r;
	}
	
	public static String startprocess(String[] s){
		
		String r = "";
		GameID G = GID.get(s[2]);
		if(s[1].equals("SET"))	{
			G.begin = true;
			 r = "OK";
		} else if(s[1].equals("GET"))	{
			if(G.begin)	{
				 r = "OK";
			} else	{
				 r = "FALSE";
			}
		}
		else	{
			return "ERROR INVALID GET/SET ARG";
		}
		
		return r;
	}

	
	public static String highscore(String[] s)	{
		UserID U = UID.get(s[3]);
		String r = "OK|";
		if(s[2].equals("PUT")){
			U.highscore = Integer.parseInt(s[4]);
			r+=s[4];
		} else if(s[2].equals("GET")){
			r+= String.valueOf(U.highscore);
		} 
		
		return r;
	}
	
	//SCORE|HIGH|PUT/GET|UID|SCORE
	//SCORE|ADD|PUT/GET|UID|SCORE
	public static String addScore(String[] s)	{
		UserID U = UID.get(s[3]);
		String r = "OK";
		if(s[2].equals("PUT")){
			int score = Integer.parseInt(s[4]);
			U.prescores.add(score);
			
			if(score>U.highscore){
				U.highscore = score;
			}
			
			r+="|"+s[4];
		} else if(s[2].equals("GET")){
			if(U.prescores.isEmpty())	{
				r+="|NO SCORES";
			}	else	{
				for(Integer test: U.prescores){
					r+= "|"+String.valueOf(test);
				}
			}
		} 
		
		return r;
	}

	public static void Initialize()
		{
		
			//Library.Initialize();
		
		
		    int port = 50009; 
			int len = 128;
		    try {
				DatagramSocket sockudp = new DatagramSocket(port);
				sockudp.setSoTimeout(10000000);
				System.out.println("Database UDP Server Socket Created");
				//byte[] sendpack = new byte[len];
				
				while(true)	{	
					byte[] packetrec = new byte[len];
					
					DatagramPacket recdata = new DatagramPacket(packetrec,packetrec.length);
				sockudp.receive(recdata);
				InetAddress IPaddr = recdata.getAddress();
				port = recdata.getPort();
				String received = new String(recdata.getData());
				System.out.println("Received: " + received);
				//String send = received.toUpperCase();
				String[] array = received.split("\\|");
				String ret = "";
				
				
				
				
				
				
				if(array[0].equals("SRCH")) {
						ret = srchprocess(array);
				} else if(array[0].equals("CREA")) {
						ret = creaprocess(array);
				} else if(array[0].equals("SCORE")) {

					if(array[1].equals("HIGH"))	{
						ret = highscore(array);
					} else if(array[1].equals("ALL"))	{
						ret = addScore(array);
					}
			
				}else if(array[0].equals("GET")) {
						ret = getprocess(array);
					
				}	else if(array[0].equals("START")) {
					//if(array.length == 5||array.length == 6){
					ret = startprocess(array);
				
			}	else if(array[0].equals("JOIN")) {
					if(array.length == 4) {
						ret = joinprocess(array);
					} else
					{
						ret = "ERROR|INVALID ARG NUMBER";
					}
					
					
				}else if(array[0].equals("UPDT")) {
						ret = updtprocess(array);
				
				}

				//for(String s : array)	{
				ret+="|~";
					byte[] sendpack = new byte[len];
					System.out.println("Sending: " + ret);

					sendpack = ret.getBytes();
					DatagramPacket senddata = new DatagramPacket(sendpack, sendpack.length, IPaddr, port);
					sockudp.send(senddata);
				//}

				/*String end = "~";
				byte[] sendendpack = new byte[len];
				sendendpack = end.getBytes();
				DatagramPacket sendenddata = new DatagramPacket(sendendpack, sendendpack.length, IPaddr, port);
				sockudp.send(sendenddata);
				*/
			   } 
		  }
			catch(SocketTimeoutException e) {
				System.out.println("No Response");
			}
			catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		  
	}


	
	public static void main(String args[]) {
		
		UID = new Hashtable<String, UserID>();
		GID = new Hashtable<String, GameID>();
		activeGames = new ArrayList<GameID>();
		activeUsers = new ArrayList<UserID>();
		sessCount = 1;
		
		UserID dan = new UserID("dan", "eng", "daniel");
		UserID J = new UserID("david", "jjj", "johndoe");
		UserID quin = new UserID("qID", "qqq", "quin");
		UserID uz = new UserID("uzr", "use", "Jamie");
		activeUsers.add(J);
		activeUsers.add(uz);
		UID.put("dan", dan);
		UID.put("qID", quin);
		UID.put("david", J);
		UID.put("uzr", uz);
		GameID G1 = new GameID("TYPE2");
		G1.users.add(J);
		G1.users.add(uz);
		J.gid = G1;
		uz.gid = G1;
		GID.put("1", G1);
		activeGames.add(G1);
		//Object O 
		//john =  UID.get("dan");
		//System.out.println(john.username);
		Initialize();
		
	}

}
