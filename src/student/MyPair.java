package student;

import model.Pair;

import model.Card;
import java.util.Stack;

/**
 * Cards in a Railroad Barons game are dealt to each Player in pairs. This class is used to hold
 * one such pair of cards. Note that, if the deck is empty, one or both cards may have a value of "none."
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyPair implements Pair{

    /**
     * The first card
     */
    private Card first;
    /**
     * The second card
     */
    private Card second;
    /**
     * The stack of card
     */
    private Stack<Card> drawcard;

    /**
     * A pairs of cards which contain the first card and
     * second card.
     */
    public MyPair(Card first, Card second)
    {
        this.first=first;
        this.second=second;
    }

    /**
     * Returns the first card in the pair. Note that, if the game deck is
     * empty, the value of this card may be Card.NONE.
     * @return first card
     */
    @Override
    public Card getFirstCard() {

        return this.first;
    }

    /**
     * Returns the second card in the pair. if the game deck
     * is empty, the value of this card may be Card.NONE.
     * @return second card
     */
    @Override
    public Card getSecondCard() {

        return this.second;
    }
}
