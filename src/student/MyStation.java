package student;

import model.Space;
import model.Station;

/**
 *Represents a train station on the map. A train station is a space that has a name
 * and is at one end (origin) or the other (destination) of at least one train route.
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyStation implements Station{

    /** the row that the station is on */
    private int row;
    /** the column that the station is on */
    private int col;
    /** the name of the station */
    private String name;

    /**
     * create the station
     * @param row the row of the station in the map
     * @param col the column of the station in the map
     * @param name the name of the station
     */
    public MyStation(int row, int col, String name) {
        this.row = row;
        this.col = col;
        this.name = name;
    }

    /**
     * the name of the station
     * @return the name of the station
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * the row of the station in the map
     * @return the row
     */
    @Override
    public int getRow() {
        return this.row;
    }

    /**
     * the column the station in the map
     * @return the column
     */
    @Override
    public int getCol() {
        return this.col;
    }

    /**
     * compare to the station with the location provided
     * @param other The other space to which this space is being compared for
     *              collocation.
     *
     * @return True if the two spaces are in the same physical location (row and column)
     * in the map; false otherwise
     */
    @Override
    public boolean collocated(Space other) {
        return this.row == other.getRow() &&
                this.col == other.getCol();
    }

    /**
     * represent the station by name
     * @return name
     */
    @Override
    public String toString() {
        return this.name;
    }
}
