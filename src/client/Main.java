package client;
import java.applet.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.security.AccessController;
import java.util.Random;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketPermission;

public class Main extends Applet implements Runnable, KeyListener, MouseListener {
	

	private  String username;
	private  String GameID;
	private Image i;
	private Graphics gbuff;
	Ball b, b2;
	Platform p[];
	Item item[];
	public int score;
	double cityX = 0;
	double cityDX = 1.11;
	double cityY = 0;
	double cityDY = 0;
	URL url;
	Image city;
	boolean START =false;
	boolean lout = true;
	int levelcheck = 0;
	boolean mouseIn = false;
	boolean gameOver = false;
	public String uzrname = "";
	public boolean mouse = false;
	
	public int getScore() {
		return score;
	}
	
	
	int leveloop = 0;

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public void init() {}
	
	@Override
	public void start() {
		
		
		
		

		
		
		String hostname = "localhost";//args[0];
		int port = 50007;//Integer.parseInt(args[1]);
		

		try {
			SocketPermission sp = new SocketPermission(hostname,"connect");
			AccessController.checkPermission(sp);
			
			Socket sock = new Socket(hostname,port);
			
			BufferedInputStream sin = new BufferedInputStream(sock.getInputStream());
			BufferedOutputStream sout = new BufferedOutputStream(sock.getOutputStream());
			
			BufferedReader input =new BufferedReader(new InputStreamReader(System.in));	
			
			boolean accessgranted = false;
			String localhost = "localhost";
			
			String send; boolean exit = false;
			boolean srch = false;	
			
			while(!accessgranted) {
				System.out.println("New User? Type 'Y' to create an account, or any key to access yours: ");
				String in = input.readLine().toUpperCase();
				
				if(in.equals("Y")) {
					send = "CREA|USER|";
					System.out.println("Username?");
					uzrname = input.readLine();
					username = uzrname;
					send+=uzrname+"|";
					System.out.println("Name?");
					send+=input.readLine()+"|";
					System.out.println("Password?");
					send+=input.readLine()+"|~";				
					
				}
				else
				{
					send = "SRCH|USR|";
					System.out.println("Username?");
					uzrname = input.readLine();
					send+=uzrname+"|";
					System.out.println("Password?");
					send+=input.readLine()+"|~";
					srch=true;
				}
			
				sout.write(send.getBytes());
				sout.flush();
				
				
				byte[] serv = new byte[1024];
				
				sin.read(serv);
				String stread = new String(serv);
				String[] array = stread.split("\\|");
				
				if(array[0].equals("OK"))	{
					for(int i =1;i<array.length-1;i++)	{
						System.out.println(array[i]);
					}
					
					System.out.println("access granted");
					accessgranted = true;
					break;
				}
				else {
					if(array.length>1)
					{
						System.out.println(array[1]);
					}
				}
				
			}
		
			
			
			while(!exit)
			{	
				System.out.println("SRCH to search Users, SCORE to view history, CREA to create a game, JOIN to join a game, L to Logout and Exit: ");
				 String in = input.readLine().toUpperCase();
				 if(in.equals("X"))  {
					 exit = true;
					 lout=false;
					 stop();
					 System.exit(0);

					 break;
				 } 
				//SCORE|HIGH|PUT/GET|UID|SCORE
					//SCORE|ADD|PUT/GET|UID|SCORE
				 else if(in.equals("SCORE"))  {
					 
					 send = "SCORE|";//GAME|DFLT|"+uzrname+"|~";
					 System.out.println("ALL for complete history, HIGH for HighScore");
						send+=input.readLine()+"|";
					
						send+="GET"+"|" + uzrname+"|~";	
						sout.write(send.getBytes());
						sout.flush();
												
						byte[] servread = new byte[1024];
						
						sin.read(servread);
						String stread = new String(servread);
						//System.out.println("Read:"+(stread));
						String[] array = stread.split("\\|");
						
				//	System.out.println("Received: " + stread );
					if(array[0].equals("OK"))	{
					//	System.out.println("Success !!");
						for(int i =1;i<array.length-1;i++)	{
							System.out.println(array[i]);
						}
					}
				 }
				 else if(in.equals("CREA"))  {
					 
					 send = "CREA|GAME|DFLT|"+uzrname+"|~";
					 sout.write(send.getBytes());
						sout.flush();
						
				//		System.out.println("Sent:"+updtstr);
						
						byte[] serv = new byte[1024];
						
						sin.read(serv);
						String stread = new String(serv);
						//System.out.println("Read:"+(stread));
						String[] array = stread.split("\\|");
						
				//	System.out.println("Received: " + stread );
					if(array[0].equals("OK"))	{
					//	System.out.println("Success !!");
						for(int i =1;i<array.length-1;i++)	{
							System.out.println(array[i]);
						}
						
						GameID = array[3];
		
						System.out.println("Once All Users Have Joined, Press Any Key to Start Game");
						input.readLine();
						START = true;
						 send = "START|SET|"+GameID+"|~";
						 sout.write(send.getBytes());
							sout.flush();
							
					//		System.out.println("Sent:"+updtstr);
							
							byte[] serv3 = new byte[1024];
							
							sin.read(serv3);
							String stread3 = new String(serv3);
							//System.out.println("Read:"+(stread));
							String[] array3 = stread3.split("\\|");
							
				System.out.println("Received: " + stread3 );
						if(array3[0].equals("OK"))	{					
							exit=true;
						}
						else	{
							//error
							//System.out.println("Failed to start");	
						}
					}
					else {
						System.out.println("Failed");	
					}
					 
				 } 
				 else if(in.equals("SRCH") || in.equals("JOIN") ||  in.equals("L")) {
					 String srchstr = "";
					 String inread = "";
					
					 if( in.equals("JOIN")) {
						 srchstr = "SRCH|GAME|" + uzrname + "|";
						 System.out.println("Press any key to search all available games: ");
						 inread = input.readLine();
						 srchstr +=  "ALL|~";
					 } else if( in.equals("L")) {
						 srchstr = "SRCH|USR|" + uzrname + "|";
						 srchstr +=  "LOUT|~";
						 exit = true;
						 sout.write(srchstr.getBytes());
							sout.flush();
							
					//		System.out.println("Sent:"+updtstr);
							
							byte[] serv = new byte[1024];
							
							sin.read(serv);
							String stread = new String(serv);
							//System.out.println("Read:"+(stread));
							String[] array = stread.split("\\|");
							
					//	System.out.println("Received: " + stread );
						if(array[0].equals("OK"))	{
						//	System.out.println("Success !!");
							for(int i =1;i<array.length-1;i++)	{
								 lout=false;							}
								System.exit(0);
						}
						 
					 }
					 else {
						 srchstr = "SRCH|USR|" + uzrname + "|";
						 System.out.println("Type 'ACTV' to list all active users, and 'ALL' to list all users: ");
						  inread = input.readLine();
					 
					 
					 if(inread.equals("ACTV") || inread.equals("ALL"))	{
						 if(inread.equals("ACTV")) {
							 
							 srchstr +=  "ACTV|~";

						 }
						 else if(inread.equals("ALL")){
							 
							 srchstr +=  "ALL|~";
							
						 }
					   }
					 }
				  

					 sout.write(srchstr.getBytes());
						sout.flush();
						
				//		System.out.println("Sent:"+updtstr);
						
						byte[] serv = new byte[1024];
						
						sin.read(serv);
						String stread = new String(serv);
						//System.out.println("Read:"+(stread));
						String[] array = stread.split("\\|");
						
				//	System.out.println("Received: " + stread );
					if(array[0].equals("OK"))	{
					//	System.out.println("Success !!");
						for(int i =1;i<array.length-1;i++)	{
							System.out.println(array[i]);
						}
					}
					else {
						System.out.println("Failed");	
					}

					//	System.out.println("Received: " + stread );
						
							
							
							 if( in.equals("JOIN")) {
								 System.out.println("Enter the Game ID you would like to join, X not to join:");
								 String reading = input.readLine();
								 if(!reading.equals("X"))  {
									 
									 send = "JOIN|"+uzrname+"|"+reading+"|~";

									 	sout.write(send.getBytes());
										sout.flush();
										
								//		System.out.println("Sent:"+updtstr);
										
										byte[] serv2 = new byte[1024];
										
										sin.read(serv2);
										String stread2 = new String(serv2);
										//System.out.println("Read:"+(stread));
										String[] array2 = stread2.split("\\|");
										
								//	System.out.println("Received: " + stread );
									if(array2[0].equals("OK"))	{
									//	System.out.println("Success !!");
										for(int i =1;i<array2.length-1;i++)	{
											System.out.println(array2[i]);
										}
										
										GameID = array2[3];
										//System.out.println("Waiting for Creator to Begin Game");
									while(true)	{
										send = "START|GET|"+GameID+"|~";
										 sout.write(send.getBytes());
											sout.flush();
									//		System.out.println("Sent:"+updtstr);
											
											byte[] serv3 = new byte[1024];
											
											sin.read(serv3);
											String stread3 = new String(serv3);
											//System.out.println("Read:"+(stread));
											String[] array3 = stread3.split("\\|");
									//		System.out.println(stread3);
										//System.out.println("Received: " + stread3 );
										if(array3[0].equals("OK"))	{					
											exit=true;											
											START = true;

											break;
										}
										else	{
											//error
											//System.out.println("Failed to start");	
										}
									 }
									}
									else {
										System.out.println("Failed");	
									}
									 
										
										
								 }
							 }
							
							
							
						}
						 else  {
						 System.out.println("Sorry, invalid input");
					 }
				}

			
			sin.close();
			sout.close();
			input.close();
			
			sock.close();
			
			
		
		
		}catch(Exception e)	{
			e.printStackTrace();
		}
		
		
		
	
		try{
			url = getDocumentBase();
		} catch(Exception e)	{
			e.printStackTrace();
		}
		city = getImage(url, "images/DragWorld1.png");
//called first time application loads
		setSize(800, 600);
		addKeyListener(this);
		addMouseListener(this);
		//Pictures.music.loop();
	
		
		
		
	  if(START)	{
		pic = new Pictures(this);
		pic.username = uzrname;
		pic.GameID	= GameID;
		pic.network =0;
		
		
		score = 0;
		int platnum = 1000;
		int itemnum = 11;
		p = new Platform[platnum+10];
//		b2 = new Ball(250, 250);

		b = new Ball();
		
		int numtilepics = 3;
		b.setUsername(username);
		b.setGameID(GameID);
		item = new Item[itemnum];
		for(int i =p.length-10; i<p.length;i++)	{
			
			p[i] =  new Platform(100*(p.length-i), 300,pic);
		}
		for(int i =0; i<p.length-10;i++)	{
			Random r = new Random();
			int testy = i*80;
			int div = getHeight();
			testy = testy%div;
			testy *= i;
			testy = testy%div;
			testy = getHeight()-testy;

			p[i] = new Platform(i*333,testy ,r.nextInt(numtilepics), pic);
					//r.nextInt(2400) , getHeight() -40- r.nextInt(400), r.nextInt(numtilepics));
		}
		for(int i =0; i<item.length;i++)	{
		/*	Random r = new Random();
				switch(r.nextInt(5)){
				case 0:
					item[i] = new GravityUp(getWidth() + 5 * r.nextInt(500));
					break;
				case 1:
					item[i] = new GravityDown(getWidth() + 10 * r.nextInt(500));
					break;
				case 2:
					item[i] = new AgilityUp(getWidth() + 15 * r.nextInt(500));
					break;
				case 3:
					item[i] = new AgilityDown(getWidth() + 5 * r.nextInt(500));
					break;
				case 4:
					item[i] = new ScorePlus(getWidth() + 20 * r.nextInt(500), this);
					break;
				}*/
	
			item[i] = new Item(getWidth() + 500 * i);

			
		}
		Thread thread = new Thread(this);
		thread.start();
		
//called after init
		/*Thread t = new Thread()	{
			public void run()	{
				while(true)	{
				repaint();
					Thread.sleep(17);
				}
			}
		};*/
	  }
	}
	
	public Pictures pic;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//thread INFO
	
		while(true)	{
			
			gameOver = b.getGameOver();
			
			levelcheck++;

			if(levelcheck-leveloop == 500)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/DragonWorld2.png");

			} else 	if(levelcheck-leveloop == 1000)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/PlayerWorld1.png");

			} else 	if(levelcheck-leveloop == 5000)  {
				leveloop=5000;
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/DragonWorld4.png");

			} else 	if(levelcheck-leveloop == 4500)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/DragonWorld5.png");

			} else 	if(levelcheck-leveloop == 2500)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/DragonWorld6.png");

			}
			else 	if(levelcheck-leveloop == 3000)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/DragonWorld7.png");

			}else 	if(levelcheck-leveloop == 4000)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/DragonWorld3.png");

			}
			else 	if(levelcheck-leveloop == 3500)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/Playerworld3space.png");

			}
			else 	if(levelcheck-leveloop == 2000)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/PlayerWorld4.png");

			}
			else 	if(levelcheck-leveloop == 1500)  {
				//levelcheck = 0;
				pic.level ++;
				city = getImage(url, "images/PlayerWorld5.png");

			}
						
			if(!gameOver) {
				score++;
			}
			
			if(cityX>= getWidth() * -2) {
				cityX-= cityDX;
			}	
			else {
				cityX = 0;
				//cityDX =0;
				//cityDY = .66;
			}
			
			if(cityY> getHeight() * -2) {
				cityY -= cityDY;

			}	
			else {
				cityY = 0;
				//cityDY=0;
				//cityDX = -.66;
			}
			
			Random r = new Random();
			
			//b2.update(this);
			for(int i=0; i<p.length; i++)	{
				p[i].update(this, b);
			}
			for(int i=0; i<item.length; i++)	{
				if(item[i].isBool())	{
					item[i] = null;
			/*		switch(r.nextInt(5)){
					case 0:
						item[i] = new GravityUp(getWidth() + 10 * r.nextInt(500));
						break;
					case 1:
						item[i] = new GravityDown(getWidth() + 10 * r.nextInt(500));
						break;
					case 2:
						item[i] = new AgilityUp(getWidth() + 10 * r.nextInt(500));
						break;
					case 3:
						item[i] = new AgilityDown(getWidth() + 10 * r.nextInt(500));
						break;
					case 4:
						item[i] = new ScorePlus(getWidth() + 10 * r.nextInt(500), this);
						break;
					}
						*/
					item[i] = new Item(getWidth() + 10 * r.nextInt(500));

					item[i].setBool(false);
				}
				item[i].update(this, b);
			}
			
			b.update(this);
			repaint();
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	@Override
	public void stop() {


	}
	
	@Override
	public void destroy() {


	}
	@Override
	public void update(Graphics g) {
		if(i==null)	{
			i=createImage(this.getSize().width, this.getSize().height);
			gbuff = i.getGraphics();
		}
		gbuff.setColor(getBackground());
		gbuff.fillRect(0, 0, this.getSize().width, this.getSize().height);
		
		gbuff.setColor(getForeground());
		paint(gbuff);
		
		g.drawImage(i,0,0,this);
		
	}
	
	@Override
	public void paint(Graphics g) {

		
			g.setColor(new Color(15, 77, 147));
			g.fillRect(0, 0, getWidth(),getHeight());
			g.drawImage(city,(int)cityX,(int)cityY,this);
			for(int i=0; i<p.length; i++)	{
				p[i].paint(g);
			}
			for(int i=0; i<item.length; i++)	{
				item[i].paint(g,pic);
			}
			b.paint(g, pic);
		//	b2.paint(g);

			pic.score = score;
			String s = Integer.toString(score);
			String l = "LIVES: " + Integer.toString(b.getLives());
		
			Font f  = new Font("Serif",Font.BOLD, 18);
			g.setFont(f);
			g.drawString("QUIT", getWidth()-66, 50);
			g.drawRect(getWidth()-70,33, 55, 25);
			f  = new Font("Serif",Font.BOLD, 24);
			g.setFont(f);
			g.setColor(Color.BLACK);
			g.drawString(s, getWidth()-149, 51);
			g.setColor(new Color(198, 50, 50));
			g.drawString(s, getWidth()-150, 50);
			g.setColor(Color.WHITE);

			g.drawString(l, getWidth()-149, 100);

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode())	{
		case KeyEvent.VK_LEFT:
			b.moveLeft();
			break;
		case KeyEvent.VK_RIGHT:
			b.moveRight();
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	//	g.drawRect(getWidth()-70,33, 55, 25);
		String hostname = "localhost";//args[0];
		int port = 50007;//Integer.parseInt(args[1]);
		
		try {
			Socket sock = new Socket(hostname, port);
			BufferedInputStream sin = new BufferedInputStream(
					sock.getInputStream());
			BufferedOutputStream sout = new BufferedOutputStream(
					sock.getOutputStream());
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			if (e.getX() > getWidth() - 70 && e.getX() < getWidth() - 70 + 55) {
				if (e.getY() > 33 && e.getY() < 58) {

					String srchstr = "SRCH|USR|" + uzrname + "|";
					srchstr += "LOUT|~";
					sout.write(srchstr.getBytes());
					sout.flush();

					//		System.out.println("Sent:"+updtstr);

					byte[] serv = new byte[1024];

					sin.read(serv);
					String stread = new String(serv);
					//System.out.println("Read:"+(stread));
					String[] array = stread.split("\\|");

					//	System.out.println("Received: " + stread );
					if (array[0].equals("OK")) {
						//	System.out.println("Success !!");
						for (int i = 1; i < array.length - 1; i++) {
							lout = false;
						}
						System.exit(0);
					}

				}
			}
		} catch (Exception e2) {
			// TODO: handle exception
		}
		
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}
