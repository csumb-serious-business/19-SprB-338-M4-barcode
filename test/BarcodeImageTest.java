import common.ConsoleCapture;
import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Helper {
    static List<String> pad(List<String> list) {
        // pad the right
        int rightPadLen = list.size() > 0 ?
                BarcodeImage.MAX_WIDTH - list.get(0).length() :
                BarcodeImage.MAX_WIDTH;

        if (rightPadLen > 0) {
            String rightPad = new String(new char[rightPadLen]).replace("\0", " ");
            list.replaceAll((line) -> line += rightPad);
        }
        if (rightPadLen < 0) {
            list.replaceAll((line) -> line.substring(0, BarcodeImage.MAX_WIDTH));
        }


        // pad the top
        while (list.size() < BarcodeImage.MAX_HEIGHT) {
            list.add(0,
                    "                                                                 ");
        }


        return list;
    }

    static List<String> overpopulate(List<String> list) {
        boolean isEven = false;
        list.add("* *");
        while (list.size() < BarcodeImage.MAX_HEIGHT) {
            if (isEven) {
                list.add("* *");
                isEven = false;
            } else {
                list.add("*  ");
                isEven = true;
            }
        }
        list.add("***");
        return list;
    }

}

/**
 * tests for BarcodeImage
 *
 * @author M Robertson
 */
public class BarcodeImageTest {
    static void displayTest(List<String> lines, BarcodeImage subject) {
        String expect = String.join("\n", Helper.pad(lines)) + "\n";
        assertEquals(expect, ConsoleCapture.out.retrieve((subject::display)));
    }


    @Nested
    @DisplayName("statics")
    class Statics {
        @Test
        void maxHeightWidthTest() {
            assertEquals(30, BarcodeImage.MAX_HEIGHT);
            assertEquals(65, BarcodeImage.MAX_WIDTH);
        }

    }

    @Ignore("populated first to check output dimensions")
    @Nested
    @DisplayName("empty")
    class Empty {
        List<String> lines = new ArrayList<>();
        BarcodeImage subject = new BarcodeImage();


        @Test
        void imageDataTest() {
            displayTest(lines, subject);
        }
    }

    @Nested
    @DisplayName("populated")
    class Populated {

        List<String> lines = new ArrayList<String>() {{
            add("* * * * * * * * * * * * * * * * * *");
            add("*                                 *");
            add("***** ** * **** ****** ** **** **  ");
            add("* **************      *************");
            add("**  *  *        *  *   *        *  ");
            add("* **  *     **    * *   * ****   **");
            add("**         ****   * ** ** ***   ** ");
            add("*   *  *   ***  *       *  ***   **");
            add("*  ** ** * ***  ***  *  *  *** *   ");
            add("***********************************");
        }};


        BarcodeImage subject = new BarcodeImage(lines.toArray(new String[0]));

        @Test
        void imageDataTest() {
            displayTest(lines, subject);
        }
    }

    @Nested
    @DisplayName("oversized width")
    class OversizedWidth {
        List<String> lines = new ArrayList<String>() {{
            add("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");
            add("*                                 *                               ");
            add("******************************************************************");
        }};

        BarcodeImage subject = new BarcodeImage(lines.toArray(new String[0]));

        @Test
        void oversizedWidthTest() {
            displayTest(lines, subject);
        }
    }

    @Nested
    @DisplayName("oversized height")
    class OversizedHeight {
        List<String> lines = new ArrayList<>();
        List<String> overpopulated = Helper.overpopulate(lines);
        BarcodeImage subject = new BarcodeImage(overpopulated.toArray(new String[0]));

        @Test
        void oversizedHeightTest() {
            displayTest(overpopulated, subject);
        }

    }

}
