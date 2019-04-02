
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
		  String[] sImageIn =
		      {
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
		            
		         
		      
		      String[] sImageIn_2 =
		      {
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
		System.out.println("DEBUG::Start of Run img1");
		BarcodeImage bc = new BarcodeImage(sImageIn);
		DataMatrix dm = new DataMatrix(bc);

		// First secret message
		dm.translateImageToText();
		dm.displayTextToConsole();
		dm.displayImageToConsole();
		System.out.println("DEBUG::End of Run");
		
		// second secret message
		bc = new BarcodeImage(sImageIn_2);
		dm.scan(bc);
		System.out.println("DEBUG::Start of Run img2");
		dm.translateImageToText();
		dm.displayTextToConsole();
		dm.displayImageToConsole();
		System.out.println("DEBUG::End of Run");

		System.out.println("DEBUG::Start of Run txt");
		// create your own message
		dm.readText("You did it!  Great work.  Celebrate.");
		dm.generateImageFromText();
		dm.displayTextToConsole();
		dm.displayImageToConsole();
		System.out.println("DEBUG::End of Run");
	}
}

class BarcodeImage implements Cloneable {
	public static final int MAX_HEIGHT = 30;
	public static final int MAX_WIDTH = 65;
	private boolean[][] imageData = new boolean[MAX_HEIGHT][MAX_WIDTH];

	public BarcodeImage(String[] strIn) {
		this();

		// Todo Check Size
		for (int iRow = 0; iRow < strIn.length; iRow++) // For each item in the
														// array
		{
			int numOfColums = strIn[0].length();

			for (int iColumn = 0; iColumn < numOfColums; iColumn++) // How many
																	// in each
																	// item in
																	// the array
																	// (row
																	// items)
			{
				char[] raw = strIn[iRow].toCharArray();
				int rowIndex = MAX_HEIGHT - (strIn.length - iRow); // This way
																	// we cut
																	// down on
																	// the extra
																	// spacing
				if (raw[iColumn] == '*') {
					setPixel(rowIndex, iColumn, true);
				} else {
					setPixel(rowIndex, iColumn, false);
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
			// Do Nothing Explict dont throw

		}
		// return the clone
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
		String message = "";
		int start = 1; // Ingore the skeleton
		for (int iOfColumns = start; iOfColumns < actualWidth - 1; iOfColumns++) {
			message += readCharfromCol(iOfColumns);
		}

		text = message;

		return true;
	}

	public void displayTextToConsole() {
		System.out.println("    " + this.text);

	}

	public void displayImageToConsole() {
		image.display();
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

	public boolean readText(String inputString) 
	{
		this.text = inputString ;
		return true;
	}

	public boolean generateImageFromText() {
		  image = new BarcodeImage();
		  int col = 0;	  
		  WriteCharToCol(col++, 255);
	      for (char c : this.text.toCharArray()) {
	    	  int code = (int)c;
	          WriteCharToCol(col++, code);
	       }
	      WriteCharToCol(col, 170);
	      for (int x = 0; x <= col-1; x++) {
		         this.image.setPixel(BarcodeImage.MAX_HEIGHT - 1,x, true);
		         if(x % 2 == 0)
		        	 this.image.setPixel( BarcodeImage.MAX_HEIGHT - 10,x, true);
		         else if(x % 2 != 0){
		        	 this.image.setPixel( BarcodeImage.MAX_HEIGHT - 10,x, false);
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

		outter: for (int indexHorizontal = BarcodeImage.MAX_HEIGHT - 1; indexHorizontal >= 0; indexHorizontal--) {

			for (int indexVertical = 0; indexVertical < BarcodeImage.MAX_WIDTH; indexVertical++) {
				if (image.getPixel(indexHorizontal, indexVertical)) // Pixel
																	// detected
				{
					if (rowStart == 0 && colStart == 0)// Check if the we are at
														// the start
					{
						// Hold the value of where the first pixel is detected
						rowStart = indexHorizontal;
						colStart = indexVertical;
						break outter;// If detected break out of the outter loop no need to iterate
					}
				}
			}
		}
		
		for (int indexVeriticle = 0; indexVeriticle < BarcodeImage.MAX_HEIGHT; indexVeriticle++) {
			for (int indexHorizontal = 0; indexHorizontal < BarcodeImage.MAX_WIDTH; indexHorizontal++) {
				if (rowStart - indexVeriticle >= 0) // Can not be a negative
				{
					if (colStart + indexHorizontal < BarcodeImage.MAX_WIDTH) // Has
																				// to
																				// be
																				// less
																				// that
																				// the
																				// width
					{
						// Mapping the old to new position
						int newRow = BarcodeImage.MAX_HEIGHT - indexVeriticle - 1; // Start
																					// at
																					// the
																					// top
						int newCol = indexHorizontal;

						int oldRow = rowStart - indexVeriticle; // Go Up
						int oldCol = colStart + indexHorizontal; // Go Right

						image.setPixel(newRow, newCol, image.getPixel(oldRow, oldCol));
					}
				}
			}
		}
	
	}

	// Todo: rough idea
	int computeSignalWidth() {
		int leftCornerRow = image.MAX_HEIGHT - 1;
		int widthCount = 0;
		for (int indexColumn = 0; indexColumn < image.MAX_WIDTH; indexColumn++) { // Keep
																					// going
																					// right
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
		for (int indexRow = 0; indexRow < image.MAX_HEIGHT; indexRow++) { // Keep
																			// going
																			// up
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
		List<Integer> l1 = new ArrayList<Integer>();
		for (int i = 0; i < maxHeight; i++) {
			int nRow = adjustedRowIndex - (i + 1); // i+1 because we want to
													// start from the one after
													// the skeleton
			if (image.getPixel(nRow, col)) // If it is a solid * then we
											// continue
			{
				l1.add((int) (Math.pow(2, i))); // Since this binary add to list
												// of values
			}
		}
		for (Integer value : l1) { // Interate the list add the values 
			numVal += value;
		}

		return ((char) numVal); // return as a char for the valid ascii number
	}

	private boolean WriteCharToCol(int col, int code) {
	      
	       int height = BarcodeImage.MAX_HEIGHT - 2; //Top and Bottom excluded
	       int asciiValue = code; //asc
	 
	       byte b1 = (byte) asciiValue;
	       String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
	       for (int i = 0; i < s1.length(); i++) {
	           
	           if (s1.charAt(i) == '1') { 
	        	   this.image.setPixel(height-( s1.length()-1-i) ,col, true);
	           } else {
	        	   this.image.setPixel(height-( s1.length()-1-i) ,col, false);
	           }
	       }

	       return true;

	}

}