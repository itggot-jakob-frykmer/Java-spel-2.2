package client.objects;

import java.awt.Image;
import java.net.URL;
import java.util.Random;

import client.Main;
import client.handlers.Images;
import client.handlers.Sound;

public class Wood extends LootableWorldObject {

	public int numVary = 5;
	public int minWood = 2;

	private URL pickupSound;
	
	public Wood(int x, int y, int width, int height, double paralax, String imagePath, int objectId) {
		super(x, y, width, height, paralax, imagePath, objectId);
		Image img = Images.readImageFromPath("objects/woodHighlight.png");
		setImageHighlight(img);

		Random ra = new Random();
		int numWood = minWood + ra.nextInt(numVary);
		setNumItems(numWood);

		pickupSound = Sound.readSoundFile("sounds/objects/WoodHandle.wav");
				
		
	}

	@Override
	public void pickUpItem() {
		Main.clientPlayer.pickUpWood(getNumItems());
	}

	@Override
	public void onRemove() {
		Sound.play(pickupSound, getX(), getY(), 1f);
	}

}
