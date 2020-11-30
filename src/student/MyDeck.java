package student;

import model.Card;
import model.Deck;

import java.util.Collections;
import java.util.Stack;

/**
 * Class for a deck of cards used in a Railroad Barons game. The default deck has 20 of each
 * type of playable card.
 *
 * @author Lyan Ye, Joey Zhen
 */
public class MyDeck implements Deck {

    /** stack that contains the card can be drew */
    private Stack<Card> drawcard = new Stack<>();

    /**
     * create the deck
     */
    public MyDeck() {
        for(int x=0;x<20;x++)
        {
            drawcard.push(Card.BLACK);
            drawcard.push(Card.BLUE);
            drawcard.push(Card.GREEN);
            drawcard.push(Card.ORANGE);
            drawcard.push(Card.PINK);
            drawcard.push(Card.RED);
            drawcard.push(Card.WHITE);
            drawcard.push(Card.WILD);
            drawcard.push(Card.YELLOW);
        }
        Collections.shuffle(drawcard);
    }

    /**
     *Resets the deck to its starting state. Restores any cards that were drawn and shuffles the deck.
     */
    @Override
    public void reset() {
        drawcard.removeAllElements();
        for(int x=0;x<20;x++)
        {
            drawcard.push(Card.BLACK);
            drawcard.push(Card.BLUE);
            drawcard.push(Card.GREEN);
            drawcard.push(Card.ORANGE);
            drawcard.push(Card.PINK);
            drawcard.push(Card.RED);
            drawcard.push(Card.WHITE);
            drawcard.push(Card.WILD);
            drawcard.push(Card.YELLOW);
        }
        Collections.shuffle(drawcard);
    }

    /**
     * Draws the next card from the "top" of the deck.
     * @return The next Card, unless the deck is empty, in which case this should return Card.NONE.
     */
    @Override
    public Card drawACard() {
        if (drawcard.size()==0) {
            return Card.NONE;
        } else {
            return drawcard.pop();
        }
    }

    /**
     * Returns the number of cards that have yet to be drawn.
     * @return The number of cards that have yet to be drawn.
     */
    @Override
    public int numberOfCardsRemaining() {
        return this.drawcard.size();
    }
}

