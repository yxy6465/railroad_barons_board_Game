package student;

import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a train route in the Railroad Barons game. A route comprises tracks between two
 * stations on the map. Valid routes must include two distinct (non-equal) stations, must be
 * either horizontal or vertical, and the origin station must be north of or to the west of the
 * destination station (this simplifies some of the route methods).
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyRoute implements Route {

    /** the departure station */
    private Station origin;
    /** the destination station */
    private Station destination;
    /** the route is claimed by */
    private Baron baron;
    /** the orientation of the route */
    private Orientation orientation;
    /** the tracks that the route contains */
    private List<Track> tracks;

    /**
     * create a route
     * @param origin the station at the beginning of this route. The origin must be directly north
     *               of or to the west of the destination.
     * @param destination the station at the end of this route. The destination must be directly
     *                    south of or to the east of the origin.
     */
    public MyRoute(Station origin, Station destination) {
        this.origin = origin;
        this.destination = destination;
        this.tracks = new ArrayList<>(); // empty track list
        if (this.origin.getRow() == this.destination.getRow()) {
            this.orientation = Orientation.HORIZONTAL;
            for (int col=this.origin.getCol()+1; col<this.destination.getCol(); ++col) {
                this.tracks.add(new MyTrack(this, this.origin.getRow(), col));
            }
        } else {
            this.orientation = Orientation.VERTICAL;
            for (int row=this.origin.getRow()+1; row<this.destination.getRow(); ++row) {
                this.tracks.add(new MyTrack(this, row, this.origin.getCol()));
            }
        }
        this.baron = Baron.UNCLAIMED;
    }

    /**
     * Returns the Baron that has claimed this route. Note that this route may be unclaimed.
     * @return The Baron that has claimed this route.
     */
    @Override
    public Baron getBaron() {
        return this.baron;
    }

    /**
     * Returns the station at the beginning of this route. The origin must be directly north
     * of or to the west of the destination.
     * @return The Station at the beginning of this route.
     */
    @Override
    public Station getOrigin() {
        return this.origin;
    }

    /**
     * Returns the station at the end of this route. The destination must be directly
     * south of or to the east of the origin.
     * @return The Station at the end of this route.
     */
    @Override
    public Station getDestination() {
        return this.destination;
    }

    /**
     * Returns the orientation of this route; either horizontal or vertical.
     * @return The Orientation of this route.
     */
    @Override
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     * The set of tracks that make up this route.
     * @return The List of tracks that make up this route.
     */
    @Override
    public List<Track> getTracks() {
        return this.tracks;
    }

    /**
     * Returns the length of the route, not including the stations at the end points.
     * @return The number of Tracks comprising this route.
     */
    @Override
    public int getLength() {
        return this.tracks.size();
    }

    /**
     * Returns the number of points that this route is worth
     * @return The number of points that this route is worth.
     */
    @Override
    public int getPointValue() {
//        1 - 1 point
//        2 - 2 points
//        3 - 4 points
//        4 - 7 points
//        5 - 10 points
//        6 - 15 points
//        7 (or more) - 5 * (length - 3) points
        switch (getLength()) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
                return 7;
            case 5:
                return 10;
                default:
                    return 15;
        }
    }

    /**
     * Returns true if the route covers the ground at the location of the specified space
     * and false otherwise.
     * @param space The {@link Space} that may be in this route.
     *
     * @return True if the Space's coordinates are a part of this route, and false otherwise.
     */
    @Override
    public boolean includesCoordinate(Space space) {
        for (Track track: tracks) {
            if (track.collocated(space)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to claim the route on behalf of the specified Baron. Only unclaimed routes may
     * be claimed.
     * @param claimant The {@link Baron} attempting to claim the route. Must
     *                 not be null or {@link Baron#UNCLAIMED}.
     * @return True if the route was successfully claimed. False otherwise.
     */
    @Override
    public boolean claim(Baron claimant) {
        if (this.baron == Baron.UNCLAIMED) {
            this.baron = claimant;
            return true;
        } else {
            return false;
        }
    }
}
