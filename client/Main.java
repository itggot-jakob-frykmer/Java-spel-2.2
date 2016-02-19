package client;

import java.util.ArrayList;
import java.util.Collections;

import client.objects.WorldObject;
import client.players.OtherPlayer;
import client.players.Player;
import client.players.YourPlayer;

public class Main {

	static ArrayList<Player> players = new ArrayList<Player>();
	static ArrayList<Integer> activePlayersNumber = new ArrayList<>(); // denna lista används för att hålla ordning på spelarnas globa splarnummer

	public static YourPlayer clientPlayer;

	static void addPlayer(Player player) {
		players.add(player);
	}

	static void removePlayer() {

	}

	static void update() {
		updatePlayers();
		updateWorldObjects();
		Screen.updateScreen();
	}

	static Player getPlayerByNumber(int playerNumber) {

		Player player = new OtherPlayer(-100, 0); // temp
		
		
		int index = activePlayersNumber.indexOf(playerNumber);
		if (index != -1) {
			player = players.get(index);
		}

		return player;
	}

	static void updateWorldObjects() {
		for (int i = 0; i < MapHandler.worldObjects.size(); i++) {
			WorldObject obj = MapHandler.worldObjects.get(i);
			obj.update();
		}
	}

	static void updatePlayers() {
		for (int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			player.update();
		}
	}

	static void pushToFront(ArrayList<Object> arrayList, Object obj) {
		Collections.reverse(arrayList);
		arrayList.add(obj);
		Collections.reverse(arrayList);

	}

}
