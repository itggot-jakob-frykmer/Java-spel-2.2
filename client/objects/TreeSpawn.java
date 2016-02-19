package client.objects;

import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;

import client.Client;
import client.MapHandler;
import client.handlers.Images;
import client.handlers.Sound;

public class TreeSpawn {

	private int x;
	private int y;
	private int width;
	private int height;

	private boolean hasTree;

	private Tree tree;
	private ChoppedTree choppedTree;
	private Stump stump;
	
	private URL fallingSound;

	public TreeSpawn(int x, int y, int width, int height, boolean hasTree) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.hasTree = hasTree;

		tree = new Tree(x, y, width, height, 1, "objects/tree.png", this, -1);

		int choppedTreeHeight = (int) (height * 0.875);
		choppedTree = new ChoppedTree(x, y, width, choppedTreeHeight, 1, "objects/choppedTree.png", this, -1);

		int stumpHeight = (int) (height * 0.125);
		int stumpY = y + height - stumpHeight;
		stump = new Stump(x, stumpY, width, stumpHeight, 1, "objects/stump.png", -1);

		fallingSound = Sound.readSoundFile("sounds/objects/TreeFall.wav");
		
		
		if (hasTree) {
			spawnTree();
		} else {
			despawnTree();
		}

	}

	// när servern skickar information om en treespawner uppdateras dessa state med denna metod
	public void updateState(boolean hasTree) {

		this.hasTree = hasTree;
		if (hasTree) {
			spawnTree();
		} else {
			despawnTree();
		}
	}

	public void spawnTree() {

		// tar bort stubben
		int index = MapHandler.worldObjects.indexOf(stump);
		if (index != -1) {
			MapHandler.worldObjects.remove(index);
		}

		index = MapHandler.worldObjects.indexOf(choppedTree);
		if (index != -1) {
			MapHandler.worldObjects.remove(index);
		}

		// lägger till dem först i arrayen så de hamnar bakom allt 
		MapHandler.addWorldObjectToFront(tree);
		choppedTree.setRotation(0);
		choppedTree.reset();

	}
	// När trädet fälls
	public void despawnTree() {
		
		Sound.play(fallingSound, getX(), getY(), 1f);
		
		int index = MapHandler.worldObjects.indexOf(tree);
		if (index != -1) {
			MapHandler.worldObjects.remove(index);
		}

		// lägger till dem först i arrayen så de hamnar bakom allt :)
		MapHandler.addWorldObjectToFront(stump);
		MapHandler.addWorldObjectToFront(choppedTree);
	}

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

	public boolean hasTree() {
		return hasTree;
	}

	public void setHasTree(boolean hasTree) {
		this.hasTree = hasTree;
	}

	public void sendCutTree(int treeIndex) {

		String message = "" + treeIndex;

		Client.sendData("#SNDTREECUTDOWN", message);

	}

	public void hideChoppedTree() {
		int index = MapHandler.worldObjects.indexOf(choppedTree);
		if (index != -1) {
			MapHandler.worldObjects.remove(index);
		}
	}

	public class Tree extends InteractableWorldObject {

		TreeSpawn treeSpawn; // vilken spawn detta träd tillhör

		public Tree(int x, int y, int width, int height, double paralax, String imagePath, TreeSpawn treeSpawn, int objectId) {
			super(x, y, width, height, paralax, imagePath, objectId);
			Image highlight = Images.readImageFromPath("objects/tree_highlight.png");
			setImageHighlight(highlight);
			this.treeSpawn = treeSpawn;
		}

		@Override
		public Rectangle createCollisionBox() {
			return new Rectangle(getX(), getY(), getWidth(), getHeight());
		}

		@Override
		public void clickObject() {

		}

		@Override
		public void completeInteraction() {
			int spawnIndex = MapHandler.treeSpawns.indexOf(treeSpawn);

			treeSpawn.sendCutTree(spawnIndex); // skickar till alla andra klienter att detta träd ska bort
			treeSpawn.despawnTree(); // tar bort trädet

		}

		@Override
		public void onRemove() {
			// TODO Auto-generated method stub
			
		}

	}

	public class ChoppedTree extends WorldObject {

		double rotation;

		double startRotationSpeed = 0;
		double rotationSpeed = startRotationSpeed;
		double rotationAcceleration = 0.0002;
		TreeSpawn treeSpawn;

		public ChoppedTree(int x, int y, int width, int height, double paralax, String imagePath, TreeSpawn treeSpawn, int objectId) {
			super(x, y, width, height, paralax, imagePath, objectId);
			this.treeSpawn = treeSpawn;
		}

		@Override
		public void update() {

			double maxRot = (Math.PI / 2) * 1.09;
			if (Math.abs(rotation) < maxRot) {
				rotation += rotationSpeed;
				rotationSpeed += rotationAcceleration;
			} else { // när trädet har fallit klart
				treeSpawn.hideChoppedTree();
			}

		}

		@Override
		public Rectangle createCollisionBox() {
			return null;
		}

		public double getRotation() {
			return rotation;
		}

		public void reset() {
			rotationSpeed = startRotationSpeed;
		}

		public void setRotation(double rotation) {
			this.rotation = rotation;
		}

		@Override
		public void onRemove() {
			// TODO Auto-generated method stub
			
		}

	}

	public class Stump extends WorldObject {

		public Stump(int x, int y, int width, int height, double paralax, String imagePath, int objectId) {
			super(x, y, width, height, paralax, imagePath, objectId);
		}

		@Override
		public Rectangle createCollisionBox() {
			return new Rectangle(getX(), getY(), getWidth(), getHeight());
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

}
