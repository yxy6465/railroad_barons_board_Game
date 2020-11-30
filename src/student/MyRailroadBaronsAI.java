package student;

import model.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Lonely Version that play with 3 AI players
 * Class for a Railroad Barons game. The main entry point into the model for the entire game.
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyRailroadBaronsAI implements RailroadBarons {

    /** observer list that contains RailroadBaronsObserver */
    private List<RailroadBaronsObserver> observers = new LinkedList<>();
    /** railroadMap */
    private RailroadMap map;
    /** deck contains cards */
    private Deck deck;
    /** queue of players */
    private Queue<Player> players = new LinkedList<>();
    /** queue of AI players */
    private Queue<MyPlayerAI> myplayersAI = new LinkedList<>();

    /** the list contains all the westernMost station */
    private List<Station> westmost = new LinkedList<>();
    /** the list contains all the easternMost station */
    private List<Station> eastmost = new LinkedList<>();
    /** the list contains all the northernMost station */
    private List<Station> northmost = new LinkedList<>();
    /** the list contains all the southernMost station */
    private List<Station> southmost = new LinkedList<>();

    /** you */
    private MyPlayer player1;
    /** true when this is your turn*/
    private boolean turn = true;
    /**
     * create 3 AI players and myself, and add them to the queue
     */
    public MyRailroadBaronsAI() {
        this.player1 = new MyPlayer(Baron.RED, this);
        MyPlayerAI player2 = new MyPlayerAI(Baron.GREEN, this);
        MyPlayerAI player4 = new MyPlayerAI(Baron.BLUE, this);
        MyPlayerAI player3 = new MyPlayerAI(Baron.YELLOW, this);
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);
        myplayersAI.add(player2);
        myplayersAI.add(player3);
        myplayersAI.add(player4);


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
        player1.reset(this.deck.drawACard(), this.deck.drawACard(), this.deck.drawACard(),
                this.deck.drawACard());
        player1.setGraph(westmost, eastmost, northmost, southmost);
        for (MyPlayerAI p: myplayersAI) {
            p.reset(this.deck.drawACard(), this.deck.drawACard(), this.deck.drawACard(),
                    this.deck.drawACard());
            p.setGraph(westmost, eastmost, northmost, southmost);
        }
        player1.startTurn(new MyPair(deck.drawACard(), deck.drawACard()));
        for (RailroadBaronsObserver o: observers) {
            o.turnStarted(this, player1);
        }
    }

    /**
     * Starts a new Railroad Barons game with the specified map and deck of cards. This means that
     * the game should work with any implementation of the Deck interface (not just a specific
     * implementation)! Otherwise, the starting state of the game is the same as a normal game.
     * AI players will play the game automatically.
     *
     * @param map The {@link RailroadMap} on which the game will be played.
     * @param deck The Deck of cards used to play the game. This may be ANY implementation of the
     *             Deck interface, meaning that a valid implementation of the RailroadBarons interface
     *             should use only the Deck interface and not a specific implementation.
     */
    @Override
    public void startAGameWith(RailroadMap map, Deck deck) {
        try {
            if (!gameIsOver()) {
                if (this.turn == true) {
                    this.map = map;
                    this.deck = deck;
                    player1.startTurn(new MyPair(deck.drawACard(), deck.drawACard()));
                    for (RailroadBaronsObserver o : observers) {
                        o.turnStarted(this, player1);
                    }
                }
                else {
                    this.map = map;
                    this.deck = deck;
                    myplayersAI.peek().startTurn(new MyPair(deck.drawACard(), deck.drawACard()));
                    for (RailroadBaronsObserver o : observers) {
                        o.turnStarted(this, myplayersAI.peek());
                    }
                    int shortest = this.map.getLengthOfShortestUnclaimedRoute();
                    List<Route> routes =  (List<Route>) (this.map.getRoutes());
                    Route route = routes.get(0);
                    for (Route r : routes) {
                        if (r.getLength() == shortest && r.getBaron() == Baron.UNCLAIMED) {
                            route = r;
                        }
                    }
                    if (myplayersAI.peek().canClaimRoute(route)) {
                        myplayersAI.peek().claimRoute(route);
                        this.map.routeClaimed(route);
                    }
                    endTurn();
                }
            }
            else {
                Player winner = player1;
                int highestScore = winner.getScore();
                for (Player p : myplayersAI) {
                    if (p.getScore() > highestScore) {
                        winner = p;
                        highestScore = p.getScore();
                    }
                }
                for (RailroadBaronsObserver observer : observers) {
                    observer.gameOver(this, winner);
                }
            }
        }catch (RailroadBaronsException e) {
            e.printStackTrace();
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
        if (this.turn == true) {
            return this.player1.canClaimRoute(this.map.getRoute(row, col));
        }
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
        if (turn == true) {
            if (canCurrentPlayerClaimRoute(row, col)) {
                Route route = this.map.getRoute(row, col);
                player1.claimRoute(route);
                this.map.routeClaimed(route);
            }
        }
    }

    /**
     * Called when the current player ends their turn.
     */
    @Override
    public void endTurn() {
        if (this.turn == true) {
            for (RailroadBaronsObserver o: observers) {
                o.turnEnded(this, player1);
            }
            this.turn = false;
            startAGameWith(this.map, this.deck);
        }
        else {
            if (getCurrentPlayer().getBaron() == Baron.BLUE) {
                for (RailroadBaronsObserver o: observers) {
                    o.turnEnded(this, getCurrentPlayer());
                }
                this.turn = true;
            }
            else {
                for (RailroadBaronsObserver o: observers) {
                    o.turnEnded(this, getCurrentPlayer());
                }
            }
            myplayersAI.add(myplayersAI.poll());
            startAGameWith(this.map, this.deck);
        }
    }

    /**
     * Returns the player whose turn it is.
     * @return The Player that is currently taking a turn.
     */
    @Override
    public Player getCurrentPlayer() {
        if (this.turn == true) {
            return this.player1;
        }
        return this.myplayersAI.peek();
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
            for (Player p : myplayersAI) {
                if (p.canContinuePlaying(this.map.getLengthOfShortestUnclaimedRoute()) == false) {
                    count++;
                }
            }
            int check = 0;
            if (this.player1.canContinuePlaying(this.map.getLengthOfShortestUnclaimedRoute()) == false) {
                check = 1;
            }
            return count + check == myplayersAI.size() + 1;

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