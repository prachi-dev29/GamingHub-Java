import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class EasyLevelWinner {
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;
    protected int userId;
    private static final String DB_URL = "jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db";
    protected EasyMediumDatabase database;
    protected int currentScore = 20;

    public void setCurrentScore(int score) {
        this.currentScore = score;
    }

    public EasyLevelWinner(int userId) {
        this.userId = userId;
        updateUserScoreToTwenty(userId);
        database = new EasyMediumDatabase();
        
        frame = new JFrame("Easy Level Winner");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        EasyLevelWinnerPanel panel = new EasyLevelWinnerPanel(this);
        panel.setPreferredSize(new Dimension(boardWidth, boardHeight));

        Timer timer = new Timer(1000/60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.updateBird();
                panel.repaint();
            }
        });
        timer.start();

        NavBar navBar = new NavBar(frame);
        frame.add(navBar, BorderLayout.NORTH);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public void proceedToMediumLevel() {
        database.duplicateEasyToMediumWithZeroScore(userId);
        database.closeConnection();
    }

    private void updateUserScoreToTwenty(int userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE score SET score = 20 WHERE id = (SELECT id FROM score WHERE user_id = ? ORDER BY id DESC LIMIT 1)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                System.out.println("Most recent score set to 20 for user_id: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void duplicateEasyToMediumWithZeroScore(int userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String insertSQL = "INSERT INTO score (user_id, mode, score) SELECT user_id, 'medium', 0 FROM score WHERE user_id = ? AND mode = 'easy' ORDER BY id DESC LIMIT 1";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                System.out.println("Duplicated latest easy row to medium with score 0 for user_id: " + userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EasyLevelWinner(1);
    }
}

class EasyLevelWinnerPanel extends JPanel {
    private Image backgroundImg;
    private Image birdImg;
    private int birdX = 0;
    private int birdY = 0;
    private int velocityX = 2;
    private int velocityY = 2;
    private final int birdWidth = 68;
    private final int birdHeight = 48;
    private JLabel levelLabel;
    private JLabel completeLabel;
    private JLabel scoreLabel;
    private JButton nextLevelButton;
    private JButton quitButton;
    private EasyLevelWinner parent;

    public EasyLevelWinnerPanel(EasyLevelWinner parent) {
        this.parent = parent;
        setLayout(null);

        try {
            backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
            birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
            if (birdImg == null) {
                System.err.println("Error: Could not load bird image");
            }
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }

        levelLabel = new JLabel("LEVEL");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 48));
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setBounds(30, 100, 300, 60);
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(levelLabel);

        completeLabel = new JLabel("COMPLETE!");
        completeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        completeLabel.setForeground(Color.WHITE);
        completeLabel.setBounds(30, 200, 300, 60);
        completeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(completeLabel);

        scoreLabel = new JLabel("Score: " + parent.currentScore + "+");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(30, 300, 300, 40);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLabel);

        nextLevelButton = createStyledButton("Next Level", 50, 400);
        quitButton = createStyledButton("Quit", 50, 500);

        nextLevelButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db")) {
                String sql = "INSERT INTO score (user_id, mode, score) SELECT user_id, 'medium', 0 FROM score WHERE user_id = ? AND mode = 'easy' ORDER BY id DESC LIMIT 1";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, parent.userId);
                    pstmt.executeUpdate();
                    System.out.println("Duplicated last easy score entry to medium with score 0 for user_id: " + parent.userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            currentFrame.dispose();
            new SelectModeFrame(parent.userId, true);
        });

        quitButton.addActionListener(e -> {
            parent.database.closeConnection();
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            currentFrame.dispose();
            new SelectModeFrame(parent.userId, false);
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, EasyLevelWinner.boardWidth, EasyLevelWinner.boardHeight, null);
        }
        if (birdImg != null) {
            g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
        }
    }

    public void updateBird() {
        birdX += velocityX;
        birdY += velocityY;

        if (birdX <= 0 || birdX + birdWidth >= EasyLevelWinner.boardWidth) {
            velocityX = -velocityX;
            birdX = Math.max(0, Math.min(birdX, EasyLevelWinner.boardWidth - birdWidth));
        }

        if (birdY <= 0 || birdY + birdHeight >= EasyLevelWinner.boardHeight) {
            velocityY = -velocityY;
            birdY = Math.max(0, Math.min(birdY, EasyLevelWinner.boardHeight - birdHeight));
        }
    }
}