package client.players.actionbar;

import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import client.handlers.Images;
import client.players.ActionBar;

public class DisplayAbility {
	private int id;
	private int x;
	private int y;
	private int size;
	private boolean round;
	private boolean showCounter;
	private int amount;
	private int keyBind;

	private Image image;
	private boolean hovered = false;

	private Method method;
	private Object methodHolder;
	private Object[] parameters;

	public DisplayAbility(int id, int x, int y, int size, boolean round, boolean showCounter, int keyBind, Method method, Object methodHolder, Object parameter) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.size = size;
		this.image = Images.readImageFromPath("ui/abilities/ability" + id + ".png");
		this.round = round;
		this.showCounter = showCounter;
		this.keyBind = keyBind;

		this.method = method; // vilken metod denna ability ska köra
		this.methodHolder = methodHolder; // vilket object som håller metoden denna ability ska köra

		// sparar parameterns som ska skickas med methoden som denna ability ska köra
		if (parameter != null) { // Om ingen parameter ska skickas med methoden är 'parameter' null. Om den inte är null sparas parametern i en lista över parametrar, annars blir listan null
			parameters = new Object[1];
			parameters[0] = parameter;
		} else {
			parameters = null;
		}

		// betyder att man har oändligt med charges
		if (!showCounter) {
			amount = -1;
		} else {
			amount = 5;
		}

	}

	public void use() {
		// om man har oändligt eller har mer än 0
		if (amount == -1 || amount > 0) {
			try {
				method.invoke(methodHolder, parameters);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			if (amount != -1) {
				amount--;
			}
		}

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	public Image getImage() {
		return image;
	}

	public Image getOverlay() {
		Image img = null;
		if (round) {
			if (hovered) {
				img = ActionBar.powerHighlight;
			} else {
				img = ActionBar.powerShadow;
			}
		} else {
			if (hovered) {
				img = ActionBar.abilityHighlight;
			} else {
				img = ActionBar.abilityShadow;
			}
		}
		return img;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public boolean getShowCounter() {
		return showCounter;
	}

	public int getKeyBind() {
		return keyBind;
	}

}
