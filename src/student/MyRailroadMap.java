package student;

import model.*;

import java.util.*;

/**
 * Represents a Railroad Barons map, which consists of empty spaces, stations, tracks, and routes.
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyRailroadMap implements model.RailroadMap {

    /** the observers list of RailroadMapObserver */
    private List<RailroadMapObserver> observers = new LinkedList<>();
    /** a list contains routes */
    private List<Route> routes;
    /** 2D array that contains routes*/
    private Space[][] map;

    /**
     * create the MyRailroadMap
     * @param routes
     */
    public MyRailroadMap(List<Route> routes) {
        this.routes = routes;
        this.map = new Space[this.getRows()][this.getCols()];
        for (int row=0;row<this.getRows(); ++row) {
            for (int col=0; col<this.getCols(); ++col) {
                for(Route route: this.routes) {
                    if (route.getOrigin().getRow()==row &&
                            route.getOrigin().getCol()==col) {
                        map[row][col] = route.getOrigin();
                    }
                    else if (route.getDestination().getRow()==row &&
                            route.getDestination().getCol()==col) {
                        map[row][col] = route.getDestination();
                    }

                    for (Track t: route.getTracks()) {
                        if (t.getCol()==col && t.getRow()==row) {
                            map[row][col] = t;
                        }
                    }
                }
            }
        }
        for (int row=0;row<this.getRows(); ++row) {
            for (int col = 0; col < this.getCols(); ++col) {
                if (map[row][col] == null) {
                    map[row][col] = new MySpace(row, col);
                }
            }
        }

    }

    /**
     * Adds the specified observer to the map. The observer will be notified of significant
     * events involving this map such as when a route has been claimed by a Baron.
     *
     * @param observer The {@link RailroadMapObserver} being added to the map.
     */
    @Override
    public void addObserver(RailroadMapObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Removes the specified observer from the map. The observer will no longer be notified of
     * significant events involving this map.
     *
     * @param observer The observer to remove from the collection of registered observers that will
     *                 be notified of significant events involving this map.
     */
    @Override
    public void removeObserver(RailroadMapObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Returns the number of rows in the map. This is determined by the location of the
     * southernmost station on the map.
     * @return The number of rows in the map.
     */
    @Override
    public int getRows() {
        int row = routes.get(0).getDestination().getRow();
        for (Route route: routes) {
            if (route.getDestination().getRow() > row) {
                row = route.getDestination().getRow();
            }
        }
        return row + 1;
    }

    /**
     * Returns the number of columns in the map. This is determined by the location of the
     * easternmost station on the map.
     * @return The number of columns in the map.
     */
    @Override
    public int getCols() {
        int col = routes.get(0).getDestination().getCol();
        for (Route route: routes) {
            if (route.getDestination().getCol() > col) {
                col = route.getDestination().getCol();
            }
        }
        return col + 1;
    }

    /**
     * Returns the space located at the specified coordinates.
     *
     * @param row The row of the desired {@link Space}.
     * @param col The column of the desired {@link Space}.
     *
     * @return
     */
    @Override
    public Space getSpace(int row, int col) {
        return this.map[row][col];
    }

    /**
     * Returns the route that contains the track at the specified location (if such a route exists}.
     * @param row The row of the location of one of the tracks in the route.
     * @param col The column of the location of one of the tracks in the route.
     *
     * @return The Route that contains the Track at the specified location, or null if there is no such Route.
     */
    @Override
    public Route getRoute(int row, int col) {
        for (Route route: routes) {
            if ((route.getOrigin().getRow() == row &&
                    route.getOrigin().getCol() == col) ||
                    (route.getDestination().getRow() == row &&
                    route.getDestination().getCol() == col)) {
                return route;
            } for (Track t: route.getTracks()) {
                    if (t.getRow() == row && t.getCol() == col) {
                        return route;
                    }
            }
        }
        return null;
    }

    /**
     * Called to update the map when a Baron has claimed a route.
     * @param route The {@link Route} that has been claimed.
     */
    @Override
    public void routeClaimed(Route route) {
        for (RailroadMapObserver map: observers) {
            map.routeClaimed(this, route);
        }

    }

    /**
     * Returns the length of the shortest unclaimed route in the map.
     * @return The length of the shortest unclaimed Route.
     */
    @Override
    public int getLengthOfShortestUnclaimedRoute() {
        int length = 0;
        for (Route route: routes) {
            if (route.getBaron() == Baron.UNCLAIMED) {
                length = route.getLength();
            }
        }
        for (Route route: routes) {
            if (route.getBaron() == Baron.UNCLAIMED && route.getLength() < length) {
                length = route.getLength();
            }
        }
        return length;
    }

    /**
     * Returns all of the Routes in this map.
     * @return A Collection of all of the Routes in this RailroadMap.
     */
    @Override
    public Collection<Route> getRoutes() {
        return this.routes;
    }
}
