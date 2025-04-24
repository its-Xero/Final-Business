package com.project.Logic;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private final ArrayList<Carte> cartes;

    public Deck() {
        cartes = new ArrayList<>();
        initialiserDeck(); // hna mea lwl intialisation apres mélange
        melanger(); 
    }
    private void initialiserDeck() {
        char[] couleurs = {'r', 'b', 'j', 'v'}; // Couleurs : rouge (r), bleu (b), jaune (j), vert (v)

        // ajouter les cartes normales (numérotées de 0 à 9)
        for (char couleur : couleurs) {
            for (int numero = 0; numero <= 9; numero++) {
                cartes.add(new CarteNormale(numero, couleur)); // Ajoute une carte normale
                if (numero != 0) { // Les cartes numérotées de 1 à 9 ont deux exemplaires
                    cartes.add(new CarteNormale(numero, couleur));
                }
            }
        }

        // ajouter les cartes d'action (+2, Inverser, Passer)
        String[] actions = {"+2", "Inverser", "Passer"};
        for (char couleur : couleurs) {
            for (String action : actions) {
                cartes.add(new CarteAction(action, couleur)); 
                cartes.add(new CarteAction(action, couleur)); 
            }
        }
        // ajouter les cartes (wild et wildfour)
        for (int i = 0; i < 4; i++) {
            cartes.add(new CarteSpeciale("wild")); 
            cartes.add(new CarteSpeciale("wildfour"));
        }

        // Vérification du nombre li nsitha 
        if (cartes.size() != 108) {
            throw new IllegalStateException("ERREUR : Le nombre total de cartes dans le deck est incorrect. Attendu : 108, Trouvé : " + cartes.size());
        }
    }

    public final void melanger() {
        Collections.shuffle(cartes);
    }
    public Carte piocher() {
        if (cartes.isEmpty()) {
            System.out.println("Le deck est vide !");
            return null;
        }
        return cartes.remove(cartes.size() - 1); // Pioche la dernière carte du deck
    }

    public void reinitialiserDeck() {
        cartes.clear(); // vide le deck
        initialiserDeck(); // Réinitialise le deck
        melanger(); 
    }

    public void afficherDeck() {
        for (Carte carte : cartes) {
            System.out.println("la carte est: " + carte);
        }
    }

    public void remettreDansDeck(Carte carte) {
        cartes.add(carte); 
    }

    // obtenir le nombre de cartes dans le deck
    public int getNombreDeCartes() {
        return cartes.size();
    }

    public static void main(String[] args) {
        Deck deck = new Deck();

        System.out.println("Deck initial :");
        deck.afficherDeck(); 

        System.out.println("\nNombre total de cartes : " + deck.getNombreDeCartes());

        System.out.println("\nPioche de 110 cartes :");
        for (int i = 0; i < 110; i++) {
            Carte cartePiochee = deck.piocher(); 
            if (cartePiochee != null) {
                System.out.println("Carte piochée : " + cartePiochee); 
            }
        }

        System.out.println("\nDeck apres pioche :");
        deck.afficherDeck();

        System.out.println("\nReinitialisation du deck...");
        deck.reinitialiserDeck();

        System.out.println("\nDeck apres reinitialisation :");
        deck.afficherDeck();
    }
}