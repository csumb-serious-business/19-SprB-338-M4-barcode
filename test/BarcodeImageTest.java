import common.ConsoleCapture;
import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Helper {
    static String repeatedString(int count, String toRepeat) {
        return new String(new char[count]).replace("\0", toRepeat);
    }

    static List<String> pad(List<String> list) {
        // pad the right
        int rightPadLen = list.size() > 0 ?
                BarcodeImage.MAX_WIDTH - list.get(0).length() :
                BarcodeImage.MAX_WIDTH;

        if (rightPadLen > 0) {
            String rightPad = repeatedString(rightPadLen, " ");
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

        for (int i = 0; i < BarcodeImage.MAX_HEIGHT; i++) {
            String str = repeatedString(i, "*") + repeatedString((BarcodeImage.MAX_WIDTH - 1) - i, " ");
            if (isEven) {
                list.add(str + "*");
                isEven = false;
            } else {
                list.add(str + " ");
                isEven = true;
            }
        }
        list.add(0, "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        list.add("*****************************************************************");
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

    static void getPixelTest(List<String> lines, BarcodeImage subject) {
        // too low x
        boolean actual = subject.getPixel(-1, 0);

        // min x
        actual = subject.getPixel(0, 0);


        // max x
        actual = subject.getPixel(BarcodeImage.MAX_WIDTH, 0);

        // too high x
        actual = subject.getPixel(BarcodeImage.MAX_WIDTH + 1, 0);

        // too low y
        actual = subject.getPixel(0, -1);

        // min y
        actual = subject.getPixel(0, 0);

        // max y
        actual = subject.getPixel(0, BarcodeImage.MAX_HEIGHT);

        // too high y
        actual = subject.getPixel(0, BarcodeImage.MAX_HEIGHT + 1);


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
            //overpopulated will be rejected, so we expect an empty in actual
            List<String> expect = new ArrayList<>();
            displayTest(expect, subject);
        }

    }

}
