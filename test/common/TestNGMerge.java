package common;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * merges parameter lists from TestNG data providers
 *
 * @author M Robertson
 * adapted from: stackoverflow.com/questions/25549443
 */
public class TestNGMerge {
    public static Object[][] merge(Object[][] a1, Object[][] a2) {
        List<Object[]> parameters = new LinkedList<>();
        for (Object[] o : a1) {
            for (Object[] o2 : a2) {
                parameters.add(mergeAll(o, o2));
            }
        }
        return parameters.toArray(new Object[0][0]);
    }

    @SafeVarargs
    public static <T> T[] mergeAll(T[] head, T[]... tail) {
        // calculate length merged array
        int totalLength = head.length;
        for (T[] array : tail) {
            totalLength += array.length;
        }
        // copy head array into merged and subsequent arrays from tail
        T[] merged = Arrays.copyOf(head, totalLength);
        int offset = head.length;
        for (T[] array : tail) {
            System.arraycopy(array, 0, merged, offset, array.length);
            offset += array.length;
        }

        return merged;
    }
}
