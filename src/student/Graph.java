package student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The graph is used to search
 *
 * @author Lyan Ye
 */
public class Graph {

    /** graph stores station node */
    private Map<String, Node> graph;

    /**
     * create a graph
     */
    public Graph() {
        this.graph = new HashMap<>();
    }

    /**
     * check if the map has the node with this nodename
     * @param nodeName the name of the node(station)
     * @return true if the map contains the node, false otherwise
     */
    public boolean hasNode(String nodeName) {
        return this.graph.containsKey(nodeName);
    }

    /**
     * get the node from the map
     * @param nodeName the key of the name
     * @return the node
     */
    public Node getNode(String nodeName) {
        if (hasNode(nodeName)){
            return this.graph.get(nodeName);
        }
        return null;
    }

    /**
     * add the neighbor to the node
     * @param node the station node
     * @param neighbor the neighbor is added the node
     */
    public void addNeighbor (Node node, Node neighbor) {
        if (hasNode(node.getName())) {
            this.graph.get(node.getName()).addNeighbor(neighbor);
        }
    }

    /**
     * get the list of the node
     * @param node the node
     * @return the list of the node(neighbors of this station)
     */
    public List<Node> getNeighbors (Node node) {
        return this.graph.get(node.getName()).getNeighbors();
    }

    /**
     * create a new node if the node is not exist
     * @param nodeName the name of the station node
     * @return node
     */
    public Node makeNode(String nodeName) {
        if (!this.graph.containsKey(nodeName)) {
            this.graph.put(nodeName, new Node(nodeName));
        }
        return this.graph.get(nodeName);
    }


}
