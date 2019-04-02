import java.util.ArrayList;
import java.util.List;

/**
 * testdata generator for BarcodeImage
 *
 * @author M Robertson
 */
class BarcodeImageTestData {
    final List<String> lines;
    final List<String> padded;
    final BarcodeImage subject;

    BarcodeImageTestData(List<String> lines) {
        // null lines -> default constructor
        if (lines == null) {
            this.lines = new ArrayList<>();
            this.subject = new BarcodeImage();
        } else {
            this.lines = lines;
            this.subject = new BarcodeImage(lines.toArray(new String[0]));
        }
        // lines too big -> clear lines, since Barcode should reject
        if (this.lines.size() > BarcodeImage.MAX_HEIGHT) {
            this.lines.clear();
        }
        this.padded = pad(this.lines);

    }

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

    static List<String> overpopulated() {
        List<String> list = new ArrayList<>();
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
