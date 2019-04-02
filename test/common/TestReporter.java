package common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * reports on results from test-fixture tests
 *
 * @author M Robertson
 */
public class TestReporter {
    private static final LinkedList<LinkedHashMap<String, String>> completedTests = new LinkedList<>();
    private static final LinkedList<LinkedHashMap<String, String>> completedDetails = new LinkedList<>();
    private static final LinkedHashMap<String, Integer> fieldLengths = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> currentTest = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> currentDetails = new LinkedHashMap<>();

    private static final String DETAILS_DELIMITER_DEFAULT = " -- ";
    private static final String DETAILS_TITLE_DEFAULT = "test details";
    private static final String SUMMARY_TITLE_DEFAULT = "test summary";

    private static int currentTestNumber = 1;
    private static String detailsTitle = DETAILS_TITLE_DEFAULT;
    private static String summaryTitle = SUMMARY_TITLE_DEFAULT;

    /**
     * adds an item to the current test summary report
     *
     * @param key   the heading to place this test info under in the test summary
     * @param value the value to put into the test summary
     */
    public static void add(String key, Object value) {
        String stringValue = value.toString();
        int maxAdded = Math.max(key.length(), stringValue.length());
        currentTest.put(key, stringValue);

        // update field lengths
        if (fieldLengths.keySet().contains(key)) {
            int maxCompleted = Math.max(fieldLengths.get(key), maxAdded);
            fieldLengths.put(key, maxCompleted);
        } else {
            fieldLengths.put(key, maxAdded);
        }
    }

    /**
     * adds a item to the current test details report
     *
     * @param key   the heading for this test detail
     * @param value the value to put into this test detail
     */
    public static void addDetail(String key, Object value) {
        String stringValue = value.toString();
        if (currentDetails.keySet().contains(key)) {
            currentDetails.put(key, currentDetails.get(key) + " -> " + stringValue);
        } else {
            currentDetails.put(key, stringValue);
        }
    }

    /**
     * indicates to the test reporter that the current test is completed
     * so it can store the summary and details contents
     * and also prepare to either print or collect contents for the next test case
     */
    public static void testComplete() {
        if (currentTest.size() > 0)
            completedTests.add(new LinkedHashMap<>(currentTest));

        if (currentDetails.size() > 0)
            completedDetails.add(new LinkedHashMap<>(currentDetails));

        currentTest.clear();
        currentDetails.clear();
        currentTestNumber++;
    }

    /**
     * manually resets test number for test suites
     */
    public static void resetTestCount() {
        currentTestNumber = 0;
    }

    /**
     * @return the current test's number
     * useful for correlating test summaries with test details
     */
    public static int getCurrentTestNumber() {
        return currentTestNumber;
    }

    /**
     * prints the test summary for all completed tests
     * then clears that contents
     */
    public static void printSummary() {
        printSummaryHeader();
        for (LinkedHashMap<String, String> completed : completedTests) {
            StringBuilder line = new StringBuilder("|");
            for (Map.Entry<String, String> result : completed.entrySet()) {
                line.append(String.format(" %-" + fieldLengths.get(result.getKey()) + "s |", result.getValue()));
            }
            System.out.println(line);
        }

        completedTests.clear();
        fieldLengths.clear();
        detailsTitle = DETAILS_TITLE_DEFAULT;
        summaryTitle = SUMMARY_TITLE_DEFAULT;
    }

    /**
     * prints the test details for all completed tests
     * then clears that contents
     *
     * @param delimiter the delimiter between the test key and value
     *                  useful for formatting output
     */
    public static void printDetails(String delimiter) {
        System.out.println("\n" + headerFrom(detailsTitle));
        for (LinkedHashMap<String, String> details : completedDetails) {
            for (Map.Entry<String, String> entry : details.entrySet()) {
                if (entry.getKey().equals("test #")) {
                    System.out.println(entry.getKey() + entry.getValue());
                } else {
                    System.out.println("  " + entry.getKey() + delimiter + entry.getValue() + "\n");
                }
            }
        }
        completedDetails.clear();
    }

    /**
     * prints the test details for all completed tests
     * then clears that contents
     */
    public static void printDetails() {
        printDetails(DETAILS_DELIMITER_DEFAULT);
    }

    /**
     * prints a header for the test summary report
     */
    private static void printSummaryHeader() {
        StringBuilder header = new StringBuilder("|");
        for (Map.Entry<String, Integer> field : fieldLengths.entrySet()) {
            header.append(String.format(" %-" + field.getValue() + "s |", field.getKey()));
        }

        System.out.println("\n" + headerFrom(summaryTitle));
        System.out.println(header);
    }

    /**
     * @return the calculated width of all fields (which corresponds to the header's ideal size)
     */
    private static int headerSize() {
        return fieldLengths.values().stream().mapToInt(Integer::intValue).sum() + (3 * fieldLengths.size());
    }

    /**
     * sets the printed title for the details report
     *
     * @param title new title to use
     */
    public static void setDetailsTitle(String title) {
        detailsTitle = title;

    }

    /**
     * sets the printed title for the summary report
     *
     * @param title new title to use
     */
    public static void setSummaryTitle(String title) {
        summaryTitle = title;
    }

    /**
     * given a title, returns a properly formatted header to be printed
     *
     * @param title the title to use
     * @return a properly formatted header
     */
    private static String headerFrom(String title) {
        String prefix = " -- ";
        String suffix = " ";
        int add = headerSize() - (prefix.length() + title.length() + suffix.length());
        if (add <= 0) {
            return title;
        }
        title = prefix + title + " ";

        StringBuilder str = new StringBuilder(title);
        char[] ch = new char[add];
        Arrays.fill(ch, '-');
        str.append(ch);
        str.append(suffix);
        return str.toString();
    }
}
