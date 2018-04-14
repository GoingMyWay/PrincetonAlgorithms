import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.GrahamScan;

import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;
import java.lang.IllegalArgumentException;


public class SAP {

    private final Digraph G;
    private int anc = -1;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("ERROR: Graph G is null");
        this.G = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        isValidVertiX(v);
        isValidVertiX(w);

        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int minLength = Integer.MAX_VALUE, len = 0;
        for (int i = 0; i < G.V(); i ++) {
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

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        isValidVertiX(v);
        isValidVertiX(w);

        return length(v, w) == -1? -1: this.anc;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        for (Integer vs : v) isValidVertiX(vs);
        for (Integer ws : w) isValidVertiX(ws);

        HashSet<Integer> lenSet = new HashSet<Integer>();
        for (int vId: v) {
            for (int wId: w) {
                lenSet.add(this.length(vId, wId));
            }
        }
        return Collections.max(lenSet);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        HashMap<Integer, Integer> LenghAncMap = new HashMap<>();
        for (int vid: v) {
            for (int wid: w) {
                int curLen = length(vid, wid);
                LenghAncMap.put(curLen, this.anc);
            }
        }
        return Collections.max(LenghAncMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private void isValidVertiX(int v) {
        if (v < 0 || v > G.V())
            throw new IllegalArgumentException("ERROR: v out of range");
    }
}