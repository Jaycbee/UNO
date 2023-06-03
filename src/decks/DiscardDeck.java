package decks;

import java.util.LinkedList;




// Class for discard deck; 
// The discard deck efficiently only save in memory the last played card

public class DiscardDeck {
    private String last; // [r|g|b|y][n][stack_num]

    public DiscardDeck (String initial) {
        this.last = initial + 1;
    }

    // play card and update last card; returns true if playable - false otherwise
    // Warning: assumes card is valid in structure and is in actually the player's hand!
    public boolean play (String card) {
        if (!isValid(card)) {return false;}

        char n1 = card.charAt(1);
        char n2 = last.charAt(1);

        if ((n1 == n2) && (n1 == 'a' || n1  == 'd')) {
            int stack = Integer.valueOf(String.valueOf(last.charAt(2)));
            last = card + (stack + 1) ;   
        } else {
            last = card + 1;
        }

        return true;
    }

    // pick a valid card from player hand (CPUs)
    // valid = valid based on last card
    // Finds first valid card and returns it
    public String choose (LinkedList<String> hand) {
        for (String card : hand) {
            if (isValid(card)) {
                return card;
            }
        }
        return "__";
    }

    // Remove the stack number from the recently discarded card
    public void reduceStack () {
        this.last = this.last.substring(0, 2) + 0;
    }

    // Internal validation; Checks if playable
    // Match new card against last card
    public boolean isValid (String card) {
        char c1 = card.charAt(0), n1 = card.charAt(1);
        char n2 = last.charAt(1), c2 =  last.charAt(0);

        return c1 == c2 || n1 == n2|| n1 == 'd' || n1 == 'e';
    }

  
    // Method to retrieve last played with stack number removed
    public String getLastCard() {
        return this.last.substring(0, 2); //chop off stack n
    }
    // Method to retrieve last played component
    public String getCardComponent(int component) {
        return String.valueOf(this.last.charAt(component));
    }

}
