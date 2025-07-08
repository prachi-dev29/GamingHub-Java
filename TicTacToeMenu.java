import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public class TicTacToeMenu {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private JFrame frame;
    private int playerOId;
    private int playerXId;
    private String playerOName;
    private String playerXName;

    public TicTacToeMenu() {
        this("Player O", "Player X", 1, 2);
    }

    public TicTacToeMenu(String playerOName, String playerXName, int playerOId, int playerXId) {
        this.playerOName = playerOName;
        this.playerXName = playerXName;
        this.playerOId = playerOId;
        this.playerXId = playerXId;
        frame = new JFrame("Tic-Tac-Toe Menu");
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

        JLabel titleLabel = createStyledLabel("Tic Tac Toe", 40);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 0, 50, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(titleLabel, gbc);

        JButton startButton = createStyledButton("Start");
        startButton.addActionListener(e -> {
            frame.dispose();
            new TicTacToeGame(playerOName, playerXName, playerOId, playerXId);
        });
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        centerPanel.add(startButton, gbc);

        JButton backButton = createStyledButton("Back to Hub");
        backButton.addActionListener(e -> {
            frame.dispose();
        });
        gbc.gridy = 2;
        centerPanel.add(backButton, gbc);

        JButton quitButton = createStyledButton("Quit");
        quitButton.addActionListener(e -> frame.dispose());
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
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
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setPreferredSize(new Dimension(200, 50));
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
            new TicTacToeMenu();
        });
    }
}