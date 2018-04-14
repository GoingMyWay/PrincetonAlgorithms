import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.ArrayList;
import java.util.HashMap;


public class WordNet {

    private final Digraph G;
    private final ArrayList<String> nounList;
    private final HashMap<String, ArrayList<Integer>> nounMaps;
    private final SAP sap;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new java.lang.IllegalArgumentException();

        In synsetsIn = new In(synsets);
        In hypernymsIn = new In(hypernyms);

        nounList = new ArrayList<>();
        nounMaps = new HashMap<>();
        int vertixNum = 0;
        for (String line: synsetsIn.readAllLines()) {
            String[] split = line.split(",");
            int id = Integer.parseInt(split[0]);
            String nouns = split[1];
            for (String noun: nouns.split(" ")) {
                ArrayList<Integer> arr;
                if (nounMaps.containsKey(noun)) arr = nounMaps.get(noun);
                else arr = new ArrayList<>();
                arr.add(id);
                nounMaps.put(noun, arr);
            }
            nounList.add(nouns);
            vertixNum++;
        }

        // build graph
        G = new Digraph(vertixNum);
        for (String line: hypernymsIn.readAllLines()) {
            String[] split = line.split(",");
            int v = Integer.parseInt(split[0]);
            for (int i = 1; i < split.length; i++) {
                int w = Integer.parseInt(split[i]);
                G.addEdge(v, w);
            }
        }

        // detect whether it is a directed cycle graph
        DirectedCycle directedCycle = new DirectedCycle(G);
        if (directedCycle.hasCycle()) {
            throw new java.lang.IllegalArgumentException("Graph G is a directed cycle graph");
        }

        // detect whether it has more than two roots
        int counter = 0;
        for (int i = 0; i < G.V(); i++) {
            if (0 == G.outdegree(i)) counter++;
        }

        if (counter != 1) {
            throw new java.lang.IllegalArgumentException("Graph G has more than one root vertix");
        }
        // initialize SAP
        sap = new SAP(G);

    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounMaps.keySet();
    }

    // TODO
    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new java.lang.IllegalArgumentException();
        return nounMaps.containsKey(word);
    }

    // TODO
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new java.lang.IllegalArgumentException();

        return sap.length(nounMaps.get(nounA), nounMaps.get(nounB));
    }

    // TODO
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new java.lang.IllegalArgumentException();

        return nounList.get(sap.ancestor(nounMaps.get(nounA), nounMaps.get(nounB)));
    }
}
