package client.players;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;

import client.Screen;
import client.handlers.Images;
import client.handlers.Sound;

public abstract class Player {

	private int x;
	private int y;
	private final int width = 77;
	private final int height = 110;
	private int playerNumber;
	private int characterType;

	private boolean movingLeft = false;
	private boolean movingRight = false;
	private boolean movingUp = false;
	private boolean movingDown = false;

	private boolean facingLeft = true;

	private boolean falling = true;
	private boolean jumping = false;
	private boolean moving = false;

	private Image currentImage;
	private int imageChangeDelay = 1;
	private int imageChangeTick = 0;
	int currentImageIndex = 0;

	/*
	 * private int changedRunningImageTick = 0; // ökar varje gång man byter bild private int runningSoundDelayTick = 8;
	 */
	boolean dashing = false;

	private int imageChangeStandingDelay = 70;

	private HealthBar healthBar;

	private ArrayList<Image> movingImages = new ArrayList<Image>();
	private ArrayList<Image> standingImages = new ArrayList<Image>();
	private Image fallingImage;

	private URL runningSound;

	public Player(int playerNumber, int characterType) {
		this.playerNumber = playerNumber;
		this.characterType = characterType;
		initImages();
		initSounds();

		healthBar = new HealthBar();
	}

	public void initSounds() {
		runningSound = Sound.readSoundFile("sounds/players/FootstepGrass.wav");
	}

	public void initImages() {
		for (int i = 0; i < 14; i++) {
			Image img = Images.readImageFromPath("characters/" + characterType + "/moving/" + i + ".png");
			movingImages.add(img);
		}

		for (int i = 0; i < 2; i++) {
			Image img = Images.readImageFromPath("characters/" + characterType + "/standing/" + i + ".png");
			standingImages.add(img);
		}

		fallingImage = movingImages.get(13);
		currentImage = standingImages.get(0);
	}

	// funktion för att uppdatera för både sin egen och andra spelare
	public void commonUpdate() {
		updateCurrentImage();
	}

	public void updateCurrentImage() {

		imageChangeTick++;

		if (isDashing()) {
			currentImage = fallingImage;
		} else if (isFalling()) {
			currentImage = fallingImage;
		} else {

			// Byter bild för när man rör sig
			if (isMoving()) {
				
				if (imageChangeTick % imageChangeDelay == 0) {

					// gör så att bild indexet loopar runt hela arrayen
					currentImageIndex++;
					if (currentImageIndex == movingImages.size()) {
						currentImageIndex = 0;
					}

					currentImage = movingImages.get(currentImageIndex); // byter bild

					// Gör så att spring-ljudet bara spelas upp vissa gånger bilden byts
					if (currentImageIndex == 1 || currentImageIndex == 8) {
						Sound.play(runningSound, getX(), getY(), 1f);
					}

					// changedRunningImageTick++;
				}

			} else { // byter bild för när man står stilla

				if (imageChangeTick % imageChangeStandingDelay == 0) {

					// gör så att bild indexet loopar runt hela arrayen
					currentImageIndex++;
					if (currentImageIndex >= standingImages.size()) {
						currentImageIndex = 0;
					}

					currentImage = standingImages.get(currentImageIndex);
				}
			}
		}

	}

	public ArrayList<Image> getMovingImages() {
		return movingImages;
	}

	public void setImageChangeDelay(int imageChangeDelay) {
		this.imageChangeDelay = imageChangeDelay;
	}

	public int getImageChangeDelay() {
		return imageChangeDelay;
	}

	public boolean isMovingLeft() {
		return movingLeft;
	}

	public void setMovingLeft(boolean movingLeft) {
		this.movingLeft = movingLeft;
	}

	public boolean isMovingRight() {
		return movingRight;
	}

	public void setMovingRight(boolean movingRight) {
		this.movingRight = movingRight;
	}

	public boolean isMovingUp() {
		return movingUp;
	}

	public void setMovingUp(boolean movingUp) {
		this.movingUp = movingUp;
	}

	public boolean isMovingDown() {
		return movingDown;
	}

	public void setMovingDown(boolean movingDown) {
		this.movingDown = movingDown;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public abstract void update();

	public abstract void paint(Graphics2D g2d);

	public int getPlayerNumber() {
		return playerNumber;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Image getImage() {
		return currentImage;
	}

	public Rectangle getFeetCollisionBox() {
		int height = (int) (this.height * 0.09);
		int width = (int) ((this.width) * 0.3);

		int x = this.x + this.width / 2 - width / 2; // sätter den i mitten
		int y = this.y - height + this.height;

		return new Rectangle(x, y, width, height);
	}

	public Rectangle getCollisionBox() {
		return new Rectangle(x, y, width, height);
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isDashing() {
		return dashing;
	}

	public boolean isFacingLeft() {
		return facingLeft;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	public HealthBar getHealthBar() {
		return healthBar;
	}

	public int getHealth() {
		return healthBar.getHealth();
	}

	public void setHealth(int health) {
		this.healthBar.setHealth(health);
	}

	public void setDashing(boolean dashing) {
		this.dashing = dashing;
	}

	public class HealthBar {

		private int health;
		private int maxHealth = 100;
		private Image gradImage;
		private int displayWidth = 100;
		private int displayHeight = 9;

		private int xOffset = -10;
		private int yOffset = -20;

		public HealthBar() {
			this.health = maxHealth;
			this.gradImage = Images.readImageFromPath("ui/health_grad.png");
		}

		public void paint(Graphics2D g2d) {
			g2d = (Graphics2D) g2d.create();

			int barWidth = (int) (((getHealth() * 1.0) / (maxHealth)) * displayWidth);

			int x = Screen.fixX(getX() + xOffset, 1);
			int y = Screen.fixY(getY() + yOffset, 1);

			g2d.setColor(new Color(100, 0, 0));
			g2d.fillRect(x, y, displayWidth, displayHeight);
			g2d.setColor(new Color(200, 0, 0));
			g2d.fillRect(x, y, barWidth, displayHeight);
			g2d.drawImage(gradImage, x, y, displayWidth, displayHeight, null);

			// g2d.Arc(200, 200, 400, 400, 90, -90);
		}

		public int getHealth() {
			return health;
		}

		public void setHealth(int health) {
			if (health > maxHealth) {
				health = maxHealth;
			}
			if (health < 0) {
				health = 0;
			}
			this.health = health;
		}

	}

}
