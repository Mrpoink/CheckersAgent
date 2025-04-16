package problems;

    /**
     * Represents a square on a Tic-Tac-Toe board.
     *
     * Note that Java automatically generates equals() and
     * hashCode() for records, making this class safe to use
     * as a key in hash-based data structures (e.g., HashMap)
     *
     * @param row the row number of the square
     * @param column the column number of the square
     */
    public record Square(int row, int column) { }
