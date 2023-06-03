/*
 * | Main class for 1 vs N UNO |
 * 
 * Representation:
 * - All UNO cards are represented as strings as [char Color][char Number][Stack_Number] 
 * - The Color component can be: r = red, g = green, b = blue, y = yellow, or _ = unknown (wild)
 * - The Number component is a single hex number where a = +2, b = reverse, c = skip, d = +4, and e = wild
 * - The Stack component is the number of times that the card has been stacked up until that point. 
 * 
 * - Players are represented as numbers 0-NUM_PLAYERS
 * - Player 0 is always the user and the rest are reserved for CPUs
 * 
 * Specific Rules (Not mentioned at https://www.letsplayuno.com/news/guide/20181213/30092_732567.html):
 * - Each player is given an amount of cards specified by INITIAL_CARDS (see below).
 * - For each turn that a player has, they must play a card in their hand or draw until they can.
 * - A player can only draw if they have no playable cards in their deck. 
 * - A player cannot play more than one card on their turn.
 *
 * 
 * Output:
 * - In the console, the program will output colored ANSI formatted text for cards.
 * - This means that the terminal used to run the program must be able support it.
 * 
 * Code:
 * - The game is split into 2 classes
 * - The DrawDeck class handles the draw deck and provides methods to draw and build the drawdeck
 * - The DiscardDeck class handles the discard/play deck and provides a way to play cards on to it
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import decks.DrawDeck;
import decks.DiscardDeck;

public class UNO {
    
    private final static  int NUM_PLAYERS = 4; 
    private final static  int INITIAL_CARDS = 7;

    private static boolean cw = true;
    private static boolean flag = true;
    private static int last_player = 0;

    private static Scanner scanner =  new Scanner(System.in);  
    private static DrawDeck drawDeck = new DrawDeck();
    private static DiscardDeck discardDeck = new DiscardDeck(drawDeck.getFirst());

    private static Queue<Integer> turns = new LinkedList<Integer>();
    private static LinkedList<String>[] hands = new LinkedList[NUM_PLAYERS];
    
    // Start a round of UNO and return winner
    public static int start() {

        // Initialize player hands by drawing cards from draw deck
        // Add initial player turns to the turn queue
        turns.clear(); turns.add(0);
        
        for (int i = 0; i < NUM_PLAYERS; i++) {
            System.out.format("player %s: ", i);
            hands[i] = new LinkedList<String>();
            drawCard(hands[i], INITIAL_CARDS);
        }   
        
    
        // Output starting card that was drawn from the draw deck
        System.out.format(
            "\nStarting card: %s\n%s\n", 
            expandCard(discardDeck.getLastCard()),
            ("-").repeat(35)
        );

        // Begin single round; stops once player has won
        while (!turns.isEmpty()) {
            
         
            int player = turns.remove();

            
            LinkedList<String> hand = hands[player];

            // Announce player's turn
            System.out.format("player %s's outcome:\n", player);

            // Get previous actions affecting player
            String action = discardDeck.getCardComponent(1);
            int multiplier = Integer.valueOf(
                discardDeck.getCardComponent(2)
            );

            // Replenish turns in the queue with the starting from the current player
            updateTurns(player);
            
            
            if (!flag) {

                // The "flag" prevents the next player from being affected by a previously played action card...
                // A true "flag" means that the last discarded card is an action card and it has been handled.
                // A false "flag" means that an action card has not been handled or the last discarded card is a 0-9 card.

                flag = true;
               
                switch (action) {

                     // handle draw 2 with stacked multiplier
                    case "a" : {
                        drawCard(hand, 2 * multiplier); 
                        discardDeck.reduceStack(); 
                        continue;
                    } 
                    // handle reverse 
                    case "b" : { 
                        cw = !cw; updateTurns(player);
                        System.out.format( "skipped!\n"); 
                        continue;
                    }
                    // handle skip
                    case "c" : System.out.format( "skipped!\n"); continue; 
                    // handle draw 4 with stacked multiplier
                    case "d" : {
                         drawCard(hand, 4 * multiplier); 
                         discardDeck.reduceStack();
                         continue;
                    }
                }

                flag = false;
            } else {
                flag = false;
            }
                
            // draw until card played from CPU or Human Player
            if (player == 0) {while (!discardDeck.play(getPlayerChoice()));}  else {
                while (!discardDeck.play(discardDeck.choose(hand))) {
                    drawCard(hand, 1);
                }
            }

            last_player = player;

            // remove actual played card entry from their hand based on the what was discarded
            hand.removeFirstOccurrence(discardDeck.getLastCard());

            // output discarded card
            System.out.format(
                "player discarded %s\n", 
                expandCard(discardDeck.getLastCard())
            );
     
            // check for winning condition
            if (isEmpty(hands)) {
                break;
            }
        }

      return last_player;
    }


    private static void updateTurns(int player) {
        turns.clear();
        if (cw) {turns.add((player + 1) % (NUM_PLAYERS));} 
         
         else {
            if (player == 0) {turns.add(NUM_PLAYERS - 1);} 
            else {turns.add(player - 1);}
        }
    }

    // Draw card from the drawDeck n times
    // Handle case where drawDeck is empty by repopulating it with unused cards
    private static void drawCard(LinkedList<String> hand, int n) {
        if (drawDeck.getSize() < n) {
            drawDeck.repopulate(hands);
        } 

        drawDeck.transfer(hand, n);
        System.out.format(
            "%s cards given to player\n", n
        );
    }


    // Retrieves player choice; returns existing card string
    // Input validation happens here!
    private static String getPlayerChoice() {
        String selectedCard = "";
    
        // Continuously ask player for their choice
        // Allow player to draw if they can't make a choice based on their hand
        while (true) {  
            int hand_size = hands[0].size();
            boolean can_draw = true; System.out.print("\n");


            // Output player's hand to the console
            // Check if there are any playable cards to consider draw option
            for (int i = 0; i < hand_size; i++) {

                // replace default wild card colors with placeholder
                String card = hands[0].remove(i).replaceFirst ("(\\w)(d|e)", "_$2"); 
                hands[0].add(i, card);
                
                // check if card is able to be played; set can_draw flag
                if (discardDeck.isValid(card)) {can_draw = false;}
                System.out.format("\t%s: %s\n", i, expandCard(card));
            }

            // Output draw option if player has no playable cards 
            if (can_draw) {
                System.out.format(
                    "\t%s: draw a card!\n", hand_size
                );
            }
           
        
            try { // handle input from the player

                 System.out.format(
                    "\nCurrent card: %s\nInput: ", 
                    expandCard(discardDeck.getLastCard())
                );

                int choice = Integer.parseInt(scanner.nextLine()); 

                if ((choice == hand_size) && (can_draw == true)) {
                    drawCard(hands[0], 1); continue;
                } else {
                    selectedCard = hands[0].get(choice);
                    // Allow player to choose color if wild card
                    if (selectedCard.charAt(0) == '_') {
                        System.out.print("Color: ");

                        for (int i = 0; i < drawDeck.colors.length; i++) {
                            System.out.format("|%s: %s|", i,  expandCard(drawDeck.colors[i] + "\0"));
                        }
    
                        int colorChoice = Integer.parseInt(scanner.nextLine()); 
                        selectedCard = selectedCard.replaceFirst (
                            "(\\w)(d|e)", drawDeck.colors[colorChoice] + "$2"
                        );
                        
                        hands[0].remove(choice);
                    }
                }

            } catch (NumberFormatException | IndexOutOfBoundsException  e) {
                System.out.println("Please enter a valid number!"); continue;
            } break;
        }

        return selectedCard;
    }

 
    // Returns wether any hands are empty
    // If true, a player has won.
    private static boolean isEmpty(LinkedList<String>[] lists){
        for (LinkedList<String> list : lists ) {
            if (list.isEmpty()) {return true;};
        }
        return false;
    }

    // Expand card into a human readable format!
    // Uses ANSI escape characters for pretty formatting
    public static String expandCard(String card) {
        String r = "";

        final String UNDERLINE = "\033[4;37m";
        final String CLEAR = "\033[0m";

        final String[] H = {
            "\033[0;31m", //Red
            "\033[0;32m", //Green
            "\033[0;34m", // Blue
            "\033[0;33m", // Yellow
        };


        char color = card.charAt(0); switch (color) {
            case 'r' : r += H[0]   + "Red";    break;
            case 'g' : r += H[1]   + "Green";  break;
            case 'b' : r += H[2]   + "Blue";   break;
            case 'y' : r += H[3]   + "Yellow"; break;
            case '_' : r += H[3]   + "?"; break;
        }
   
        r += CLEAR + " " + UNDERLINE;
        char type = card.charAt(1); switch (type) {
            case 'a' : r +=  "DRAW 2";  break;
            case 'b' : r +=  "REVERSE"; break;
            case 'c' : r +=  "SKIP";    break;
            case 'd' : r +=  "DRAW 4";  break;
            case 'e' : r +=  "WILD";    break;
            default : r += type  ;
        }
        
        return r + CLEAR;
    }

}

