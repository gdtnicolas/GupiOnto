/**
 * Created by quentin on 20/01/16.
 */
public class WordWeight {

    private String word;
    private float weight;


    public WordWeight(String word, float weight) {
        this.word = word;
        this.weight = weight;
    }

    public String getWord() {
        return word;
    }

    public float getWeight() {
        return weight;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "WordWeight{" +
                "word='" + word + '\'' +
                ", weight=" + weight +
                '}';
    }
}
