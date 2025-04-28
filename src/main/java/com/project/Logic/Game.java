package com.project.Logic;


import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Game {
    public List<Player> getJoueurs() {
        return joueurs;
    }

    private List<Player> joueurs;
    private Deck jeuDeCartes;
    public List<Carte> cartesSurTable;
    private boolean sensHoraire;
    private int indexDuCurrentJoueur;
    private int turnCount = 0;

    public int getTurnCount() {
        return turnCount;
    }

    public void incrementTurn() {
        turnCount++;
    }


    public List<Carte> getCartesSurTable() {
        return cartesSurTable;
    }

    public Deck getJeuDeCartes() {
        return jeuDeCartes;
    }

    public int getIndexDuCurrentJoueur() {
        return indexDuCurrentJoueur;
    }
    // Constructeur
    public Game(List<Player> joueurs, Deck jeuDeCartes) {
        if (jeuDeCartes == null) {
            throw new IllegalArgumentException("ERREUR , le jeu de cartes ne doit pas etre NULL.");
        }

        this.joueurs = joueurs;
        this.jeuDeCartes = jeuDeCartes;
        this.cartesSurTable = new ArrayList<>(); 
        this.sensHoraire = true; 
        this.indexDuCurrentJoueur = 0; 
    }

    public void piocher(Player joueur) {
        if (!aCarteValide(joueur)) {
            Carte cartePiochee = jeuDeCartes.piocher();
            if (cartePiochee != null) {
                joueur.addCard(cartePiochee);
                System.out.println(joueur.getName() + " Tu viens de piocher la carte : " + cartePiochee);
                incrementTurn();
            } else {
                System.out.println("Le deck est vide !");
            }
        } else {
            System.out.println(joueur.getName() + " Sorry, tu as une carte valide, tu ne peux pas piocher !");
        }
    }

    public void draw(Player joueur, int number) {
        int i;
        for (i = 0; i < number; i++){
            joueur.addCard(jeuDeCartes.piocher());
        }
    }

    public void Pass() {
        Player currentJoueur = joueurs.get(indexDuCurrentJoueur);
        System.out.println(currentJoueur.getName() + " passe son tour.");
        NextPlayer();
    }

    public boolean aCarteValide(Player joueur) {
        List<Carte> mainDuPlayer = joueur.getMain();
        if (cartesSurTable.isEmpty()) {
            return false; // Si aucune carte sur la table, le joueur ne peut pas jouer
        }
        Carte derniereCarteSurLeDeck = cartesSurTable.get(0);
        
        for (Carte carte : mainDuPlayer) {
            if (carte.getCouleur() == derniereCarteSurLeDeck.getCouleur() || carte.getValeur().equals(derniereCarteSurLeDeck.getValeur())) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayable(Carte card, Carte topCard) {
        if (topCard == null) {
            return true;
        }
        // If the card is a wild card, it can be played on anything.
        if (card instanceof CarteSpeciale) {
            return true;
        }
        //if the topcard is a wild card, the card can be played if the user declares the right color
        if(topCard instanceof CarteSpeciale){
            return true;
        }

        // Otherwise, check for matching color or value.
        return card.getCouleur() == topCard.getCouleur() || card.getValeur().equals(topCard.getValeur());
    }


    // Nouvelle méthode pour obtenir la liste des cartes jouables
    public List<Carte> getPlayableCards(Player joueur) {
        List<Carte> playableCards = new ArrayList<>();
        List<Carte> mainDuPlayer = joueur.getMain();
        
        if (cartesSurTable.isEmpty()) {
            return playableCards; // Liste vide si aucune carte sur la table
        }
        
        Carte derniereCarteSurLeDeck = cartesSurTable.get(0);
        
        for (Carte carte : mainDuPlayer) {
            if (carte.getCouleur() == derniereCarteSurLeDeck.getCouleur() || 
                carte.getValeur().equals(derniereCarteSurLeDeck.getValeur())) {
                playableCards.add(carte);
            }
        }
        
        return playableCards;
    }

    public void NextPlayer() {
        if (!cartesSurTable.isEmpty()) {
            System.out.println("Carte actuelle sur la table : " + cartesSurTable.get(0));
        }
        if (sensHoraire) {
            indexDuCurrentJoueur = (indexDuCurrentJoueur + 1) % joueurs.size();
        } else {
            indexDuCurrentJoueur = (indexDuCurrentJoueur - 1 + joueurs.size()) % joueurs.size();
        }
    }

    public void skip() {
        if (sensHoraire) {
            indexDuCurrentJoueur = (indexDuCurrentJoueur + 1) % joueurs.size();
        } else {
            indexDuCurrentJoueur = (indexDuCurrentJoueur - 1 + joueurs.size()) % joueurs.size();
        }
    }

    public String choisirCouleur() {
        Scanner scanner = new Scanner(System.in);
        String couleurChoisie;

        while (true) {
            System.out.println("Choisissez une couleur (rouge, jaune, vert, bleu) : ");
            couleurChoisie = scanner.nextLine();
            if (couleurChoisie.equals("rouge") || couleurChoisie.equals("jaune") || 
                couleurChoisie.equals("vert") || couleurChoisie.equals("bleu")) {
                break; 
            } else {
                System.out.println("Couleur invalide. Veuillez choisir entre rouge, jaune, vert ou bleu.");
            }
        }
        return couleurChoisie;
    }

    public void ReactToCard(Carte carteVisible) {
        if (carteVisible instanceof CarteAction) {
            String action = carteVisible.getValeur(); 
            switch (action) {
                case "Inverser":
                    sensHoraire = !sensHoraire;
                    System.out.println("Le sens du jeu a été inversé !");
                    NextPlayer();
                    break;
                case "+2":
                    Player nextJoueur = joueurs.get((indexDuCurrentJoueur + (sensHoraire ? 1 : -1) + joueurs.size()) % joueurs.size());
                    nextJoueur.addCard(jeuDeCartes.piocher());
                    nextJoueur.addCard(jeuDeCartes.piocher());
                    System.out.println(nextJoueur.getName() + " a pioché 2 cartes !");
                    NextPlayer();
                    break;
                case "Passer":
                    System.out.println("Le joueur suivant perd son tour !");
                    NextPlayer();
                    Pass();
                    break;
                default:
                    System.out.println("Cette carte d'action n'a pas d'effet spécial.");
                    break;
            }
        } else if (carteVisible instanceof CarteSpeciale) {
            String valeur = carteVisible.getValeur();
            switch (valeur) {
                case "wild":
                    String nouvelleCouleur = choisirCouleur();
                    System.out.println("La couleur a été changée en : " + nouvelleCouleur);
                    NextPlayer();
                    break;
                case "wildfour":
                    String chosenColor = choisirCouleur();
                    System.out.println("Le joueur a choisi la couleur : " + chosenColor);
                    Player joueurSuivant = joueurs.get((indexDuCurrentJoueur + (sensHoraire ? 1 : -1) + joueurs.size()) % joueurs.size());
                    for (int i = 0; i < 4; i++) {
                        joueurSuivant.addCard(jeuDeCartes.piocher());
                    }
                    System.out.println(joueurSuivant.getName() + " a pioché 4 cartes et perd son tour !");
                    NextPlayer();
                    Pass();
                    break;
                default:
                    System.out.println("Cette carte spéciale n'a pas d'effet spécial.");
                    break;
            }
        } else if (carteVisible instanceof CarteNormale) {
            System.out.println("Cette carte est une carte normale et n'a pas d'effet spécial.");
        } else {
            System.out.println("Type de carte inconnu.");
        }
    }

    public void distribuerCartesDebut() {
        for (Player joueur : joueurs) {
            for (int j = 0; j < 7; j++) {
                Carte carte = jeuDeCartes.piocher();
                joueur.addCard(carte);
            }
        }
    }

    public void tirageFirst() {
        Carte carte1 = jeuDeCartes.piocher();
        while (carteNotNormale(carte1)) {
            jeuDeCartes.remettreDansDeck(carte1);
            jeuDeCartes.melanger();
            carte1 = jeuDeCartes.piocher();
        }
        cartesSurTable.add(carte1);
        System.out.println("La première carte visible sur la table est : " + carte1);
    }

    public boolean carteNotNormale(Carte carte) {
        if (carte instanceof CarteAction) {
            String action = carte.getValeur();
            return action.equals("+2") || action.equals("Inverser") || action.equals("Passer");
        } else if (carte instanceof CarteSpeciale) {
            String valeur = carte.getValeur();
            return valeur.equals("wild") || valeur.equals("wildfour");
        }
        return false; 
    }

    public boolean endGAME() {
        for (Player joueur : joueurs) {
            if (joueur.getMain().isEmpty()) {
                System.out.println(joueur.getName() + " a gagné ! Félicitations !");
                return true;
            }
        }
        return false;
    }

    public void startGame() {
        distribuerCartesDebut();
        tirageFirst();
        while (!endGAME()) {
            Player currentPlayer = joueurs.get(indexDuCurrentJoueur);
            System.out.println("C'est au tour de " + currentPlayer.getName());
            
            // Afficher les cartes du joueur
            System.out.println("Vos cartes : " + currentPlayer.getMain());
    
            if (!aCarteValide(currentPlayer)) {
                System.out.println("Vous ne pouvez pas jouer, vous devez piocher une carte.");
                piocher(currentPlayer);
                incrementTurn();
            } else {
                // Correction de la condition qui utilisait incorrectement carteNotNormale
                jouerCarte(currentPlayer);
                incrementTurn();
            }
            
            NextPlayer();
        }
    }
    
    public void jouerCarte(Player joueur) {
        Scanner scanner = new Scanner(System.in);
        List<Carte> mainDuJoueur = joueur.getMain();
        List<Carte> cartesJouables = getPlayableCards(joueur);
        Carte cardToPlay = joueur.getCurrentCardToPlay();
        
        if (cartesJouables.isEmpty()) {
            System.out.println("Vous n'avez aucune carte jouable. Vous devez piocher.");
            piocher(joueur);
            return;
        }

        // Afficher toutes les cartes du joueur
        System.out.println("Vos cartes :");
        for (int i = 0; i < mainDuJoueur.size(); i++) {
            Carte carte = mainDuJoueur.get(i);
            String status = cartesJouables.contains(carte) ? "(JOUABLE)" : "(non jouable)";
            System.out.println(i + ": " + carte + " " + status);
        }

        boolean carteJouee = false;

        while (!carteJouee) {
            System.out.println("Entrez le numéro de la carte que vous voulez jouer ou -1 pour piocher : ");
            try {
                int choix = scanner.nextInt();
                
                if (choix == -1) {
                    piocher(joueur);
                    return;
                }
                
                // Vérifier que le choix est valide
                if (choix >= 0 && choix < mainDuJoueur.size()) {
                    Carte carteChoisie = mainDuJoueur.get(choix);
                    
                    // Vérifier si la carte choisie est jouable
                    if (cartesJouables.contains(carteChoisie)) {
                        // Jouer la carte
                        Carte ancienneCarteDessus = cartesSurTable.get(0);
                        cartesSurTable.add(0, carteChoisie); // Ajouter au début de la liste
                        joueur.getMain().remove(carteChoisie);
                        System.out.println(joueur.getName() + " a joué la carte : " + carteChoisie);
                        
                        // Réagir aux effets spéciaux de la carte
                        ReactToCard(carteChoisie);
                        
                        carteJouee = true; // Une carte a été jouée, sortir de la boucle
                    } else {
                        System.out.println("Cette carte ne peut pas être jouée ! Veuillez choisir une carte jouable.");
                    }
                } else {
                    System.out.println("Choix invalide ! Veuillez entrer un numéro entre 0 et " + (mainDuJoueur.size() - 1) + " ou -1 pour piocher.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Veuillez entrer un nombre valide !");
                scanner.nextLine(); // Vider le buffer
            }
        }
    }

    public void reverseDirection() {
        this.sensHoraire = !this.sensHoraire;
    }
}