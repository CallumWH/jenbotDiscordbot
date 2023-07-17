import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class MainTest {

    @Test
    public void runProgram() {
        Collection<Integer> numbers = Arrays.asList(1, 2, 1, 3);
        for (int number : findUniqueNumbers(numbers))
            System.out.println(number);
    }

    public static Collection<Integer> findUniqueNumbers(Collection<Integer> numbers) {

        Map<Integer, Long> counts =
                numbers.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        Collection<Integer> numbersSingleOccurance = new ArrayList<>();

        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {

            if (entry.getValue() == 1) {
                numbersSingleOccurance.add(entry.getKey());
            }
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        return numbersSingleOccurance;
//        Set<Integer> uniqueValues = new HashSet(numbers);
//        Collection<Integer> numbersSingleOccurance = new ArrayList<>();
//        for(Integer number : uniqueValues) {
//            if(Collections.frequency(numbers, number) == 1) {
//                numbersSingleOccurance.add(number);
//            }
//        }
//        return numbersSingleOccurance;
    }
}
