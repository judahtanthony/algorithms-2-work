import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] alph = new char[256];
        for (int i = 0; i < 256; ++i) {
            alph[i] = (char) i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            // Scan for character.
            for (int i = 0; i < 256; ++i) {
                if (alph[i] == c) {
                    BinaryStdOut.write(i, 8);
                    // Move to front
                    if (i > 0) {
                        System.arraycopy(alph, 0, alph, 1, i);
                        alph[0] = c;
                        break;
                    }
                }
            }
        }

        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] alph = new char[256];
        for (int i = 0; i < 256; ++i) {
            alph[i] = (char) i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write(alph[c], 8);
            char tmp = alph[c];
            // Move to front
            if (c > 0) {
                System.arraycopy(alph, 0, alph, 1, c);
                alph[0] = tmp;
            }
        }
        BinaryStdOut.flush();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
