package Temple;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Temple.DestWall;
import Temple.SoundPlayer;
import Temple.TankControl;
import Temple.GameObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Scanner;

import static javax.imageio.ImageIO.read;

@SuppressWarnings("serial")
public class GameMain extends JPanel
{

	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final int MAP_WIDTH = 1152;
	public static final int MAP_HEIGHT = 832;
	private static boolean storm = false;
	
	private static BufferedImage world, screen, stormEye;
	private static BufferedImage[] gameOverScreen;
	
	private static Image backGround, wall, swordimg, goldImg, blockVer, door, boost;
	private static Image[][] move = new Image[4][4];
	private static Image[][] beetle = new Image[2][4];
	private static Image[][] scorpion = new Image[2][4];
	private static Graphics2D buffer;
	private static Graphics2D g2;
	
	private static long time;
	
	private static Wall sword;
	
	private static JFrame jf;
	private Player p1;
	
	private GameObject victoryDoor;
	private ArrayList<GameObject> gameObjs;
	private ArrayList<MovingWall> movingWalls;
	private ArrayList<NPC> NPCs;
	private ArrayList<GameObject> gold;
	private ArrayList<Wall> checkpoints;
	
	private static GameObject sandStorm;
	
	private static Image UI, livesIcon;
	
	private static int s1x, s1y; //used for screen tracking of player
	
	private final Image playerSprite[] = new Image[16];
	
	private SoundPlayer music;

    public static void main(String[] args) {
		jf = new JFrame("Pyramid Panic");
        GameMain game = new GameMain();
        game.init();   
        //initialize JFrame
        jf.addWindowListener(new WindowAdapter() {
        });
        jf.getContentPane().add("Center", game);
        jf.pack();
        jf.setSize(GameMain.SCREEN_WIDTH, GameMain.SCREEN_HEIGHT + 30);
        jf.setVisible(true);
        jf.setResizable(false);
		jf.setBackground(Color.black);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {        	
        	while (true) { 
            	game.update();
                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {

        }

    }
    
    
	public void init()
	{			
		
		//initialize structures
		music = new SoundPlayer(1,"resources/sounds/Music.wav");		
        world = new BufferedImage(GameMain.MAP_WIDTH, GameMain.MAP_HEIGHT, BufferedImage.TYPE_INT_RGB);
		gameObjs = new ArrayList<GameObject>();
		NPCs = new ArrayList<NPC>();
		movingWalls = new ArrayList<MovingWall>();
		gold = new ArrayList<GameObject>();
		checkpoints = new ArrayList<Wall>();
		
        //Read resources from storage
        try {   	       	
        	int i, j;
        	for(i =0; i< 4; i++)
        	{
        		for(j =0; j<4; j++)
        		{
            		playerSprite[i] = ImageIO.read(GameMain.class.getResource("resources/playerSprite.png")).getSubimage(j, i, 32, 48);

        		}
        	}
        	
        	for(i=0; i<4; i++)
        	{
        		move[0][i] = ImageIO.read(GameMain.class.getResource("resources/player/d" + i + ".png"));
        		move[1][i] = ImageIO.read(GameMain.class.getResource("resources/player/l" + i + ".png"));
        		move[2][i] = ImageIO.read(GameMain.class.getResource("resources/player/r" + i + ".png"));
        		move[3][i] = ImageIO.read(GameMain.class.getResource("resources/player/u" + i + ".png"));
        	}
        	
        	for(i = 0; i<4; i++)
        	{
        		beetle[0][i] = ImageIO.read(GameMain.class.getResource("resources/beetle/up/" + i + ".png"));
        		scorpion[0][i] = ImageIO.read(GameMain.class.getResource("resources/scorpion/left/" + i + ".png"));
        	}
        	for(i = 0; i<4; i++)
        	{
        		beetle[1][i] = ImageIO.read(GameMain.class.getResource("resources/beetle/down/" + i + ".png"));
        		scorpion[1][i] = ImageIO.read(GameMain.class.getResource("resources/scorpion/right/" + i + ".png"));
        	}

        	boost = ImageIO.read(GameMain.class.getResource("resources/boost.png"));
        	door = ImageIO.read(GameMain.class.getResource("resources/Door.gif"));
        	blockVer = ImageIO.read(GameMain.class.getResource("resources/Block_vert.gif"));
        	livesIcon = ImageIO.read(GameMain.class.getResource("resources/Lives.gif"));
        	UI = ImageIO.read(GameMain.class.getResource("resources/Panel.gif"));
        	stormEye = ImageIO.read(GameMain.class.getResource("resources/smallEye.png"));
        	goldImg = ImageIO.read(GameMain.class.getResource("resources/Treasure1.gif"));
            backGround = ImageIO.read(GameMain.class.getResource("resources/sand.png"));
            wall = ImageIO.read(GameMain.class.getResource("resources/wall.png"));
            gameOverScreen = new BufferedImage[2];
            gameOverScreen[0] = ImageIO.read(GameMain.class.getResource("resources/Background1.bmp"));
            gameOverScreen[1] = ImageIO.read(GameMain.class.getResource("resources/Congratulation.gif"));
            swordimg = ImageIO.read(GameMain.class.getResource("resources/Sword.gif"));
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }


		
		p1 = new Player(move[0][0], 700, 700, move, this);
		loadMap();
        
        TankControl tc = new TankControl(p1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);

        jf.addKeyListener(tc);
        sandStorm = new Wall(stormEye, p1.getX(), p1.getY());
	    time = System.nanoTime();
	}
	
	
	public void update()
	{			
	    detectCollisions();
	        
	    if (System.nanoTime() - time > 500000000)
	    {
	    	time = System.nanoTime();
	    	if(sword == null && !storm)
	    		p1.setScore(-1);
	    }

			
		//update game objects
	    int i;
	    for(i = 0; i< gameObjs.size(); i++)
	    {
	    	gameObjs.get(i).update();
	    	if(!gameObjs.get(i).getShow())
	    		gameObjs.remove(i);
	    }
	    for(i=0; i<NPCs.size(); i++)
	    {
	    	NPCs.get(i).update();
	    	if(!NPCs.get(i).getShow())
	    		NPCs.remove(i);
	    }
	    for(i=0; i<movingWalls.size(); i++)
	    {
	    	movingWalls.get(i).update();
	    }
		
		//update tanks
		p1.update();
	    repaint();
	}
	
	private void detectCollisions()
	{		
	 	   //player found the door
	 	   if(p1.getBox().intersects(victoryDoor.getBox()))
	 		   gameOver(1);
		
		//player finds sword
		if(sword != null)
		{
			if(p1.getBox().intersects(sword.getBox()))
			{
				playSound(2, "resources/sounds/PowerStart.wav");
				sword = null;
				storm = true;
			}		
		}
		
		int i;   
		p1.collided = false;
		
		//player collide walls
 	   for(i=0; i<gameObjs.size(); i++)
 	   {		   
 	 		if(p1.getBox().intersects(gameObjs.get(i).getBox()))
 	 		 {
 	 	 		 p1.collide();
 	 		 }
       }
 	   
 	   //player gets gold
 	   for(i=0; i<gold.size(); i++)
 	   {		   
 	 		if(p1.getBox().intersects(gold.get(i).getBox()))
 	 		 {
 	 	 		 p1.setScore(10);
 	 	 		 gold.remove(i);
 	 	 		 playSound(2, "resources/sounds/Treasure.wav");
 	 		 }
       }
 	   
 	   
 	   //player collide NPCs
 	   for(i=0; i<NPCs.size(); i++)
 	   {		   
 	 		if(NPCs.get(i).getBox().intersects(p1.getBox()))
 	 		 {
 	 	 		 p1.die();
 	 	 		 NPCs.get(i).collided = true;
 	 		 }
       }
 	   
 	   
 	   //Player moving Walls
 	   for(i=0; i<movingWalls.size(); i++)
 	   {		   
 	 		if(movingWalls.get(i).getBox().intersects(p1.getBox()))
 	 		 {
 	 	 		if(p1.getY() > movingWalls.get(i).getY())
 	 	 			movingWalls.get(i).setDirection(-1);
 	 	 		else
 	 	 			movingWalls.get(i).setDirection(1);
 	 			p1.collided = true;
 	 	 		movingWalls.get(i).collided = true;
 	 		 }
       }
 	   
 	   //player reach checkpoints
 	   for(i=0; i<checkpoints.size(); i++)
 	   {		   
 	 		if(checkpoints.get(i).getBox().intersects(p1.getBox()))
 	 		 {
 	 	 		 p1.updateSpawn(checkpoints.get(i).getX(), checkpoints.get(i).getY());
 	 	 		checkpoints.remove(checkpoints.get(i));
 	 	    	playSound(2,"resources/sounds/Click.wav");
 	 		 }
       }

 	   
	}
	
	
    public void paintComponent(Graphics g) 
    {    		
    	g2 = (Graphics2D) g;
        buffer = world.createGraphics();
        buffer.drawImage(backGround, 0, 0, null);
        
        //draw objects
        int i;
        for(i = 0; i< NPCs.size(); i++)
        {
        	NPCs.get(i).draw(buffer, null);
        }
        
        for(i = 0; i< gameObjs.size(); i++)
        {
        	gameObjs.get(i).draw(buffer, null);
        }
        for(i = 0; i< movingWalls.size(); i++)
        {
        	movingWalls.get(i).draw(buffer, null);
        }
        for(i = 0; i< gold.size(); i++)
        {
        	gold.get(i).draw(buffer, null);
        }
        for(i = 0; i< checkpoints.size(); i++)
        {
        	checkpoints.get(i).draw(buffer, null);
        }
        
        victoryDoor.draw(buffer,  null);
        
        this.p1.draw(buffer, null);
        if(sword != null)
        	sword.draw(buffer, null);
    	
        //camera tracks player
        s1x = p1.getX() - (SCREEN_WIDTH/2);
        s1y = p1.getY() - (SCREEN_HEIGHT/2);
        if(s1x > MAP_WIDTH - SCREEN_WIDTH)
     	   s1x = MAP_WIDTH - SCREEN_WIDTH;
        if(s1y > MAP_HEIGHT - SCREEN_HEIGHT)
     	   s1y = MAP_HEIGHT - SCREEN_HEIGHT;
        if(s1x <= 0)
     	   s1x = 0;
        if(s1y <= 0)
     	   s1y = 0;
        screen = world.getSubimage(s1x, s1y, (SCREEN_WIDTH), SCREEN_HEIGHT);

        //Draw the sand storm to limit player visibility
        if(storm)
        {        	       	
        	sandStorm.setX(p1.getX() - sandStorm.getHalfWidth() + p1.getHalfWidth());
        	sandStorm.setY(p1.getY() - sandStorm.getHalfHeight() + p1.getHalfHeight());
            sandStorm.draw(buffer, null);
        	Color stormColor = new Color(197,163,127);
        	buffer.setColor(stormColor);
            //Set Color to sand
        	//buffer.setColor(stormColor);
        	//fill surrounding rectangles for storm sand
            buffer.fillRect(0, 0, sandStorm.getX(), MAP_HEIGHT);//left rectangle
            buffer.fillRect(sandStorm.getX(), 0, MAP_WIDTH - sandStorm.getX(), sandStorm.getY());//top
            buffer.fillRect(sandStorm.getX()+sandStorm.getWidth(), sandStorm.getY(), (MAP_WIDTH - sandStorm.getX()+sandStorm.getWidth()), MAP_HEIGHT-sandStorm.getY());//right
            buffer.fillRect(sandStorm.getX(), sandStorm.getY() + sandStorm.getHeight(), sandStorm.getWidth(), sandStorm.getHeight());//bottom
        }
        g2.drawImage(screen,0,0,null);
        
        //Draw User Interface
        g2.drawImage(UI, SCREEN_WIDTH/2 - UI.getWidth(null)/2, SCREEN_HEIGHT - UI.getHeight(null),null);
        for(i=0; i<p1.getLives(); i++)
        {
        	g2.drawImage(livesIcon, SCREEN_WIDTH/2 - UI.getWidth(null)/2 + 80 + (30*i), SCREEN_HEIGHT - UI.getHeight(null)+4, null);
        }
        
        //Draw mini map
        //g2.fillRect((SCREEN_WIDTH/2 - minimap.width/2 - 5), (SCREEN_HEIGHT - minimap.height - 5), minimap.width + 10, minimap.height + 10);
        //g2.drawImage(world, (SCREEN_WIDTH/2 - minimap.width/2), (SCREEN_HEIGHT - minimap.height), (minimap.width), minimap.height, null); 
        
        Font stringFont = new Font( "SansSerif", Font.PLAIN, 20 );
        g2.setFont( stringFont ); 
        g2.setColor(Color.black);
        String sscore = "" + p1.getScore();
    	g2.drawString(sscore, (3*SCREEN_WIDTH)/4 + 15, SCREEN_HEIGHT - 8);

    }
      
    
    public void gameOver(int winOrLose) {
	
    	this.repaint();
    	
    	playSound(2,"resources/sounds/Game over.wav");
    	//SoundPlayer dead = new SoundPlayer(2,"resources/sounds/Game over.wav");
    	//dead.play();
    	
    	try {
			
	    	Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    	//gameOver = true;
    	
    	g2 = (Graphics2D) this.getGraphics();
    	
    	//if lost
    	if(winOrLose == 0)
    	{
    		g2.fillRect(0, SCREEN_HEIGHT/2 - (gameOverScreen[winOrLose].getHeight()/2), SCREEN_WIDTH, gameOverScreen[winOrLose].getHeight() + 100);
            g2.drawImage(gameOverScreen[winOrLose], SCREEN_WIDTH/2 - gameOverScreen[winOrLose].getWidth()/2, SCREEN_HEIGHT/2 - (gameOverScreen[winOrLose].getHeight()/2 + 50), null);
    	}
    	//if won
    	else
    	{
    		g2.fillRect(0, SCREEN_HEIGHT/2 - (gameOverScreen[winOrLose].getHeight()/2), SCREEN_WIDTH, gameOverScreen[winOrLose].getHeight() + 100);
            g2.drawImage(gameOverScreen[winOrLose], SCREEN_WIDTH/2 - gameOverScreen[winOrLose].getWidth()/2, SCREEN_HEIGHT/2 - (gameOverScreen[winOrLose].getHeight()/2), null);
    	}

    	
        
        Font stringFont = new Font( "SansSerif", Font.PLAIN, 36 );
        g2.setFont( stringFont ); 

        g2.setColor(Color.black);
		g2.fillRect(0, 0, SCREEN_WIDTH, 50);
        g2.setColor(Color.white);
        g2.drawString("Score: " + p1.getScore(), SCREEN_WIDTH/2-100, 30);
        
		try {
       for(int i=4; i>= 0; i--)
       {
    	   g2.setColor(Color.white);
    	   g2.drawString("Restarting in: " + i, SCREEN_WIDTH/2-100, SCREEN_HEIGHT/2 + 150);
			Thread.sleep(1000);
    	   g2.setColor(Color.black);
    	   g2.fillRect(SCREEN_WIDTH/2 + 110, SCREEN_HEIGHT/2 + 100, 300, 75);
       }
       Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
        restart();
    }
    
    private void loadMap()
    {
		//load game Objects from text file map
    	Scanner s = null;
		s = new Scanner(getClass().getResourceAsStream("resources/map.txt"));
		
       for(int i = 0; i< 26; i++)
       {
    	   for(int j =0; j<36; j++)
    	   {
   				if(s.hasNext())
   				{
   					String x = s.next();
   					if(x.equals("1"))
   						gameObjs.add(new Wall(wall, (j*32), (i*32)));
   					if(x.equals("B"))
   						NPCs.add(new Beetle(beetle[0][0], (j*32), (i*32), beetle, 7));
   					if(x.equals("S"))
   						NPCs.add(new Scorpion(scorpion[0][0], (j*32), (i*32),scorpion, 7));
   					if(x.equals("^"))
   						movingWalls.add(new MovingWall(blockVer, (j*32), (i*32)));
   					if(x.equals("!"))
   						sword = new Wall(swordimg, (j*32), (i*32));
   					if(x.equals("$"))
   						gold.add(new Wall(goldImg, (j*32), (i*32)));
   					if(x.equals("F"))
   						victoryDoor = new Wall(door, (j*32), (i*32));
   					if(x.equals("C"))
   						checkpoints.add(new Wall(boost, (j*32), (i*32)));
   					if(x.equals("@"))
   					{
   						p1.setX(j*32);
   						p1.setY(i*32);
   						p1.setSpawn(j*32, i*32);
   					}			
   				}
    	   }
       }
       s.close();
    }

    public void restart()
    {
    	//empty structures of objects
    	while(!gameObjs.isEmpty())
    		gameObjs.remove(0);
    	while(!NPCs.isEmpty())
    		NPCs.remove(0);
    	while(!gold.isEmpty())
    		gold.remove(0);
    	while(!movingWalls.isEmpty())
    		movingWalls.remove(0);
    	while(!checkpoints.isEmpty())
    		checkpoints.remove(0);
    	storm = false;
    	victoryDoor = null;
    	sword = null;
    	
    	
    	loadMap();
    	p1.newGame();

    }
    
	
	public void playSound(int i, String sound)
	{
		SoundPlayer sd = new SoundPlayer(i,sound);
		sd.play();
	}
	
	public void storm(boolean control)
	{
		if(sword == null)
		{
			storm = control;
		}

	}
           
}