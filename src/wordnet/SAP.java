import java.util.List;
import java.util.ArrayList;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

public class SAP {
    private final Digraph graph;
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        graph = new Digraph(G);
    }
        
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        
        if (v == w) {
            return 0;
        }
        
        List<Integer> vlist = new ArrayList<Integer>();
        vlist.add(v);
        List<Integer> wlist = new ArrayList<Integer>();
        wlist.add(w);
        int[] min = doubleBFS(vlist, wlist);
        return min[0];
    }
        
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        
        if (v == w) {
            return v;
        }
        
        List<Integer> vlist = new ArrayList<Integer>();
        vlist.add(v);
        List<Integer> wlist = new ArrayList<Integer>();
        wlist.add(w);
        int[] min = doubleBFS(vlist, wlist);
        return min[1];
    }
        
    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int[] min = doubleBFS(v, w);
        return min[0];
    }
    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] min = doubleBFS(v, w);
        return min[1];
    }
    
    private int[] doubleBFS(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        
        Queue<Integer> vnext = new Queue<Integer>();
        boolean[] vMarked = new boolean[graph.V()];
        int[] vDistTo = new int[graph.V()];
        Queue<Integer> wnext = new Queue<Integer>(); 
        boolean[] wMarked = new boolean[graph.V()];
        int[] wDistTo = new int[graph.V()];
        
        for (int s : v) {
            validateVertex(s);
            vnext.enqueue(s);
            vMarked[s] = true;
            vDistTo[s] = 0;
        }
        for (int s : w) {
            validateVertex(s);
            wnext.enqueue(s);
            wMarked[s] = true;
            wDistTo[s] = 0;
            if (vMarked[s]) {
                return new int[] { 0, s };
            }
        }
        
        int[] min = { Integer.MAX_VALUE, -1 };
        while (!vnext.isEmpty() || !wnext.isEmpty()) {
            if (!vnext.isEmpty()) {
                int currv = vnext.dequeue();
                for (int nextv : graph.adj(currv)) {
                    int travelled = vDistTo[currv] + 1;
                    if (wMarked[nextv]) {
                        int dist = travelled + wDistTo[nextv];
                        if (dist < min[0]) {
                            min[0] = dist;
                            min[1] = nextv;
                        }
                    }
                    if (!vMarked[nextv]) {
                        vDistTo[nextv] = travelled;
                        vMarked[nextv] = true;
                        vnext.enqueue(nextv);
                    }
                }
            }
            if (!wnext.isEmpty()) {
                int currw = wnext.dequeue();
                for (int nextw : graph.adj(currw)) {
                    int travelled = wDistTo[currw] + 1;
                    if (vMarked[nextw]) {
                        int dist = travelled + vDistTo[nextw];
                        if (dist < min[0]) {
                            min[0] = dist;
                            min[1] = nextw;
                        }
                    }
                    if (!wMarked[nextw]) {
                        wDistTo[nextw] = travelled;
                        wMarked[nextw] = true;
                        wnext.enqueue(nextw);
                    }
                }
            }
        }
        
        return min[0] < Integer.MAX_VALUE ? min : new int[] { -1, -1 };
    }
    
    private void validateVertex(int v) {
        if (v < 0 || v >= graph.V()) {
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (graph.V()-1));
        }
    }
    
    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
