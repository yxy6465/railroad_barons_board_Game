package student;

import java.util.LinkedList;
import java.util.List;

/**
 * Node that is used by graph
 *
 * @author Lyan Ye
 */
public class Node {

    /** name of the station */
    private String name;
    /** the neighbors of the node */
    private List<Node> neighbors;

    /**
     * create a node
     * @param name station name
     */
    public Node(String name) {
        this.name = name;
        this.neighbors = new LinkedList<>();
    }

    /**
     * get the name of the node(station)
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * add the neighbor station to the list
     * @param n other station node
     */
    public void addNeighbor(Node n) {
        this.neighbors.add(n);
    }

    /**
     * get the list of station node
     * @return list of station node
     */
    public List<Node> getNeighbors() {
        return this.neighbors;
    }
}
