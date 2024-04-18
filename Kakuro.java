/**
 * Author: Abhishek Amgain
 * Filename: Kakuro.java
 * Program Description: This is a Kakuro program that takes file input as a problem and then solves and prints the board.
 */

//Import files
import java.io.*;
import java.util.*;

// Class named Kakuro that solves Kakuro
public class Kakuro {
    // Creating 2D array to store the objects of its information for different
    // positions in the kakuroSolver board.
    private Info[][] board;

    /**
     * Constructor named kakuro that initializes the kakuro board from the input
     * file.
     * 
     * @param filename
     */

    public Kakuro(String filename) {

        // try Block
        try {
            // Taking file input
            Scanner file = new Scanner(new File(filename));
            String[] firstLine = file.nextLine().trim().split(" ");

            // Storing the number of rows and columns in the size1 and size2
            int size1 = Integer.parseInt(firstLine[0]);
            int size2 = Integer.parseInt(firstLine[1]);
            int row = 0;
            // Initializing the board having the size is size1*size2.
            board = new Info[size1][size2];

            // File has input
            while (file.hasNextLine()) {
                // Splitting the line with the comma
                String[] line = file.nextLine().trim().split(",");
                // Reading the line from the file
                for (int col = 0; col < size2; col++) {
                    if (line[col].equals("X")) {
                        // Creating Info object of this board[row][col]
                        board[row][col] = new Info(false, 0, 0, 0, "X");
                    } else if (line[col].contains("\\")) {
                        if (line[col].startsWith("\\")) {
                            int val = Integer.parseInt(line[col].replace("\\", ""));
                            // Creating Info object of this board[row][col]
                            board[row][col] = new Info(false, 0, val, 0, "R");
                        } else if (line[col].endsWith("\\")) {
                            int val = Integer.parseInt(line[col].replace("\\", ""));
                            board[row][col] = new Info(false, 0, 0, val, "D");
                        } else {
                            String[] parts = line[col].split("\\\\");
                            board[row][col] = new Info(false, 0, Integer.parseInt(parts[1]), Integer.parseInt(parts[0]),
                                    "DR");
                        }
                    } else if (Character.isDigit(line[col].charAt(0)) && Integer.parseInt(line[col]) > 0) {
                        board[row][col] = new Info(false, Integer.parseInt(line[col]), 0, 0, "");
                    } else {
                        // Creating Info object of this board[row][col]
                        board[row][col] = new Info(true, 0, 0, 0, "");
                    }
                }
                // Increasing to the next row
                row++;
            }
            solveProblem(0, 0);
            printSolution();
            // Closing the file
            file.close();

        }
        // catch Block
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method named solveProblem that returns true if the problem is solved, false
     * otherwise.
     * 
     * @param row current row
     * @param col current column
     * @return true if the problem is solved, false otherwise
     */
    private boolean solveProblem(int row, int col) {
        // number of rows
        final int numRows = board.length;
        // number of columns
        final int numCols = board[0].length;
        // If the current row is greater than the actual row, the the problem is solved.
        if (row >= numRows) {
            return true;
        }
        // Calculating the next row and column for the kakuro solver to be solved.
        int nextCol = (col == numCols - 1) ? 0 : col + 1;
        int nextRow = (col == numCols - 1) ? row + 1 : row;

        // If the current position is not replaceable then it goes to the next position.
        if (!(board[row][col].isReplace())) {
            return solveProblem(nextRow, nextCol);
        }

        // Put the value from range 1-9 in the empty place in the board.
        for (int i = 1; i <= 9; i++) {
            // if the value is valid for the current position
            if (isValid(i, row, col)) {
                // set the value to be in the current position
                board[row][col].setValue(i);
                // Move for the next place in the board.
                if (solveProblem(nextRow, nextCol)) {
                    return true;
                }
                // Backtracking since this current value is not appropriate
                board[row][col].setValue(0);
            }
        }
        // Problem is not solvable.
        return false;
    }

    /**
     * Method named isValid that determines whether the value num is valid for
     * the position at row and column.
     * 
     * @param num the value
     * @param row the row number
     * @param col the column number
     * @return true if the num is valid at current position, false otherwise
     */
    public boolean isValid(int num, int row, int col) {
        // Returns true if the number is valid both row wise and column wise.
        return isValidRow(num, row, col) && isValidCol(num, row, col);
    }

    /**
     * Method named isValidCol that returns true if the value is valid column wise.
     * 
     * @param num the value
     * @param row the row number
     * @param col the column number
     * @return true if the num is valid column wise
     */
    private boolean isValidCol(int num, int row, int col) {
        // temp variable that stores the sum for the column from the total to this
        // position
        int temp = num;
        // total variable that stores the total sum that has to be in that specific
        // column after this position
        int total = 0;

        // Loop to find the sum for the column from the total to this position and the
        // total sum that has to be.
        for (int i = row - 1; i >= 0; i--) {
            // we found the place where there is a vertical sum mentioned downwards.
            if (board[i][col].getDown() != 0) {
                total = board[i][col].getDown();
                break;
            }
            // adding the value to the temp
            temp += board[i][col].getValue();
            if (board[i][col].getValue() == num) {
                return false;
            }
        }
        // if the sum above the position is greater than the actual sum
        if (temp > total) {
            return false;
        }

        // if we are at the last row then the sum has to equal to the vertical sum
        // constraint fot that column
        if (row == board.length - 1) {
            if (temp < total) {
                return false;
            }
        } else if (!(board[row + 1][col].isReplace())) { // if the next place is not replaceable.
            if (temp < total) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method named isValidRow that returns true if the value is valid row wise.
     * 
     * @param num the value
     * @param row the row number
     * @param col the column number
     * @return true if the num is valid row wise
     */
    private boolean isValidRow(int num, int row, int col) {
        // temp variable that stores the sum for the row from the total to this
        // position
        int total = 0;
        // total variable that stores the total sum that has to be in that specific
        // row after this position
        int temp = num;

        // Loop to find the sum for the row from the total to this position and the
        // total sum that has to be
        for (int i = col - 1; i >= 0; i--) {
            // we found the place where there is a horizontal sum mentioned downwards.
            if (board[row][i].getAcross() != 0) {
                total = board[row][i].getAcross();
                break;
            }
            // adding the value to the temp
            temp += board[row][i].getValue();
            if (board[row][i].getValue() == num) {
                return false;
            }
        }
        // if the sum left to the position is greater than the actual sum
        if (temp > total) {
            return false;
        }
        // if we are at the last column then the sum has to equal to the horizontal sum
        // constraint fot that row
        if (col == board[0].length - 1) {
            if (temp < total) {
                return false;
            }
        } else if (!board[row][col + 1].isReplace()) { // if the next place is not replaceable.
            if (temp < total) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method named printSolution that prints the output in the desired format.
     */
    public void printSolution() {
        // Loop for the row
        for (int i = 0; i < board.length; i++) {
            // Loop for the each value in the row
            for (int j = 0; j < board[0].length; j++) {
                // Prints the output in the desired format
                if (board[i][j].getDirection().equals("X")) {
                    System.out.print("X");
                } else if (board[i][j].getDirection().equals("D")) {
                    System.out.print(board[i][j].getDown() + "\\");
                } else if (board[i][j].getDirection().equals("R")) {
                    System.out.print("\\" + board[i][j].getAcross());
                } else if (board[i][j].getDirection().equals("DR")) {
                    System.out.print(board[i][j].getDown() + "\\" + board[i][j].getAcross());
                } else {
                    System.out.print(board[i][j].getValue());
                }
                if (!(j == board[0].length - 1)) {
                    System.out.print(",");
                }
            }
            // End row and prints for the nextline
            if (!(i == board.length - 1)) {
                System.out.println();
            }
        }
    }

    // Private class named Info that contains the information about the specific
    // cell in the matrix.
    private static class Info {
        // Instance variables that stores different information about the specific cell
        // in the matrix
        private final boolean canReplace;
        private int value;
        private final int across;
        private final int down;
        private final String direction;

        /**
         * Constructor named Info that initializes the information about the cell of the
         * certain position in the matrix
         * 
         * @param canReplace is the place replaceable
         * @param value      the value in the cell
         * @param across     the value which stores the across value from that position
         * @param down       the value which stores the down value from that position
         * @param direction  the direction of that cell
         */
        public Info(boolean canReplace, int value, int across, int down, String direction) {
            // Intialization
            this.canReplace = canReplace;
            this.value = value;
            this.across = across;
            this.down = down;
            this.direction = direction;
        }

        /**
         * Method named getValue that returns the value of the cell
         * 
         * @return the value
         */
        public int getValue() {
            return value;
        }

        /**
         * Method named isReplace that returns whether the cell is replaceable or not.
         * 
         * @return true if the cell is replaceable, false otherwise
         */
        public boolean isReplace() {
            return canReplace;
        }

        /**
         * Method named getAcross that returns the across value from that position
         * 
         * @return the across value from that position
         */
        public int getAcross() {
            return across;
        }

        /**
         * Method named setValue that sets the value for the specific cell
         * 
         * @param val the value at this cell
         */
        public void setValue(int val) {
            this.value = val;
        }

        /**
         * Method named getDirection that stores the direction of the cell from this
         * specific position
         * 
         * @return the direction of this cell
         */
        public String getDirection() {
            return direction;
        }

        /**
         * Method named getDown that stores the down value from this cell.
         * 
         * @return the down value from this cell
         */
        public int getDown() {
            return down;
        }
    }
}
