package client;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Ball  {
	
	private boolean game_over = false;
	private int updtnet;
	private int getnet;
	public ArrayList<Integer> xarray;
	public ArrayList<Integer> yarray;

	boolean record = true;
	private int lives = 3;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private String username;
	private String GameID;
	
	public String getGameID() {
		return GameID;
	}

	public void setGameID(String gameID) {
		GameID = gameID;
	}

	private double gravity = 10;
	private double energyloss = 1;
	private double xFrict = 1;
	private double dt = .18;
	public double getDt() {
		return dt;
	}

	public void setDt(double dt) {
		this.dt = dt;
	}

	private int x = 400;
	private int y = 25;
	private double dx = 0;
	private double dy = 0;
	private double gameDy = -111;
	private int radius = 18;
	private int agility = 2;
	private int maxSpeed = 9;
	
	public Ball()	{
		
	}

	public Ball(int i, int j)	{
		x = i;
		y = j;
		GameID = new String();
		getnet = 0;
		updtnet = 0;
	
	}
	public double getGameDy() {
		return gameDy;
	}
	public void setGameDy(double gameDy) {
		this.gameDy = gameDy;
	}
	public int getRadius() {
		return radius;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public double getDx() {
		return dx;
	}
	public double getDy() {
		return dy;
	}
	public double getGravity() {
		return gravity;
	}
    public void setX(int x) {
		this.x = x;
	}
    public void setY(int y) {
		this.y = y;
	}
    public void setDx(double dx) {
		this.dx = dx;
	}
    public void setDy(double dy) {
		this.dy = dy;
	}
    public void setGravity(double gravity) {
		this.gravity = gravity;
	}
    
    
	public int getAgility() {
		return agility;
	}

	public void setAgility(int agility) {
		this.agility = agility;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public void moveRight()	{
		if(dx<0) {
			dx=0;
		}
		if(dx+agility < maxSpeed)	{
			//store this information on the server
			dx+=agility;
		}
	}
	
	public void moveLeft()	{
		if(dx>0) {
			dx=0;
		}
		if(dx-agility > -maxSpeed)	{		
			//store this information on the server
			dx-=agility;
		}
	}
	
public void update(Main main)	{
	if(x+dx > main.getWidth()-radius-1)   {
		x = main.getWidth()-radius-1;
		dx = -dx;
	}else if(x+dx < 0+radius){
		x = 0+radius;
		dx = -dx;
	}else  {
	
		x += dx;
	}
	
	
	
	
	if(y == main.getHeight()-radius-1)	{
		dx *= xFrict;
		if(Math.abs(dx) < .8)	{
			dx = 0;
		}
	}
	
	if(y > main.getHeight()-radius-1)   {
		y = main.getHeight() - radius- 1;
		dy *= energyloss;
		dy = gameDy;
		if(getLives()>0) {
			setLives(getLives() - 1);
		}
		if(getLives() == 0) {
			
			if(record){
				record = false;
				try {
					String hostname = "localhost";//args[0];
					int port = 50007;//Integer.parseInt(args[1]);
					
					//SCORE|HIGH|PUT/GET|UID|SCORE
					//SCORE|ADD|PUT/GET|UID|SCORE
					Socket sock = new Socket(hostname,port);
					BufferedOutputStream sout = new BufferedOutputStream(sock.getOutputStream());
					BufferedInputStream sin = new BufferedInputStream(sock.getInputStream());

						String send = "SCORE|";//GAME|DFLT|"+uzrname+"|~";
						send+="ALL"+"|";
					
						send+="PUT"+"|" + main.uzrname+"|" +String.valueOf(main.score)+"|~";	
						sout.write(send.getBytes());
						sout.flush();
						
//		System.out.println("Sent:"+updtstr);
						
						byte[] servread = new byte[1024];
						
						sin.read(servread);
						String stread = new String(servread);
						//System.out.println("Read:"+(stread));
						String[] array = stread.split("\\|");
						
//	System.out.println("Received: " + stread );
					if(array[0].equals("OK"))	{
					//	System.out.println("Success !!");
						for(int i =1;i<array.length-1;i++)	{
							//System.out.println(array[i]);
						}
					}
					
					sock.close();
					sin.close();
					sout.close();
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			
			
			
			}
			
			
			
			game_over = true;
		}
	} else  {
		//velocity
		dy += gravity*dt;
		//position
		y += dy*dt + .5*gravity*dt*dt;
	}			
	if(y <= radius+dy)	{

		//y = (int)(radius+dy);
		dy = 20;
		
		
	}

	

	// TODO Auto-generated method stub
	
/*
	Thread thread = new Thread(this);
	thread.start();
	*/
}

	public void paint(Graphics g, Pictures pic)	{
		
		if(game_over) {
			g.setColor(Color.WHITE);
			g.drawString("Game Over", 300, 300);
		}
		
		String send = "UPDT|" + pic.username + "|" + String.valueOf(x) + "|" + String.valueOf(y)+"|~";
		String hostname = "localhost";//args[0];
		int port = 50007;//Integer.parseInt(args[1]);
		updtnet++;
		try	{
		
		//	xarray.clear();
		//	yarray.clear();
			Socket sock = new Socket(hostname,port);
			sock.setSoTimeout(2000);
			BufferedInputStream sin = new BufferedInputStream(sock.getInputStream());
			BufferedOutputStream sout = new BufferedOutputStream(sock.getOutputStream());
			sout.write(send.getBytes());
			sout.flush();
			
			//System.out.println("Sent:"+send);
			
			byte[] serv = new byte[1024];
			xarray = new ArrayList<Integer>();
			yarray = new ArrayList<Integer>();
			sin.read(serv);
			String stread = new String(serv);
		//	System.out.println("Read:"+(stread));
			String[] array = stread.split("\\|");
			if(array[0].equals("OK"))	{
				//System.out.print(stread);
				int numusers = Integer.parseInt(array[1]);
				int counter = 2;
				for(int i = 0; i<numusers;i++)	{
					int curx = Integer.parseInt(array[counter]);
					xarray.add(curx);
					counter++;
				}
				for(int i = 0; i<numusers;i++)	{
					int cury = Integer.parseInt(array[counter]);
					yarray.add(cury);
					counter++;
				}
			} else	{
				System.out.println("Error");
			}		
		/*
		getnet++;
		pic.network++;
	//if(pic.network%1000==0){
		//String hostname = "localhost";//args[0];
	//	int port = 50005;//Integer.parseInt(args[1]);
		xarray = new ArrayList<Integer>();
		yarray = new ArrayList<Integer>();
		 send = "GET|" + pic.GameID+"|" + pic.username + "|~";
		xarray.clear();
		yarray.clear();
		
		//	Socket sock = new Socket(hostname,port);
			sock.setSoTimeout(100);
		//	BufferedInputStream sin = new BufferedInputStream(sock.getInputStream());
	//		BufferedOutputStream sout = new BufferedOutputStream(sock.getOutputStream());
			sout.write(send.getBytes());
			sout.flush();
			
			//System.out.println("Sent:"+send);
			
			byte[] serv2 = new byte[1024];
			
			sin.read(serv2);
			 stread = new String(serv2);
		//	System.out.println("Read:"+(stread));
		    array = stread.split("\\|");
			if(array[0].equals("OK"))	{
				System.out.print(stread);
				int numusers = Integer.parseInt(array[1]);
				for(int i = 2; i<array.length;i+=2)	{
					int curx = Integer.parseInt(array[i]);
					xarray.add(curx);
					int cury = Integer.parseInt(array[i+1]);
					yarray.add(cury);
					numusers--;
					if(numusers == 0)   {
						break;
					}
				}			
			} else	{
				System.out.println("Error");
			}
			*/
			for(int i=0; i<xarray.size();i++){
				//g.setColor(Color.RED);
				
			//	g.fillOval(xarray.get(i)-radius, yarray.get(i)-radius, radius*2, radius*2);//x,y are starting points (0,0) is top left
			if(i==0){
				g.drawImage(pic.ballplanet, xarray.get(i)-radius, yarray.get(i)-radius, pic.main);
			}else if(i==1){
				g.drawImage(pic.ballplanet2, xarray.get(i)-radius, yarray.get(i)-radius, pic.main);
			}else{
				g.drawImage(pic.ballplanet3, xarray.get(i)-radius, yarray.get(i)-radius, pic.main);

			}
			}
			sin.close();
			sout.close();
			sock.close();
	
		} catch(Exception e)	{
			e.printStackTrace();
		}
		
		
	
//		g.setColor(Color.CYAN);
		//g.fillOval(x-radius, y-radius, radius*2, radius*2);//x,y are starting points (0,0) is top left

	}

	public boolean getGameOver() {
		// TODO Auto-generated method stub
		return game_over;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}
}
