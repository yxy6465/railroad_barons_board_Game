package student;

import model.*;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * read the file to create routes to make a map
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyMapMaker implements model.MapMaker{

    /** hash map that contains station */
    private HashMap<Integer, Station> stations = new HashMap<>();
    /** list that contains routes */
    private List<Route> routes;

    /**
     * initial the routes to be linkedList
     */
    public MyMapMaker() {
       routes = new LinkedList<>();
    }

    /**
     * read the file to create the routes to make a map
     * @param in The {@link InputStream} used to read the {@link RailroadMap
     * map} data.
     * @return a map
     * @throws RailroadBaronsException
     */
    @Override
    public RailroadMap readMap(InputStream in) throws RailroadBaronsException{
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
            routes = new LinkedList<>();
            String line = reader.readLine();
            boolean check = true;
            while (line != null) {
                if (line.equals("##ROUTES##")) {
                    check = false;
                    line = reader.readLine();
                }
                if (check) {
                    String[] informations = line.split(" ", 4);
                    int key = Integer.parseInt(informations[0]);
                    Station station = new MyStation(Integer.parseInt(informations[1]),
                            Integer.parseInt(informations[2]), informations[3]);
                    this.stations.put(key, station);
                    line = reader.readLine();
                }
                else {
                    String[] informations = line.split(" ");
                    int origin = Integer.parseInt(informations[0]);
                    int destination = Integer.parseInt(informations[1]);
                    Route route = new MyRoute(stations.get(origin), stations.get(destination));
                    if (informations[2].equals("RED")) {
                        route.claim(Baron.RED);
                    }
                    else if (informations[2].equals("GREEN")) {
                        route.claim(Baron.GREEN);
                    }
                    else if (informations[2].equals("BLUE")) {
                        route.claim(Baron.BLUE);
                    }
                    else if (informations[2].equals("YELLOW")) {
                        route.claim((Baron.YELLOW));
                    }
                    this.routes.add(route);
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new MyRailroadMap(this.routes);
    }

    /**
     * wirte a file based on the game
     * @param map The {@link RailroadMap map} to write out to the
     * {@link OutputStream}.
     * @param out The {@link OutputStream} to which the
     * {@link RailroadMap map} data should be written.
     *
     * @throws RailroadBaronsException
     */
    public void writeMap(RailroadMap map, OutputStream out) throws RailroadBaronsException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        PrintWriter pw = new PrintWriter(writer);
        pw.flush();
        for (int id: stations.keySet()) {
            pw.println(id + " " + stations.get(id).getRow() + " " +
                    stations.get(id).getCol() + " " + stations.get(id).getName());
        }
        pw.println("##ROUTES##");
        for (Route route: map.getRoutes()) {
            Station ori = route.getOrigin();
            Station des = route.getDestination();
            int origin = 0;
            int destination = 0;
            for (int id: stations.keySet()) {
                if (stations.get(id).equals(ori)) {
                    origin = id;
                }
                if (stations.get(id).equals(des)) {
                    destination = id;
                }
            }
            pw.println(origin + " " + destination + " " + route.getBaron());
        }
    }
}

