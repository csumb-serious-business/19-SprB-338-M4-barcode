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

		System.out.print("Start");
		BarcodeImage bc = new BarcodeImage(sImageIn);
		bc.display();
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
		for (int iRow = 0; iRow < strIn.length; iRow++) //For each item in the array
		{
			int numOfColums = strIn[0].length();
			
			for (int iColumn = 0; iColumn < numOfColums; iColumn++) // How many in each item in the array (row items)
			{
				char[] raw = strIn[iRow].toCharArray();
				int rowIndex = MAX_HEIGHT - (strIn.length - iRow); //This way we cut down on the extra spacing 
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
		      for(iRow = 0; iRow < MAX_HEIGHT; iRow++)
		      {
		         System.out.print("|"+iRow+"|");
		         for(iCol = 0; iCol < MAX_WIDTH; iCol++)
		         {
		            if(getPixel(iRow, iCol) == true) 
		            {
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

		return false;
	}

	public void displayTextToConsole() {

	}

	public void displayImageToConsole() {

	}

	public boolean scan(BarcodeImage imageIN) {
		 try 
	      {
	         this.image = (BarcodeImage) imageIN.clone();
	         cleanImage(); //Adjusting to the left bottom
	         actualHeight = computeSignalHeight();
	 	     actualWidth = computeSignalWidth();
	      } catch(CloneNotSupportedException e)
	      {
	       
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
		   //Temp Holders to get the Starting point of the image before the optimiztion
		   int rowStart=0, colStart = 0;
		   
		   outter:
		   for (int indexHorizontal = BarcodeImage.MAX_HEIGHT-1; indexHorizontal >= 0; indexHorizontal--) 
		   {
		
			   for (int indexVertical = 0; indexVertical < BarcodeImage.MAX_WIDTH; indexVertical++) {
				   if (image.getPixel(indexHorizontal, indexVertical)) //Pixel detected
				   {
					   if(rowStart == 0 && colStart == 0)//Check if the we are at the start 
					   {
						 //Hold the value of where the first pixel is detected
						   rowStart = indexHorizontal; 
						   colStart = indexVertical;
						   break  outter;//If detected break out of the outter loop
					   }
				   }
			   }
		   }
		   
	
		   for (int indexVeriticle = 0; indexVeriticle < BarcodeImage.MAX_HEIGHT; indexVeriticle++) {
			   for (int indexHorizontal = 0; indexHorizontal < BarcodeImage.MAX_WIDTH; indexHorizontal++) {
				   if(rowStart - indexVeriticle >= 0) //Can not be a negative 
				   {
					   if( colStart + indexHorizontal < BarcodeImage.MAX_WIDTH) // Has to be less that the width 
					   {
						   //Mapping the old to new position
						   int newRow = BarcodeImage.MAX_HEIGHT-indexVeriticle-1; //Start at the top
						   int newCol = indexHorizontal;
						   
						   int oldRow = rowStart-indexVeriticle; //Go Up
						   int oldCol = colStart+indexHorizontal; //Go Right
						   
						   image.setPixel(newRow , newCol, image.getPixel(oldRow, oldCol));
					   }
				   }
			   }
		   }
		    image.display(); //debugging
	   }
	
	//Todo: rough idea
	 int computeSignalWidth() {
	      int leftCornerRow = image.MAX_HEIGHT-1;
	      int widthCount = 0;
	      for(int indexColumn=0; indexColumn<image.MAX_WIDTH; indexColumn++) 
	      { //Keep going right
	         if(image.getPixel(leftCornerRow, indexColumn)) 
	         {
	            widthCount++;
	         }
	      }	      
		 return widthCount;
	 }

	 //Todo: rough idea
	 int computeSignalHeight() {
	      int leftCornerCol = 0;
	      int heightCount = 0;
	      for(int indexRow=0; indexRow<image.MAX_HEIGHT; indexRow++) 
	      { //Keep going up
	         if(image.getPixel(indexRow,  leftCornerCol)) 
	         {
	        	 heightCount ++;
	         }
	      }	      
		 return  heightCount;
	 }

	 private char readCharFromCol(int col) {
		 return 'a';
	 }

	 private boolean WriteCharToCol(int col, int code){
		 return true;
	 }

}