import common.ConsoleCapture;
import common.TestNGMerge;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * tests of BarcodeImage
 *
 * @author M Robertson
 */
public class BarcodeImageTest {
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
            BarcodeImageTestData.overpopulated();

    @DataProvider(name = "images")
    public Object[][] images() {
        return new Object[][]{
                {"empty", new BarcodeImageTestData(null)},
                {"populated", new BarcodeImageTestData(IMAGE_NORMAL)},
                {"too wide", new BarcodeImageTestData(IMAGE_OVERSIZED_WIDTH)},
                {"too tall", new BarcodeImageTestData(IMAGE_OVERSIZED_HEIGHT)}
                // todo: add 30x65 size & rename populated -> some filled
                // todo: a 1x1, 2x2 & 3x3 sizes
        };
    }

    @DataProvider(name = "coordinates")
    public Object[][] coordinates() {
        return new Object[][]{
                {"too low X", -1, 0},
                {"min X", 0, 0},
                {"max X", BarcodeImage.MAX_WIDTH, 0},
                {"too high X", BarcodeImage.MAX_WIDTH + 1, 0},
                {"too low Y", 0, -1},
                {"min Y", 0, 0},
                {"max Y", 0, BarcodeImage.MAX_HEIGHT},
                {"too high Y", 0, BarcodeImage.MAX_HEIGHT + 1}
        };
    }

    @DataProvider(name = "set-cases")
    public Object[][] setcases() {
        return new Object[][]{
                {true},
                {false}
        };
    }

    @DataProvider(name = "images+coordinates")
    public Object[][] images_coordinates() {
        return TestNGMerge.merge(images(), coordinates());
    }

    @DataProvider(name = "images+coordinates+setcases")
    public Object[][] images_coordinates_setcases() {
        return TestNGMerge.merge(images_coordinates(), setcases());
    }

    @Test(dataProvider = "images")
    public void cloneTest(String name, BarcodeImageTestData data) throws CloneNotSupportedException {
        String expect = ConsoleCapture.out.retrieve(data.subject::displayToConsole);
        BarcodeImage clone = data.subject.clone();
        String actual = ConsoleCapture.out.retrieve(clone::displayToConsole);
        assertEquals(actual, expect);
    }

    @Test(dataProvider = "images")
    public void displayTest(String name, BarcodeImageTestData data) {
        String expect = String.join("\n", data.padded) + "\n";
        String actual = ConsoleCapture.out.retrieve(data.subject::displayToConsole);
        assertEquals(actual, expect);

    }

    @Test(dataProvider = "images+coordinates")
    public void getPixelTest(String iName, BarcodeImageTestData data,
                             String cName, int x, int y) {
        boolean actual = data.subject.getPixel(x, y);
        char got;
        try {
            // subject has
            got = data.lines.get(x + 1).charAt(y + 1);
        } catch (IndexOutOfBoundsException ex) {
            got = 'x';
        }
        boolean expect = got == '*';
        assertEquals(actual, expect);
    }

    @Test(dataProvider = "images+coordinates+setcases")
    public void setPixelTests(String iName, BarcodeImageTestData data,
                              String cName, int x, int y,
                              boolean setcase) {
        BarcodeImage subject = data.subject;
        // set succeeded?
        boolean valid = subject.isValidCoordinate(x, y);
        boolean previous = valid && subject.getPixel(x, y);
        boolean writeStatus = subject.setPixel(x, y, setcase);
        assertEquals(valid, writeStatus);

        // write value correct?
        boolean get = subject.getPixel(x, y);
        boolean expect = valid ? setcase : previous;
        assertEquals(expect, get);
    }
}