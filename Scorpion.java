package Temple;

import java.awt.Image;
import java.util.Random;

public class Scorpion extends NPC {
	
	private Random rand = new Random();
	
	public Scorpion(Image img, int x, int y, Image[][] sprites, int length) {
		super(img, x, y, sprites);

		this.sprites = sprites;
		dest = new int[2];
		dest[0] = this.x;
		dest[1] =  x - (length * 32);
		int randSeed = rand.nextInt(500);
		for(int i =0; i<randSeed; i++)
		{
			update();
		}
	}


	public void update()
	{

			this.x += speed;
			oldx = x;
		    collisionBox.setLocation(x, y);
			if(x == dest[0] || x == dest[1] )
			{
				speed = speed* -1;
				row = (row + 1) % 2;
			}


		stepCount--;
		if(stepCount == 0)
		{
			col = (col + 1) % 4;
			img = sprites[row][col];
			stepCount = 16;
		}
	}
	
}