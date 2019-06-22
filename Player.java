package Temple;

import java.awt.Image;
import java.awt.Point;

public class Player extends MoveableGameObject {
	
	public Player(Image img, int x, int y, Image[][] sprites, GameMain thisGame) {
		super(img, x, y);
		this.spriteSheet = sprites;
		this.thisGame = thisGame;
		this.spawn = new Point(y, x);
	}
	
	private final int LIVESMAX = 3;
	private int lives = LIVESMAX;
    private int walkingSpeed = 1;
    private GameMain thisGame;
    private Point spawn;
    private int respawnSequence;
    private int score = 0;

    
    private int imgRow;
    private int imgCol;
       
    private int direction = 0;
    
    private Image[][] spriteSheet;
       
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    
    private int stepCount = 32;

    void toggleUpPressed() {
        this.UpPressed = true;
    	if(direction<0 )
        {
        	direction = 1;
        }
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }
    
    void togglePower() {
    	thisGame.storm(false);
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }
    
    void unTogglePower() {
    	thisGame.storm(true);
    }
    
public void update() {
		
	if(lives <= 0)
		thisGame.gameOver(0);
	
	if(collided)
	{
		setX(oldx);
		setY(oldy);
		collided = false;
		return;
	}
	
	oldx = x;
	oldy = y;	
	
	if(UpPressed)
	{
		this.y -= walkingSpeed;
		stepCount--;
        imgRow = 3;
	}
	else if(DownPressed)
	{
		this.y += walkingSpeed;
		stepCount--;
        imgRow = 0;
	}
	else if(LeftPressed)
	{
		this.x -= walkingSpeed;
		stepCount--;
        imgRow = 1;
	}
	else if(RightPressed)
	{
		this.x += walkingSpeed;
		stepCount--;
        imgRow = 2;
	}
		
	img = spriteSheet[imgRow][imgCol];
	if(stepCount <= 0)
	{
    	imgCol = (imgCol + 1) % 4;
    	stepCount = 16;
	}

    collisionBox.setLocation(x, y);
    if(respawnSequence > 0)
    {
    	if(respawnSequence % 20 == 0)
    	{
        	toggleShow();
    	}
    	respawnSequence --;
    	if(respawnSequence == 0)
    		this.show = true;
    }   
}


public void die()
{
	lives--;

	respawnSequence = 250;
	x = spawn.x;
	y = spawn.y;
    collisionBox.setLocation(x, y);
    SoundPlayer die = new SoundPlayer(2,"resources/sounds/Die.wav");
    if(lives > 0)
    	die.play();
}

public void setSpawn(int x, int y)
{
	this.spawn.x = x;
	this.spawn.y = y;
}

public int getLives()
{
	return lives;
}

public void updateSpawn(int x, int y)
{
	spawn.x = x;
	spawn.y = y;
}

public void newGame()
{
	this.x = spawn.x;
	this.y = spawn.y;
	this.lives = LIVESMAX;
	this.stepCount = 0;
	this.respawnSequence = 0;
	score = 0;
}

public int getScore()
{
	return score;
}

public void setScore(int s) { score += s; }

}
