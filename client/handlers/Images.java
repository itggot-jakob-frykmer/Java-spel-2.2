/*
 HÃ¤r lÃ¤ses nÃ¥gra utav bilderna som anvÃ¤nds in


 */
package client.handlers;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Images {

	public static Image imgMainBackground;
	public static Image imgBackgroundGradient;
	public static Image imgCursor;
	static Image imgSign;

	static String[] acceptedImgExtensions = { "png", "jpg", "jpeg", "gif" };

	// static ArrayList<String> acceptedImageExtensions = new ArrayList<String>("png", "jpg", "jpeg", "gif");

	// körs när spelet startas fär att läsa in bilder
	public static void initImages() throws NullPointerException {

		try {
			ImageIcon imgIcon;

			imgIcon = new ImageIcon(Images.class.getClassLoader().getResource("images/backgrounds/background0.png"));
			imgMainBackground = imgIcon.getImage();

			imgIcon = new ImageIcon(Images.class.getClassLoader().getResource("images/backgrounds/background2.png"));
			imgBackgroundGradient = imgIcon.getImage();

			imgIcon = new ImageIcon(Images.class.getClassLoader().getResource("images/sign.jpg"));
			imgSign = imgIcon.getImage();

			imgIcon = new ImageIcon(Images.class.getClassLoader().getResource("images/ui/cursor.png"));
			imgCursor = imgIcon.getImage();

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		System.out.println("Images done");

	}

	public static Image readImageFromPath(String path) {
		Image img = null;
		try {
			ImageIcon imgIcon = new ImageIcon(Images.class.getClassLoader().getResource("images/" + path));
			img = imgIcon.getImage();

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return img;
	}

	static Image readImageFromAbsolutePath(String path) {
		Image img = null;
		try {
			ImageIcon imgIcon = new ImageIcon(path);
			img = imgIcon.getImage();

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return img;
	}

	public static int getPropHeightFromImage(int displayWidth, Image img) {

		int imgWidth = img.getWidth(null);
		int imgHeight = img.getHeight(null);

		double heightToWidth = (imgHeight * 1.0 / imgWidth * 1.0);

		int displayHeight = (int) (displayWidth * heightToWidth);

		return displayHeight;
	}

	// läser in alla bilder från en mapp
	// static ArrayList<Image> readAllImagesFromPath(String path) {
	// System.out.println(path);
	/*
	 * System.out.println("KÖR1"); String absolute = Images.class.getProtectionDomain().getCodeSource().getLocation().getPath(); System.out.println("Absolute: " + absolute); Image img2 = readImageFromAbsolutePath(absolute + path); System.out.println("img : " + img2); System.out.println(Images.class.getClassLoader().getResource("images/" + path + "00.png")); path = Images.class.getClassLoader().getResource("images/" + path).getPath(); System.out.println("PATH:" + path); ArrayList<Image> images =
	 * new ArrayList<Image>(); // path = path.replace("%20", " "); File folder = new File(path); System.out.println(folder); System.out.println(folder.exists());
	 */
	/*
	 * ArrayList<Image> images = new ArrayList<Image>(); URL url = Images.class.getClassLoader().getResource("images/"); System.out.println(url);
	 * 
	 * File folder = null; try { folder = new File(url.toURI()); } catch (URISyntaxException e1) { // TODO Auto-generated catch block e1.printStackTrace(); } System.out.println(folder.exists());
	 * 
	 * File[] listOfFiles = folder.listFiles(); try { for (int i = 0; i < listOfFiles.length; i++) { if (listOfFiles[i].isFile()) { // hämtar vilket filtyp det är String[] fileSplit = listOfFiles[i].getAbsolutePath().split("\\."); String fileExtension = fileSplit[fileSplit.length - 1];
	 * 
	 * // kollar så att filen som hittades är en bild if (Arrays.asList(acceptedImgExtensions).contains(fileExtension)) { Image img = readImageFromAbsolutePath(listOfFiles[i].getPath()); images.add(img); }
	 * 
	 * } } } catch (NullPointerException e) { e.printStackTrace(); }
	 * 
	 * return images; }
	 */

	public static void main(String[] args) {

	}
}
