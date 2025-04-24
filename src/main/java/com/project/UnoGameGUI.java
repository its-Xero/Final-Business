package com.project;
import com.library.view.*;
import com.library.view.Button;
import com.library.view.Label;
import com.library.view.Panel;
import com.project.Logic.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class UnoGameGUI {

    private Game game;
    private SFrame frame;
    private Panel gameBoardPanel;
    private Panel[] playerPanels;
    private Label[] playerLabels;
    private Label[] playerScoresLabels;
    Panel[] playerCardsPanels;
    private Panel deckPanel;
    private Panel discardPilePanel;
    private Label messageLabel;
    private Button drawCardButton;
    private int numberOfPlayers;
    private List<Player> players;
    private Deck deck;

    public UnoGameGUI() {
        // Initialize game and GUI
        initializeGame();
        initializeGUI();
    }

    private void initializeGame() {
        // Get number of players using OptionPane
        String input = OptionPane.showUnoInputDialog(null, "Enter number of players (1-10):");
        try {
            numberOfPlayers = Integer.parseInt(input);
            if (numberOfPlayers < 1 || numberOfPlayers > 10) {
                OptionPane.showUnoMessageDialog(null, "Invalid number of players. Exiting.");
                System.exit(0);
            }
        } catch (NumberFormatException e) {
            OptionPane.showUnoMessageDialog(null, "Invalid input. Exiting.");
            System.exit(0);
        }

        // Create players
        players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            String playerName = "Player " + (i + 1); // Simple names for now
            players.add(new Player(playerName));
        }
        deck = new Deck();
        game = new Game(players, deck);
    }

    // Add this updated method to your class:
// Replace your initializeGUI() method with this improved version:
    private void initializeGUI() {
        // Create main frame
        frame = new SFrame("Uno Game");
        frame.setLayout(new BorderLayout(10, 10));

        // Create central game board panel
        gameBoardPanel = Panel.createGameBoardPanel();
        gameBoardPanel.setLayout(new GridBagLayout());
        gameBoardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(gameBoardPanel, BorderLayout.CENTER);

        // Create discard pile panel
        discardPilePanel = Panel.createPlayerPanel();
        discardPilePanel.setPreferredSize(new Dimension(100, 140));
        GridBagConstraints gbcDiscard = new GridBagConstraints();
        gbcDiscard.gridx = 1;
        gbcDiscard.gridy = 0;
        gbcDiscard.insets = new Insets(10, 10, 10, 10);
        gameBoardPanel.add(discardPilePanel, gbcDiscard);

        // Create deck panel
        deckPanel = Panel.createPlayerPanel();
        deckPanel.setPreferredSize(new Dimension(100, 140));
        GridBagConstraints gbcDeck = new GridBagConstraints();
        gbcDeck.gridx = 0;
        gbcDeck.gridy = 0;
        gbcDeck.insets = new Insets(10, 10, 10, 10);
        gameBoardPanel.add(deckPanel, gbcDeck);

        // Create draw card button
        drawCardButton = Button.createBlueButton("Draw Card");
        drawCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player currentPlayer = game.getJoueurs().get(game.getIndexDuCurrentJoueur());
                game.piocher(currentPlayer);
                updatePlayerHands();
                updateGameBoard();
                if (!game.endGAME()) {
                    game.NextPlayer();
                    updateCurrentPlayerDisplay();
                }
            }
        });
        GridBagConstraints gbcDrawButton = new GridBagConstraints();
        gbcDrawButton.gridx = 0;
        gbcDrawButton.gridy = 1;
        gbcDrawButton.insets = new Insets(0, 10, 10, 10);
        gameBoardPanel.add(drawCardButton, gbcDrawButton);

        // Create message label
        messageLabel = Label.createTitleLabel("");
        GridBagConstraints gbcMessage = new GridBagConstraints();
        gbcMessage.gridx = 0;
        gbcMessage.gridy = 2;
        gbcMessage.gridwidth = 2;
        gbcMessage.insets = new Insets(10, 10, 10, 10);
        gameBoardPanel.add(messageLabel, gbcMessage);

        // Create panels for player cards with gaps - using a different layout
        JPanel playersPanel = new JPanel();
        // Using GridLayout with horizontal gap of 20 pixels between player panels
        playersPanel.setLayout(new GridLayout(1, numberOfPlayers, 20, 0));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(playersPanel, BorderLayout.SOUTH);

        // Initialize arrays
        playerPanels = new Panel[numberOfPlayers];
        playerLabels = new Label[numberOfPlayers];
        playerScoresLabels = new Label[numberOfPlayers];
        playerCardsPanels = new Panel[numberOfPlayers];

        // Create player panels
        for (int i = 0; i < numberOfPlayers; i++) {
            playerPanels[i] = Panel.createPlayerPanel();
            playerPanels[i].setLayout(new BorderLayout());
            playerPanels[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 1)
            ));

            // Create labels for player name and score with better styling
            playerLabels[i] = Label.createPlayerLabel(players.get(i).getName());
            playerLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
            playerScoresLabels[i] = Label.createScoreLabel("Score: 0");

            Panel topPanel = new Panel();
            topPanel.setOpaque(false);
            topPanel.setLayout(new BorderLayout());
            topPanel.add(playerLabels[i], BorderLayout.WEST);
            topPanel.add(playerScoresLabels[i], BorderLayout.EAST);
            playerPanels[i].add(topPanel, BorderLayout.NORTH);

            // Create scroll pane for cards with improved styling
            IScrollPane scrollPane = new IScrollPane();
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(null);
            scrollPane.setOpaque(true);
            scrollPane.getViewport().setOpaque(true);

            // Create panel for cards
            playerCardsPanels[i] = new Panel();
            playerCardsPanels[i].setOpaque(true);
            playerCardsPanels[i].setBackground(new Color(240, 240, 240, 200));
            // Using FlowLayout with larger horizontal gap (10px) between cards
            playerCardsPanels[i].setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

            scrollPane.setViewportView(playerCardsPanels[i]);
            playerPanels[i].add(scrollPane, BorderLayout.CENTER);

            // Add player panel to the players container
            playersPanel.add(playerPanels[i]);
        }

        // Initialize game
        game.distribuerCartesDebut();
        game.tirageFirst();

        // Update UI
        updateGameBoard();
        updatePlayerHands();
        updateCurrentPlayerDisplay();

        // Set frame size and make visible
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Update this method to make cards look better and interact properly:
    private void updatePlayerHands() {
        for (int i = 0; i < numberOfPlayers; i++) {
            // Store reference to the panel before clearing it
            Panel currentPanel = playerCardsPanels[i];

            // Clear panel with proper synchronization to avoid visual glitches
            SwingUtilities.invokeLater(() -> {
                currentPanel.removeAll();
            });

            Player player = game.getJoueurs().get(i);

            // Calculate an appropriate width based on cards - prevent layout issues
            int panelWidth = Math.max(400, player.getMain().size() * 85);
            currentPanel.setPreferredSize(new Dimension(panelWidth, 130));

            for (final Carte card : player.getMain()) {
                String cardValue = getCardValueString(card);
                Color cardColor = getCardColor(card);

                // Create card panel - use JPanel for more predictable rendering
                final JPanel cardPanel = new JPanel();
                cardPanel.setLayout(new BorderLayout());
                cardPanel.setPreferredSize(new Dimension(70, 110));
                cardPanel.setBackground(cardColor);
                cardPanel.setOpaque(true); // Ensure color is visible

                // Use a simple single border to reduce animation glitches
                boolean isCurrentPlayerCard = (player == game.getJoueurs().get(game.getIndexDuCurrentJoueur()));
                if (isCurrentPlayerCard) {
                    cardPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                } else {
                    cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                }

                // Add card value text
                JLabel valueLabel = new JLabel(cardValue, JLabel.CENTER);
                valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
                valueLabel.setForeground(Color.WHITE);
                valueLabel.setHorizontalAlignment(JLabel.CENTER);
                cardPanel.add(valueLabel, BorderLayout.CENTER);

                // Simplified mouse handling to prevent animation bugs
                final Player currentPlayer = player;
                cardPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Only process click if it's this player's turn
                        if (currentPlayer == game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
                            playCardDirectly(currentPlayer, card);
                        } else {
                            messageLabel.setText("It's not your turn!");
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (currentPlayer == game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
                            cardPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                            // No position change to avoid animation glitches
                            frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (currentPlayer == game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
                            cardPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                        } else {
                            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        }
                        // Reset cursor
                        frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                });

                currentPanel.add(cardPanel);
            }

            // Use SwingUtilities to properly update UI
            SwingUtilities.invokeLater(() -> {
                currentPanel.revalidate();
                currentPanel.repaint();
            });
        }

        // Final UI refresh
        SwingUtilities.invokeLater(() -> {
            frame.revalidate();
            frame.repaint();
        });
    }


    private void playCardDirectly(Player player, Carte card) {
        System.out.println("DEBUG: Attempting to play card: " + card.getCouleur() + card.getValeur());

        // Check if it's this player's turn
        if (player != game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
            OptionPane.showUnoMessageDialog(frame, "It's not your turn!");
            return;
        }

        // Check if the card is playable
        if (!game.isPlayable(card, game.getCartesSurTable().get(0))) {
            OptionPane.showUnoMessageDialog(frame, "You cannot play this card!");
            return;
        }

        // Handle wild card color selection
        if (card.getCouleur() == 'n' || card.getValeur().equals("wild") || card.getValeur().equals("wildfour")) {
            String[] options = {"Red", "Blue", "Yellow", "Green"};
            int choice = OptionPane.showOptionDialog(frame, "Choose a color:", "Wild Card",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            char newColor;
            switch (choice) {
                case 0: newColor = 'r'; break;
                case 1: newColor = 'b'; break;
                case 2: newColor = 'j'; break;
                case 3: newColor = 'v'; break;
                default: newColor = 'r'; break; // Default to red if dialog is closed
            }

            // Set the wild card's color
            try {
                // Try to set the color directly
                if (card instanceof CarteSpeciale) {
                    ((CarteSpeciale) card).setCouleur(newColor);
                } else {
                    // Fallback using reflection if direct method not available
                    card.getClass().getMethod("setCouleur", char.class).invoke(card, newColor);
                }
                System.out.println("DEBUG: Set wild card color to: " + newColor);
            } catch (Exception e) {
                System.err.println("DEBUG: Could not set wild card color: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Show animation or effect when card is played
        messageLabel.setText(player.getName() + " played " + getColorName(card.getCouleur()) + " " + card.getValeur());

        // Remove card from player's hand
        player.getMain().remove(card);

        // Add card to discard pile
        game.getCartesSurTable().add(0, card);

        // Update UI before applying special effects
        updateGameBoard();
        updatePlayerHands();

        // Check for win condition
        if (player.getMain().isEmpty()) {
            OptionPane.showUnoMessageDialog(frame, player.getName() + " wins!");
            System.exit(0);
            return;
        }

        // Now handle special card effects with explicit output for debugging
        boolean specialCardEffectApplied = false;

        // Capture current player index before any effects
        int currentPlayerIndex = game.getIndexDuCurrentJoueur();
        System.out.println("DEBUG: Current player before effect: " + currentPlayerIndex);

        // Apply special effects based on card value
        if (card.getValeur().equals("skip")) {
            System.out.println("DEBUG: Applying SKIP effect");
            messageLabel.setText("Skipped next player's turn!");
            game.NextPlayer(); // Skip the next player's turn
            specialCardEffectApplied = true;
        }
        else if (card.getValeur().equals("reverse")) {
            System.out.println("DEBUG: Applying REVERSE effect");
            messageLabel.setText("Direction reversed!");

            // Try to call reverseDirection if it exists
            try {
                java.lang.reflect.Method reverseMethod = game.getClass().getMethod("reverseDirection");
                reverseMethod.invoke(game);
            } catch (Exception e) {
                System.err.println("DEBUG: reverseDirection method not found: " + e.getMessage());
                // Fallback for 2-player games: acts like skip
                if (numberOfPlayers <= 2) {
                    game.NextPlayer();
                } else {
                    // Manually implement reversal for >2 players if method doesn't exist
                    // This is a placeholder - ideally game class should handle this
                    System.out.println("DEBUG: Manual reverse implementation needed");
                }
            }
            specialCardEffectApplied = true;
        }
        else if (card.getValeur().equals("draw2")) {
            System.out.println("DEBUG: Applying DRAW2 effect");
            // Get next player
            int nextPlayerIndex = (game.getIndexDuCurrentJoueur() + 1) % numberOfPlayers;
            Player nextPlayer = game.getJoueurs().get(nextPlayerIndex);

            messageLabel.setText(nextPlayer.getName() + " draws 2 cards and loses turn!");

            // Force them to draw 2 cards
            for (int i = 0; i < 2; i++) {
                game.piocher(nextPlayer);
            }

            // Skip their turn (move to the player after them)
            game.NextPlayer();
            specialCardEffectApplied = true;
        }
        else if (card.getValeur().equals("wildfour")) {
            System.out.println("DEBUG: Applying WILDFOUR effect");
            // Get next player
            int nextPlayerIndex = (game.getIndexDuCurrentJoueur() + 1) % numberOfPlayers;
            Player nextPlayer = game.getJoueurs().get(nextPlayerIndex);

            messageLabel.setText(nextPlayer.getName() + " draws 4 cards and loses turn!");

            // Force them to draw 4 cards
            for (int i = 0; i < 4; i++) {
                game.piocher(nextPlayer);
            }

            // Skip their turn (move to the player after them)
            game.NextPlayer();
            specialCardEffectApplied = true;
        }

        // Go to next player if no special effect was applied
        if (!specialCardEffectApplied) {
            System.out.println("DEBUG: No special effect, moving to next player normally");
            game.NextPlayer();
        }

        int newPlayerIndex = game.getIndexDuCurrentJoueur();
        System.out.println("DEBUG: New current player after effects: " + newPlayerIndex);

        // Update UI again after applying effects
        updatePlayerHands();
        updateCurrentPlayerDisplay();
    }

    private void playCard(Player player, Carte card) {
        // First check if it's this player's turn
        if (player != game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
            OptionPane.showUnoMessageDialog(frame, "It's not your turn!");
            return;
        }

        // Then check if the card is playable
        if (!game.isPlayable(card, game.getCartesSurTable().get(0))) {
            OptionPane.showUnoMessageDialog(frame, "This card cannot be played!");
            return;
        }

        // Now, play the card
        // Remove the card from player's hand
        player.getMain().remove(card);
        // Add the card to the discard pile
        game.getCartesSurTable().add(0, card);

        // Handle special card effects if needed
        handleSpecialCardEffects(card);

        // Update the UI
        updatePlayerHands();
        updateGameBoard();

        // Check if the game is over
        if (player.getMain().isEmpty()) {
            OptionPane.showUnoMessageDialog(frame, player.getName() + " wins!");
            System.exit(0);
        } else {
            // Move to next player
            game.NextPlayer();
            updateCurrentPlayerDisplay();
        }
    }

    private void handleSpecialCardEffects(Carte card) {
        // Skip card
        if (card.getValeur().equals("skip")) {
            game.NextPlayer(); // Skip the next player's turn
        }
        // Reverse card
        else if (card.getValeur().equals("reverse")) {
            // Implement reverse logic if your Game class has this functionality
            // game.reverseDirection();
        }
        // Draw Two card
        else if (card.getValeur().equals("draw2")) {
            int nextPlayerIndex = (game.getIndexDuCurrentJoueur() + 1) % game.getJoueurs().size();
            Player nextPlayer = game.getJoueurs().get(nextPlayerIndex);
            for (int i = 0; i < 2; i++) {
                game.piocher(nextPlayer);
            }
        }
        // Wild Draw Four card
        else if (card.getValeur().equals("wildfour")) {
            int nextPlayerIndex = (game.getIndexDuCurrentJoueur() + 1) % game.getJoueurs().size();
            Player nextPlayer = game.getJoueurs().get(nextPlayerIndex);
            for (int i = 0; i < 4; i++) {
                game.piocher(nextPlayer);
            }
        }
    }

    private String getCardValueString(Carte card) {
        if (card instanceof CarteSpeciale) {
            if (card.getValeur().equals("wild")) {
                return "W";
            } else if (card.getValeur().equals("wildfour")) {
                return "W4";
            }
        }
        return card.getValeur();
    }
    private String getColorName(char colorChar) {
        return switch (colorChar) {
            case 'r' -> "Red";
            case 'b' -> "Blue";
            case 'j' -> "Yellow";
            case 'v' -> "Green";
            default -> "Wild";
        };
    }

    private Color getCardColor(Carte card) {
        return switch (card.getCouleur()) {
            case 'r' -> new Color(211, 47, 47); // Warmer Red
            case 'b' -> new Color(25, 118, 210); // Slightly darker Blue
            case 'j' -> new Color(255, 193, 7); // Amber Yellow - less bright
            case 'v' -> new Color(56, 142, 60); // Forest Green
            case 'n' -> // Wild cards
                    new Color(69, 69, 69); // Darker Gray
            default -> {
                System.out.println("DEBUG: Unknown card color: " + card.getCouleur());
                yield new Color(150, 150, 150); // Gray for unknown
            }
        };
    }


    private GridBagConstraints getPlayerPanelConstraints(int playerIndex, int numberOfPlayers) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1; // All player panels are in the second row (gridy = 1)

        if (numberOfPlayers <= 2) {
            gbc.gridx = playerIndex; // For 1 or 2 players, use gridx 0 and 1
            gbc.gridwidth = 1;
            gbc.weightx = 0.5; // Distribute space evenly
        } else if (numberOfPlayers == 3) {
            if (playerIndex == 0) {
                gbc.gridx = 0;
                gbc.gridwidth = 1;
                gbc.weightx = 0.33;
            } else if (playerIndex == 1) {
                gbc.gridx = 1;
                gbc.gridwidth = 1;
                gbc.weightx = 0.34;
            } else {
                gbc.gridx = 2;
                gbc.gridwidth = 1;
                gbc.weightx = 0.33;
            }
        } else if (numberOfPlayers == 4) {
            gbc.gridx = playerIndex;
            gbc.gridwidth = 1;
            gbc.weightx = 0.25;
        } else if (numberOfPlayers == 5){
            gbc.gridx = playerIndex;
            gbc.gridwidth = 1;
            gbc.weightx = 0.20;
        }
        else { // For 6 players
            gbc.gridx = playerIndex / 2; // 0, 0, 1, 1, 2, 2
            gbc.gridy = 1 + (playerIndex % 2); // 1, 2, 1, 2, 1, 2
            gbc.gridwidth = 1;
            gbc.weightx = 0.33; //adjust
            gbc.weighty = 0.5;
            if(playerIndex % 2 == 0){
                gbc.anchor = GridBagConstraints.NORTH;
            }
            else{
                gbc.anchor = GridBagConstraints.SOUTH;
            }

        }

        gbc.fill = GridBagConstraints.BOTH; // Stretch panels to fill their cells
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding around the panels

        return gbc;
    }

    private void updateGameBoard() {
        // Use SwingUtilities for thread safety
        SwingUtilities.invokeLater(() -> {
            // Update discard pile
            discardPilePanel.removeAll();

            if (!game.getCartesSurTable().isEmpty()) {
                Carte topCard = game.getCartesSurTable().get(0);
                String cardValue = getCardValueString(topCard);
                Color cardColor = getCardColor(topCard);
                System.out.println("DEBUG: Top card: " + cardValue + ", Color: " + topCard.getCouleur());

                // Use JPanel for better rendering
                Panel topCardPanel = new Panel(new BorderLayout());
                topCardPanel.setPreferredSize(new Dimension(90, 130));
                topCardPanel.setBackground(cardColor);
                topCardPanel.setOpaque(true); // Critical for showing color
                topCardPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

                Label topCardLabel = new Label(cardValue, JLabel.CENTER);
                topCardLabel.setFont(new Font("Arial", Font.BOLD, 28));
                topCardLabel.setForeground(Color.WHITE);
                topCardPanel.add(topCardLabel, BorderLayout.CENTER);

                Label discardLabel = new Label("DISCARD", JLabel.CENTER);
                discardLabel.setForeground(Color.WHITE);
                topCardPanel.add(discardLabel, BorderLayout.SOUTH);

                discardPilePanel.add(topCardPanel);
            }

            discardPilePanel.revalidate();
            discardPilePanel.repaint();

            // Update deck panel
            deckPanel.removeAll();

            Panel deckCardPanel = new Panel(new BorderLayout());
            deckCardPanel.setPreferredSize(new Dimension(90, 130));
            deckCardPanel.setBackground(new Color(25, 25, 112)); // Dark blue for deck
            deckCardPanel.setOpaque(true);
            deckCardPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

            Label deckCountLabel = new Label(String.valueOf(game.getJeuDeCartes().getNombreDeCartes()), JLabel.CENTER);
            deckCountLabel.setFont(new Font("Arial", Font.BOLD, 28));
            deckCountLabel.setForeground(Color.WHITE);
            deckCardPanel.add(deckCountLabel, BorderLayout.CENTER);

            Label deckLabel = new Label("DECK", JLabel.CENTER);
            deckLabel.setForeground(Color.WHITE);
            deckCardPanel.add(deckLabel, BorderLayout.SOUTH);

            deckPanel.add(deckCardPanel);
            deckPanel.revalidate();
            deckPanel.repaint();
        });
    }


    private void handleCardClick(Carte card, Player player) {
        // Check if it's the player's turn
        if (player != game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
            OptionPane.showUnoMessageDialog(frame, "It's not your turn!");
            return;
        }

        // Check if the card is playable
        if (!game.isPlayable(card, game.getCartesSurTable().get(0))) {
            OptionPane.showUnoMessageDialog(frame, "You cannot play this card!");
            return;
        }

        // Handle wild card color selection
        if (card instanceof CarteSpeciale) {
            if (card.getValeur().equals("wild") || card.getValeur().equals("wildfour")) {
                String[] options = {"Red", "Blue", "Yellow", "Green"};
                int choice = JOptionPane.showOptionDialog(frame, "Choose a color:", "Wild Card",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                char newColor = switch (choice) {
                    case 0 -> 'r';
                    case 1 -> 'b';
                    case 2 -> 'j';
                    case 3 -> 'v';
                    default -> 'r';
                };

                // Set the wild card's color
                try {
                    // Try to use reflection to set color if there's no direct method
                    card.getClass().getMethod("setCouleur", char.class).invoke(card, newColor);
                } catch (Exception e) {
                    System.err.println("Could not set wild card color: " + e.getMessage());
                }
            }
        }

        // Remove card from player's hand
        player.getMain().remove(card);

        // Add card to discard pile
        game.getCartesSurTable().add(0, card);

        // Apply special card effects
        applySpecialCardEffects(card);

        // Update UI
        updateGameBoard();
        updatePlayerHands();

        // Check for win condition
        if (player.getMain().isEmpty()) {
            OptionPane.showUnoMessageDialog(frame, player.getName() + " wins!");
            System.exit(0);
            return;
        }

        // Move to next player (if not skipped by a special card)
        if (!card.getValeur().equals("skip") && !card.getValeur().equals("reverse")) {
            game.NextPlayer();
        }

        updateCurrentPlayerDisplay();
    }

    private void debugGameState() {
        System.out.println("===== GAME STATE DEBUG =====");
        System.out.println("Current player index: " + game.getIndexDuCurrentJoueur());
        System.out.println("Current player: " + game.getJoueurs().get(game.getIndexDuCurrentJoueur()).getName());
        System.out.println("Top card: " + game.getCartesSurTable().get(0).getValeur() +
                " " + game.getCartesSurTable().get(0).getCouleur());
        System.out.println("Number of cards in deck: " + game.getJeuDeCartes().getNombreDeCartes());
        System.out.println("===========================");
    }

    private void applySpecialCardEffects(Carte card) {
        System.out.println("Applying special effect for card: " + card.getValeur());

        // Skip card
        if (card.getValeur().equals("skip")) {
            messageLabel.setText("Skip next player's turn!");
            game.NextPlayer(); // Skip to the player after next
        }

        // Reverse card (for more than 2 players)
        else if (card.getValeur().equals("reverse")) {
            messageLabel.setText("Direction reversed!");
            if (numberOfPlayers > 2) {
                // For games with more than 2 players, check if reverseDirection method exists
                try {
                    // Try to call the reverseDirection method if it exists
                    java.lang.reflect.Method reverseMethod = game.getClass().getMethod("reverseDirection");
                    reverseMethod.invoke(game);
                } catch (Exception e) {
                    // If the method doesn't exist, we'll implement a basic version here
                    System.err.println("reverseDirection method not found: " + e.getMessage());
                    // Reverse direction logic would go here if needed
                    // For now, just skip the next player's turn as a fallback
                    game.NextPlayer();
                }
            } else {
                // For 2 player games, reverse acts like skip
                game.NextPlayer();
            }
        }

        // Draw Two card
        else if (card.getValeur().equals("draw2")) {
            messageLabel.setText("Next player draws 2 cards!");
            // Get next player
            int nextPlayerIndex = (game.getIndexDuCurrentJoueur() + 1) % numberOfPlayers;
            Player nextPlayer = game.getJoueurs().get(nextPlayerIndex);

            // Debug output
            System.out.println("Next player " + nextPlayer.getName() + " draws 2 cards");

            // Force them to draw 2 cards
            for (int i = 0; i < 2; i++) {
                game.piocher(nextPlayer);
            }

            // Skip their turn
            game.NextPlayer();
        }

        // Wild Draw Four
        else if (card.getValeur().equals("wildfour")) {
            messageLabel.setText("Next player draws 4 cards!");
            // Get next player
            int nextPlayerIndex = (game.getIndexDuCurrentJoueur() + 1) % numberOfPlayers;
            Player nextPlayer = game.getJoueurs().get(nextPlayerIndex);

            // Debug output
            System.out.println("Next player " + nextPlayer.getName() + " draws 4 cards");

            // Force them to draw 4 cards
            for (int i = 0; i < 4; i++) {
                game.piocher(nextPlayer);
            }

            // Skip their turn
            game.NextPlayer();
        }
        // For normal cards, the NextPlayer() call will be handled in playCardDirectly
    }


    private void updateCurrentPlayerDisplay() {
        int currentPlayerIndex = game.getIndexDuCurrentJoueur();

        for (int i = 0; i < numberOfPlayers; i++) {
            if (i == currentPlayerIndex) {
                // Highlight current player's panel with glow effect
                playerPanels[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 5, 5, 5),
                        BorderFactory.createLineBorder(Color.YELLOW, 3)
                ));
                playerLabels[i].setForeground(new Color(255, 160, 0)); // Bright orange
                playerLabels[i].setText("➤ " + players.get(i).getName() + " (Your Turn)");

                // Add a subtle animation or highlight
                playerPanels[i].setBackground(new Color(240, 240, 200));
            } else {
                // Reset other player panels
                playerPanels[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 5, 5, 5),
                        BorderFactory.createLineBorder(new Color(100, 100, 100), 1)
                ));
                playerLabels[i].setForeground(Color.BLACK);
                playerLabels[i].setText(players.get(i).getName());
                playerPanels[i].setBackground(Panel.createPlayerPanel().getBackground());
            }
        }

        // Update message to indicate current player
        messageLabel.setText(players.get(currentPlayerIndex).getName() + "'s turn");
    }


    private void displayWinner() {
        String winnerName = game.getJoueurs().get(game.getIndexDuCurrentJoueur()).getName();
        OptionPane.showUnoMessageDialog(frame, winnerName + " wins the game!");
        System.exit(0);
    }

    private int calculateScore(Player player) {
        int score = 0;
        for (Carte card : player.getMain()) {
            if (card instanceof CarteNormale) {
                score += Integer.parseInt(card.getValeur());
            } else if (card instanceof CarteAction) {
                score += 20;
            } else if (card instanceof CarteSpeciale) {
                score += 50;
            }
        }
        return score;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(UnoGameGUI::new);
    }
}

