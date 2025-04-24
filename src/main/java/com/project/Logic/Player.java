package com.project.Logic;


import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Player {
    private final String playerName;          
    private final ArrayList<Carte> cardsInHand;   
    private final Random rng;
    private Carte currentCardToPlay;

    // Constructor
    public Player(String playerName) {
        this.playerName = playerName;
        this.cardsInHand = new ArrayList<>(); 
        this.rng = new Random();
    }

    public void setCurrentCardToPlay(Carte card) {
        this.currentCardToPlay = card;
    }
    public Carte getCurrentCardToPlay() {
        return this.currentCardToPlay;
    }

    // Add a card to the player's hand
    public void addCard(Carte card) {
        this.cardsInHand.add(card);
    }

    // Remove a card from the player's hand
    public void playCard(Carte card) {
        if (!this.cardsInHand.remove(card)) {
            System.out.println("Warning: Tried to play a card not in hand.");
        }
    }

    // Show the player's cards
    public void displayHand() {
        System.out.println(playerName + "'s hand:");
        if (cardsInHand.isEmpty()) {
            System.out.println("  (No cards left!)");
        } else {
            for (Carte card : cardsInHand) {
                card.afficher();
            }
        }
    }

    // Draw a card from the deck
    public void drawFromDeck(Deck deck) {
        Carte newCard = deck.piocher();
        if (newCard != null) {
            addCard(newCard);
            System.out.println(playerName + " drew a card: " + newCard);
        } else {
            System.out.println("Deck is empty! " + playerName + " can't draw.");
        }
    }

    // Check if a card can be played
    public boolean hasPlayableCard(Carte topCard) {
        for (Carte card : cardsInHand) 
        {
            if (card.getCouleur() == topCard.getCouleur() || card.getValeur().equals(topCard.getValeur()) || card instanceof CarteSpeciale || (topCard instanceof CarteSpeciale && card.getCouleur() == topCard.getCouleur()))
            { 
                return true;
            }
        }
        return false;
    }

    // Select a card to play (AI logic)
    public Carte selectCardToPlay(Carte topCard) {
        ArrayList<Carte> validCards = new ArrayList<>();
        for (Carte card : cardsInHand) {
            
            if (card.getCouleur() == topCard.getCouleur() || card.getValeur().equals(topCard.getValeur()) || card instanceof CarteSpeciale || (topCard instanceof CarteSpeciale && card.getCouleur() == topCard.getCouleur())) 
            { 
                validCards.add(card);
            }
        }
        if (validCards.isEmpty()) {
            return null;
        }
        return validCards.get(rng.nextInt(validCards.size()));
    }

    public boolean hasWon() {
        return cardsInHand.isEmpty();
    }

    public String getName() {
        return playerName;
    }


    @SuppressWarnings("ConvertToTryWithResources")
    public char choisirCouleur() {
        Scanner scanner = new Scanner(System.in);
        char chosenColor = ' ';
        boolean validChoice = false;

        while (!validChoice) {
            System.out.println(playerName + ", choisissez une couleur : (R)ouge, (V)ert, (B)leu, (J)aune");
            String input = scanner.nextLine().toUpperCase();

            if (input.length() == 1) {
                chosenColor = input.charAt(0);
                if (chosenColor == 'R' || chosenColor == 'V' || chosenColor == 'B' || chosenColor == 'J') {
                    validChoice = true;
                } else {
                    System.out.println("Choix invalide. Veuillez entrer R, V, B ou J.");
                }
            } else {
                System.out.println("Veuillez entrer une seule lettre (R, V, B ou J).");
            }
        }
        scanner.close();
        return chosenColor;
    }

    public ArrayList<Carte> getMain() {
        return cardsInHand;
    }
    
    public static void main(String[] args) {
        Deck deck = new Deck();
        deck.melanger();

        Player player = new Player("islam");

        // Add some cards to the player's hand
        player.addCard(new CarteNormale(5, 'r')); 
        player.addCard(new CarteAction("Passer", 'b')); 
        player.addCard(new CarteSpeciale("wild")); 

        System.out.println("Initial hand:");
        player.displayHand();

        System.out.println("\nExample of drawing card");
        player.drawFromDeck(deck);

        System.out.println("\nUpdated hand after drawing:");
        player.displayHand();

        Carte topCard = new CarteNormale(9, 'v'); 
        System.out.print("\ntop card ");
        topCard.afficher();
        System.out.println();
        if (player.hasPlayableCard(topCard)) {
            System.out.println("Player has playable cards");
            Carte cardToPlay = player.selectCardToPlay(topCard);
            System.out.print("Player chooses to play ");
            cardToPlay.afficher();
            System.out.println();
            player.playCard(cardToPlay);
        } else {
            System.out.println("Player has no playable cards.");
        }

        System.out.println("\nFinal hand:");
        player.displayHand();

       
        System.out.println("\nExample of choosing a color for a wild card:");
        char chosenColor = player.choisirCouleur();
        System.out.println("Chosen color: " + chosenColor);
        
        System.out.println("\nVerification if the player has won");
        if (player.hasWon()) {
            System.out.println("Player has won");
        } else {
            System.out.println("Player has not won yet.");
        }
    }
}