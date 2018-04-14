public class Outcast {

    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        wordNet = wordnet;
    }
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[][] lenArr = new int[nouns.length][nouns.length];
        boolean[][] marked = new boolean[nouns.length][nouns.length];
        for (int i = 0; i < nouns.length; i ++) {
            for (int j = 0; j < nouns.length; j ++) {
                if (i != j) {
                    if (marked[j][i]) lenArr[i][j] = lenArr[j][i];
                    else lenArr[i][j] = wordNet.distance(nouns[i], nouns[j]);
                    marked[i][j] = true;
                } else lenArr[i][j] = 0;
            }
        }

        int maxValue = Integer.MIN_VALUE, maxIndex = -1;
        for (int i = 0; i < nouns.length; i ++) {
            int sumValue = 0;
            for (int j = 0; j < nouns.length; j ++) sumValue += lenArr[i][j];
            if (sumValue > maxValue) {
                maxValue = sumValue;
                maxIndex = i;
            }
        }
        return nouns[maxIndex];
    }
}
