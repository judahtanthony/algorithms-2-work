import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Topological;

public class WordNet {
    private final Map<String, List<Integer>> nouns;
    private final List<String[]> verticies;
    private final SAP sap;
    
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        // symbol table
        nouns = new HashMap<String, List<Integer>>();
        verticies = new ArrayList<String[]>();

        // read in the data from csv file
        int maxID = -1;
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] tokens = line.split(",");
            int id = Integer.parseInt(tokens[0]);
            if (id > maxID) {
                maxID = id;
            }
            String[] words = tokens[1].split(" ");
            verticies.add(id, words);
            for (String w : words) {
                if (!nouns.containsKey(w)) {
                    nouns.put(w, new ArrayList<Integer>());
                }
                nouns.get(w).add(id);
            }
        }
        
        Digraph graph = new Digraph(maxID + 1);
        in = new In(hypernyms);
        int numNonRoots = 0;
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] tokens = line.split(",");
            int from = Integer.parseInt(tokens[0]);
            if (tokens.length > 1) {
                ++numNonRoots;
            }
            for (int i = 1; i < tokens.length; ++i) {
                graph.addEdge(from, Integer.parseInt(tokens[i]));
            }
        }
        
        
        if (verticies.size() > (numNonRoots + 1)) {
            throw new IllegalArgumentException("hypernyms must form a DAG");
        }
        
        Topological topo = new Topological(graph);
        if (!topo.hasOrder()) {
            throw new IllegalArgumentException("hypernyms must form a DAG");
        }
        
        sap = new SAP(graph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return nouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        validateNoun(nounA);
        validateNoun(nounB);
        if (nounA.equals(nounB)) {
            return 0;
        }
        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        validateNoun(nounA);
        validateNoun(nounB);
        if (nounA.equals(nounB)) {
            return String.join(" ", verticies.get(nouns.get(nounA).get(0)));
        }
        int anc = sap.ancestor(nouns.get(nounA), nouns.get(nounB));
        return String.join(" ", verticies.get(anc));
    }

    private void validateNoun(String noun) {
        if (!isNoun(noun)) {
            throw new IllegalArgumentException(noun + " is not a noun");
        }
    }
    
    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        for (String w : wn.nouns()) {
            StdOut.println(w);
        }
    }
}
