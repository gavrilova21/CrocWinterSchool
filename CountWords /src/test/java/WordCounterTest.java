import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class WordCounterTest {

    @Test
    public void testCounter(){
        String text = "Aab, aaa-AAA! bba aab Bba, aa 123";
        WordCounter counter = new WordCounter(text);
        HashMap<String, Integer> resultHashMap = new HashMap<>();
        resultHashMap.put("aaa", 2);
        resultHashMap.put("aa", 1);
        resultHashMap.put("aab", 2);
        resultHashMap.put("123", 1);
        resultHashMap.put("bba", 2);
        Assertions.assertEquals(counter.countWords(), resultHashMap);
    }
}
