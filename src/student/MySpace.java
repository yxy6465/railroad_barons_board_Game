package student;

import model.Space;

/**
 * Represents a space on the Railroad Barons map.
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MySpace implements Space {

    /** row that the space is on */
    private int row;
    /** column that the space is on */
    private int col;

    /**
     * create a space
     * @param row the row of space is at
     * @param col the column of space is at
     */
    public MySpace(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row of the space's location in the map.
     * @return the row of the space's location in the map.
     */
    @Override
    public int getRow() {
        return this.row;
    }

    /**
     * the column of the space's location in the map.
     * @return the column of the space's location in the map.
     */
    @Override
    public int getCol() {
        return this.col;
    }

    /**
     * Returns true if the other space is occupying the same physical location in the map
     * as this space.
     * @param other The other space to which this space is being compared for
     *              collocation.
     *
     * @return True if the two spaces are in the same physical location (row and column)
     * in the map; false otherwise.
     */
    @Override
    public boolean collocated(Space other) {
        return this.row == other.getRow() &&
                this.col == other.getCol();
    }
}
