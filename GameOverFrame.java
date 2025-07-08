import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameOverFrame extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private GameDatabase2048 gameDatabase;
    private int userId;

    public GameOverFrame(int finalScore, Game2048 parentGame, int userId) {
        this.userId = userId;
        gameDatabase = new GameDatabase2048();

        System.out.println("GameOverFrame: userId=" + userId + ", finalScore=" + finalScore);
        gameDatabase.updateLatestScoreForUser(userId, finalScore);

        setTitle("Game Over!");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentGame);
        setResizable(false);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.BLACK);

        NavBar2048 navBar = new NavBar2048();
        mainContainer.add(navBar, BorderLayout.NORTH);

        JPanel backgroundPanel = new JPanel() {
            private Image bg;
            {
                try {
                    bg = ImageIO.read(new File("Background.png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel gameOverLabel = new JLabel("Game Over!");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("Final Score: " + finalScore);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton retryButton = createStyledButton("Retry");
        retryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        retryButton.addActionListener(e -> {
            System.out.println("Retry pressed for userId=" + userId);
            gameDatabase.duplicateLatestScoreWithZero(userId);

            parentGame.dispose();
            dispose();
            new Menu2048(userId).setVisible(true);
        });

        JButton quitButton = createStyledButton("Quit");
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.addActionListener(e -> {
            parentGame.dispose();
            dispose();
        });

        panel.add(gameOverLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(scoreLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(retryButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(quitButton);

        backgroundPanel.add(panel, BorderLayout.CENTER);
        mainContainer.add(backgroundPanel, BorderLayout.CENTER);
        setContentPane(mainContainer);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(200, 40));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 30, 30));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameOverFrame(0, null, 0);
        });
    }
}