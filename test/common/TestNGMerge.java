package common;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * todo: add desc
 *
 * @author todo
 */
public class TestNGMerge {
    public static Object[][] merge(Object[][] a1, Object[][] a2) {
        List<Object[]> objectCodesList = new LinkedList<Object[]>();
        for (Object[] o : a1) {
            for (Object[] o2 : a2) {
                objectCodesList.add(mergeAll(o, o2));
            }
        }
        return objectCodesList.toArray(new Object[0][0]);
    }

    @SafeVarargs
    public static <T> T[] mergeAll(T[] first, T[]... rest) {
        //calculate the total length of the final object array after the concat
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        //copy the first array to result array and then copy each array completely to result
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }
}
