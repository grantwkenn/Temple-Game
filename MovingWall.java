package Temple;

import java.awt.Image;

public class MovingWall extends Wall {
	
	private int direction = 1;
	private SoundPlayer slide;
	
	public MovingWall(Image img, int x, int y) {
		super(img, x, y);
		slide = new SoundPlayer(2,"resources/sounds/Block.wav");
	}
	 public void update()
	 {
		 if(collided)
		 {
			slide.play();
			collided = false;
			this.y += direction;
			this.collisionBox.setLocation(x, y);
		 }

	 }
	 
	 public void setDirection(int d)
	 {
		 if(d < 0)
			 this.direction = -2;
		 else if (d > 0)
			 this.direction = 2;
	 }
	 

}
