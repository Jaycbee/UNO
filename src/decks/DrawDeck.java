package decks;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class DrawDeck {

    public final  String[] colors = {"r", "g" , "b",  "y"};
    
    private Stack<String> deck;
    private ArrayList<String> blacklist;
 

    // Class initialization
    // Create and shuffle deck
    public DrawDeck () {
        this.deck = new Stack<String>();
        this.blacklist = new ArrayList<String>();
        
        build();
    }

    // Cards are directly added to a player's hand 
    public void transfer (LinkedList<String> hand, int n) {
        for (int i = 0; i < n; i++) {
            hand.add(this.deck.pop());
        }
    }
    
    // Repopulate the deck by creating new cards and excluding currently held ones
    // This way means that there are fewer memory requirements
    public void repopulate (LinkedList<String> hands[]) {
        blacklist.clear();

        for (LinkedList<String> x : hands) {
            blacklist.addAll(0, x);
        }

        build();
    }


    public String getFirst() {return this.deck.pop();}
    public int getSize() {return this.deck.size();}
    

    // Uses hexadecimal representation
    // Create 2 sets of each of the colors 
    // Add 4 sets of wild cards with default random colors
    // Call Shuffle to randomize order
    private void build() {
        for (String c : colors) {
            addCard(c + 0);
    
            for (int i = 1; i < 26 ; i++) {    
                addCard(c + Integer.toHexString(i % 13));
            } 
        }
  
        for (int i = 0; i < 4; i++) {
            String defaultColor = colors[((int) (Math.random() * 4))];

            addCard(defaultColor + "d");
            addCard(defaultColor + "e");
        }

        shuffle(this.deck);
    }


    private void addCard(String card) {
        if (!this.blacklist.contains(card)) {
            this.deck.push(card);
        }
    }


    // Fast method for randomizing stack entries
    // Pick a random card, remove it, and add it to the top
    // The shuffle happens twice (hence the * 2) to improve randomness
    private static <T> void shuffle (Stack <T> stack) {
        final int size = stack.size();
    
        for (int i = 0; i < size * 3; i++) {
            stack.push(
                stack.remove (
                    (int) (Math.random() * size) 
                )
            );
        }
    }
}
