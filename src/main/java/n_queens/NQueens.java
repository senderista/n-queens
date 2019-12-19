package n_queens;

import java.util.Arrays;

/** A class solving the N-Queens problem for given N,
 * with the additional proviso that no 3 queens may
 * lie in the same line, regardless of its angle.
 * @author Tobin Baker
 * @author tobin.d.baker@gmail.com
 * @version 1.0
*/
public class NQueens {
    /**
     * The dimension of the board and the number of queens to place on the board.
     */
    private final int N;
    /**
     * A sparse representation of the board, where we only need to track one column position in each row:
     * since at most one queen can occupy a row to avoid attacking, and each row must be occupied in order
     * to sum to N queens, there must be precisely one queen per row.
     */
    private final int[] colPosInRow;

    /**
     * @param n the dimension of the board and number of queens to place
     */
    public NQueens(int n) {
        this.N = n;
        //
        int[] colPosInRow = new int[n];
        // we use -1 as a sentinel value for an unoccupied position
        Arrays.fill(colPosInRow, -1);
        this.colPosInRow = colPosInRow;
    }

    /**
     * Validates that the proposed position satisfies our criteria.
     * Should be private and instance-level, but made static and package-visible to support unit tests.
     * @param row the row index of the proposed position
     * @param col the column index of the proposed position
     * @return boolean the proposed position satisfies our criteria
     */
    static boolean validatePosition(int[] colPosInRow, int row, int col) {
        // iterate over all previous rows
        for (int i = 0; i < row; i++) {
            // first check if there is already a queen in this column
            if (colPosInRow[i] == col) {
                return false;
            }
            // next check if there is a queen diagonally from this position
            if (Math.abs(row - i) == Math.abs(col - colPosInRow[i])) {
                return false;
            }
            // now check all existing pairs starting with the current row to see if this position is collinear with the pair
            for (int j = i + 1; j < row; j++) {
                // calculate slope of line formed by existing pair
                double slope = ((double)(j - i)) / ((double)(colPosInRow[j] - colPosInRow[i]));
                // calculate slope of line formed by first position in pair and candidate position
                double candidateSlope = ((double)(row - i)) / ((double)(col - colPosInRow[i]));
                // if the slopes match, the three positions form a straight line, so reject this position
                if (slope == candidateSlope) {
                    return false;
                }
            }
        }
        // we can place a queen in this position
        return true;
    }

    /**
     * Instance wrapper for <code>validatePosition</code>
     * @param row the row index of the proposed position
     * @param col the column index of the proposed position
     * @return boolean the proposed position satisfies our criteria
     */
    private boolean internalValidatePosition(int row, int col) {
        return validatePosition(this.colPosInRow, row, col);
    }

    /**
     * Starting from <code>row</code>, find a valid solution of the board.
     * @param row the row of the board at which to place a new queen
     * @return boolean a valid solution of the board has been found
     */
    private boolean findValidConfigurationStartingAtRow(int row) {
        // if all rows are exhausted, we have found a candidate solution.
        if (row == this.N) {
            return true;
        }
        // iterate over all columns for this row and try to place a queen
        for (int col = 0; col < N; col++) {
            // if this column is a valid position, place a queen
            if (internalValidatePosition(row, col)) {
                this.colPosInRow[row] = col;
                // now the tricky part: we recur on next row and keep going until we find a full solution.
                // if we don't find a full solution after recursing from this placement, we need to backtrack.
                if (findValidConfigurationStartingAtRow(row + 1)) {
                    return true;
                }
                // we couldn't find a valid solution starting from this placement, so revert this placement
                // and try the next column for this row.
                this.colPosInRow[row] = -1;
            }
        }
        // if we get here, we couldn't find any valid placement for the current row, so return false
        // to force backtracking.
        return false;
    }

    /**
     * Find a valid solution for the entire board.
     * @return boolean a valid solution of the board has been found
     */
    public boolean findSolution() {
        return findValidConfigurationStartingAtRow(0);
    }

    /**
     * Print a visual representation of the solution to standard output.
     */
    public void printSolution() {
        for (int i = 0; i < this.N; i++) {
            for (int j = 0; j < this.N; j++) {
                if (this.colPosInRow[i] == j) {
                    System.out.print("Q ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Find a solution to the extended N-Queens problem for given N.
     * @param args contains a single integer representing the dimension of the board and number of queens to place
     */
    public static void main(String[] args) {
        assert args.length == 1;
        final int N = Integer.parseInt(args[0]);
        NQueens n_queens = new NQueens(N);
        if (n_queens.findSolution()) {
            n_queens.printSolution();
        } else {
            System.out.println("No solution found for dimension " + N);
            System.exit(1);
        }
    }
}
