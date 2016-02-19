package client.players;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import client.Screen;
import client.handlers.Images;
import client.players.actionbar.CounterIcon;
import client.players.actionbar.DisplayAbility;
import client.players.actionbar.Icon;
import client.players.actionbar.ReadyIcon;

// första: 0,07678
// andra: 0,24713
// ability size 0,10767
// yoffset: ability: 0,4429
// articact power size: 0,1571
// artifact power yOffset: 0,0588

public class ActionBar {

	private Image actionBarImage;
	private int width;
	private int height;
	private int x;
	private int y;

	static ArrayList<DisplayAbility> abilities = new ArrayList<>();
	static ArrayList<Icon> icons = new ArrayList<>();

	public static Image abilityHighlight;
	public static Image abilityShadow;

	public static Image powerHighlight;
	public static Image powerShadow;

	private int potionIndex;

	public ActionBar(int abilityOneId, int abilityTwoId, int powerId, YourPlayer player) {
		actionBarImage = Images.readImageFromPath("ui/actionbar.png");

		// räknar ut hur stor actionbaren ska vara och var den ska sitta
		width = (int) (Screen.screenWidth * 0.3);
		height = Images.getPropHeightFromImage(width, actionBarImage);
		y = Screen.screenHeight - height;
		x = Screen.screenWidth / 2 - width / 2;

		int abilitySize = (int) (width * 0.10767);
		int powerSize = (int) (width * 0.1671);

		// räknar ut var första ability ska sitta
		int xOffset = (int) (x + width * 0.07678);
		int yOffset = (int) (y + height * 0.4429);

		Method method = null; // vilken metod en ability ska köra
		Class[] paramtersTypes = new Class[1]; // Säger vilken datatyp parametern på en method som en ability kör ska vara. Behövs endast om en method tar en parameter

		try {

			// lägger till första ability
			paramtersTypes[0] = int.class;
			method = YourPlayer.class.getMethod("castSpell", paramtersTypes);
			DisplayAbility ability = new DisplayAbility(abilityOneId, xOffset, yOffset, abilitySize, false, false, KeyEvent.VK_1, method, player, 0);
			abilities.add(ability);

			// lägger till andra ability
			xOffset = (int) (x + width * 0.24713); // räknar ut var andra ability ska sitta
			paramtersTypes[0] = int.class;
			method = YourPlayer.class.getMethod("castSpell", paramtersTypes);
			ability = new DisplayAbility(abilityTwoId, xOffset, yOffset, abilitySize, false, false, KeyEvent.VK_2, method, player, 0);
			abilities.add(ability);

			// lägger till tredje ability
			xOffset = (int) (x + width - (width * 0.24713) - abilitySize); // räknar ut var tredje ability ska sitta
			method = YourPlayer.class.getMethod("usePotion");
			ability = new DisplayAbility(4, xOffset, yOffset, abilitySize, false, true, KeyEvent.VK_4, method, player, null);
			abilities.add(ability);

			potionIndex = abilities.indexOf(ability);

			// räknar ut var fjärde ability ska sitta
			/*
			 * xOffset = (int) (x + width - (width * 0.07678) - abilitySize); ability = new DisplayAbility(abilityFourId, xOffset, yOffset, abilitySize, false, true, KeyEvent.VK_5); abilities.add(ability);
			 */

			// räknar ut var powerability ska sitta
			xOffset = (int) (x + width / 2 - powerSize / 2);
			yOffset = (int) (y + height * 0.0588);
			ability = new DisplayAbility(powerId, xOffset, yOffset, powerSize, true, false, KeyEvent.VK_3, method, player, null);
			abilities.add(ability);

			abilityHighlight = Images.readImageFromPath("ui/ability_highlight.png");
			abilityShadow = Images.readImageFromPath("ui/ability_shadow.png");

			powerHighlight = Images.readImageFromPath("ui/power_highlight.png");
			powerShadow = Images.readImageFromPath("ui/power_shadow.png");

			int iconWidth = (int) (width * 0.1);
			int iconHeight = iconWidth;

			Icon icon = new ReadyIcon(x + width - iconWidth * 2, y - 20, iconWidth, iconHeight, player.getDoubleJumpReady(), player.getDoubleJumpCooldown(), player.getDoubleJumpCooldownCounter(), "ui/icons/doubleJumpReady.png", "ui/icons/doubleJumpNotReady.png");
			icons.add(icon);

			icon = new ReadyIcon(x + width - iconWidth * 4, y - 20, iconWidth, iconHeight, player.getDashReady(), player.getDashCooldown(), player.getDashCooldownCounter(), "ui/icons/dashReady.png", "ui/icons/dashNotReady.png");
			icons.add(icon);

			icon = new CounterIcon(x + 20, y - 20, iconWidth, iconHeight, "ui/icons/wood.png", player.getNumWood());
			icons.add(icon);

		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DisplayAbility getAbility(int index) {
		return abilities.get(index);
	}

	public DisplayAbility getPotionAbility() {
		return abilities.get(potionIndex);
	}

	public void update() {

	}

	public void paint(Graphics2D g2d) {

		Graphics2D g2 = Screen.reverseZoom(g2d); // reversar zoomen

		// målar alla ability ikoner
		for (int i = 0; i < abilities.size(); i++) {
			DisplayAbility ability = abilities.get(i);

			g2.drawImage(ability.getImage(), ability.getX(), ability.getY(), ability.getSize(), ability.getSize(), null);
			g2.drawImage(ability.getOverlay(), ability.getX(), ability.getY(), ability.getSize(), ability.getSize(), null);

			// på vissa abilities ska en counter visas för att visa hur många charges man har kvar
			if (ability.getShowCounter()) {
				String text = ability.getAmount() + "";
				Font font = new Font("Calibri", Font.BOLD, 40);

				int textWidth = Screen.getTextWidth(font, text);

				int fontX = ability.getX() + ability.getSize() - textWidth - 5;
				int fontY = ability.getY() + ability.getSize() - 5;

				g2.setFont(font);
				g2.setColor(Color.white);
				g2.drawString(ability.getAmount() + "", fontX, fontY);
			}
		}

		// målar alla ikoner
		for (int i = 0; i < icons.size(); i++) {
			Icon icon = icons.get(i);

			icon.paint(g2);

		}

		g2.drawImage(actionBarImage, x, y, width, height, null);
	}

	public Image getActionBarImage() {
		return actionBarImage;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void handleKeyEvent(KeyEvent e) {
		int keyCode = e.getKeyCode();
		for (int i = 0; i < abilities.size(); i++) {
			DisplayAbility ability = abilities.get(i);

			if (ability.getKeyBind() == keyCode) {
				ability.use();
			}
		}
	}

	public void handleMouseEvent(MouseEvent e) {
		Rectangle mouseRect = new Rectangle(e.getX(), e.getY(), 1, 1);

		for (int i = 0; i < abilities.size(); i++) {
			DisplayAbility ability = abilities.get(i);

			Rectangle abilityRect = new Rectangle(ability.getX(), ability.getY(), ability.getSize(), ability.getSize());
			if (mouseRect.intersects(abilityRect)) {
				ability.setHovered(true);
			} else {
				ability.setHovered(false);
			}
		}
	}

	


}
