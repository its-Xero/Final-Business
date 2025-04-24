package com.project.Logic;


import java.util.ArrayList;
import java.util.Scanner;

public class Main{
    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Player> players = new ArrayList<>();
        Deck jeuDeCartes = new Deck();
        // Get number of players
        System.out.println("entrer le nombre de joueur");
        int numberOfPlayers = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        
        if (numberOfPlayers < 1 || numberOfPlayers > 10) {
            System.out.println("Nombre de joueurs invalide. Le jeu nécessite 1 à 10 joueurs.");
            scanner.close();
            return;
        }
        
        // Create players
        for (int i = 0; i < numberOfPlayers; i++) {
            System.out.println("Nom du joueur " + (i + 1) + " :");
            String playerName = scanner.nextLine();
            players.add(new Player(playerName));
        }
        
        
        Game game = new Game(players , jeuDeCartes);
        game.startGame();
        
        scanner.close();
    }

}