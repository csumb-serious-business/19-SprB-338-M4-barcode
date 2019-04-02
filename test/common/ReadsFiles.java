package common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * a helper class for reading data from files
 *
 * @author M Robertson
 */
public class ReadsFiles {

    /**
     * given a relative-path to a file located within this project
     * returns a list of lines from that file
     *
     * @param fileName path to a file
     * @return a List of strings from fileName's contents
     * @throws IOException catch-all for IO issues
     */
    public static List<String> listFrom(String fileName) throws IOException {
        Path filePath = new File(fileName).toPath();
        return Files.readAllLines(filePath, Charset.defaultCharset());
    }

    /**
     * given a relative-path to a file located within this project
     * provides a stream of lines from that file
     *
     * @param fileName path to a file
     * @return a stream of strings from that fileName's contents
     * @throws IOException catch-all for IO issues
     */
    public static Stream<String> streamFrom(String fileName) throws IOException {
        Path filePath = new File(fileName).toPath();
        return Files.lines(filePath, Charset.defaultCharset());
    }

    /**
     * a convenience method to print the contents of a file
     * without needing to handle IOExceptions
     *
     * @param fileName the file with contents to be printed
     */
    public static void printContentsOf(String fileName) {
        try {
            listFrom(fileName).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
