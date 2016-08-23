package stdlib;

/* ***********************************************************************
 *  Compilation:  javac StdArrayIO.java
 *  Execution:    java StdArrayIO < input.txt
 *
 *  A library for reading in 1D and 2D arrays of integers, doubles,
 *  and booleans from standard input and printing them out to
 *  standard output.
 *
 *  % more tinyDouble1D.txt
 *  4
 *    .000  .246  .222  -.032
 *
 *  % more tinyDouble2D.txt
 *  4 3
 *    .000  .270  .000
 *    .246  .224 -.036
 *    .222  .176  .0893
 *   -.032  .739  .270
 *
 *  % more tinyBoolean2D.txt
 *  4 3
 *    1 1 0
 *    0 0 0
 *    0 1 1
 *    1 1 1
 *
 *  % cat tinyDouble1D.txt tinyDouble2D.txt tinyBoolean2D.txt | java StdArrayIO
 *  4
 *    0.00000   0.24600   0.22200  -0.03200
 *
 *  4 3
 *    0.00000   0.27000   0.00000
 *    0.24600   0.22400  -0.03600
 *    0.22200   0.17600   0.08930
 *    0.03200   0.73900   0.27000
 *
 *  4 3
 *  1 1 0
 *  0 0 0
 *  0 1 1
 *  1 1 1
 *
 *************************************************************************/


/**
 *  <i>Standard array IO</i>. This class provides methods for reading
 *  in 1D and 2D arrays from standard input and printing out to
 *  standard output.
 *  <p>
 *  For additional documentation, see
 *  <a href="http://introcs.cs.princeton.edu/22libary">Section 2.2</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i>
 *  by Robert Sedgewick and Kevin Wayne.
 */
public class StdArrayIO {

    /**
     * Read in and return an array of Strings from standard input.
     */
    public static String[] readString1D() {
        final int N = StdIn.readInt();
        final String[] a = new String[N];
        for (int i = 0; i < N; i++) {
            a[i] = StdIn.readString();
        }
        return a;
    }

    /**
     * Print an array of Strings to standard output.
     */
    public static void print(Object[] a) {
        final int N = a.length;
        StdOut.println(N);
        for (int i = 0; i < N; i++) {
            StdOut.printf("%s ", a[i]);
        }
        StdOut.println();
    }


    /**
     * Read in and return an M-by-N array of Strings from standard input.
     */
    public static String[][] readString2D() {
        final int M = StdIn.readInt();
        final int N = StdIn.readInt();
        final String[][] a = new String[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                a[i][j] = StdIn.readString();
            }
        }
        return a;
    }

    /**
     * Print the M-by-N array of Strings to standard output.
     */
    public static void print(Object[][] a) {
        final int M = a.length;
        final int N = a[0].length;
        StdOut.println(M + " " + N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                StdOut.printf("%s ", a[i][j]);
            }
            StdOut.println();
        }
    }

    /**
     * Read in and return an array of doubles from standard input.
     */
    public static double[] readDouble1D() {
        final int N = StdIn.readInt();
        final double[] a = new double[N];
        for (int i = 0; i < N; i++) {
            a[i] = StdIn.readDouble();
        }
        return a;
    }

    /**
     * Print an array of doubles to standard output.
     */
    public static void print(double[] a) {
        final int N = a.length;
        StdOut.println(N);
        for (int i = 0; i < N; i++) {
            StdOut.printf("%9.5f ", a[i]);
        }
        StdOut.println();
    }


    /**
     * Read in and return an M-by-N array of doubles from standard input.
     */
    public static double[][] readDouble2D() {
        final int M = StdIn.readInt();
        final int N = StdIn.readInt();
        final double[][] a = new double[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                a[i][j] = StdIn.readDouble();
            }
        }
        return a;
    }

    /**
     * Print the M-by-N array of doubles to standard output.
     */
    public static void print(double[][] a) {
        final int M = a.length;
        final int N = a[0].length;
        StdOut.println(M + " " + N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                StdOut.printf("%9.5f ", a[i][j]);
            }
            StdOut.println();
        }
    }


    /**
     * Read in and return an array of ints from standard input.
     */
    public static int[] readInt1D() {
        final int N = StdIn.readInt();
        final int[] a = new int[N];
        for (int i = 0; i < N; i++) {
            a[i] = StdIn.readInt();
        }
        return a;
    }

    /**
     * Print an array of ints to standard output.
     */
    public static void print(int[] a) {
        final int N = a.length;
        StdOut.println(N);
        for (int i = 0; i < N; i++) {
            StdOut.printf("%9d ", a[i]);
        }
        StdOut.println();
    }


    /**
     * Read in and return an M-by-N array of ints from standard input.
     */
    public static int[][] readInt2D() {
        final int M = StdIn.readInt();
        final int N = StdIn.readInt();
        final int[][] a = new int[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                a[i][j] = StdIn.readInt();
            }
        }
        return a;
    }

    /**
     * Print the M-by-N array of ints to standard output.
     */
    public static void print(int[][] a) {
        final int M = a.length;
        final int N = a[0].length;
        StdOut.println(M + " " + N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                StdOut.printf("%9d ", a[i][j]);
            }
            StdOut.println();
        }
    }


    /**
     * Read in and return an array of booleans from standard input.
     */
    public static boolean[] readBoolean1D() {
        final int N = StdIn.readInt();
        final boolean[] a = new boolean[N];
        for (int i = 0; i < N; i++) {
            a[i] = StdIn.readBoolean();
        }
        return a;
    }

    /**
     * Print an array of booleans to standard output.
     */
    public static void print(boolean[] a) {
        final int N = a.length;
        StdOut.println(N);
        for (int i = 0; i < N; i++) {
            if (a[i]) StdOut.print("1 ");
            else      StdOut.print("0 ");
        }
        StdOut.println();
    }

    /**
     * Read in and return an M-by-N array of booleans from standard input.
     */
    public static boolean[][] readBoolean2D() {
        final int M = StdIn.readInt();
        final int N = StdIn.readInt();
        final boolean[][] a = new boolean[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                a[i][j] = StdIn.readBoolean();
            }
        }
        return a;
    }

    /**
     * Print the  M-by-N array of booleans to standard output.
     */
    public static void print(boolean[][] a) {
        final int M = a.length;
        final int N = a[0].length;
        StdOut.println(M + " " + N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (a[i][j]) StdOut.print("1 ");
                else         StdOut.print("0 ");
            }
            StdOut.println();
        }
    }


    /**
     * Test client.
     */
    public static void main(String[] args) {

        // read and print an array of doubles
        final double[] a = StdArrayIO.readDouble1D();
        StdArrayIO.print(a);
        StdOut.println();

        // read and print a matrix of doubles
        final double[][] b = StdArrayIO.readDouble2D();
        StdArrayIO.print(b);
        StdOut.println();

        // read and print a matrix of doubles
        final boolean[][] d = StdArrayIO.readBoolean2D();
        StdArrayIO.print(d);
        StdOut.println();
    }

}
