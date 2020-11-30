package student;

import model.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Classic version
 * Class for a Railroad Barons game. The main entry point into the model for the entire game.
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyRailRoadBarons implements RailroadBarons {


    /** observer list that contains RailroadBaronsObserver */
    private List<RailroadBaronsObserver> observers = new LinkedList<>();
    /** railroadMap */
    private RailroadMap map;
    /** deck contains cards */
    private Deck deck;
    /** queue of players */
    private Queue<Player> players = new LinkedList<>();
    /** the queue contains player of MyPlayer type */
    private Queue<MyPlayer> myplayers = new LinkedList<>();

    /** the list contains all the westernMost station */
    private List<Station> westmost = new LinkedList<>();
    /** the list contains all the easternMost station */
    private List<Station> eastmost = new LinkedList<>();
    /** the list contains all the northernMost station */
    private List<Station> northmost = new LinkedList<>();
    /** the list contains all the southernMost station */
    private List<Station> southmost = new LinkedList<>();

    /**
     * create 4 players and add them to the queue
     */
    public MyRailRoadBarons() {
        MyPlayer player1 = new MyPlayer(Baron.RED, this);
        MyPlayer player2 = new MyPlayer(Baron.GREEN, this);
        MyPlayer player4 = new MyPlayer(Baron.BLUE, this);
        MyPlayer player3 = new MyPlayer(Baron.YELLOW, this);
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        myplayers.add(player1);
        myplayers.add(player2);
        myplayers.add(player3);
        myplayers.add(player4);


    }

    /**
     * Adds a new observer to the collection of observers that will be notified when the
     * state of the game changes.
     * @param observer  The RailroadBaronsObserver to add to the Collection of observers.
     */
    @Override
    public void addRailroadBaronsObserver(RailroadBaronsObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes the observer from the collection of observers that will be notified when the
     * state of the game changes.
     * @param observer The {@link RailroadBaronsObserver} to remove.
     */
    @Override
    public void removeRailroadBaronsObserver(RailroadBaronsObserver observer) {
        observers.remove(observer);

    }

    /**
     * Starts a new Railroad Barons game with the specified map and a default deck of cards.
     * If a game is currently in progress, the progress is lost. There is no warning! By default,
     * a new game begins with:
        A default deck that contains 20 of each color of card and 20 wild cards.
        4 players, each of which has 45 train pieces.
        An initial hand of 4 cards dealt from the deck to each player
     * @param map The {@link RailroadMap} on which the game will be played.
     */
    @Override
    public void startAGameWith(RailroadMap map) {
        this.map = map;
        this.deck = new MyDeck();
        this.westmost = new LinkedList<>();
        this.eastmost = new LinkedList<>();
        this.northmost = new LinkedList<>();
        this.southmost = new LinkedList<>();
        this.setDirectionMost();
        for (MyPlayer p: myplayers) {
            p.reset(this.deck.drawACard(), this.deck.drawACard(), this.deck.drawACard(),
                    this.deck.drawACard());
            p.setGraph(westmost, eastmost, northmost, southmost);
        }
        myplayers.peek().startTurn(new MyPair(deck.drawACard(), deck.drawACard()));
        for (RailroadBaronsObserver o: observers) {
            o.turnStarted(this, myplayers.peek());
        }
    }

    /**
     * Starts a new Railroad Barons game with the specified map and deck of cards. This means that
     * the game should work with any implementation of the Deck interface (not just a specific
     * implementation)! Otherwise, the starting state of the game is the same as a normal game.
     *
     * @param map The {@link RailroadMap} on which the game will be played.
     * @param deck The Deck of cards used to play the game. This may be ANY implementation of the
     *             Deck interface, meaning that a valid implementation of the RailroadBarons interface
     *             should use only the Deck interface and not a specific implementation.
     */
    @Override
    public void startAGameWith(RailroadMap map, Deck deck) {
        if (!gameIsOver()) {
            this.map = map;
            this.deck = deck;
            myplayers.peek().startTurn(new MyPair(deck.drawACard(), deck.drawACard()));
            for (RailroadBaronsObserver o: observers) {
                o.turnStarted(this, myplayers.peek());
            }
        }
        else {
            Player winner = myplayers.peek();
            int highestScore = winner.getScore();
            for (Player p: myplayers) {
                if (p.getScore() > highestScore) {
                    winner = p;
                    highestScore = p.getScore();
                }
            }
            for (RailroadBaronsObserver observer: observers) {
                observer.gameOver(this, winner);
            }
        }
    }

    /**
     * Returns the map currently being used for play. If a game is not in progress, this may be null!
     * @return railroad map
     */
    @Override
    public RailroadMap getRailroadMap() {
        return this.map;
    }

    /**
     * Returns the number of cards that remain to be dealt in the current game's deck.
     * @return The number of cards that have not yet been dealt in the game's Deck.
     */
    @Override
    public int numberOfCardsRemaining() {
        return deck.numberOfCardsRemaining();
    }

    /**
     * Returns true iff the current player can claim the route at the specified location,
     * i.e. the player has enough cards and pieces, and the route is not currently claimed
     * by another player. Should delegate to the Player.canClaimRoute(Route) method on the
     * current player.
     *
     * @param row The row of a {@link Track} in the {@link Route} to check.
     * @param col The column of a {@link Track} in the {@link Route} to check.
     * @return True iff the Route can be claimed by the current player.
     */
    @Override
    public boolean canCurrentPlayerClaimRoute(int row, int col) {
        return getCurrentPlayer().canClaimRoute(this.map.getRoute(row, col));
    }

    /**
     *Attempts to claim the route at the specified location on behalf of the current player.
     *
     * @param row The row of a {@link Track} in the {@link Route} to claim.
     * @param col The column of a {@link Track} in the {@link Route} to claim.
     * @throws RailroadBaronsException
     */
    @Override
    public void claimRoute(int row, int col) throws RailroadBaronsException {
        if (canCurrentPlayerClaimRoute(row, col)) {
            Route route = this.map.getRoute(row, col);
            getCurrentPlayer().claimRoute(route);
            this.map.routeClaimed(route);
        }
    }

    /**
     * Called when the current player ends their turn.
     */
    @Override
    public void endTurn() {
        for (RailroadBaronsObserver o: observers) {
            o.turnEnded(this, getCurrentPlayer());
        }
        myplayers.add(myplayers.poll());
        startAGameWith(this.map, this.deck);


    }

    /**
     * Returns the player whose turn it is.
     * @return The Player that is currently taking a turn.
     */
    @Override
    public Player getCurrentPlayer() {
        return this.myplayers.peek();
    }

    /**
     * Returns all of the players currently playing the game.
     * @return The Players currently playing the game.
     */
    @Override
    public Collection<Player> getPlayers() {
        return players;
    }

    /**
     * Indicates whether or not the game is over. This occurs when no more plays can be made.
     * Reasons include:
        No one player has enough pieces to claim a route.
        No one player has enough cards to claim a route.
        All routes have been claimed.
     * @return True if the game is over, false otherwise.
     */
    @Override
    public boolean gameIsOver() {
        if (this.map.getLengthOfShortestUnclaimedRoute() == 0) {
            return true;
        }
        if (this.deck.numberOfCardsRemaining() == 0) {
            int count = 0;
            for (Player p : myplayers) {
                if (p.canContinuePlaying(this.map.getLengthOfShortestUnclaimedRoute()) == false) {
                    count++;
                }
            }
            return count == myplayers.size();

        }
        return false;
    }

    /**
     * set the westernmost, easternmost, northernmost and southernmost list
     */
    public void setDirectionMost() {
        List<Route> routes = (List<Route>)this.map.getRoutes();
        Station startH = routes.get(0).getOrigin();
        Station endH = routes.get(0).getOrigin();
        Station startV = routes.get(0).getOrigin();
        Station endV = routes.get(0).getOrigin();
        //get one westernmost, easternmost, northernmost and southernmost station
        for (Route route: this.map.getRoutes()) {
            if (route.getOrigin().getCol() < startH.getCol() ||
                    route.getDestination().getCol() < startH.getCol()) {
                startH = route.getOrigin();
            }
            if (route.getOrigin().getRow() < startV.getRow() ||
                    route.getDestination().getRow() < startV.getRow()) {
                startV = route.getOrigin();
            }
            if (route.getDestination().getCol() > endH.getCol() ||
                    route.getOrigin().getCol() > endH.getCol()) {
                endH = route.getDestination();
            }
            if (route.getDestination().getRow() > endV.getRow() ||
                    route.getOrigin().getRow() > endV.getRow()) {
                endV = route.getDestination();
            }

        }
        //put all the station that has the same location as those most location to the list
        for (Route route: this.map.getRoutes()) {
            if (route.getOrigin().getCol() == startH.getCol()) {
                if (!this.westmost.contains(route.getOrigin())) {
                    this.westmost.add(route.getOrigin());
                }
            }

            if (route.getDestination().getCol() == startH.getCol()) {
                if (!this.westmost.contains(route.getDestination())) {
                    this.westmost.add(route.getDestination());
                }
            }

            if (route.getOrigin().getRow() == startV.getRow()) {
                if (!this.northmost.contains(route.getOrigin())) {
                    this.northmost.add(route.getOrigin());
                }
            }
            if (route.getDestination().getRow() == startV.getRow()) {
                if (!this.northmost.contains(route.getDestination())) {
                    this.northmost.add(route.getDestination());
                }
            }

            if (route.getDestination().getCol() == endH.getCol()) {
                if (!this.eastmost.contains(route.getDestination())) {
                    this.eastmost.add(route.getDestination());
                }
            }
            if (route.getOrigin().getCol() == endH.getCol()) {
                if (!this.eastmost.contains(route.getOrigin())) {
                    this.eastmost.add(route.getOrigin());
                }
            }

            if (route.getDestination().getRow() == endV.getRow()) {
                if (!this.southmost.contains(route.getDestination())) {
                    this.southmost.add(route.getDestination());
                }
            }
            if (route.getOrigin().getRow() == endV.getRow()) {
                if (!this.southmost.contains(route.getOrigin())) {
                    this.southmost.add(route.getOrigin());
                }
            }
        }
    }

}