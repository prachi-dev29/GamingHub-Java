import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameOverScreen {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private JFrame frame;
    private GameTicTacToeDatabase database;
    private String playerOName;
    private String playerXName;
    private int playerOScore;
    private int playerXScore;
    private int playerOId;
    private int playerXId;

    public GameOverScreen(String playerOName, String playerXName, int playerOScore, int playerXScore, int playerOId, int playerXId) {
        this.playerOName = playerOName;
        this.playerXName = playerXName;
        this.playerOScore = playerOScore;
        this.playerXScore = playerXScore;
        this.playerOId = playerOId;
        this.playerXId = playerXId;
        
        this.database = new GameTicTacToeDatabase();
        
        frame = new JFrame("Game Over");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel() {
            private Image backgroundImg;

            {
                try {
                    URL imageUrl = getClass().getResource("./backgroundTic-Tac-Toe.png");
                    if (imageUrl != null) {
                        backgroundImg = ImageIO.read(imageUrl);
                    } else {
                        System.err.println("Could not find background image");
                    }
                } catch (IOException e) {
                    System.err.println("Error loading images: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImg != null) {
                    g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());

        NavBarTicTacToe navBar = new NavBarTicTacToe(frame);
        mainPanel.add(navBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = createStyledLabel("Game Over!", 40);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(titleLabel, gbc);

        JLabel resultsLabel = createStyledLabel("Final Results", 24);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        centerPanel.add(resultsLabel, gbc);

        JLabel playerOLabel = createStyledLabel(playerOName + " (O): " + playerOScore, 20);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(playerOLabel, gbc);

        JLabel playerXLabel = createStyledLabel(playerXName + " (X): " + playerXScore, 20);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        centerPanel.add(playerXLabel, gbc);

        String winnerText;
        if (playerOScore > playerXScore) {
            winnerText = playerOName + " Wins!";
        } else if (playerXScore > playerOScore) {
            winnerText = playerXName + " Wins!";
        } else {
            winnerText = "It's a Tie!";
        }
        JLabel winnerLabel = createStyledLabel(winnerText, 28);
        winnerLabel.setForeground(new Color(46, 204, 113));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 40, 0);
        centerPanel.add(winnerLabel, gbc);

        if (database != null) {
            try {
                database.updateLatestScore(playerOId, playerOScore);
                database.updateLatestScore(playerXId, playerXScore);
                database.printAllResults();
            } catch (Exception e) {
                System.err.println("Error updating results: " + e.getMessage());
            }
        }

        JButton retryButton = createStyledButton("Retry");
        retryButton.addActionListener(e -> {
            if (database != null) {
                try {
                    database.duplicateLatestRowsForPlayers(playerOId, playerXId);
                    database.printAllResults();
                } catch (Exception ex) {
                    System.err.println("Error duplicating latest row: " + ex.getMessage());
                }
            }
            frame.dispose();
            new TicTacToeGame(playerOName, playerXName, playerOId, playerXId);
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 20, 0);
        centerPanel.add(retryButton, gbc);

        JButton quitButton = createStyledButton("Quit");
        quitButton.addActionListener(e -> {
            if (database != null) {
                database.close();
            }
            frame.dispose();
        });
        gbc.gridy = 6;
        centerPanel.add(quitButton, gbc);

        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setOpaque(false);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));
        paddedPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.add(paddedPanel, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setPreferredSize(new Dimension(130, 35));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
        });
        
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GameOverScreen("Player O", "Player X", 2, 3, 2, 1);
        });
    }
}