package client.players;

public class Spell {

	private int x;
	private int y;
	private int type;

	public Spell(int startX, int startY, int type) {
		this.x = startX;
		this.y = startY;
		this.type = type;

		initSpellFromType(type);
	}

	public void initSpellFromType(int type) {

		if (type == 0) {
			System.out.println("CREATED");
		} else {

		}
	}

}
