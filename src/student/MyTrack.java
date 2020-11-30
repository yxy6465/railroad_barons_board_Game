package student;

import model.*;

/**
 * Represents a track segment on the map. Tracks combine to form routes.
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyTrack implements Track{

    /** the row that the track is on */
    private int row;
    /** the column that the track is on */
    private int col;
    /** the route that the track belongs to */
    private Route route;

    /**
     * create the track
     * @param route the route of which this track is part
     * @param row the row of the track
     * @param col the column of the track
     */
    public MyTrack(Route route, int row, int col) {
        this.route = route;
        this.row = row;
        this.col = col;
    }

    /**
     * the orientation of the track
     * @return the orientation of the track
     */
    @Override
    public Orientation getOrientation() {
        return this.route.getOrientation();
    }

    /**
     * Returns the current owner of this track, either unclaimed if the track has not been
     * claimed, or the owner that corresponds with the color of the player that successfully
     * claimed the route of which this track is a part.
     * @return the baron that has claimed on this track
     */
    @Override
    public Baron getBaron() {
        return this.route.getBaron();
    }

    /**
     * Returns the route of which this track is a part.
     * @return The Route that contains this track.
     */
    @Override
    public Route getRoute() {
        return this.route;
    }

    /**
     * the row of the track in the map
     * @return the row
     */
    @Override
    public int getRow() {
        return this.row;
    }

    /**
     * the column of the track in the map
     * @return the column
     */
    @Override
    public int getCol() {
        return this.col;
    }

    /**
     * compare the location between two object
     * @param other The other space to which this space is being compared for
     *              collocation.
     *
     * @return true if two locate on the same space in the map, false otherwise
     */
    @Override
    public boolean collocated(Space other) {
        return this.row == other.getRow() &&
                this.col == other.getCol();
    }
}
