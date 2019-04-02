package common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Captures output from System.out and/or System.err so it can be evaluated in tests
 * public static final fields out and err mirror that of System.out and System.err
 *
 * @author M Robertson
 */
public class ConsoleCapture {
    public static final Capturable err = new Impl(StreamType.ERROR);

    public static final Capturable out = new Impl(StreamType.OUT);

    /**
     * an enumeration of capturable PrintStreams
     */
    private enum StreamType {
        ERROR, OUT
    }

    /**
     * models a capturable PrintStream
     */
    public interface Capturable {
        /**
         * redirects output from System.xxx into a new PrintStream
         * that output is collected for output via xxx.retrieve()
         */
        void start();

        /**
         * closes the capture PrintStream and resets it to System.xxx, provides the captured stream contents
         *
         * @return collected PrintStream output as a single String
         */
        String retrieve();

        /**
         * closes the capture PrintStream and resets it to System.xxx, provides the captured stream contents
         *
         * @return collected PrintStream output as a List of Strings
         */
        List<String> retrieveAsList();

        String retrieve(Unit unit);

        List<String> retrieveAsList(Unit unit);

    }

    /**
     * implementation for a Capturable PrintStream for either System.out or System.err
     */
    private static class Impl implements Capturable {
        private final PrintStream original;
        private final StreamType type;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        /**
         * create a new implemented Capturable for a given type
         *
         * @param type type of PrintStream to capture from: OUT, ERROR
         */
        Impl(StreamType type) {
            this.type = type;
            if (type == StreamType.ERROR)
                this.original = System.err;
            else
                this.original = System.out;
        }

        @Override
        public List<String> retrieveAsList() {
            return Arrays.asList(retrieve().split("\n"));
        }

        @Override
        public void start() {
            try {
                PrintStream stream = new PrintStream(baos, false, "UTF-8");
                if (type == StreamType.ERROR)
                    System.setErr(stream);
                else
                    System.setOut(stream);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String retrieve() {
            String result = "";
            try {
                result = baos.toString("UTF-8");
                if (type == StreamType.ERROR) {
                    System.err.flush();
                    System.setErr(original);
                } else {
                    System.out.flush();
                    System.setOut(original);
                }
                baos = new ByteArrayOutputStream();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public String retrieve(Unit unit) {
            start();
            unit.apply();
            return retrieve();
        }

        @Override
        public List<String> retrieveAsList(Unit unit) {
            start();
            unit.apply();
            return retrieveAsList();
        }
    }
}
