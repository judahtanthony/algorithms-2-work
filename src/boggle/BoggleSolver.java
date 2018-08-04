
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;


public class BoggleSolver {
    private static final int R = 26;
    private Node dict;      // root of trie

    // R-way trie node
    private static class Node {
        private Node[] next = new Node[R];
        private boolean isWord;
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        // Build the dictionary.
        for (String word : dictionary) {
            dict = add(dict, word, 0);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        StdOut.printf("Board:\n%s\n\n", board);
        Set<String> words = new HashSet<String>();
        int rows = board.rows();
        int cols = board.cols();
        int[][] grid = new int[rows][cols];
        char[] buffer = new char[rows * cols * 2]; // times 2 if all Qs.

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = ((int) board.getLetter(i, j)) - 65;
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                findWords(grid, rows, cols, i, j,
                          dict, buffer, 0, words);
            }
        }

        return words;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        switch (word.length()) {
            case 0:
            case 1:
            case 2:
                return 0;
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
        }
        return 11;
    }


    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d >= key.length()) {
            x.isWord = true;
        }
        else {
            int c = key.codePointAt(d) - 65;
            x.next[c] = add(
                    x.next[c],
                    key,
                    (c == 16 /* Q */ ? d + 2 : d + 1));
        }
        return x;
    }

    private void findWords(int[][] grid, int rows, int cols, int i, int j,
                           Node node, char[] buffer, int d, Set<String> words) {
        if (i < 0 || j < 0 || i >= rows || j >= cols) return;
        if (grid[i][j] < 0) return;
        if (node.next[grid[i][j]] == null) return;
        // We found a character!
        int ci = grid[i][j];
        node = node.next[ci]; // Move node into sub-tree.
        buffer[d++] = (char) (ci + 65); // Add to our word buffer.
        // We have a 'Q'.  Let's donate a complimentary 'U' to it.
        if (ci == 16) buffer[d++] = 'U';
        // We found a word!
        if (d >= 3 && node.isWord) words.add(new String(buffer, 0, d));

        // Now look at neighbors.
        grid[i][j] = -1; // Mark this block used.
        findWords(grid, rows, cols, i - 1, j - 1, node, buffer, d, words);
        findWords(grid, rows, cols, i - 1, j, node, buffer, d, words);
        findWords(grid, rows, cols, i - 1, j + 1, node, buffer, d, words);
        findWords(grid, rows, cols, i, j - 1, node, buffer, d, words);
        findWords(grid, rows, cols, i, j + 1, node, buffer, d, words);
        findWords(grid, rows, cols, i + 1, j - 1, node, buffer, d, words);
        findWords(grid, rows, cols, i + 1, j, node, buffer, d, words);
        findWords(grid, rows, cols, i + 1, j + 1, node, buffer, d, words);
        grid[i][j] = ci; // Free up this block to be used again.
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}