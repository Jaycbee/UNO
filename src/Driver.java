import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) throws Exception {

        int wins = 0, losses = 0;
        Scanner userInput = new Scanner(System.in);  

        // Get save file input
        System.out.print("Please enter save file: ");
        File saveFile =  new File(userInput.nextLine());
  
        // Get file from player
        Scanner fileInput = new Scanner(saveFile);
        
        // get old records from save...
        if (!(saveFile.length() == 0)) {
            wins = Integer.valueOf(fileInput.nextLine().split("-")[1]);
            losses = Integer.valueOf(fileInput.nextLine().split("-")[1]);
        } fileInput.close();

        //Start game loop
     
        while (true) {
            // Start game
            System.out.println("Starting Game...");
            int winner = UNO.start();

            // Announce winner
            System.out.format("player %s has won!\n", winner);

            if (winner == 0) {
                wins += 1;
            } else {
                losses += 1;
            }

            // Ask player if they want to play again
            System.out.println("Play Again (N/Y)?: ");
            if (userInput.nextLine().matches("N")) {break;}
        }


        // save and end program
        FileWriter output = new FileWriter(saveFile);
        output.write(String.format("Wins-%s\nLosses-%s",  wins, losses));
        output.flush(); output.close();
        userInput.close();
    }
}
