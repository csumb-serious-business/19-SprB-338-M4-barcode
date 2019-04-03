/* ------------------------------------------------------------------------- *\
Authors:    C Ahangama, J Asato, M Robertson, R Talmage
Class:      CST338
Assignment: M4 Optical Barcode Readers
Date:       4/2/2019
\* ------------------------------------------------------------------------- */

/**
 * Represents the functionality of a stored Barcode
 */
interface BarcodeIO {
    /**
     * Stores a barcode image
     *
     * @param barcodeImage the image to store
     * @return true if the scan was successful
     */
    public boolean scan(BarcodeImage barcodeImage);

    /**
     * Stores a plaintext message
     *
     * @param text the text to store
     * @return true if the read process succeeded
     */
    public boolean readText(String text);

    /**
     * Converts/stores an already stored plaintext message
     * into its corresponding barcode image
     *
     * @return true if successful
     */
    public boolean generateImageFromText();


    /**
     * Converts/stores an already stored barcode image
     * into its corresponding plaintext message
     *
     * @return true if successful
     */
    public boolean translateImageToText();

    /**
     * Prints the contents of the currently stored
     * plaintext message to the console
     */
    public void displayTextToConsole();

    /**
     * Prints the contents of the currently stored
     * barcode image to the console
     */
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

/**
 * Represents a 2D dot-matrix pattern or 'barcode image'
 */
class BarcodeImage implements Cloneable {
    public static final int MAX_HEIGHT = 30;
    public static final int MAX_WIDTH = 65;

    private final boolean[][] imageData;

    /**
     * Creates a barcode image with empty image data
     */
    public BarcodeImage() {
        imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];
    }

    /**
     * Creates a barcode image with a given dataset
     *
     * @param strData the dataset to populate this barcode image with
     *                if the dataset is too large,
     *                the stored barcode will be empty
     */
    public BarcodeImage(String[] strData) {
        this();

        // guardrail on height
        if (!checkSize(strData)) {
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

    /**
     * Given a row and column, fetches the value at those positions
     *
     * @param row    the row of the value to fetch
     * @param column the column of the value to fetch
     * @return the found value  or `false` in case of error
     */
    boolean getPixel(int row, int column) {
        if (isValidCoordinate(row, column)) {
            return imageData[row][column];
        }

        // bad practice, false indicates both error & false states
        // leave since it is per spec
        return false;
    }

    /**
     * Overwrites the value at a particular row/column intersection
     *
     * @param row    the row of the value to overwrite
     * @param column the column of the value to overwrite
     * @param value  the value to write at that position
     * @return true if write was succesful
     */
    boolean setPixel(int row, int column, boolean value) {
        if (isValidCoordinate(row, column)) {
            imageData[row][column] = value;
            return true;
        }
        return false;
    }


    /**
     * Validates a whether a given `String[]`
     * meets nullability & size requirements.
     *
     * @param data the data to validate
     * @return true if data meets the requirements
     */
    private boolean checkSize(String[] data) {
        if (data == null || data.length > MAX_HEIGHT) {
            return false;
        }

        for (String s : data) {
            if (s == null || s.length() > MAX_WIDTH) {
                return false;
            }
        }

        return true;
    }


    /**
     * Checks whether a given pair of coordinates
     * represents a valid position in this barcode image
     *
     * @param row    the row to check
     * @param column the column to check
     * @return true if position is valid
     */
    boolean isValidCoordinate(int row, int column) {
        return 0 <= column && column < MAX_WIDTH &&
                0 <= row && row < MAX_HEIGHT;
    }


    @Override
    public BarcodeImage clone() throws CloneNotSupportedException {
        BarcodeImage cloned = (BarcodeImage) super.clone();

        // Deep Copy
        for (int iRow = 0; iRow < MAX_HEIGHT; iRow++) {
            for (int iCol = 0; iCol < MAX_WIDTH; iCol++) {
                cloned.imageData[iRow][iCol] = this.getPixel(iRow, iCol);
            }
        }

        return cloned;
    }

    /**
     * Prints this barcode image
     */
    public void displayToConsole() {
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

/**
 * Represents a pseudo data matrix lacking error correction and
 * encoding functionality.
 * Holds a BarcodeImage and its textual representation
 */
class DataMatrix implements BarcodeIO {
    public static final char BLACK_CHAR = '*';
    public static final char WHITE_CHAR = ' ';

    private BarcodeImage image;
    private String text;
    private int actualWidth;
    private int actualHeight;

    /**
     * Creates an empty data matrix
     */
    public DataMatrix() {
        image = new BarcodeImage();
        text = "";
        actualWidth = 0;
        actualHeight = 0;

    }

    /**
     * Creates a data matrix with a given barcode image
     * the plaintext of the image will remain
     * empty until `scan` is called
     *
     * @param image the image to populate into this data matrix
     */
    public DataMatrix(BarcodeImage image) {
        this();
        scan(image);
    }

    /**
     * Creates a data matrix with a given plaintext
     * the barcode image will remain
     * empty until `readText`is called
     *
     * @param text the text to populate into this data matrix
     */
    public DataMatrix(String text) {
        this();
        readText(text);
    }

    @Override
    public boolean scan(BarcodeImage barcodeImage) {
        try {
            image = barcodeImage.clone();
            cleanImage(); // Adjusting to the left bottom
            actualHeight = computeSignalHeight();
            actualWidth = computeSignalWidth();
        } catch (CloneNotSupportedException e) { /* do nothing */ }
        return true;
    }

    @Override
    public boolean readText(String text) {
        // todo need guardrail, we know that width < 63
        this.text = text;
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

    /**
     * Detects the rightmost edge of the actual image within barcode image
     * to calculate the actual width of the image
     *
     * @return the calculated width
     */
    private int computeSignalWidth() {
        //todo how is this working?
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

    /**
     * Detects the topmost edge of the actual image within image
     * to calculate the actual height of the image
     *
     * @return the calculated height
     */
    private int computeSignalHeight() {
        //todo how is this working?
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


    @Override
    public boolean translateImageToText() {
        StringBuilder message = new StringBuilder();
        int start = 1; // ingore the skeleton
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
        image.displayToConsole();
    }


    /**
     * Detects the left top, right and bottom edges of the `image` within
     * the barcode image and transposes that image such that
     * the left and bottom edges of both images align
     * with that of the barcode image.
     */
    private void cleanImage() {
        int rowStart = 0;
        int colStart = 0;

        for (int r = BarcodeImage.MAX_HEIGHT - 1; r >= 0; r--) {
            for (int c = 0; c < BarcodeImage.MAX_WIDTH; c++) {
                // pixel detected?
                if (image.getPixel(r, c)) {
                    // at the start?
                    if (rowStart == 0 && colStart == 0) {
                        // Hold the value of where the first pixel is detected
                        rowStart = r;
                        colStart = c;
                        break;
                    }
                }
            }
        }

        for (int r = 0; r < BarcodeImage.MAX_HEIGHT; r++) {
            for (int c = 0; c < BarcodeImage.MAX_WIDTH; c++) {
                // must be non-negative
                if (rowStart - r >= 0) {
                    // must be less than width
                    if (colStart + c < BarcodeImage.MAX_WIDTH) {
                        // Map the old to new position, start at top
                        int newRow = BarcodeImage.MAX_HEIGHT - r - 1;

                        int oldRow = rowStart - r; // Go Up
                        int oldCol = colStart + c; // Go Right

                        image.setPixel(newRow, c, image.getPixel(oldRow, oldCol));
                    }
                }
            }
        }
    }

    /**
     * Prints the uncropped (raw) barcode image
     */
    public void displayRawImage() {
        image.displayToConsole();
    }

    /**
     * Resets all pixels in barcode image to `false` (blank)
     */
    public void clearImage() {
        image = new BarcodeImage();
    }


    /**
     * Fetches the corresponding ascii character for a given column
     *
     * @param column the column to decode into a character
     * @return the corresponding decoded character from the sum of the
     * columns values
     */
    private char readCharfromCol(int column) {
        int sum = 0;

        // Exclude bottom and top
        int maxHeight = actualHeight - 2;
        int adjustedRowIndex = BarcodeImage.MAX_HEIGHT - 1;
        for (int i = 0; i < maxHeight; i++) {
            // start 1 beyond the skeleton
            int nRow = adjustedRowIndex - (i + 1);

            // if pixel is populated, add it
            if (image.getPixel(nRow, column)) {
                sum += (int) (Math.pow(2, i));
            }
        }

        // corresponding ascii for sum
        return ((char) sum);
    }

    /**
     * Writes the corresponding boolean sequence into a column in
     * the barcode image for a given coded ascii character
     *
     * @param column the column to write into
     * @param ascii  the ascii character to encode
     * @return true if the write was successful
     */
    private boolean WriteCharToCol(int column, int ascii) {
        // todo guardrail -> return false

        // Exclude bottom and top
        int height = BarcodeImage.MAX_HEIGHT - 2;

        byte b1 = (byte) ascii;
        String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF))
                .replace(' ', '0');
        for (int i = 0; i < s1.length(); i++) {
            int row = height - (s1.length() - 1 - i);

            if (s1.charAt(i) == '1') {
                image.setPixel(row, column, true);
            } else {
                image.setPixel(row, column, false);
            }
        }

        return true;

    }


}