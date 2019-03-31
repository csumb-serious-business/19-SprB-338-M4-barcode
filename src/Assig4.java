/* ------------------------------------------------------------------------- *\
Authors:    C Ahangama, J Asato, M Robertson, R Talmage
Class:      CST338
Assignment: M4 Optical Barcode Readers
Date:       4/2/2019
\* ------------------------------------------------------------------------- */

interface BarcodeIO {
	public boolean scan(BarcodeImage bc);

	public boolean readText(String text);

	public boolean generateImageFromText();

	public boolean translateImageToText();

	public void displayTextToConsole();

	public void displayImageToConsole();

}

public class Assig4 {
	public static void main(String[] args) {
		String[] sImageIn = { "                                               ",
				"                                               ", "                                               ",
				"     * * * * * * * * * * * * * * * * * * * * * ", "     *                                       * ",
				"     ****** **** ****** ******* ** *** *****   ", "     *     *    ****************************** ",
				"     * **    * *        **  *    * * *   *     ", "     *   *    *  *****    *   * *   *  **  *** ",
				"     *  **     * *** **   **  *    **  ***  *  ", "     ***  * **   **  *   ****    *  *  ** * ** ",
				"     *****  ***  *  * *   ** ** **  *   * *    ", "     ***************************************** ",
				"                                               ", "                                               ",
				"                                               "

		};

		String[] sImageIn_2 = { "                                          ",
				"                                          ", "* * * * * * * * * * * * * * * * * * *     ",
				"*                                    *    ", "**** *** **   ***** ****   *********      ",
				"* ************ ************ **********    ", "** *      *    *  * * *         * *       ",
				"***   *  *           * **    *      **    ", "* ** * *  *   * * * **  *   ***   ***     ",
				"* *           **    *****  *   **   **    ", "****  *  * *  * **  ** *   ** *  * *      ",
				"**************************************    ", "                                          ",
				"                                          ", "                                          ",
				"                                          "

		};

		System.out.print("Start");
		BarcodeImage bc = new BarcodeImage(sImageIn);
		DataMatrix dm = new DataMatrix(bc);

		// First secret message
		dm.translateImageToText();
		dm.displayTextToConsole();
		dm.displayImageToConsole();

		// second secret message
		bc = new BarcodeImage(sImageIn_2);
		dm.scan(bc);
		dm.translateImageToText();
		dm.displayTextToConsole();
		dm.displayImageToConsole();

		// create your own message
		dm.readText("What a great resume builder this is!");
		dm.generateImageFromText();
		dm.displayTextToConsole();
		dm.displayImageToConsole();
	}
}

class BarcodeImage implements Cloneable {
	public static final int MAX_HEIGHT = 30;
	public static final int MAX_WIDTH = 65;
	private boolean[][] imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];

	public BarcodeImage(String[] strIn) {
		this();

		// Todo Check Size

		for (int i = 0; i < strIn.length; i++) // How many row
		{
			for (int j = 0; j < strIn[0].length(); j++) // How many in row
			{
				char[] raw = strIn[i].toCharArray();
				int rowIndex = MAX_HEIGHT - (strIn.length - i);
				if (raw[j] == '*') {
					setPixel(rowIndex, j, true);
				} else {
					setPixel(rowIndex, j, false);
				}
			}
		}

	}

	public BarcodeImage() {
		for (boolean[] arr2 : imageData) {
			for (boolean val : arr2) {
				val = false;
			}
		}
	}

	boolean setPixel(int row, int col, boolean value) {
		// Todo Validation
		imageData[row][col] = value;
		return true;

	}

	boolean getPixel(int row, int col) {
		return imageData[row][col];
	}

	public BarcodeImage clone() throws CloneNotSupportedException {
		BarcodeImage returnBC = null;
		try {
			returnBC = (BarcodeImage) super.clone();

			// Deep Copy
			for (int i = 0; i < MAX_HEIGHT; i++) {
				for (int j = 0; j < MAX_WIDTH; j++) {
					returnBC.imageData[i][j] = this.getPixel(i, j);
				}
			}

		} catch (CloneNotSupportedException ex) {
			// Do Nothing Explict dont throw

		}
		// return the clone
		return returnBC;

	}

}

class DataMatrix implements BarcodeIO {
	// DATA
	private BarcodeImage image;
	public static final char BLACK_CHAR = '*';
	public static final char WHITE_CHAR = ' ';
	private int actualWidth, actualHeight = 0;
	private String text;
	private static final String defText = "undefined";

	public DataMatrix() {
		image = new BarcodeImage();
		text = defText;
		actualWidth = 0;
		actualHeight = 0;

	}

	public DataMatrix(BarcodeImage imageIn) {
		this();
		scan(imageIn);
	}

	public DataMatrix(String textIn) {
		this();
		readText(text);
	}

	public boolean translateImageToText() {

		return false;
	}

	public void displayTextToConsole() {

	}

	public void displayImageToConsole() {

	}

	public boolean scan(BarcodeImage _) {

		return false;
	}

	public boolean readText(String _) {
		return false;
	}

	public boolean generateImageFromText() {
		return false;
	}

	// Private Helpers For image manipulation
	private void cleanImage() {

	}

}