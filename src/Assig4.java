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
        final String[] IMAGE_1 = {
                "                                               ",
                "                                               ",
                "                                               ",
                "     * * * * * * * * * * * * * * * * * * * * * ",
                "     *                                       * ",
                "     ****** **** ****** ******* ** *** *****   ",
                "     *     *    ****************************** ",
                "     * **    * *        **  *    * * *   *     ",
                "     *   *    *  *****    *   * *   *  **  *** ",
                "     *  **     * *** **   **  *    **  ***  *  ",
                "     ***  * **   **  *   ****    *  *  ** * ** ",
                "     *****  ***  *  * *   ** ** **  *   * *    ",
                "     ***************************************** ",
                "                                               ",
                "                                               ",
                "                                               "

        };


        final String[] IMAGE_2 = {
                "                                          ",
                "                                          ",
                "* * * * * * * * * * * * * * * * * * *     ",
                "*                                    *    ",
                "**** *** **   ***** ****   *********      ",
                "* ************ ************ **********    ",
                "** *      *    *  * * *         * *       ",
                "***   *  *           * **    *      **    ",
                "* ** * *  *   * * * **  *   ***   ***     ",
                "* *           **    *****  *   **   **    ",
                "****  *  * *  * **  ** *   ** *  * *      ",
                "**************************************    ",
                "                                          ",
                "                                          ",
                "                                          ",
                "                                          "

        };


        BarcodeImage bc = new BarcodeImage(IMAGE_1);
        DataMatrix dm = new DataMatrix(bc);

        // First secret message
        dm.translateImageToText();
        dm.displayTextToConsole();
        dm.displayImageToConsole();

        // second secret message
        bc = new BarcodeImage(IMAGE_2);
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

    private final boolean[][] imageData;

    public BarcodeImage(String[] strData) {
        this();

        // guardrail on height
        if (strData.length > MAX_HEIGHT) {
            return;
        }

        // for each row
        for (int r = 0; r < strData.length; r++) {
            char[] raw = strData[r].toCharArray();

            //TODO this looks too complex. Traversing forward when need reverse?
            int rowIndex = MAX_HEIGHT - (strData.length - r);

            // for each column
            for (int c = 0; c < strData[0].length(); c++) {


                if (raw[c] == '*') {
                    setPixel(rowIndex, c, true);
                } else {
                    setPixel(rowIndex, c, false);
                }
            }
        }

    }

    public BarcodeImage() {
        imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];
    }

    private boolean checkSize(String[] data) {
        if (data == null || data.length > MAX_HEIGHT) {
            return false;
        }

        for (String s : data) {
            if (s == null || s.length() > MAX_WIDTH) return false;
        }

        return true;
    }


    boolean setPixel(int row, int col, boolean value) {
        if (isValidCoordinate(row, col)) {
            imageData[row][col] = value;
            return true;
        }
        return false;
    }

    boolean getPixel(int row, int col) {
        if (isValidCoordinate(row, col)) {
            return imageData[row][col];
        }

        // bad practice, false indicates both error & false states
        // leave since it is per spec
        return false;
    }

    boolean isValidCoordinate(int row, int column) {
        return 0 <= column && column < MAX_WIDTH &&
                0 <= row && row < MAX_HEIGHT;
    }


	@Override
	public BarcodeImage clone() throws CloneNotSupportedException {
		BarcodeImage returnBC = null;
		try {
			returnBC = (BarcodeImage) super.clone();

			// Deep Copy
			for (int iRow = 0; iRow < MAX_HEIGHT; iRow++) {
				for (int iCol = 0; iCol < MAX_WIDTH; iCol++) {
					returnBC.imageData[iRow][iCol] = this.getPixel(iRow, iCol);
				}
			}

		} catch (CloneNotSupportedException ex) {/* do nothing */}
		// return the clone
		return returnBC;

	}


    public void display() {
        for (int r = 0; r < MAX_HEIGHT; r++) {
            for (int c = 0; c < MAX_WIDTH; c++) {
                if (getPixel(r, c)) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

}

class DataMatrix implements BarcodeIO {
    public static final char BLACK_CHAR = '*';
    public static final char WHITE_CHAR = ' ';


    private BarcodeImage image;
    private int actualWidth;
    private int actualHeight;
    private String text;

    public DataMatrix() {
        image = new BarcodeImage();
        text = "";
        actualWidth = 0;
        actualHeight = 0;

    }

    public DataMatrix(BarcodeImage imageIn) {
        this();
        scan(imageIn);
    }

    public DataMatrix(String textIn) {
        this();
        readText(textIn);
    }

    @Override
    public boolean translateImageToText() {
        StringBuilder message = new StringBuilder();
        int start = 1; // Ingore the skeleton
        for (int iOfColumns = start; iOfColumns < actualWidth - 1; iOfColumns++) {
            message.append(readCharfromCol(iOfColumns));
        }

        text = message.toString();

        return true;
    }

    @Override
    public void displayTextToConsole() {
        System.out.println(text);

    }

    @Override
    public void displayImageToConsole() {
        image.display();
    }

    @Override
    public boolean scan(BarcodeImage imageIN) {
    	try {
    		this.image = (BarcodeImage) imageIN.clone();
    		cleanImage(); // Adjusting to the left bottom
    		actualHeight = computeSignalHeight();
    		actualWidth = computeSignalWidth();
		} catch (CloneNotSupportedException e) {

		}
		return true;
	}


    @Override
    public boolean readText(String inputString) {
        this.text = inputString;
        return true;
    }

    @Override
    public boolean generateImageFromText() {
        image = new BarcodeImage();
        int col = 0;
        WriteCharToCol(col++, 255);
        for (char c : this.text.toCharArray()) {
            int code = (int) c;
            WriteCharToCol(col++, code);
        }
        WriteCharToCol(col, 170);
        for (int x = 0; x <= col - 1; x++) {
            this.image.setPixel(BarcodeImage.MAX_HEIGHT - 1, x, true);
            if (x % 2 == 0)
                this.image.setPixel(BarcodeImage.MAX_HEIGHT - 10, x, true);
            else if (x % 2 != 0) {
                this.image.setPixel(BarcodeImage.MAX_HEIGHT - 10, x, false);
            }
        }


        cleanImage();
        actualHeight = computeSignalHeight();
        actualWidth = computeSignalWidth();
        return true;
    }

    // Private Helpers For image manipulation to move it to the left bottom
    private void cleanImage() {
        // Temp Holders to get the Starting point of the image before the
        // optimiztion
        int rowStart = 0, colStart = 0;

        //todo labels are disallowed, call a method instead
        outter:
        for (int indexHorizontal = BarcodeImage.MAX_HEIGHT - 1; indexHorizontal >= 0; indexHorizontal--) {

            for (int indexVertical = 0; indexVertical < BarcodeImage.MAX_WIDTH; indexVertical++) {
                // pixel detected
                if (image.getPixel(indexHorizontal, indexVertical)) {
                    // at the start?
                    //todo lint says always true
                    if (rowStart == 0 && colStart == 0) {
                        // Hold the value of where the first pixel is detected
                        rowStart = indexHorizontal;
                        colStart = indexVertical;
                        break outter;// If detected break out of the outer loop no need to iterate
                    }
                }
            }
        }

        for (int indexVeriticle = 0; indexVeriticle < BarcodeImage.MAX_HEIGHT; indexVeriticle++) {
            for (int indexHorizontal = 0; indexHorizontal < BarcodeImage.MAX_WIDTH; indexHorizontal++) {
                // must be non-negative
                if (rowStart - indexVeriticle >= 0) {
                    // must be less than width
                    if (colStart + indexHorizontal < BarcodeImage.MAX_WIDTH) {
                        // Map the old to new position, start at top
                        int newRow = BarcodeImage.MAX_HEIGHT - indexVeriticle - 1;

                        int oldRow = rowStart - indexVeriticle; // Go Up
                        int oldCol = colStart + indexHorizontal; // Go Right

                        image.setPixel(newRow, indexHorizontal, image.getPixel(oldRow, oldCol));
                    }
                }
            }
        }

    }

    int computeSignalWidth() {
        int leftCornerRow = BarcodeImage.MAX_HEIGHT - 1;
        int widthCount = 0;
        // keep going right
        for (int indexColumn = 0; indexColumn < BarcodeImage.MAX_WIDTH; indexColumn++) {
            if (image.getPixel(leftCornerRow, indexColumn)) {
                widthCount++;
            }
        }
        return widthCount;
    }

    int computeSignalHeight() {
        int leftCornerCol = 0;
        int heightCount = 0;
        // keep going up
        for (int indexRow = 0; indexRow < BarcodeImage.MAX_HEIGHT; indexRow++) {
            if (image.getPixel(indexRow, leftCornerCol)) {
                heightCount++;
            }
        }
        return heightCount;
    }

    private char readCharfromCol(int col) {
        int sum = 0;

        // Exclude bottom and top
        int maxHeight = actualHeight - 2;
        int adjustedRowIndex = BarcodeImage.MAX_HEIGHT - 1;
        for (int i = 0; i < maxHeight; i++) {
            // start 1 beyond the skeleton
            int nRow = adjustedRowIndex - (i + 1);

            // if pixel is populated, add it
            if (image.getPixel(nRow, col)) {
                sum += (int) (Math.pow(2, i));
            }
        }

        // corresponding ascii for sum
        return ((char) sum);
    }

    private boolean WriteCharToCol(int col, int ascii) {
        // todo guardrail -> return false

        // Exclude bottom and top
        int height = BarcodeImage.MAX_HEIGHT - 2;

        byte b1 = (byte) ascii;
        String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF))
                .replace(' ', '0');
        for (int i = 0; i < s1.length(); i++) {
            int row = height - (s1.length() - 1 - i);

            if (s1.charAt(i) == '1') {
                image.setPixel(row, col, true);
            } else {
                image.setPixel(row, col, false);
            }
        }

        return true;

    }

    /**
     * @return actualHeight of the DataMatrix
     */
    public int getActualHeight() {
        return actualHeight;
    }

    /**
     * @return actualWidth of the DataMatrix
     */
    public int getActualWidth() {
        return actualWidth;
    }


}