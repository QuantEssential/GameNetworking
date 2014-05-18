package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.Random;
	
public class Platform {
	

	private int dx;
	private double dy;
	private int x, y, width, height;
	URL url;
	Image platform;
	int frame;
	int c;
	public Pictures pic;
	
	public Platform()	{
		dx = -1;
		dy = -1;
		x = 300;
		y = 300;
		width = 177;
		height = 67;
	}
	
	public Platform(int x, int y, Pictures p) {
		this.x = x;
		this.y = y;
		width = 109;
		height = 34;
		dy = .01;

		dx = -1;
		pic=p;
		platform = p.platform;
		frame = 0;
	}
	
	public Platform(int x, int y, int rand,Pictures p) {
		pic=p;
		this.x = x;
		this.y = y;
		dy = 0;
		dx = -1;	
		frame = 0;

		if(rand==0)	{
			width = 109;
			height = 34;
			platform = pic.platform;
		} else if(rand==1){
			width = 81;
			height = 31;
			platform = pic.p3;
		} else if(rand==2){
			width = 80;
			height = 29;
			platform = pic.p2;
		} else if(rand==3)	{
			platform = pic.p4;
			width = 89;
			height = 31;
		} else if(rand==4)	{
			platform = pic.p5;
			width = 128;
			height = 64;
		}
		
	}

	public void update(Main main, Ball b)	{
		x += - pic.level;
		y+=dy;
		checkForCollision(b);
		/*if(x < 0-width) {
			//Random r = new Random();
			pic.height-=120;
		
			int test = Math.abs( pic.height/2);
			int test2 = Math.abs(main.getHeight());
			if(test>test2)	{
				pic.height=0;
			}
			y = main.getHeight() -40 + pic.height;
			x = main.getWidth() +pic.level*100;
			
		}*/
	}

	public void checkForCollision(Ball b)	{
		int ballX = b.getX();
		int ballY = b.getY();
		int radius = b.getRadius();
		
		if(ballY+radius > y && ballY-radius < y+height)	{
			if(ballX+radius> x && ballX-radius<x+width){
				//Pictures.bounce.play();
				double newdy = b.getGameDy();
				b.setDy(-77);
				/*if(ballY-radius < y+height/2)	{
					b.setDy(77);
					//b.setY(y+height);
				}
				else   {
					b.setDy(-77);
					//b.setY(y);
				}*/
				//while(ballY+radius > y && ballY+radius < y+height && ballX> x && ballX<x+width)	{
					
				//}
				//b.setDx(-b.getDx());

			}
		}
		/*
		if(ballX+radius > x && ballX-radius < x+width){
			if(ballY-radius > y && ballY+radius < y+height)	{
				//Pictures.bounce.play();
				double newdy = b.getGameDy();
				if(b.getDx()<0)	{
					b.setDx(3);
					//b.setY(y+height);
				}
				else if(b.getDx()>0)	{
					b.setDx(-3);
					//b.setY(y);
				}
				//while(ballY+radius > y && ballY+radius < y+height && ballX> x && ballX<x+width)	{
					
				//}
				//b.setDx(-b.getDx());

			}
		}*/
	}
	
	public void paint(Graphics g) {

			
		g.setColor(Color.BLUE);
		g.fillRect(x, y, width, height);
		//g.drawRect(x,y,width,height);
		//g.drawImage(platform,  x, y, Pictures.main);
		frame = 0;
		g.drawImage(platform, x, y, x+width, y+height, 0, height*frame, width, height + height*frame, pic.main);
		if(frame == 1){
			frame = 0;
		} else	{
			frame++;
		}
	}
}
