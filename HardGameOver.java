import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class HardGameOver {
    private final int score;
    private static final int boardWidth = 360;
    private static final int boardHeight = 640;
    private JFrame frame;
    private int userId;

    public HardGameOver(int score, int userId) {
        this.score = score;
        this.userId = userId;
        frame = new JFrame("Game Over - Hard Mode");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db")) {
            String updateSql = "UPDATE score SET score = ? WHERE id = (SELECT id FROM score WHERE user_id = ? AND mode = 'hard' ORDER BY id DESC LIMIT 1)";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, score);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
                System.out.println("Updated latest hard score row for user_id: " + userId + " to score " + score);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        HardGameOverPanel panel = new HardGameOverPanel(score, userId);
        panel.setPreferredSize(new Dimension(boardWidth, boardHeight - 40));
        mainContainer.add(panel, BorderLayout.CENTER);

        Timer timer = new Timer(1000/60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.updateBird();
                panel.repaint();
            }
        });
        timer.start();

        frame.add(mainContainer);
        frame.pack();
        frame.setVisible(true);
    }
}

class HardGameOverPanel extends JPanel {
    private final int score;
    private Image backgroundImg;
    private Image birdImg;
    private JLabel gameLabel;
    private JLabel overLabel;
    private JLabel scoreLabel;
    private JButton retryButton;
    private JButton quitButton;
    private int userId;
    
    private int birdX = 0;
    private int birdY = 0;
    private int velocityX = 4;
    private int velocityY = 4;
    private final int birdWidth = 68;
    private final int birdHeight = 48;

    public HardGameOverPanel(int score, int userId) {
        this.score = score;
        this.userId = userId;
        setLayout(null);
        
        try {
            backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
            birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
            if (birdImg == null || backgroundImg == null) {
                System.err.println("Error: Could not load one or more images");
            }
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }

        gameLabel = new JLabel("GAME");
        gameLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameLabel.setForeground(Color.WHITE);
        gameLabel.setBounds(30, 60, 300, 60);
        gameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(gameLabel);

        overLabel = new JLabel("OVER");
        overLabel.setFont(new Font("Arial", Font.BOLD, 48));
        overLabel.setForeground(Color.WHITE);
        overLabel.setBounds(30, 120, 300, 60);
        overLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(overLabel);

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 36));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(30, 200, 300, 50);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLabel);

        retryButton = createStyledButton("Retry", 50, 280);
        quitButton = createStyledButton("Quit", 50, 360);

        retryButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db")) {
                String duplicateSql = "INSERT INTO score (user_id, mode, score) SELECT user_id, mode, 0 FROM score WHERE user_id = ? AND mode = 'hard' ORDER BY id DESC LIMIT 1";
                try (PreparedStatement pstmt = conn.prepareStatement(duplicateSql)) {
                    pstmt.setInt(1, userId);
                    pstmt.executeUpdate();
                    System.out.println("Duplicated latest hard score row for user_id: " + userId + " with score 0");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            currentFrame.dispose();
            new HardStart(userId);
        });

        quitButton.addActionListener(e -> {
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            currentFrame.dispose();
            new SelectModeFrame(userId, false);
        });
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBounds(x, y, 260, 50);
        add(button);
        return button;
    }

    public void updateBird() {
        birdX += velocityX;
        birdY += velocityY;

        if (birdX <= 0 || birdX + birdWidth >= 360) {
            velocityX = -velocityX;
            birdX = Math.max(0, Math.min(birdX, 360 - birdWidth));
        }

        if (birdY <= 0 || birdY + birdHeight >= 600) {
            velocityY = -velocityY;
            birdY = Math.max(0, Math.min(birdY, 600 - birdHeight));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, 360, 600, null);
        g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
    }
}