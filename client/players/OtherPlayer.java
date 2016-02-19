package client.players;

import java.awt.Graphics2D;

import client.Screen;

public class OtherPlayer extends Player {

	public OtherPlayer(int playerNumber, int characterType) {
		super(playerNumber, characterType);
	}

	public void update() {
		commonUpdate();
	}

	public void paint(Graphics2D g2d) {
		getHealthBar().paint(g2d);
		// om karaktären går åt höger ska bilden spelgas, detta görs genom att bredden görs negativ och x värdet flyttas lika långt som karaktärens bredd
		int modWidth = 1;
		int modX = 0;

		// om man går åt vänster
		if (isFacingLeft()) {
			modWidth = -1;
			modX = -getWidth();
		}

		g2d.drawImage(getImage(), Screen.fixX(getX(), 1) - modX, Screen.fixY(getY(), 1), getWidth() * modWidth, getHeight(), null);

	}

}
