import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.IllegalArgumentException;


public class WordNet {

    private final Digraph G;
    private final ArrayList<String> NounList;
    private final HashMap<String, ArrayList<Integer>> NounMaps;
    private final SAP sap;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new  IllegalArgumentException();

        In synsetsIn = new In(synsets);
        In hypernymsIn = new In(hypernyms);

        NounList = new ArrayList<>();
        NounMaps = new HashMap<>();
        int vertixNum = 0;
        for (String line: synsetsIn.readAllLines()) {
            String[] split = line.split(",");
            int id = Integer.parseInt(split[0]);
            String nouns = split[1];
            for (String noun: nouns.split(" ")) {
                ArrayList<Integer> arr;
                if (NounMaps.containsKey(noun)) arr = NounMaps.get(noun);
                else arr = new ArrayList<>();
                arr.add(id);
                NounMaps.put(noun, arr);
            }
            NounList.add(nouns);
            vertixNum ++;
        }

        // build graph
        G = new Digraph(vertixNum);
        for (String line: hypernymsIn.readAllLines()) {
            String[] split = line.split(",");
            int v = Integer.parseInt(split[0]);
            for (int i = 1; i < split.length; i ++) {
                int w = Integer.parseInt(split[i]);
                G.addEdge(v, w);
            }
        }

        // detect whether it is a directed cycle graph
        DirectedCycle directedCycle = new DirectedCycle(G);
        if (directedCycle.hasCycle()) {
            throw new IllegalArgumentException("Graph G is a directed cycle graph");
        }

        // detect whether it has more than two roots
        int counter = 0;
        for (int i = 0; i < G.V(); i ++) {
            if (0 == G.outdegree(i)) counter ++;
        }

        if (counter != 1) {
            throw new IllegalArgumentException("Graph G has more than one root vertix");
        }
        // initialize SAP
        sap = new SAP(G);

    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return NounMaps.keySet();
    }

    // TODO
    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return NounMaps.containsKey(word);
    }

    // TODO
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();

        return sap.length(NounMaps.get(nounA), NounMaps.get(nounB));
    }

    // TODO
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) throw new IllegalArgumentException();

        return NounList.get(sap.ancestor(NounMaps.get(nounA), NounMaps.get(nounB)));
    }
}
