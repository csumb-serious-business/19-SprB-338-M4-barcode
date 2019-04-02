import common.ConsoleCapture;
import common.Coordinate;

import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * tests for BarcodeImage
 *
 * @author M Robertson
 */
public class BarcodeImageTestJUnit {
    private static final List<String> IMAGE_NORMAL = new ArrayList<String>() {{
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

    private static final List<String> IMAGE_OVERSIZED_WIDTH =
            new ArrayList<String>() {{
                add("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");
                add("*                                 *                               ");
                add("******************************************************************");
            }};

    private static final List<String> IMAGE_OVERSIZED_HEIGHT =
            BarcodeTestData.overpopulated();

    /// test datasets /////////////////////////////////////////////////////////

    private static final Map<String, Coordinate> COORDINATE_CASES =
            new LinkedHashMap<String, Coordinate>() {{
                put("too low X", new Coordinate(-1, 0));
                put("min X", new Coordinate(0, 0));
                put("max X", new Coordinate(BarcodeImage.MAX_WIDTH, 0));
                put("too high X", new Coordinate(BarcodeImage.MAX_WIDTH + 1, 0));

                put("too low Y", new Coordinate(0, -1));
                put("min Y", new Coordinate(0, 0));
                put("max Y", new Coordinate(0, BarcodeImage.MAX_HEIGHT));
                put("too high Y", new Coordinate(0, BarcodeImage.MAX_HEIGHT + 1));
            }};

    private static final Map<String, BarcodeTestData> IMAGE_CASES =
            new LinkedHashMap<String, BarcodeTestData>() {{
                put("empty", new BarcodeTestData(null));
                put("populated", new BarcodeTestData(IMAGE_NORMAL));
                put("too wide", new BarcodeTestData(IMAGE_OVERSIZED_WIDTH));
                put("too tall", new BarcodeTestData(IMAGE_OVERSIZED_HEIGHT));

            }};

    private static final Map<String, Boolean> SET_CASES =
            new LinkedHashMap<String, Boolean>() {{
                put("true", true);
                put("false", false);
            }};


    /// test plans ////////////////////////////////////////////////////////////
    static void displayTest(List<String> paddedLines, BarcodeImage subject) {
        String expect = String.join("\n", paddedLines) + "\n";
        assertEquals(expect, ConsoleCapture.out.retrieve((subject::display)));
    }

    static void getPixelTest(List<String> lines, BarcodeImage subject) {
        COORDINATE_CASES.forEach((k, v) -> {
            int r = v.getX();
            int c = v.getY();
            boolean actual = subject.getPixel(r, c);

            char got;
            try {
                // subject has
                got = lines.get(r + 1).charAt(c + 1);
            } catch (IndexOutOfBoundsException ex) {
                got = 'x';
            }

            boolean expect = got == '*';
            assertEquals(expect, actual, "got: " + got + "coord: " + v);

        });
    }


    static void setPixelTest(BarcodeImage subject) {

        COORDINATE_CASES.forEach((ck, cv) -> {
                    // coord exists?
                    int r = cv.getX();
                    int c = cv.getY();
                    boolean valid = subject.isValidCoordinate(r, c);

                    SET_CASES.forEach((sk, sv) -> {
                        // set succeeded?
                        boolean toSet = sv;
                        boolean previous = valid && subject.getPixel(r, c); // false if invalid
                        boolean writeStatus = subject.setPixel(r, c, true);
                        assertEquals(valid, writeStatus,
                                "valid: " + valid + ", toSet: " + toSet + ", previous: "
                                        + previous + ", writeStatus: " + writeStatus);

                        // write value correct?
                        boolean get = subject.getPixel(r, c);
                        boolean expect = valid ? toSet : previous;
                        assertEquals(expect, get, "valid: " + valid + ", toSet: " + toSet + ", previous: "
                                + previous + ", writeStatus: " + writeStatus + ", expect: " + expect + ", get: " + get);
                    });
                }
        );
    }


    /// execution /////////////////////////////////////////////////////////
    @Nested
    @DisplayName("barcode image statics")
    class Statics {
        @Test
        void maxHeightWidthTest() {
            assertEquals(30, BarcodeImage.MAX_HEIGHT);
            assertEquals(65, BarcodeImage.MAX_WIDTH);
        }

    }

    @Nested
    @DisplayName("barcode image tests")
    class Tests {
        @Test
        void displayTests() {
            IMAGE_CASES.forEach((k, v) -> displayTest(v.padded, v.subject));

        }

        @Test
        void getPixelTests() {
            IMAGE_CASES.forEach((k, v) -> getPixelTest(v.lines, v.subject));

        }

        @Ignore("needs fix")
        @Test
        void setPixelTests() {
            IMAGE_CASES.forEach((k, v) -> setPixelTest(v.subject));
        }
    }


}
