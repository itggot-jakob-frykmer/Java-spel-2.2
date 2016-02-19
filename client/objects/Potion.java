package client.objects;

import java.awt.Image;
import java.net.URL;

import client.Main;
import client.handlers.Images;
import client.handlers.Sound;

public class Potion extends LootableWorldObject {

	private URL pickupSound;
	
	public Potion(int x, int y, int width, int height, double paralax, String imagePath, int objectId) {
		super(x, y, width, height, paralax, imagePath, objectId);
		Image img = Images.readImageFromPath("potions/potionHighlight.png");
		setImageHighlight(img);
		setNumItems(1);
		
		pickupSound = Sound.readSoundFile("sounds/objects/PotionHandle.wav");
	}

	@Override
	public void pickUpItem() {
		Main.clientPlayer.pickUpPotion(getNumItems());
	}

	@Override
	public void onRemove() {
		Sound.play(pickupSound, 1f);
		
	}

}
