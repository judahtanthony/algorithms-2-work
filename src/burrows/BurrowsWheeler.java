import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        char[] input = s.toCharArray();
        int len = input.length;

        CircularSuffixArray cs = new CircularSuffixArray(s);
        for (int i = 0; i < cs.length(); ++i) {
            if (cs.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < cs.length(); ++i) {
            int offset = cs.index(i);
            if (offset > 0) {
                BinaryStdOut.write(input[offset - 1]);
            }
            else {
                BinaryStdOut.write(input[len - 1]);
            }
        }

        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();
        char[] input = t.toCharArray();
        int len = input.length;

        char[] ordered = new char[len];
        int[] next = new int[len];

        int[] count = new int[256 + 1];
        for (int i = 0; i < len; i++) {
            count[input[i] + 1]++;
        }

        // compute cumulates
        for (int r = 0; r < 256; r++) {
            count[r + 1] += count[r];
        }

        // move data
        for (int i = 0; i < len; i++) {
            next[count[input[i]]] = i;
            ordered[count[input[i]]] = input[i];
            count[input[i]]++;
        }

        int c = first;
        for (int i = 0; i < len; i++) {
            BinaryStdOut.write(ordered[c]);
            c = next[c];
        }

        BinaryStdOut.flush();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if      (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
