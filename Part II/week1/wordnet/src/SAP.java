import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;


public class SAP {

    private final Digraph G;
    private int anc = -1;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new java.lang.IllegalArgumentException("ERROR: Graph G is null");
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        isValidVertiX(v);
        isValidVertiX(w);

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        return lengthHelper(bfsV, bfsW);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        isValidVertiX(v);
        isValidVertiX(w);

        return length(v, w) == -1 ? -1 : this.anc;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (null == v || null == w) throw new IllegalArgumentException("ERROR: null pointer");
        for (int vs : v) isValidVertiX(vs);
        for (int ws : w) isValidVertiX(ws);

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        return lengthHelper(bfsV, bfsW);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (null == v || null == w) throw new IllegalArgumentException("ERROR: null pointer");
        for (int vs : v) isValidVertiX(vs);
        for (int ws : w) isValidVertiX(ws);
        length(v, w);
        return this.anc;
    }

    private int lengthHelper(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW) {
        int minLength = Integer.MAX_VALUE, len = 0;
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                len = bfsV.distTo(i) + bfsW.distTo(i);
                if (len < minLength) {
                    minLength = len;
                    anc = i;
                }
            }
        }
        return minLength == Integer.MAX_VALUE ? -1 : minLength;
    }

    private void isValidVertiX(int v) {
        if (v < 0 || v > G.V())
            throw new java.lang.IllegalArgumentException("ERROR: v out of range");
    }
}