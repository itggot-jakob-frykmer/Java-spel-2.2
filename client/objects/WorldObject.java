package client.objects;

import java.awt.Image;
import java.awt.Rectangle;

import client.handlers.Images;

public abstract class WorldObject {

	private int x;
	private int y;
	private int width;
	private int height;
	private double paralax;
	private Image image;
	private Rectangle collisionBox;
	private int objectId;
	

	public WorldObject(int x, int y, int width, int height, double paralax, String imagePath, int objectId) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.paralax = paralax;
		this.image = Images.readImageFromPath(imagePath);
		this.objectId = objectId;
		setCollisionBox(createCollisionBox());
	}

	public abstract void update();
	
	public abstract Rectangle createCollisionBox();

	public abstract void onRemove();
	
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Image getImage() {
		return image;
	}

	public Rectangle getCollisionBox() {
		return collisionBox;
	}

	public void setCollisionBox(Rectangle rect) {
		this.collisionBox = rect;
	}

	public double getParalax() {
		return paralax;
	}
	
	public int getObjectId() {
		return objectId;
	}

}
