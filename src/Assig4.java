/* ------------------------------------------------------------------------- *\
Authors:    C Ahangama, J Asato, M Robertson, R Talmage
Class:      CST338
Assignment: M4 Optical Barcode Readers
Date:       4/2/2019
\* ------------------------------------------------------------------------- */

import java.util.*;

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
        String[] sImageIn = {
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


        String[] sImageIn_2 = {
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
        System.out.println("DEBUG::Start of Run");
        BarcodeImage bc = new BarcodeImage(sImageIn);
        System.out.println("DEBUG::Before just on the Intake");
        bc.display();

        System.out.println("DEBUG::After Scanning and Cleaing");
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
        // for each row in strIn
        if(checkSize(strIn) == false) return;
        for (int iRow = 0; iRow < strIn.length; iRow++) {
            int numOfColums = strIn[0].length();

            // for each column in columns
            for (int iColumn = 0; iColumn < numOfColums; iColumn++) {
                char[] raw = strIn[iRow].toCharArray();

                // cut down on the extra spacing
                int rowIndex = MAX_HEIGHT - (strIn.length - iRow);

                if (raw[iColumn] == '*') {
                    setPixel(rowIndex, iColumn, true);
                } else {
                    setPixel(rowIndex, iColumn, false);
                }
            }
        }

    }

	public BarcodeImage(){
    	for(int i = 0; i < MAX_WIDTH; i++)
    	{
    	    for(int j = 0; j < MAX_HEIGHT; j++)
    	    {
    	    	setPixel(j, i, false);
    	    }
    	}  	
    }
	
    private boolean checkSize(String[] data) {
        if (data.length > MAX_HEIGHT || data == null) return false;
        for(String s: data) {
        	if(s.length() > MAX_WIDTH) return false;
        }
        return true;
    }


    boolean setPixel(int row, int col, boolean value) {
    	if(row > MAX_HEIGHT || col > MAX_WIDTH || row < 0 || col < 0) return false; 
        imageData[row][col] = value;
        return true;

    }

    boolean getPixel(int row, int col) {
    	if(row > MAX_HEIGHT || col > MAX_WIDTH || row < 0 || col < 0) return false;
        return imageData[row][col];
    }
    
    public void displayToConsole() {
    	for(boolean[]arr: imageData) {
    		for(boolean val: arr) {
    			if(val) System.out.print('*');
    			else System.out.print(' ');
    		}
    		System.out.print('\n');
    	}
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

        } catch (CloneNotSupportedException ex) {
            // Explicitly Do Nothing (don't throw)

        }

        // return
        return returnBC;

    }

    public void display() {
        int iRow, iCol;
        System.out.println();
        for (iRow = 0; iRow < MAX_HEIGHT; iRow++) {
            System.out.print("|" + iRow + "|");
            for (iCol = 0; iCol < MAX_WIDTH; iCol++) {
                if (getPixel(iRow, iCol) == true) {
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
    private static final String defText = "undefined";
    // DATA
    private BarcodeImage image;
    private int actualWidth, actualHeight = 0;
    private String text;

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
        String message = "";
        int start = 1; // Ingore the skeleton
        for (int iOfColumns = start; iOfColumns < actualWidth - 1; iOfColumns++) {
            message += readCharfromCol(iOfColumns);
        }

        text = message;

        return false;
    }

    public void displayTextToConsole() {
        System.out.println("    " + this.text);

    }

    public void displayImageToConsole() {

    }

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

    public boolean readText(String _) {
        return false;
    }

    public boolean generateImageFromText() {
        return false;
    }

    // Private Helpers For image manipulation to move it to the left bottom
    private void cleanImage() {
        // Temp vars for the Starting point of the image before the optimiztion
        int rowStart = 0;
        int colStart = 0;

        outter:
        for (int indexHorizontal = BarcodeImage.MAX_HEIGHT - 1; indexHorizontal >= 0; indexHorizontal--) {

            for (int indexVertical = 0; indexVertical < BarcodeImage.MAX_WIDTH; indexVertical++) {
                // pixel detected
                if (image.getPixel(indexHorizontal, indexVertical)) {

                    // are we at the start?
                    if (rowStart == 0 && colStart == 0) {
                        // Hold the value of where the first pixel is detected
                        rowStart = indexHorizontal;
                        colStart = indexVertical;
                        break outter; // break If detected
                    }
                }
            }
        }

        for (int indexVeriticle = 0; indexVeriticle < BarcodeImage.MAX_HEIGHT; indexVeriticle++) {
            for (int indexHorizontal = 0; indexHorizontal < BarcodeImage.MAX_WIDTH; indexHorizontal++) {
                // Can't be negative
                if (rowStart - indexVeriticle >= 0) {

                    // must be less than the width
                    if (colStart + indexHorizontal < BarcodeImage.MAX_WIDTH) {
                        // Mapping the old to new position, starting at the top
                        int newRow = BarcodeImage.MAX_HEIGHT - indexVeriticle - 1;
                        int newCol = indexHorizontal;

                        int oldRow = rowStart - indexVeriticle; // Go Up
                        int oldCol = colStart + indexHorizontal; // Go Right

                        image.setPixel(newRow, newCol, image.getPixel(oldRow, oldCol));
                    }
                }
            }
        }
        image.display(); // debugging
    }

    // Todo: rough idea
    int computeSignalWidth() {
        int leftCornerRow = image.MAX_HEIGHT - 1;
        int widthCount = 0;

        // keep going right
        for (int indexColumn = 0; indexColumn < image.MAX_WIDTH; indexColumn++) {
            if (image.getPixel(leftCornerRow, indexColumn)) {
                widthCount++;
            }
        }
        return widthCount;
    }

    // Todo: rough idea
    int computeSignalHeight() {
        int leftCornerCol = 0;
        int heightCount = 0;
        // keep going up
        for (int indexRow = 0; indexRow < image.MAX_HEIGHT; indexRow++) {
            if (image.getPixel(indexRow, leftCornerCol)) {
                heightCount++;
            }
        }
        return heightCount;
    }

    private char readCharfromCol(int col) {
        int numVal = 0;
        int maxHeight = actualHeight - 2;// Exclude bottom and top
        int adjustedRowIndex = image.MAX_HEIGHT - 1;
        List<Integer> l1 = new ArrayList<>();
        for (int i = 0; i < maxHeight; i++) {

            // i+1 because we want to start from the one after the skeleton
            int nRow = adjustedRowIndex - (i + 1);

            // If it is a solid * then we continue
            if (image.getPixel(nRow, col)) {
                // Since this binary add to list of values
                l1.add((int) (Math.pow(2, i)));
            }
        }
        // Interate the list
        for (Integer value : l1) {
            numVal += value;
        }

        return ((char) numVal); // return as a char for the valid ascii number
    }

    private boolean WriteCharToCol(int col, int code) {
        return true;
    }

}