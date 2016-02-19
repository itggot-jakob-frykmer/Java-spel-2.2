package client.players.actionbar;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.concurrent.atomic.AtomicInteger;

import client.Screen;
import client.handlers.Images;


public abstract class Icon {

	int x;
	int y;
	int width;
	int height;

	public Icon(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public abstract void paint(Graphics2D g2);

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

	public abstract Image getImage();

}

