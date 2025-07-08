import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class TicTacToeGame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private JFrame frame;
    private JButton[] buttons;
    private boolean isPlayerXTurn = true;
    private JLabel statusLabel;
    private JButton resetButton;
    private JButton backButton;
    private String playerXName;
    private String playerOName;
    private int playerXScore = 0;
    private int playerOScore = 0;
    private JLabel scoreLabel;
    private int playerOId;
    private int playerXId;

    public TicTacToeGame(String playerOName, String playerXName, int playerOId, int playerXId) {
        this.playerOName = playerOName;
        this.playerXName = playerXName;
        this.playerOId = playerOId;
        this.playerXId = playerXId;
        
        frame = new JFrame("Tic-Tac-Toe Game");
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

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = createStyledLabel("Tic Tac Toe", 32);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        scoreLabel = createStyledLabel(playerOName + " (O): " + playerOScore + "  |  " + 
                                     playerXName + " (X): " + playerXScore, 16);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(scoreLabel, BorderLayout.CENTER);

        statusLabel = createStyledLabel(playerOName + "'s Turn (O)", 20);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setOpaque(false);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        buttons = new JButton[9];
        for (int i = 0; i < 9; i++) {
            buttons[i] = createGameButton();
            final int index = i;
            buttons[i].addActionListener(e -> handleButtonClick(index));
            boardPanel.add(buttons[i]);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        resetButton = createStyledButton("Reset Game");
        resetButton.addActionListener(e -> resetGame());
        
        backButton = createStyledButton("End Game");
        backButton.addActionListener(e -> {
            frame.dispose();
            new GameOverScreen(playerOName, playerXName, playerOScore, playerXScore, playerOId, playerXId);
        });

        buttonPanel.add(resetButton);
        buttonPanel.add(backButton);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(boardPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    public TicTacToeGame(String playerOName, String playerXName) {
        this(playerOName, playerXName, 1, 2);
    }

    private JButton createGameButton() {
        JButton button = new JButton("");
        button.setFont(new Font("Arial", Font.BOLD, 48));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185, 200));
        button.setPreferredSize(new Dimension(80, 80));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(52, 152, 219, 200));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(41, 128, 185, 200));
                }
            }
        });
        
        return button;
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

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void handleButtonClick(int index) {
        if (buttons[index].getText().equals("")) {
            String symbol = isPlayerXTurn ? "O" : "X";
            buttons[index].setText(symbol);
            buttons[index].setForeground(isPlayerXTurn ? Color.YELLOW : Color.WHITE);
            
            if (checkWin()) {
                String winner = isPlayerXTurn ? playerOName : playerXName;
                if (isPlayerXTurn) playerOScore++; else playerXScore++;
                updateScoreLabel();
                statusLabel.setText(winner + " Wins!");
                disableAllButtons();
            } else if (checkDraw()) {
                statusLabel.setText("It's a Draw!");
            } else {
                isPlayerXTurn = !isPlayerXTurn;
                statusLabel.setText((isPlayerXTurn ? playerOName : playerXName) + 
                                  "'s Turn (" + (isPlayerXTurn ? "O" : "X") + ")");
            }
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < 9; i += 3) {
            if (!buttons[i].getText().equals("") &&
                buttons[i].getText().equals(buttons[i + 1].getText()) &&
                buttons[i].getText().equals(buttons[i + 2].getText())) {
                highlightWinningButtons(i, i + 1, i + 2);
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (!buttons[i].getText().equals("") &&
                buttons[i].getText().equals(buttons[i + 3].getText()) &&
                buttons[i].getText().equals(buttons[i + 6].getText())) {
                highlightWinningButtons(i, i + 3, i + 6);
                return true;
            }
        }

        if (!buttons[0].getText().equals("") &&
            buttons[0].getText().equals(buttons[4].getText()) &&
            buttons[0].getText().equals(buttons[8].getText())) {
            highlightWinningButtons(0, 4, 8);
            return true;
        }

        if (!buttons[2].getText().equals("") &&
            buttons[2].getText().equals(buttons[4].getText()) &&
            buttons[2].getText().equals(buttons[6].getText())) {
            highlightWinningButtons(2, 4, 6);
            return true;
        }

        return false;
    }

    private void highlightWinningButtons(int... indices) {
        for (int index : indices) {
            buttons[index].setBackground(new Color(46, 204, 113, 200));
        }
    }

    private boolean checkDraw() {
        for (JButton button : buttons) {
            if (button.getText().equals("")) {
                return false;
            }
        }
        return true;
    }

    private void disableAllButtons() {
        for (JButton button : buttons) {
            if (button.getText().equals("")) {
                button.setEnabled(false);
            }
        }
    }

    private void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setEnabled(true);
            button.setBackground(new Color(41, 128, 185, 200));
        }
        isPlayerXTurn = true;
        statusLabel.setText(playerOName + "'s Turn (O)");
    }

    private void updateScoreLabel() {
        scoreLabel.setText(playerOName + " (O): " + playerOScore + "  |  " + 
                         playerXName + " (X): " + playerXScore);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TicTacToeGame("Player O", "Player X");
        });
    }
}