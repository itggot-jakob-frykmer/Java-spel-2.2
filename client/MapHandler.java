package client;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;

import client.objects.BackgroundObject;
import client.objects.Building;
import client.objects.Platform;
import client.objects.Potion;
import client.objects.TreeSpawn;
import client.objects.Wood;
import client.objects.WorldObject;

public class MapHandler {

	public static ArrayList<WorldObject> worldObjects = new ArrayList<>();
	public static ArrayList<TreeSpawn> treeSpawns = new ArrayList<>();

	static int worldWidth = 0;
	static int worldHeight = 0;
	static int groundLevel;

	// när man ansluter från man information om hur världen ser ut, i denna metod behandlas denna info
	static void initObjectsFromServer(String message) {
		try {
			String[] objects = message.split("=");
			for (int i = 0; i < objects.length; i++) {
				addObjectFromInfo(objects[i]);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	// lägger till ett object med information från servern
	static void addObjectFromInfo(String objectInfo) {
		String[] info = objectInfo.split("@");

		int x = Integer.parseInt(info[0]);
		int y = Integer.parseInt(info[1]);
		int width = Integer.parseInt(info[2]);
		int height = Integer.parseInt(info[3]);
		double paralax = Double.parseDouble(info[4]);
		String itemType = info[5];
		int versionType = Integer.parseInt(info[6]);
		int objectId = Integer.parseInt(info[7]);

		if (itemType.equals("platform")) {
			WorldObject object = new Platform(x, y, width, height, paralax, "platforms/platform" + versionType + ".png", objectId);
			worldObjects.add(object);
		}

		else if (itemType.equals("building")) {
			WorldObject object = new Building(x, y, width, height, paralax, "buildings/building" + versionType + ".png", objectId);
			worldObjects.add(object);
		}

		else if (itemType.equals("backgroundObject")) {
			WorldObject object = new BackgroundObject(x, y, width, height, paralax, "backgrounds/background" + versionType + ".png", objectId);
			worldObjects.add(object);
		}

		else if (itemType.equals("wood")) {
			WorldObject object = new Wood(x, y, width, height, paralax, "objects/wood.png", objectId);
			worldObjects.add(object);
		}

		else if (itemType.equals("potion")) {
			WorldObject object = new Potion(x, y, width, height, paralax, "potions/potion" + versionType + ".png", objectId);
			worldObjects.add(object);
		}

	}

	// uppdaterar en tree spawner med ett meddelande från servern
	static void updateStateOfTreeSpawn(String message) {
		String[] info = message.split("@");

		int index = Integer.parseInt(info[0]);
		boolean hasTree = info[1].equals("1");

		treeSpawns.get(index).updateState(hasTree);
	}

	// initialiserar alla tree spawners med meddelande från servern
	static void initTreeSpawns(String message) {
		String[] objects = message.split("=");
		for (int i = 0; i < objects.length; i++) {

			String[] info = objects[i].split("@");

			int x = Integer.parseInt(info[0]);
			// System.out.println(x);
			int y = Integer.parseInt(info[1]);
			int width = Integer.parseInt(info[2]);
			int height = Integer.parseInt(info[3]);
			boolean hasTree = (info[4].equals("1"));

			TreeSpawn TS = new TreeSpawn(x, y, width, height, hasTree);
			treeSpawns.add(TS);
		}
	}

	static void cutTreeFromServer(String message) {
		String[] info = message.split("&");
		int treeSpawnIndex = Integer.parseInt(info[1]);
		TreeSpawn TS = treeSpawns.get(treeSpawnIndex);
		TS.despawnTree();
	}

	// tar emot information om mappen från servern
	static void initMapInfo(String message) {
		String[] info = message.split("@");
		worldWidth = Integer.parseInt(info[0]);
		worldHeight = Integer.parseInt(info[1]);
		groundLevel = Integer.parseInt(info[2]);

		Toolkit tk = Toolkit.getDefaultToolkit();

		// hämtar datorns skärm storlek
		int screenWidth = (int) (tk.getScreenSize().getWidth());
		int screenHeight = (int) (tk.getScreenSize().getHeight());

		Screen.scaleWidthZoom = screenWidth / (worldWidth * 1.0);
		Screen.scaleHeightZoom = screenHeight / (worldHeight * 1.0);
	}

	public static void removeObject(WorldObject obj) {
		obj.onRemove();
		worldObjects.remove(obj);
	}

	public static void sendRemoveObject(WorldObject obj) {

		int id = obj.getObjectId();

		String message = id + "";

		Client.sendData("#SNDCLIENTREMOVEWORLDOBJECT", message);

	}

	// tar bort ett object med information från servern
	static void removeObjectFromServer(String message) {
		String[] split = message.split("&");

		int id = Integer.parseInt(split[1]);
		removeWorldObjectById(id);
	}

	static void removeWorldObjectById(int id) {
		WorldObject obj = getWorldObjectById(id);
		removeObject(obj);
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

	public static void addWorldObjectToFront(WorldObject obj) {
		Collections.reverse(MapHandler.worldObjects);
		worldObjects.add(obj);
		Collections.reverse(MapHandler.worldObjects);
	}

}
