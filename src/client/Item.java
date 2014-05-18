package client;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Item {

	private int x, y, dx, radius;
	private Main main;
	private boolean bool = false;
	
	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public Item(int x)	{
		this.x = x;
		dx = -2;
		radius = 24;
		Random r = new Random();		
		y = r.nextInt(400) + radius;
	}
	
	public void update(Main main, Ball b)	{
		this.main = main;
		x += dx;
		checkForCollision(b);
		if(x < 0-radius) {
			bool = true;
		}
	}

	public void checkForCollision(Ball b)	{
		int ballX = b.getX();
		int ballY = b.getY();
		int ballR = b.getRadius();
		
		int a  = x - ballX;
		int j = y - ballY;
		int col = radius + ballR;
		double c = Math.sqrt((double)(a*a) + (double) (j*j));
		if(c<col)	{
			performAction(b);
			bool = true;
		}
	/*	if(ballY+radius > y && ballY+radius < y+height)	{
			if(ballX> x && ballX<x+width){
				double newdy = b.getGameDy();
				b.setDy(newdy);
			}
		}*/
	}
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void performAction(Ball b)	{
		int test = b.getLives() + 1;
		if(test>1) {
			b.setLives(test);
		}
	}
	
	
	
	public void paint(Graphics g, Pictures pic) {

		//g.setColor(Color.RED);
		g.drawImage(pic.ball, x-radius, y-radius, pic.main);

		//g.fillOval(x-radius, y-radius, radius*2, radius*2);//x,y are starting points (0,0) is top left

//		g.fillRect(x, y, width, height);
		//g.drawRect(x,y,width,height);
	}
}
