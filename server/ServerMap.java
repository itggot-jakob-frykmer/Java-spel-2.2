package server;

import java.util.ArrayList;
import java.util.Random;

import client.objects.WorldObject;

public class ServerMap implements Runnable {

	static ArrayList<WorldObject> worldObjects = new ArrayList<>();
	static ArrayList<TreeSpawn> treeSpawns = new ArrayList<>();

	static int worldWidth = 5333 * 3;
	static int worldHeight = 3000 * 3;
	static int groundLevel = worldHeight - 2000;

	static int sleep = 30;

	static int idCounter = 0;

	public ServerMap() {
		loadLevel(0);
		Thread thread = new Thread(this);
		thread.start();
	}

	public static void sendWorldObjects(int playerNumber) {
		String worldInfo = "SNDWORLDOBJECTS!"; // SENDING MAP INFO
		for (int i = 0; i < worldObjects.size(); i++) {
			WorldObject obj = worldObjects.get(i);

			worldInfo += getInfoFromWorldObject(obj) + "=";
		}

		// System.out.println(worldInfo);
		Server.sendToClient(playerNumber, worldInfo);
	}

	public static void sendTreeSpawns(int playerNumber) {

		String worldInfo = "SNDTREESPAWNS!"; // SENDING TREE SPAWNS
		for (int i = 0; i < treeSpawns.size(); i++) {
			TreeSpawn TS = treeSpawns.get(i);

			worldInfo += getInfoFromTreeSpawn(TS) + "=";
		}
		Server.sendToClient(playerNumber, worldInfo);
	}

	public static void sendWorldInfo(int playerNumber) {
		String worldInfo = "SNDMAPINFO!" + worldWidth + "@" + worldHeight + "@" + groundLevel;
		Server.sendToClient(playerNumber, worldInfo);
		ServerMap.sendWorldObjects(playerNumber);
		sendTreeSpawns(playerNumber);
	}

	static void loadLevel(int level) {
		if (level == 0) {
			WorldObject obj;

			fillBackground();
			fillGround();

			obj = new WorldObject(3000, groundLevel - 400, 1920, 172, 1, "platform", 0);
			worldObjects.add(obj);

			obj = new WorldObject(0, 0, 350, 277, 1, "building", 0);
			worldObjects.add(obj);

			obj = new WorldObject(0, 0, 330, 277, 1, "building", 1);
			worldObjects.add(obj);

			obj = new WorldObject(200, groundLevel - 100, 30, 45, 1, "potion", 0);
			worldObjects.add(obj);

			int x = 100;
			int treeHeight = 350;
			int treeWidth = 150;

			for (int i = 0; i < 30; i++) {
				TreeSpawn spawner = new TreeSpawn(x, groundLevel - treeHeight, treeWidth, treeHeight, 10000, 100);
				treeSpawns.add(spawner);
				x += 1000;
			}

		}
	}

	static void fillBackground() {
		WorldObject obj;
		int backgroundWidth = 1920;
		int backgroundHeight = 1080;
		int numBackgroundsX = (worldWidth / backgroundWidth) + 1;
		int numBackgroundsY = (worldHeight / backgroundHeight) + 1;

		for (int i = -1; i < numBackgroundsX; i++) {
			for (int n = -1; n < numBackgroundsY; n++) {
				obj = new WorldObject(backgroundWidth * i, backgroundHeight * n, backgroundWidth, backgroundHeight, 0.2, "backgroundObject", 1);
				worldObjects.add(obj);
			}
		}

	}

	static void fillGround() {
		WorldObject obj;

		int x = 0;
		int y = groundLevel;
		int groundWidth = 1920;
		int groundHeight = 215;
		int backgroundHeight = 1080;

		int numGrounds = (worldWidth / groundWidth) + 1;

		for (int i = 0; i < numGrounds; i++) {
			obj = new WorldObject(x, y - backgroundHeight, groundWidth, 1080, 1, "backgroundObject", 3);
			worldObjects.add(obj);

			obj = new WorldObject(x, y, groundWidth, groundHeight, 1, "platform", 1);
			worldObjects.add(obj);

			x += groundWidth;
		}

	}

	public static void addObjectLive(int x, int y, int width, int height, double paralax, String itemType, int versionType) {
		WorldObject obj = new WorldObject(x, y, width, height, paralax, itemType, versionType);
		worldObjects.add(obj);
		sendAddObject(obj);
	}

	private static class TreeSpawn {

		private int x;
		private int y;
		private int width;
		private int height;
		private int delay;
		private int tickSleep;
		private int spawnChance;
		private boolean hasTree = true;

		private boolean woodSpawned = false;
		private int woodSpawnDelay = 700;
		private int woodSpawnCounter = 0;

		public TreeSpawn(int x, int y, int width, int height, int delay, int spawnChance) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.spawnChance = spawnChance;
			int rem = delay % sleep;
			this.delay = delay - rem;

		}

		public void update() {
			tickSleep += sleep;

			if (tickSleep % delay == 0) {
				Random ra = new Random();
				int randInt = ra.nextInt(100);
				// gör så att det finns en chans att det spawnar
				if (randInt < spawnChance) {
					if (!hasTree) {
						spawnTree();
					}
				}
			}

			// om det inte finns ett träd och wood inte har spawnat ännu
			if (!hasTree && !woodSpawned) {
				woodSpawnCounter += sleep;

				if (woodSpawnCounter >= woodSpawnDelay) {
					spawnWood();
					woodSpawned = true;
				}
			}

		}

		public void spawnTree() {
			hasTree = true;
			sendState();

		}

		public void despawnTree() {
			hasTree = false;
			// återställer så wood kan spawna igen
			woodSpawned = false;
			woodSpawnCounter = 0;
		}

		public void sendState() {
			String info = "SNDSTATEOFTREESPAWN!"; // sennding state of treespawn

			int index = treeSpawns.indexOf(this);
			int intHasTree = (hasTree ? 1 : 0);

			info += index + "@" + intHasTree;

			Server.sendToAllClients(info);

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

		public int getDelay() {
			return delay;
		}

		public void setDelay(int delay) {
			this.delay = delay;
		}

		public boolean hasTree() {
			return hasTree;
		}

		public void setHasTree(boolean hasTree) {
			this.hasTree = hasTree;
		}

		public void spawnWood() {
			int extraX = 100;
			int xInc = (int) (height * 0.3);

			int woodSize = 40;

			for (int i = 0; i < 3; i++) {
				ServerMap.addObjectLive(x + extraX + xInc * i, y + height - woodSize, woodSize, woodSize, 1, "wood", 0);
			}

		}
	}

	private static class WorldObject {

		private int x;
		private int y;
		private int width;
		private int height;
		private double paralax;
		private String itemType;
		private int versionType;
		private int objectId;

		public WorldObject(int x, int y, int width, int height, double paralax, String itemType, int versionType) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.paralax = paralax;
			this.itemType = itemType;
			this.versionType = versionType;
			this.objectId = idCounter;
			idCounter++;
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

		public String getItemType() {
			return itemType;
		}

		public void setItemType(String itemType) {
			this.itemType = itemType;
		}

		public int getVersionType() {
			return versionType;
		}

		public double getParalax() {
			return paralax;
		}

		public int getObjectId() {
			return objectId;
		}

	}

	@Override
	public void run() {
		while (true) {

			// uppdaterar alla treespawns
			for (int i = 0; i < treeSpawns.size(); i++) {
				TreeSpawn TS = treeSpawns.get(i);
				TS.update();
			}

			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static String getInfoFromTreeSpawn(TreeSpawn TS) {
		String info = "";

		int x = TS.getX();
		int y = TS.getY();
		int width = TS.getWidth();
		int height = TS.getHeight();
		int hasTree = (TS.hasTree()) ? 1 : 0;

		info += x + "@" + y + "@" + width + "@" + height + "@" + hasTree;

		return info;

	}

	public static String getInfoFromWorldObject(WorldObject obj) {
		String info = "";

		int x = obj.getX();
		int y = obj.getY();
		int width = obj.getWidth();
		int height = obj.getHeight();
		double paralax = obj.getParalax();
		String itemType = obj.getItemType();
		int versionType = obj.getVersionType();
		int objectId = obj.getObjectId();

		info += x + "@" + y + "@" + width + "@" + height + "@" + paralax + "@" + itemType + "@" + versionType + "@" + objectId;

		return info;
	}

	public static void sendAddObject(WorldObject obj) {
		String info = "SNDADDOBJECT!" + getInfoFromWorldObject(obj);
		Server.sendToAllClients(info);
	}

	static void cutTreeFromClient(String message) {
		String[] split1 = message.split("£");
		String[] info = split1[0].split("&");

		int treeSpawnIndex = Integer.parseInt(info[1]);
		TreeSpawn TS = treeSpawns.get(treeSpawnIndex);
		TS.despawnTree();

	}

	// tar bort ett object med information från en klient
	static void removeObjectFromClient(String message) {
		String[] split1 = message.split("£");
		String[] info = split1[0].split("&");

		int id = Integer.parseInt(info[1]);
		WorldObject obj = getWorldObjectById(id);
		worldObjects.remove(obj);
	}

	static WorldObject getWorldObjectById(int id) {
		WorldObject returnObj = null;
		for (int i = 0; i < worldObjects.size(); i++) {
			WorldObject obj = worldObjects.get(i);
			if (obj.getObjectId() == id) {
				returnObj = obj;
			}
		}

		return returnObj;
	}

}
