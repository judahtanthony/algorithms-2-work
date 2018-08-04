import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wn;
    
    public Outcast(WordNet wordnet) {        // constructor takes a WordNet object
        wn = wordnet;
    }
    public String outcast(String[] nouns) {   // given an array of WordNet nouns, return an outcast
        int maxDist = -1;
        String word = "";
        
        for (String w : nouns) {
            int dist = 0;
            for (String w2 : nouns) {
                if (!w.equals(w2)) {
                   dist += wn.distance(w, w2);
                }
            }
            if (dist > maxDist) {
                maxDist = dist;
                word = w;
            }
        }
        
        return word;
    }
    public static void main(String[] args) { // see test client below
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}