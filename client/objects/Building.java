package client.objects;

import java.awt.Rectangle;

public class Building extends WorldObject {

	public Building(int x, int y, int width, int height, double paralax, String imagePath, int objectId) {
		super(x, y, width, height, paralax, imagePath, objectId);
	}

	public Rectangle createCollisionBox() {
		Rectangle rect = new Rectangle(getX(), getX(), getWidth(), getHeight());
		return rect;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub
		
	}
}