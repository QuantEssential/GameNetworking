package client;

import java.applet.AudioClip;
import java.awt.Image;
import java.net.URL;

public class Pictures {
	
	   public String username;
	   public String GameID;

	   public int network;
	   public Main main;
	   public Image platform,p2,p3,p4,p5, ball, ballplanet,ballplanet2,ballplanet3;
	   URL url;
	   AudioClip music;
	   int level = 1;
	   public int height;
	   public int score;
	   
	public Pictures(Main m)	{
		try{
			url = m.getDocumentBase();
		} catch(Exception e)	{
			e.printStackTrace();
		}
		//music = main.getAudioClip(url,"Music/audioclip.au");
		height=0;
		main = m;
		platform = m.getImage(url, "images/fractal6.png");
		p2 =  m.getImage(url, "images/fractal5.png");
		p3 =  m.getImage(url, "images/fractal3.png");
		p4 =  m.getImage(url, "images/fractal7.png");
		p5 =  m.getImage(url, "images/fractal8.png");
		ball =  m.getImage(url, "images/ball.png");
		ballplanet =  m.getImage(url, "images/BALLPLANET.png");
		ballplanet2 =  m.getImage(url, "images/PLANETBALL2.png");
		ballplanet3 =  m.getImage(url, "images/newballplanet.png");

	}

}
