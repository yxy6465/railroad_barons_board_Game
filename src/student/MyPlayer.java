package student;

import model.*;

import java.util.*;

/**
 * The class for a class that represents a player in a Railroad Barons game.
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyPlayer implements Player {

    /** the score of player gets */
    private int score;
    /** the pieces of train the player has */
    private int trainpieces;
    /** the color represents the player */
    private Baron baron;
    /** the collection of routes that the player claims */
    private Collection<Route> claimedroutes;
    /** the observers' list */
    private List<PlayerObserver> observers = new LinkedList<>();
    /** the pairs that the player has */
    private Stack<Pair> pairs;
    /** whether the player has claimed on this turn or not */
    private boolean claimed;
    /** the cards the player has */
    private List<Card> cards;
    /** a list contains the qualified card type that can claim the routes */
    private List<Card> qualify;
    /** a list contains the qualified card type that can claim the routes on it own*/
    private List<Card> useItself;
    /** a list contains the qualified card type that can claim the routes with one wild card */
    private List<Card> useWithWild;

    /** the railroadBarons */
    private RailroadBarons railroadBarons;

    /** check if the westernmost to easternmost routes are exist */
    private boolean WestToEast = false;
    /** check if the westernmost to easternmost routes are exist */
    private boolean NorthToSouth = false;
    /** check if this player has awarded the bonus of westernmost to easternmost */
    private boolean bonusH = false;
    /** check if this player has awarded the bonus of northernmost to southernmost */
    private boolean bonusV = false;

    /** bonus point */
    private int bonus = 0;

    /** the list contains all the westernMost station */
    private List<Station> westmost;
    /** the list contains all the easternMost station */
    private List<Station> eastmost;
    /** the list contains all the northernMost station */
    private List<Station> northmost;
    /** the list contains all the southernMost station */
    private List<Station> southmost;

    /**the graph is used to search the path */
    private Graph graph;


    /**
     * create a player
     * @param baron the color represent the player
     */
    public MyPlayer(Baron baron, RailroadBarons railroadBarons) {
        this.pairs = new Stack<>();
        this.claimedroutes = new LinkedList<>();
        this.cards = new LinkedList<>();
        this.baron = baron;
        this.trainpieces = 45;
        this.score = 0;
        this.claimed = false;
        this.railroadBarons = railroadBarons;
        this.graph = new Graph();
    }

    /**
     * This is called at the start of every game to reset the player to its initial state:
        Number of train pieces reset to the starting number of 45.
        All remaining cards cleared from hand.
        Score reset to 0.
        Claimed routes cleared.
        Sets the most recently dealt Pair of cards to two Card.NONE values.
     * @param dealt The hand of cards dealt to the player at the start of the game. By default
     *              this will be 4 cards.
     */
    @Override
    public void reset(Card... dealt) {
        this.bonus = 0;
        this.bonusV = false;
        this.bonusH = false;
        this.NorthToSouth = false;
        this.WestToEast = false;
        this.trainpieces = 45;
        this.score = 0;
        this.pairs = new Stack<>();
        this.claimedroutes = new LinkedList<>();
        this.pairs.push(new MyPair(Card.NONE, Card.NONE));
        this.cards = new LinkedList<>();
        this.cards.add(dealt[0]);
        this.cards.add(dealt[1]);
        this.cards.add(dealt[2]);
        this.cards.add(dealt[3]);
        this.graph = new Graph();
        for (PlayerObserver observer: observers) {
            observer.playerChanged(this);
        }

    }

    /**
     * Adds an observer that will be notified when the player changes in some way.
     * @param observer The new {@link PlayerObserver}.
     */
    @Override
    public void addPlayerObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer so that it is no longer notified when the player changes
     * in some way.
     * @param observer The {@link PlayerObserver} to remove.
     */
    @Override
    public void removePlayerObserver(PlayerObserver observer) {
        observers.remove(observer);
    }

    /**
     * The baron as which this player is playing the game.
     * @return The Baron as which this player is playing.
     */
    @Override
    public Baron getBaron() {
        return this.baron;
    }

    /**
     * Used to start the player's next turn. A pair of cards is dealt to the player, and the
     * player is once again able to claim a route on the map.
     * @param dealt a pair of cards to the player. Note that one or both of these cards may
     *              have a value of Card.NONE.
     */
    @Override
    public void startTurn(Pair dealt) {
        if (this.pairs.size() == 1 && this.getLastTwoCards().getFirstCard() == Card.NONE
                && this.getLastTwoCards().getSecondCard() == Card.NONE) {
            this.pairs.pop();
        }
        this.pairs.push(dealt);
        this.cards.add(dealt.getFirstCard());
        this.cards.add(dealt.getSecondCard());
        this.claimed = false;
    }

    /**
     * Returns the most recently dealt pair of cards. Note that one or both of the cards may
     * have a value of Card.NONE.
     * @return The most recently dealt Pair of Cards.
     */
    @Override
    public Pair getLastTwoCards() {
        return this.pairs.peek();
    }

    /**
     * Returns the number of the specific kind of card that the player currently has in hand.
     * Note that the number may be 0.
     * @param card The {@link Card} of interest.
     * @return The number of the specified type of Card that the player currently has in hand.
     */
    @Override
    public int countCardsInHand(Card card) {
        int count = 0;
        for (Card c: this.cards) {
            if (c==card) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of game pieces that the player has remaining. Note that the number may be 0.
     * @return The number of game pieces that the player has remaining.
     */
    @Override
    public int getNumberOfPieces() {
        return this.trainpieces;
    }

    /**
     * Returns true iff the following conditions are true:
        The route is not already claimed by this or some other baron.
        The player has not already claimed a route this turn (players are limited to one claim per turn).
        The player has enough cards (including ONE wild card, if necessary) to claim the route.
        The player has enough train pieces to claim the route.
     * @param route The {@link Route} being tested to determine whether or not
     *              the player is able to claim it.
     * @return True if the player is able to claim the specified Route, and false otherwise.
     */
    @Override
    public boolean canClaimRoute(Route route) {
        if (route.getBaron() != Baron.UNCLAIMED || this.claimed == true) {
            return false;
        }
        this.qualify = new LinkedList<>();
        this.useItself = new LinkedList<>();
        this.useWithWild = new LinkedList<>();
        for (Card c: Card.values()) {
            if (c != Card.WILD && c!= Card.NONE && c!= Card.BACK) {
                int count = countCardsInHand(c);
                if (count >= route.getLength()) {
                    this.qualify.add(c);
                    this.useItself.add(c);
                }
                if (this.cards.contains(Card.WILD) && count + 1 == route.getLength() && count > 0) {
                    this.qualify.add(c);
                    this.useWithWild.add(c);
                }
            }
        }
        if (route.getBaron() == Baron.UNCLAIMED && this.claimed == false &&
                (this.trainpieces >= route.getLength()) &&
                (this.qualify.size() != 0)) {
            return true;
        }
        return false;
    }

    /**
     * Claims the given route on behalf of this player's Railroad Baron. It is possible that
     * the player has enough cards in hand to claim the route by using different combinations of card.
     * It is up to the implementor to employ an algorithm that determines which cards to use,
     * but here are some suggestions:
        ·Use the color with the lowest number of cards necessary to match the length of the route.
        ·Do not use a wild card unless absolutely necessary (i.e. the player has length-1 cards of some color
         in hand and it is the most numerous card that the player holds).
     * @param route The {@link Route} to claim.
     *
     * @throws RailroadBaronsException
     */
    @Override
    public void claimRoute(Route route) throws RailroadBaronsException {
            Card use1 = Card.NONE;
            Card use2 = Card.NONE;
            if (!this.useItself.isEmpty()) {
                use1 = this.useItself.get(0);
                int least1 = countCardsInHand(this.useItself.get(0));
                for (Card c: this.useItself) {
                    int count = countCardsInHand(c);
                    if (count < least1) {
                        least1 = count;
                        use1 = c;
                    }
                }
            }
            if (!this.useWithWild.isEmpty()) {
                use2 = this.useWithWild.get(0);
            }
            route.claim(this.baron);
            this.claimedroutes.add(route);
            this.trainpieces = this.trainpieces - route.getLength();
            Node node1 = this.graph.makeNode(route.getOrigin().getName());
            Node node2 = this.graph.makeNode(route.getDestination().getName());
            this.graph.addNeighbor(node1, node2);
            this.graph.addNeighbor(node2, node1);
            this.score = this.score + route.getPointValue();
            if (this.bonusH==false || this.bonusV == false) {
                MostToMost();
                if (this.bonusH==false && this.WestToEast==true) {
                    this.score = this.score +  5*(this.railroadBarons.getRailroadMap().getCols());
                    this.bonusH = true;
                }
                if (this.bonusV == false && this.NorthToSouth==true) {
                    this.score = this.score + 5*(this.railroadBarons.getRailroadMap().getRows());
                    this.bonusV = true;
                }

            }
            if (!this.useItself.isEmpty()) {
                for (int i = 0; i < route.getLength(); i++) {
                    this.cards.remove(use1);
                }
            }
            else {
                this.cards.remove(Card.WILD);
                for (int i = 1; i<route.getLength(); i++) {
                    this.cards.remove(use2);
                }
            }
            this.claimed = true;
            for (PlayerObserver observer: observers) {
                observer.playerChanged(this);
            }
    }

    /**
     * Returns the collection of routes claimed by this player.
     * @return The Collection of Routes claimed by this player.
     */
    @Override
    public Collection<Route> getClaimedRoutes() {
        return this.claimedroutes;
    }

    /**
     * Returns the players current score based on the point value of each route that the
     * player has currently claimed.
     * @return The player's current score.
     */
    @Override
    public int getScore() {
        return this.score;
    }

    /**
     * Returns true iff the following conditions are true:
        ·The player has enough cards (including wild cards) to claim a route of the specified length.
        ·The player has enough train pieces to claim a route of the specified length.
     * @param shortestUnclaimedRoute The length of the shortest unclaimed
     *                               {@link Route} in the current game.
     *
     * @return True if the player can claim such a route, and false otherwise.
     */
    @Override
    public boolean canContinuePlaying(int shortestUnclaimedRoute) {
        if (this.trainpieces < shortestUnclaimedRoute) {
            return false;
        }
        this.qualify = new LinkedList<>();
        int check = 0;
        if (this.cards.contains(Card.WILD)) {
            check = 1;
        }
        for (Card c: Card.values()) {
            if (c != Card.WILD && c!= Card.NONE && c!= Card.BACK) {
                int count = countCardsInHand(c);
                if (count >= shortestUnclaimedRoute || (count + check == shortestUnclaimedRoute && count > 0)) {
                    this.qualify.add(c);
                }
            }
        }
        if (this.qualify.size() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * to print out the name of the player
     * @return name of player
     */
    @Override
    public String toString() {
        return "Player " + this.baron.toString();
    }

    /**
     * get the map
     * @return the map
     */
    public RailroadMap getMap() {
        return this.railroadBarons.getRailroadMap();
    }

    /**
     * initial the direction most list
     * @param westmost the list contains all the westernMost station
     * @param eastmost the list contains all the easternMost station
     * @param northmost the list contains all the northernMost station
     * @param southmost the list contains all the southernMost station
     */
    public void setGraph(List<Station> westmost, List<Station> eastmost,
                         List<Station> northmost, List<Station> southmost) {
        this.westmost = westmost;
        this.eastmost = eastmost;
        this.northmost = northmost;
        this.southmost = southmost;
    }

    /**
     * Create a path from a starting node to a finishing node if such
     * a path exists.
     * Uses depth-first search to determine if a path exists.
     * This pair of public and private methods uses a path list.
     * Another approach, shown below in the BFS code, is associating a
     * predecessor with each node.
     *
     * @rit.pre the arguments correspond to nodes in the graph
     * @param graph the graph to be searched
     * @param start  name associated with starting node
     * @param finish name associated with finishing node
     * @return an iteration from start to finish, or null if none exists
     */
    public static Iterable< Node > buildPathDFS(
            Graph graph, String start, String finish ) {

        // assumes input check occurs previously
        Node startNode, finishNode;
        startNode = graph.getNode( start );
        finishNode = graph.getNode( finish );

        // Construct a visited set of all nodes reachable from the start
        Set< Node > visited = new HashSet<>();
        visited.add( startNode );
        return buildPathDFS( graph, startNode, finishNode, visited );
    }

    /**
     * Recursive function that visits all nodes reachable from the given node
     * in depth-first search fashion. Visited list is updated in the process.
     *
     * @param graph the graph to be searched
     * @param start the node from which to search
     * @param finish the node for which to search
     * @param visited the list of nodes that have already been visited
     * @return the path from start to finish as a List,
     *         or if no path, an empty List.
     */
    private static List< Node > buildPathDFS(
            Graph graph, Node start, Node finish, Set< Node > visited ) {

        List< Node > path = null;

        if ( start.equals( finish ) )  {
            path = new LinkedList<>();
            // Put finish node in path. (Should be 1st node added.)
            path.add( start );
            return path;
        }

        for ( Node nbr : graph.getNeighbors( start ) ) {
            if ( !visited.contains( nbr ) ) {
                visited.add( nbr );
                path = buildPathDFS( graph, nbr, finish, visited );
                if ( path != null ) {
                    // Prepend this node to the successful path.
                    path.add( 0, start );
                    return path;
                }
            }
        }

        return null; // Failed on finding path from all neighbors. :-(
    }

    /**
     * Determine if a path of some length exists between a starting node
     * and a finishing node. If and only if this is true do the two nodes
     * belong to the same connected component in the graph.
     * <br>
     * This is the simplest version that only answers yes or no.
     * It keeps from getting into infinite cycles by
     *
     * @rit.pre the arguments name nodes in the graph.
     * @param graph the graph to be searched
     * @param start  the name associated with the starting node
     * @param finish the name associated with the finishing node
     * @return boolean indicating whether or not a path exists.
     */
    public static boolean canReachDFS(
            Graph graph, String start, String finish ) {

        // Assumes input check occurs previously
        Node startNode, finishNode;
        startNode = graph.getNode( start );
        finishNode = graph.getNode( finish );
        if (startNode == null || finishNode == null) {
            return false;
        }

        Set< Node > visited = new HashSet<>();
        visited.add( startNode );

        // Visit all nodes and check if finish is in visited set.
        visitDFS( graph, startNode, visited );
        return visited.contains( finishNode );
    }

    /**
     * Recursive function that visits all nodes reachable from the given node
     * in depth-first search fashion.  Visited list is updated in the process.
     *
     * @param graph the graph to be searched
     * @param node    the node from which to search
     * @param visited the list of nodes that have already been visited
     */
    private static void visitDFS( Graph graph, Node node, Set< Node > visited ) {
        graph.getNeighbors( node ).forEach( nbr -> {
            if ( !visited.contains( nbr ) ) {
                visited.add( nbr );
                visitDFS( graph, nbr, visited );
            }
        } );
    }

    /**
     * to check if there exists such a westernmost to easternmost and northernmost
     * to southernmost routes
     */
    public void MostToMost() {
        //check if westernmost to easternmost is available
        List<Node> path;
        for (Station start: westmost) {
            for (Station end : eastmost) {
                if (canReachDFS(this.graph, start.getName(), end.getName())) {
                    path = (List<Node>)buildPathDFS(this.graph, start.getName(), end.getName());
                    if (path.size() >= 3) {
                        WestToEast = true;
                    }

                }
            }
        }
        // check if northernmost to southernmost is available
        List<Node> p;
        for (Station s: northmost) {
            for (Station e: southmost) {
                if (canReachDFS(this.graph, s.getName(), e.getName())) {
                    p = (List<Node>) buildPathDFS(this.graph, s.getName(), e.getName());
                    if (p.size() >= 3) {
                        NorthToSouth = true;
                    }
                }
            }
        }
    }
}



