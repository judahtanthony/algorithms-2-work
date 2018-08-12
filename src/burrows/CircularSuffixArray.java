import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class CircularSuffixArray {
    private int len;
    final private int[] offsets;
    private static final int CUTOFF =  15;   // cutoff to insertion sort

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("s must be not null");
        }
        offsets = new int[s.length()];
        len = 0;
        while (len < s.length()) {
            offsets[len] = len;
            ++len;
        }
        // A customize 3-way Quicksort.
        sort(s, offsets, 0);
    }
    // length of s
    public int length() {
        return len;
    }
    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= len) {
            throw new IllegalArgumentException("Out of range");
        }
        return offsets[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray(args[0]);
        StdOut.println(args[0]);
        for (int i = 0; i < csa.length(); ++i) {
            StdOut.println(csa.index(i));
        }
    }

    private static void sort(String source, int[] offsets, int d) {
        StdRandom.shuffle(offsets);
        sort(source, offsets, 0, offsets.length-1, 0);
    }

    private static void sort(String source, int[] offsets, int lo, int hi, int d) {
        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(source, offsets, lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        int v = charAt(source, offsets[lo], d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(source, offsets[i], d);
            if      (t < v) exch(offsets, lt++, i++);
            else if (t > v) exch(offsets, i, gt--);
            else              i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        sort(source, offsets, lo, lt-1, d);
        if (v >= 0) sort(source, offsets, lt, gt, d+1);
        sort(source, offsets, gt+1, hi, d);
    }

    private static int charAt(String source, int offset, int d) {
        assert d >= 0 && d <= source.length();
        if (d == source.length()) return -1;
        return source.charAt((offset + d) % source.length());
    }

    private static void exch(int[] offsets, int i, int j) {
        int temp = offsets[i];
        offsets[i] = offsets[j];
        offsets[j] = temp;
    }

    private static void insertion(String source, int[] offsets, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(source, offsets[j], offsets[j-1], d); j--)
                exch(offsets, j, j-1);
    }

    private static boolean less(String source, int v, int w, int d) {
        for (int i = d; i < source.length(); i++) {
            if (charAt(source, v, i) < charAt(source, w, i)) return true;
            if (charAt(source, v, i) > charAt(source, w, i)) return false;
        }
        return false;
    }
}
