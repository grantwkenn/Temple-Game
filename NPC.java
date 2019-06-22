package Temple;

import java.awt.Image;

public abstract class NPC extends MoveableGameObject {

	protected int lives;
	protected Image[][] sprites;
	protected int row = 0, col = 0;
	protected int speed = -1, stepCount = 16;
	protected int[] dest;
	
	public NPC(Image img, int x, int y, Image[][] sprites) {
		super(img, x, y);
		this.sprites = sprites;
	}

}
