package server;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import datalayer.Database.GameID;
import datalayer.Database.UserID;


public class Server {
	

	Server(int port) throws Exception {
		Start(port);
	}
	
	
	
	private void Start(int port) throws Exception {
		
		int len = 128;
		int udpport = 50009; 
		DatagramSocket sockudp = new DatagramSocket();
		sockudp.setSoTimeout(200);
		System.out.println("TCP Server Socket Created");
		System.out.println("UDP Client Socket Created");

		InetAddress addr =  InetAddress.getByName("localhost");


		// Selector
		
		Selector theSelector = Selector.open(); //SelectorProvider.provider().openSelector();
		
		// Server socket channel
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false); // make non-blocking
		
		// bind to local address
		InetSocketAddress isa = new InetSocketAddress(port);
		ssc.socket().bind(isa);
	
		
		//System.out.println("Starting on "+port);
		
		// register the server socket with the selector
		SelectionKey acceptKey = ssc.register(theSelector, SelectionKey.OP_ACCEPT);
		
		int keysAdded = 0;
		
		// while loop
		while ((keysAdded = theSelector.select())>0) {
			
		//	System.out.println("Select returned");
			
			// get the ready keys
			Set<SelectionKey> readyK = theSelector.selectedKeys();
			// get an iterator on the set of keys
			Iterator<SelectionKey> i = readyK.iterator();
			
			// process the ready keys
			while (i.hasNext()) {
				SelectionKey sk = (SelectionKey)i.next(); // get the next key
				i.remove(); // remove from the ready set
				
				// check if it is the serversocket
				if (sk.isAcceptable()) {
					//System.out.println("Process accept");
					
					// Get the ServerSocketChannel
					ServerSocketChannel nssc = (ServerSocketChannel)sk.channel();
					
					// accept the connection
					SocketChannel sc = nssc.accept();
					
					// Configure the SocketChannel as non blocking
					sc.configureBlocking(false);
					
					// register the SocketChannel with the selector
					sc.register(theSelector, SelectionKey.OP_READ);
					
				} else if (sk.isReadable()) { // if a readable key
					//System.out.println("Process read");
					
					// get the SocketChannel
					SocketChannel sc = (SocketChannel)sk.channel();
					
					// Create a ByteBuffer for the data
					ByteBuffer dst = ByteBuffer.allocate(1000);
					
					// read the data from the channel
					int numread = sc.read(dst);
					if (numread>0) {
						//System.out.println("Read:"+dst.toString());
						System.out.println("Data Read from TCP:"+new String(dst.array()));
						String stread = new String(dst.array());
						String[] array = stread.split("\\|");


						if(array[0].equals("CREA")||array[0].equals("SRCH")||array[0].equals("JOIN")||array[0].equals("SCORE")) {
						
								
								byte[] packetrec = new byte[len];
								byte[] packet = new byte[len];
								packet = stread.getBytes();
								DatagramPacket senddata = new DatagramPacket(packet, packet.length, addr, udpport);
								
								sockudp.send(senddata);

								DatagramPacket recfirstdata = new DatagramPacket(packetrec,packetrec.length);

								sockudp.receive(recfirstdata);
								String recstring1 = new String(recfirstdata.getData());
								System.out.println("Received from data Layer: " + recstring1 );
								String[] UDParray = recstring1.split("\\|");
								dst.clear();
								String retmsg = "";
								if(array[0].equals("CREA") && !array[1].equals("USER") && UDParray[0].equals("OK"))	{
									
									String gid = UDParray[3];
									String uid = array[3];
									usergame.put(uid,gid);//maps userid to gameID
									GameInfo ginfo = new GameInfo();
									ginfo.initX(uid,"400");
									ginfo.initY(uid,"25");
									games.put(gid, ginfo);  //maps gameID to GameInfo
								} else if(array[0].equals("JOIN") && UDParray[0].equals("OK")) {
									String uid = array[1];
									String gid = UDParray[3];
									if(games.containsKey(gid)) {
										GameInfo ginfo = games.get(gid);
										ginfo.initX(uid,"400");
										ginfo.initY(uid,"25");	
										usergame.put(uid, gid);
									} else	{
										retmsg = "ERROR GAMES DOESNT EXIST";
									}
								}
								//if(UDParray[0].equals("OK")) {
									/* retmsg = "OK";
									for(int j =1;j<UDParray.length;j++)	{
										retmsg+= "|" + UDParray[j];
									}
									retmsg+="|~";*/
									retmsg = recstring1;
								//}
								try {
									dst.put(retmsg.getBytes());
								} catch(BufferOverflowException e)	{
									e.printStackTrace();
								}


								System.out.println("Sending: " +retmsg);

								dst.flip();
								sc.write(dst);
							} 
						
						else if(array[0].equals("START")||array[0].equals("UPDT")) {//array[0].equals("GET") ||
							
							String ret = "";
							
							if(array[0].equals("UPDT")) {
								//	if(array.length == 4) {
										ret = updtprocess(array);
									//} else
									{
									//	ret = "ERROR|INVALID ARG NUMBER";
									}
								}/*
							 else if(array[0].equals("GET")) {
									//if(array.length == 5||array.length == 6){
										ret = getprocess(array);
									//} else
									{
									//	ret = "ERROR|INVALID ARG NUMBER";
									}
								}*/	else if(array[0].equals("START")) {
									//if(array.length == 5||array.length == 6){
									ret = startprocess(array);
								//} else
								
								//	ret = "ERROR|INVALID ARG NUMBER";
							}
								else	{
									ret = "ERROR";
								}
							dst.clear();

							ret+="|~";
							try {
								dst.put(ret.getBytes());
							} catch(BufferOverflowException e)	{
								e.printStackTrace();
							}
							
							dst.flip();
							sc.write(dst);
							System.out.println("Sending: " + ret);
							
						}
/*
						} else	{
							//error message
						}
						
					*/	
						
						// flip the buffer to prepare it for writing
						// resets the position to 0 and the limit to
						// the previous position
						// only needed as we are reusing the ByteBuffer
						/*dst.flip();
						System.out.println("Read:"+dst.toString());
					
						// write the buffer to the ScoketChannel
						sc.write(dst);*/
					} else { // close
						System.out.println("Close client socket");
						
						// close the SocketChannel
						sc.close();
						
						// unregister the key from the set monitored by the selector
						// places on the canceled key list
						sk.cancel();
						
					}
				}
			}
		}
		
		System.out.println("Done");
		ssc.close();
		acceptKey.cancel();

	}


	public  class GameInfo	{
		
		public  HashMap<String, String> xlocation;
		public  HashMap<String, String> ylocation;
		
		GameInfo()	{
			ylocation = new HashMap<String, String>();
			xlocation = new HashMap<String, String>();
		}
		
		public void initX(String uid, String xloc)	{
			xlocation.put(uid, xloc);
		}
		public void updtX(String uid, String xloc)	{
			xlocation.remove(uid);
			xlocation.put(uid, xloc);
		}
		
		public void initY(String uid, String yloc)	{
			ylocation.put(uid, yloc);
		}
		public void updtY(String uid, String yloc)	{
			ylocation.remove(uid);
			ylocation.put(uid, yloc);
		}
		
		public String getsize()	{
			return String.valueOf(xlocation.size());
		
		}
		
		public String getX(String uid)	{
			//String r = xlocation.get(uid);
			String r = "";
			for(String value: xlocation.values()){
			  // if(uid.equals(xlocation.get(uid)))	{
					r+="|"+value;
			//	}
			}
			return r;
		}
		
		public String getY(String uid)	{
			String r = "";
			for(String value: ylocation.values()){
				//if(uid.equals(ylocation.get(uid)))	{
					r+="|"+value;
			//	}
			}
			return r;
		}
		
	}
	
	
	public static boolean isInt(int n) {
		   return n % 1 == 0;
		}
	
	
	public static String updtprocess(String[] s)	{
		String uid = s[1];
		String gid = usergame.get(uid);
		GameInfo gTest = games.get(gid);
		
		int x = Integer.parseInt(s[2]);
		int y = Integer.parseInt(s[3]);
		if(isInt(x) && isInt(y))	{
			
			gTest.updtX(uid, s[2]);
			gTest.updtY(uid, s[3]);
		}
		
		String r = "OK|";
		r+= gTest.getsize();
		
		//if(!U.username.equals(s[2]))	{
		
				r += gTest.getX(uid);
				r += gTest.getY(uid);
	
		
		
		
		return r;
	}
	

	public static String startprocess(String[] s){
		
		String r = "";
	
		if(s[1].equals("SET"))	{
			started.put(s[2], "true");
			r = "OK";
		} 	else if(s[1].equals("GET"))	{
			
			if(started.containsKey(s[2]))	{
				String b = started.get(s[2]);		
			if(b.equals("true"))	{
				 r = "OK";
			} else	{
				 r = "FALSE";
			}
		  }
			else	{
				r = "ERROR GAME NOT INITIALIZED";
			}
		}
		else	{
			return "ERROR INVALID GET/SET ARG";
		}
		
		return r;
	}
	

	public static HashMap<String, String> usergame;
	public static HashMap<String, String> started;
	public static HashMap<String, GameInfo> games;
	//public static HashMap<String, String> ylocation;

	

	public static int port;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		started = new HashMap<String, String>();
		usergame = new HashMap<String, String>();
		games = new HashMap<String, GameInfo>();
		 port = 50007; // Integer.parseInt(args[0]);
		
		try {
			Server t = new Server(port);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
