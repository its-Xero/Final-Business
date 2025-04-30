package com.project;
import com.library.view.*;
import com.library.view.UButton;
import com.library.view.RLabel;
import com.library.view.SPanel;
import com.library.view.ROptionPane;
import com.project.Logic.*;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class UnoGameGUI {

    private Game game;
    private SFrame frame;
    private SPanel gameBoardPanel;
    private SPanel[] playerPanels;
    private RLabel[] playerLabels;
    SPanel[] playerCardsPanels;
    private SPanel deckPanel;
    private SPanel discardPilePanel;
    private RLabel messageLabel;
    private UButton drawCardButton;
    private int numberOfPlayers;
    private List<Player> players;
    private Deck deck;
    private UButton logButton;
    private JDialog logDialog;
    private JTable logTable;
    private DefaultTableModel logTableModel;
    private final List<String> gameLogs = new ArrayList<>();


    public UnoGameGUI() {
        // Initialize game and GUI
        initializeGame();
        initializeGUI();
    }

    private void initializeGame() {
        // Repeat until a valid number of players is entered
        while (true) {
            String input = ROptionPane.showUnoInputDialog(null, "Enter number of players (2-6):");
            try {
                numberOfPlayers = Integer.parseInt(input);
                if (numberOfPlayers >= 2 && numberOfPlayers <= 6) {
                    break; // Valid input, exit loop
                } else {
                    ROptionPane.showUnoMessageDialog(null, "Please enter a number between 2 and 6.");
                }
            } catch (NumberFormatException e) {
                ROptionPane.showUnoMessageDialog(null, "Invalid input. Please enter a number.");
            }
        }

        // Create players
        players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            String playerName = "Player " + (i + 1); // Simple names for the players
            players.add(new Player(playerName));
        }
        deck = new Deck();
        game = new Game(players, deck);
    }


    private void initializeGUI() {
        // Create main frame
        frame = new SFrame("Uno Game");
        frame.setLayout(new BorderLayout(10, 10));
        frame.setExtendedState(SFrame.MAXIMIZED_BOTH);
        initializeLoggingSystem();

        // Create central game board panel
        gameBoardPanel = SPanel.createGameBoardPanel();
        gameBoardPanel.setLayout(new GridBagLayout());
        gameBoardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(gameBoardPanel, BorderLayout.CENTER);

        // Create discard pile panel
        discardPilePanel = SPanel.createPlayerPanel();
        discardPilePanel.setPreferredSize(new Dimension(100, 140));
        GridBagConstraints gbcDiscard = new GridBagConstraints();
        gbcDiscard.gridx = 1;
        gbcDiscard.gridy = 0;
        gbcDiscard.insets = new Insets(10, 10, 10, 10);
        gameBoardPanel.add(discardPilePanel, gbcDiscard);

        // Create deck panel
        deckPanel = SPanel.createPlayerPanel();
        deckPanel.setPreferredSize(new Dimension(100, 140));
        GridBagConstraints gbcDeck = new GridBagConstraints();
        gbcDeck.gridx = 0;
        gbcDeck.gridy = 0;
        gbcDeck.insets = new Insets(10, 10, 10, 10);
        gameBoardPanel.add(deckPanel, gbcDeck);

        // Create draw card button
        drawCardButton = UButton.createBlueButton("Draw Card");
        drawCardButton.addActionListener(_ -> {
            Player currentPlayer = game.getJoueurs().get(game.getIndexDuCurrentJoueur());
            addLogEntry(currentPlayer.getName() + " draws a card");
            game.piocher(currentPlayer);
            updatePlayerHands();
            updateGameBoard();
            if (!game.endGAME()) {
                game.NextPlayer();
                updateCurrentPlayerDisplay();
            }
        });
        GridBagConstraints gbcDrawButton = new GridBagConstraints();
        gbcDrawButton.gridx = 0;
        gbcDrawButton.gridy = 1;
        gbcDrawButton.insets = new Insets(0, 10, 10, 10);
        gameBoardPanel.add(drawCardButton, gbcDrawButton);

        // Create message label
        messageLabel = RLabel.createTitleLabel("");
        GridBagConstraints gbcMessage = new GridBagConstraints();
        gbcMessage.gridx = 0;
        gbcMessage.gridy = 2;
        gbcMessage.gridwidth = 2;
        gbcMessage.insets = new Insets(10, 10, 10, 10);
        gameBoardPanel.add(messageLabel, gbcMessage);

        // Create panels for player cards with gaps - using a different layout
        SPanel playersPanel = new SPanel();
        // Using GridLayout with horizontal gap of 20 pixels between player panels
        playersPanel.setLayout(new GridLayout(1, numberOfPlayers, 20, 0));
        playersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(playersPanel, BorderLayout.SOUTH);

        // Initialize arrays
        playerPanels = new SPanel[numberOfPlayers];
        playerLabels = new RLabel[numberOfPlayers];

        playerCardsPanels = new SPanel[numberOfPlayers];

        // Create player panels
        for (int i = 0; i < numberOfPlayers; i++) {
            playerPanels[i] = SPanel.createPlayerPanel();
            playerPanels[i].setLayout(new BorderLayout());
            playerPanels[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createLineBorder(new Color(100, 100, 100), 1)
            ));

            // Create labels for player name
            playerLabels[i] = RLabel.createPlayerLabel(players.get(i).getName());
            playerLabels[i].setFont(new Font("Arial", Font.BOLD, 14));


            SPanel topPanel = new SPanel();
            topPanel.setOpaque(false);
            topPanel.setLayout(new BorderLayout());
            topPanel.add(playerLabels[i], BorderLayout.WEST);

            playerPanels[i].add(topPanel, BorderLayout.NORTH);

            // Create scroll pane for cards with improved styling
            IScrollPane scrollPane = new IScrollPane();
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(null);
            scrollPane.setOpaque(true);
            scrollPane.getViewport().setOpaque(true);

            // Create panel for cards
            playerCardsPanels[i] = new SPanel();
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
        initializeLoggingSystem();

        // Set frame size and make visible
        SPanel topPanel = SPanel.createPlayerPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(logButton);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void initializeLoggingSystem() {
        // gameLogs is already initialized, just need to set up the rest
        logTableModel = new DefaultTableModel(new Object[]{"Turn", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logTable = new JTable(logTableModel);
        logTable.setAutoCreateRowSorter(true);
        logTable.setFillsViewportHeight(true);

        logDialog = new JDialog(frame, "Game Log", false);
        logDialog.setSize(500, 400);
        logDialog.setLocationRelativeTo(frame);

        JScrollPane scrollPane = new JScrollPane(logTable);
        logDialog.add(scrollPane);

        logButton = UButton.createBlueButton("Show Log");
        logButton.addActionListener(_ -> logDialog.setVisible(!logDialog.isVisible()));
    }


    private void addLogEntry(String action) {
        int currentTurn = game.getTurnCount(); // You'll need to add turn tracking to your Game class
        String entry = "Turn " + currentTurn + ": " + action;
        gameLogs.add(entry);
        logTableModel.addRow(new Object[]{currentTurn, action});

        // Auto-scroll to the bottom
        logTable.scrollRectToVisible(logTable.getCellRect(logTableModel.getRowCount()-1, 0, true));
    }

    // Update this method to make cards look better and interact properly:
    private void updatePlayerHands() {
        for (int i = 0; i < numberOfPlayers; i++) {
            // Clear the panel completely
            playerCardsPanels[i].removeAll();

            // Get the player
            Player player = game.getJoueurs().get(i);

            // Set an appropriate width for the panel based on number of cards
            int panelWidth = Math.max(400, player.getMain().size() * 85);
            playerCardsPanels[i].setPreferredSize(new Dimension(panelWidth, 130));

            // Add each card to the panel
            for (final Carte card : player.getMain()) {
                String cardValue = getCardValueString(card);
                Color cardColor = getCardColor(card);

                // Create a simple JPanel for the card - avoiding custom components
                final SPanel cardPanel = new SPanel();
                cardPanel.setLayout(new BorderLayout());
                cardPanel.setPreferredSize(new Dimension(70, 110));
                cardPanel.setBackgroundColor(cardColor);
                cardPanel.setOpaque(true); // Make sure this is true!

                // Add a border
                boolean isCurrentPlayerCard = (player == game.getJoueurs().get(game.getIndexDuCurrentJoueur()));
                if (isCurrentPlayerCard) {
                    cardPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                } else {
                    cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                }

                // Add the card value as text
                RLabel valueLabel = new RLabel(cardValue, JLabel.CENTER);
                valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
                valueLabel.setForeground(Color.WHITE);
                valueLabel.setHorizontalAlignment(JLabel.CENTER);
                cardPanel.add(valueLabel, BorderLayout.CENTER);

                // Add click listener
                cardPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            playCardDirectly(player, card);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (player == game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
                            cardPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                            frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (player == game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
                            cardPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                        } else {
                            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        }
                        frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                });

                // Add the card panel to the player's cards panel
                playerCardsPanels[i].add(cardPanel);
            }

            // Make sure to update the UI
            playerCardsPanels[i].revalidate();
            playerCardsPanels[i].repaint();
        }

        // Update the frame
        frame.revalidate();
        frame.repaint();
    }


    private void playCardDirectly(Player player, Carte card) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        System.out.println("DEBUG: Attempting to play card: " + card.getCouleur() + card.getValeur());
        addLogEntry(player.getName() + " plays " + getColorName(card.getCouleur()) + " " + card.getValeur());

        // Check if it's this player's turn
        if (player != game.getJoueurs().get(game.getIndexDuCurrentJoueur())) {
            ROptionPane.showUnoMessageDialog(frame, "It's not your turn!");
            return;
        }

        // Check if the card is playable
        if (!game.isPlayable(card, game.getCartesSurTable().get(0))) {
            ROptionPane.showUnoMessageDialog(frame, "You cannot play this card!");
            return;
        }

        // Handle wild card color selection
        if (card.getCouleur() == 'n' || card.getValeur().equals("wild") || card.getValeur().equals("wildfour")) {
            String[] options = {"Red", "Blue", "Yellow", "Green"};
            int choice = ROptionPane.showOptionDialog(frame, "Choose a color:", "Wild Card",
                    ROptionPane.DEFAULT_OPTION, ROptionPane.QUESTION_MESSAGE, null, options, options[0]);

            char newColor = switch (choice) {
                case 0 -> 'r';
                case 1 -> 'b';
                case 2 -> 'j';
                case 3 -> 'v';
                default -> 'r'; // Default to red if dialog is closed
            };

            // Set the wild card's color

                // Try to set the color directly    
                if (card instanceof CarteSpeciale) {
                    card.setCouleur(newColor);
                } else {
                    // Fallback using reflection if direct method not available
                    card.getClass().getMethod("setCouleur", char.class).invoke(card, newColor);
                }
                System.out.println("DEBUG: Set wild card color to: " + newColor);
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
            ROptionPane.showUnoMessageDialog(frame, player.getName() + " wins!");
            System.exit(0);
            return;
        }

        // Now handle special card effects with explicit output for debugging
        boolean specialCardEffectApplied = false;

        // Capture current player index before any effects
        int currentPlayerIndex = game.getIndexDuCurrentJoueur();
        System.out.println("DEBUG: Current player before effect: " + currentPlayerIndex);

        // Apply special effects based on card value
        switch (card.getValeur()) {
            case "Passer" -> {
                addLogEntry("Skipped next player's turn!");
                System.out.println("DEBUG: Applying SKIP effect");
                messageLabel.setText("Skipped next player's turn!");
                SkipEffect(); // Skip the next player's turn

                specialCardEffectApplied = true;
            }
            case "Inverser" -> {
                addLogEntry("Direction reversed!");
                System.out.println("DEBUG: Applying REVERSE effect");
                messageLabel.setText("Direction reversed!");

                // Fallback for 2-player games: acts like skip
                if (numberOfPlayers <= 2) {
                    game.reverseDirection();
                } else {
                    // Manual implementation for >2 players
                    System.out.println("DEBUG: Manual reverse implementation needed");
                    game.reverseDirection();
                }

                specialCardEffectApplied = true;
            }
            case "+2" -> {
                System.out.println("DEBUG: Applying DRAW2 effect");
                // Get next player
                int nextPlayerIndex = (game.getIndexDuCurrentJoueur() + 1) % numberOfPlayers;
                Player nextPlayer = game.getJoueurs().get(nextPlayerIndex);
                addLogEntry(nextPlayer.getName() + " draws 2 cards!");

                messageLabel.setText(nextPlayer.getName() + " draws 2 cards and loses turn!");

                // Force them to draw 2 cards
                game.draw(nextPlayer, 2);

                // Skip their turn (move to the player after them)
                game.skip();
                game.skip();
                specialCardEffectApplied = true;
            }
            case "wildfour" -> {
                System.out.println("DEBUG: Applying WILDFOUR effect");
                // Get next player
                int nextPlayerIndex = (game.getIndexDuCurrentJoueur() + 1) % numberOfPlayers;
                Player nextPlayer = game.getJoueurs().get(nextPlayerIndex);
                addLogEntry(nextPlayer.getName() + " draws 4 cards!");

                messageLabel.setText(nextPlayer.getName() + " draws 4 cards and loses turn!");

                // Force them to draw 4 cards

                game.draw(nextPlayer, 4);


                // Skip their turn (move to the player after them)
                game.skip();
                specialCardEffectApplied = true;
            }
        }

        // Go to next player if no special effect was applied
        if (!specialCardEffectApplied) {
            System.out.println("DEBUG: No special effect, moving to next player normally");
            game.skip();
        }

        int newPlayerIndex = game.getIndexDuCurrentJoueur();
        System.out.println("DEBUG: New current player after effects: " + newPlayerIndex);

        // Update UI again after applying effects
        updatePlayerHands();
        updateCurrentPlayerDisplay();
        game.incrementTurn();
    }

    public void SkipEffect() {
        // Get the current player index before the skip
        int currentPlayerIndex = game.getIndexDuCurrentJoueur();
        System.out.println("DEBUG: Skip effect - Current player index: " + currentPlayerIndex);

        // Skip the next player's turn by advancing the current player index twice
        // First advancement - skip one player
        game.skip();
        game.skip();

        // Print the new current player for debugging
        int newPlayerIndex = game.getIndexDuCurrentJoueur();
        System.out.println("DEBUG: Skip effect - New player index after skip: " + newPlayerIndex);

        // Update the UI to reflect the new current player
        updateCurrentPlayerDisplay();

        // Update the message to inform players
        messageLabel.setText("Player " + (currentPlayerIndex + 1) + " played Skip! " +
                "Player " + (newPlayerIndex) + "'s turn now.");
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
            case 'n' -> new Color(69, 69, 69); // Darker Gray
            default -> // Gray for unknown
                    new Color(150, 150, 150);
        };
    }


    private void updateGameBoard() {
        // Update discard pile
        discardPilePanel.removeAll();

        if (!game.getCartesSurTable().isEmpty()) {
            Carte topCard = game.getCartesSurTable().get(0);
            String cardValue = getCardValueString(topCard);
            Color cardColor = getCardColor(topCard);


            SPanel topCardPanel = new SPanel(new BorderLayout());
            topCardPanel.setPreferredSize(new Dimension(90, 130));
            topCardPanel.setBackgroundColor(cardColor);
            topCardPanel.setOpaque(true); // This is critical!
            topCardPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

            RLabel topCardLabel = new RLabel(cardValue, JLabel.CENTER);
            topCardLabel.setFont(new Font("Arial", Font.BOLD, 28));
            topCardLabel.setForeground(Color.WHITE);
            topCardPanel.add(topCardLabel, BorderLayout.CENTER);

            RLabel discardLabel = new RLabel("DISCARD", JLabel.CENTER);
            discardLabel.setForeground(Color.WHITE);
            topCardPanel.add(discardLabel, BorderLayout.SOUTH);

            discardPilePanel.add(topCardPanel);
        }

        discardPilePanel.revalidate();
        discardPilePanel.repaint();

        // Update deck panel
        deckPanel.removeAll();

        SPanel deckCardPanel = new SPanel(new BorderLayout());
        deckCardPanel.setPreferredSize(new Dimension(90, 130));
        deckCardPanel.setBackgroundColor(new Color(25, 25, 112)); // Dark blue
        deckCardPanel.setOpaque(true); // This is critical!
        deckCardPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        RLabel deckCountLabel = new RLabel(String.valueOf(game.getJeuDeCartes().getNombreDeCartes()), JLabel.CENTER);
        deckCountLabel.setFont(new Font("Arial", Font.BOLD, 28));
        deckCountLabel.setForeground(Color.WHITE);
        deckCardPanel.add(deckCountLabel, BorderLayout.CENTER);

        RLabel deckLabel = new RLabel("DECK", JLabel.CENTER);
        deckLabel.setForeground(Color.WHITE);
        deckCardPanel.add(deckLabel, BorderLayout.SOUTH);

        deckPanel.add(deckCardPanel);

        deckPanel.revalidate();
        deckPanel.repaint();
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
                playerLabels[i].setText(">>> " + players.get(i).getName() + " (Your Turn)");

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
                playerPanels[i].setBackground(SPanel.createPlayerPanel().getBackground());
            }
        }

        // Update message to indicate current player
        messageLabel.setText(players.get(currentPlayerIndex).getName() + "'s turn");
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(UnoGameGUI::new);
    }
}

