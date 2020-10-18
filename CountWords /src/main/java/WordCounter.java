import java.util.HashMap;

public class WordCounter {

    private String text;

    public WordCounter(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HashMap<String, Integer> countWords(){
        String[] words = text.replaceAll("\\p{Punct}", " ").toLowerCase().split("\\s+");
        HashMap<String, Integer> frequencyCounter = new HashMap<String, Integer>();

        for (String word : words) {
            int freq = frequencyCounter.getOrDefault(word, 0);
            frequencyCounter.put(word, ++freq);
        }
        return frequencyCounter;
    }

}
