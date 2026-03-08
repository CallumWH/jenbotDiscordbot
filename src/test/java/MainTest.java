import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class MainTest {

    @Test
    public void myMethod() {
        int[] array = {1, 2, 3, 3, 4, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 6, 8};
        Set<Integer> removedDuplicates =
                Arrays.stream(array).boxed().collect(Collectors.toCollection(HashSet::new));
        Collections.sort(new ArrayList<>(removedDuplicates));
        System.out.println(
                findLowestMissingInt(
                        removedDuplicates.stream().mapToInt(Integer::intValue).toArray()));
    }

    private int findLowestMissingInt(int[] array) {
        int indexStart = getFirstPositiveIndex(array);
        if (indexStart < 0) {
            return 1;
        }
        int[] positiveIntegers = Arrays.copyOfRange(array, indexStart, array.length);
        return findInArrayMissingInt(positiveIntegers);
    }

    private int findInArrayMissingInt(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != i + 1) {
                return i + 1;
            }
        }
        return array.length + 1;
    }

    private int getFirstPositiveIndex(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 0) {
                return i;
            }
        }
        return -1;
    }
}
