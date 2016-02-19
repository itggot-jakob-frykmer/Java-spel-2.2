/*
 Här sker allt grafiskt

 */
package client;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import client.handlers.Images;
import client.handlers.InputHandler;
import client.objects.BackgroundObject;
import client.objects.TreeSpawn.ChoppedTree;
import client.objects.WorldObject;
import client.players.Player;
import client.players.YourPlayer;

@SuppressWarnings("serial")
public class Screen extends JPanel implements Runnable {

	InputHandler input;

	static boolean GM = false; // GM = God mode, finns för att kunna testa spelat lättare

	public static int sleep = 5; // hur länga tråden ska vänta mellan varje gång den körs
	static int tick = 0; // tick tickar relativt hur länge tråden väntar mellan varje gång den körs så man kan använda den till att räkna tid
	int singleTick = 0; // singleTick tickar en gång varje gång main-loopen körs

	static int noMessageReceivedFor = 0;

	static String standardFont = "TimesRoman";

	// skalor utifrån skärmens upplösning
	public static double scaleWidth;
	public static double scaleHeight;

	static double scaleWidthZoom;
	static double scaleHeightZoom;

	/*
	 * static double currentScaleWidth = 1; static double currentScaleHeight = 1;
	 */
	// static double scalingSpeed = 0.005;

	static boolean fadeOut = false;

	static float currentAlpha = 0f;

	public static boolean zoomOutDone = false;

	public static int panelWidth = 1920; // panelens bredd och höjd
	public static int panelHeight = 1080;

	public static int screenWidth;
	public static int screenHeight;

	static JFrame frame;
	public static boolean devMode = false;

	// olika game states
	enum States {
		inMainMenu, inLoadingScreen, inGameMenu, inGame;
	}

	static States state;

	public Screen() {
		super();
		state = States.inMainMenu;

		// startar InputHandler och lägger in alla eventListeners
		input = new InputHandler();
		addKeyListener(input);
		addMouseMotionListener(input);
		addMouseListener(input);
		addMouseWheelListener(input);

		setFocusable(true);
		setFocusTraversalKeysEnabled(false); // gör så tab funkar

		Thread thread = new Thread(this);
		thread.start();

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		setRenderSettings(g2d, true);

		if (zoomOutDone) {
			g2d.scale(scaleWidthZoom, scaleHeightZoom);
			/*
			 * g2d.translate(panelWidth / 2, panelHeight / 2); g2d.scale(scaleWidthZoom, scaleHeightZoom); g2d.translate(-panelWidth, -panelHeight);
			 */
		} else {

			g2d.scale(scaleWidth, scaleHeight); // skalar allt enligt skalorna som räknades ut när spelet startades så att spelat funkar på alla skärmupplösningar
		}

		paintBackgrounds(g2d);

		if (devMode) {
			g2d.drawString("Playernumber: " + Client.playerNumber, 100, 100);
			g2d.drawString("Fallingspeed: " + Main.clientPlayer.getFallingSpeed(), 100, 120);

			g2d.drawString("Average delay to clients: " + Client.averageDelayToClients, 100, 140);
			g2d.drawString("x: " + Main.clientPlayer.getX(), 100, 160);
			g2d.drawString("y: " + Main.clientPlayer.getY(), 100, 180);
			g2d.drawString("LengthFallen : " + Main.clientPlayer.getLengthFallen(), 100, 200);
			g2d.drawString("Moving speed : " + Main.clientPlayer.getMovingSpeed(), 100, 220);
		}

		paintWorldObjects(g2d);
		paintPlayers(g2d);

		g2d.fillRect(fixX(0, 1), fixY(0, 1), MapHandler.worldWidth, 50);
		g2d.fillRect(fixX(0, 1), fixY(0, 1), 50, MapHandler.worldHeight);
		g2d.fillRect(fixX(MapHandler.worldWidth - 50, 1), fixY(0, 1), 50, MapHandler.worldHeight);
		g2d.fillRect(fixX(0, 1), fixY(MapHandler.worldHeight - 50, 1), MapHandler.worldWidth, 50);

		/*
		 * for (int i = 0; i < MapHandler.treeSpawns.size(); i++) { TreeSpawn TS = MapHandler.treeSpawns.get(i); g2d.fillRect(fixX(TS.getX(), 1), fixY(TS.getY(), 1), TS.getWidth(), TS.getHeight()); }
		 */

		setScreenAlpha(g2d, currentAlpha);
		paintUI(g2d);

	}

	static void setRenderSettings(Graphics2D g2d, boolean good) {
		if (good) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			// g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		}
	}

	void paintUI(Graphics2D g2d) {
		YourPlayer clientPlayer = Main.clientPlayer;
		clientPlayer.getActionBar().paint(g2d);

		g2d.drawImage(Images.imgCursor, InputHandler.scaledOnScreenMouseX, InputHandler.scaledOnScreenMouseY, 25, 25, null);

	}

	void paintBackgrounds(Graphics2D g2d) {
		Graphics2D g2 = reverseZoom(g2d); // reversrar zoomen

		g2.drawImage(Images.imgMainBackground, 0, 0, panelWidth, panelHeight, null);

		for (int i = 0; i < MapHandler.worldObjects.size(); i++) {
			WorldObject obj = MapHandler.worldObjects.get(i);
			if (obj instanceof BackgroundObject) {
				g2d.drawImage(obj.getImage(), fixX(obj.getX(), obj.getParalax()), fixY(obj.getY(), obj.getParalax()), obj.getWidth(), obj.getHeight(), null);
			}
		}

		g2.drawImage(Images.imgBackgroundGradient, 0, 0, panelWidth, panelHeight, null);
		g2d.setColor(Color.black);
		g2d.fillRect(fixX(0, 1), fixY(MapHandler.groundLevel, 1), MapHandler.worldWidth, MapHandler.worldHeight);

	}

	void paintWorldObjects(Graphics2D g2d) {
		
		for (int i = 0; i < MapHandler.worldObjects.size(); i++) {
			WorldObject object = MapHandler.worldObjects.get(i);

			if (!(object instanceof BackgroundObject) && !(object instanceof ChoppedTree)) { // bakgrunder ska inte målas här eftersom de måste målas första av allt
				g2d.drawImage(object.getImage(), fixX(object.getX(), object.getParalax()), fixY(object.getY(), object.getParalax()), object.getWidth(), object.getHeight(), null);
				// Rectangle collisionBox = object.getCollisionBox();
			} else if (object instanceof ChoppedTree) { // om det är ett chopped tree
				Graphics2D g2 = (Graphics2D) g2d.create();

				int x = fixX(object.getX(), object.getParalax());
				int y = fixY(object.getY(), object.getParalax());

				int rotX = (int) (x + object.getWidth() * 0.45);
				int rotY = (int) (y + (object.getHeight()));

				g2.rotate(((ChoppedTree) object).getRotation(), rotX, rotY);
				g2.drawImage(object.getImage(), x, y, object.getWidth(), object.getHeight(), null);
			}

		}
	}

	public static Graphics2D reverseZoom(Graphics2D g2d) {
		Graphics2D g2 = (Graphics2D) g2d.create(); // skapar en kopia av graphics objectet

		// reverear zoomen
		if (!zoomOutDone) {
			g2.scale(1 / scaleWidth, 1 / scaleHeight); // skalar allt enligt skalorna som räknades ut när spelet startades så att spelat funkar på alla skärmupplösningar
		} else {
			g2.scale(1 / scaleWidthZoom, 1 / scaleHeightZoom);
		}

		return g2;
	}

	void paintPlayers(Graphics2D g2d) {
		for (int i = 0; i < Main.players.size(); i++) {
			Player player = Main.players.get(i);
			player.paint(g2d);

		}
	}

	public static void setScreenAlpha(Graphics2D g2d, float alpha) {
		Graphics2D g2 = (Graphics2D) g2d.create();

		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2.setComposite(alcom);
		g2.fillRect(0, 0, MapHandler.worldWidth, MapHandler.worldHeight);
	}

	public static void toggleZoom() {
		fadeOut = !fadeOut;
	}

	static void updateScreen() {
		if (fadeOut) {
			if (currentAlpha < 1) {
				currentAlpha += 0.03;
				if (currentAlpha >= 1) { // när den är klar
					currentAlpha = 1;

					zoomOutDone = !zoomOutDone;
					fadeOut = false;

				}
			}
		} else {
			if (currentAlpha > 0) {
				currentAlpha -= 0.03;
				if (currentAlpha < 0) { // när den är klar
					currentAlpha = 0;
				}
			}
		}
	}

	public static int fixX(int oldX, double paralax) {
		int newX = 0;
		if (!zoomOutDone) {
			newX = (int) (oldX - (Main.clientPlayer.getX() * paralax) + Main.clientPlayer.getOnScreenX());
		} else {
			newX = (int) (oldX - 0);
		}

		return newX;
	}

	public static int fixY(int oldY, double paralax) {
		int newY = 0;
		if (!zoomOutDone) {
			newY = (int) (oldY - (Main.clientPlayer.getY() * paralax) + Main.clientPlayer.getOnScreenY());
		} else {
			newY = (int) (oldY - 0);

		}
		return newY;
	}

	// hämtar en texts bredd
	public static int getTextWidth(Font font, String text) {
		int width = 0;

		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
		width = (int) (font.getStringBounds(text, frc).getWidth());

		return width;
	}

	public static int getTextHeight(Graphics2D g2d, String text, Font font) {

		FontMetrics metrics = g2d.getFontMetrics(font);
		int height = metrics.getHeight();

		return height;
	}

	// används för att på ett enklare sätt skriva ut text på skärmen. Denna metod används tillsammans med wrapLines()
	// som gör att texten hamnar på en ny rad om textens bredd överstrider 'lineWrapWidth'
	static void paintText(int x, int y, String fontname, int style, int size, String text, Color color, Graphics2D g, int lineWrapWidth, boolean centerText) {
		Font oldFont = g.getFont();
		Font font = new Font(fontname, style, size);

		int textWidth = getTextWidth(font, text);
		if (centerText) {
			x = x - textWidth / 2;
		}

		g.setFont(font);
		g.setColor(color);

		// wrapLines() delar upp texten i olika rader
		String[] lines = wrapLines(text, font, size, lineWrapWidth);

		for (int i = 0; i < lines.length; i++) {
			g.drawString(lines[i], x, y + size + size * i);
		}
		g.setFont(oldFont);
	}

	// används för att dela in text i flera rader
	static String[] wrapLines(String text, Font font, int size, int lineWrapWidth) {
		String strReturn[] = null;

		String buildString = "";
		String nextLineString = "";

		String[] words = text.split(" ");

		for (int i = 0; i < words.length; i++) {
			// om ett ord börjar på '#' ska raden brytas där
			if (words[i].startsWith("#")) {
				nextLineString = " ";
			}

			// räknar ut bredden på texten
			AffineTransform affinetransform = new AffineTransform();
			FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
			int textWidth = (int) (font.getStringBounds(nextLineString, frc).getWidth());

			// kollar om bredden är överstridigt radbredden
			if (textWidth > lineWrapWidth) {
				buildString += "#"; // '#' visar att det ska vara en ny rad där
				nextLineString = "";
			}

			buildString += words[i] + " ";
			nextLineString += words[i] + " ";

		}

		strReturn = buildString.split("#");

		return strReturn;
	}

	// skapar framen, detta körs först av allt
	static void createFrame() {
		Images.initImages(); // laddar in bilderna

		// Toolkit tk = Toolkit.getDefaultToolkit();

		// hittar skalan för skärmen så det kan skalas senare i paint metoden
		scaleWidth = screenWidth / (Screen.panelWidth * 1.0);
		scaleHeight = screenHeight / (Screen.panelHeight * 1.0);

		// gör så att muspekaren blir en bild
		// Point hotspot = new Point(1, 1); // sätter 'hotspot' för crosshairet, (pointen där mus-event ska ske), i detta fall ska det vara i mitten av muspekaren och bilden är 34x34
		// Cursor myCursor = tk.createCustomCursor(Images.imgCursor, hotspot, "cursor");

		// skapar framen
		frame = new JFrame();
		frame.add(new Screen());
		frame.setTitle("Tessa");
		frame.setUndecorated(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(screenWidth, screenHeight);
		frame.setVisible(true);
		frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));

		// ändrar programmets ikon
		ImageIcon imgIcon = new ImageIcon(Images.class.getClassLoader().getResource("images/icon.png"));
		Image img = imgIcon.getImage();
		frame.setIconImage(img);
		System.out.println("Frame created");

		// Sound.startMusic();
	}

	public void run() {

		while (true) {
			tick += sleep;
			singleTick++;
			// om man är inne i spelet eller i inGame-menyn
			if (state == States.inGame || state == States.inGameMenu) {
				noMessageReceivedFor = noMessageReceivedFor + sleep; // räknar tid
			}
			Main.update();
			repaint();
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
