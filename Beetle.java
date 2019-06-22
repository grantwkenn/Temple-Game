package Temple;

import java.awt.Image;
import java.util.Random;

public class Beetle extends NPC {
	
	private Random rand = new Random();
	
	public Beetle(Image img, int x, int y, Image[][] sprites, int length ) {
		super(img, x, y, sprites);
		this.sprites = sprites;

		dest = new int[2];
		dest[0] = this.y;
		dest[1] = y - (length * 32);
		int randSeed = rand.nextInt(500);
		for(int i =0; i<randSeed; i++)
		{
			update();
		}
	}

	public void update()
	{
			oldx = x;
			oldy = y;
			this.y += speed;
			
	    collisionBox.setLocation(x, y);
		if(y == dest[0] || y == dest[1])
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
