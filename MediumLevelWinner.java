import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MediumLevelWinner {
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;
    private int userId;
    private static final String DB_URL = "jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db";

    public MediumLevelWinner(int userId) {
        this.userId = userId;
        updateUserScoreToTwenty(userId);
        frame = new JFrame("Medium Level Winner");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        MediumLevelWinnerPanel panel = new MediumLevelWinnerPanel(userId);
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

    private void updateUserScoreToTwenty(int userId) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE score SET score = 20 WHERE id = (SELECT id FROM score WHERE user_id = ? AND mode = 'medium' ORDER BY id DESC LIMIT 1)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                System.out.println("Most recent medium score set to 20 for user_id: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MediumLevelWinner(1);
    }
}

class MediumLevelWinnerPanel extends JPanel {
    private Image backgroundImg;
    private Image birdImg;
    private int birdX = 0;
    private int birdY = 0;
    private int velocityX = 3;
    private int velocityY = 3;
    private final int birdWidth = 68;
    private final int birdHeight = 48;
    private JLabel levelLabel;
    private JLabel completeLabel;
    private JLabel scoreLabel;
    private JButton nextLevelButton;
    private JButton quitButton;
    private int userId;

    public MediumLevelWinnerPanel(int userId) {
        this.userId = userId;
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
        completeLabel.setBounds(30, 180, 300, 60);
        completeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(completeLabel);

        scoreLabel = new JLabel("Score: 20+");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(30, 260, 300, 40);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLabel);

        nextLevelButton = createStyledButton("Next Level", 50, 340);
        quitButton = createStyledButton("Quit", 50, 420);

        nextLevelButton.addActionListener(e -> {
            MediumHardDatabase db = new MediumHardDatabase();
            db.duplicateMediumToHardWithZeroScore(userId);
            db.closeConnection();

            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            currentFrame.dispose();
            new SelectModeFrame(userId, true);
        });

        quitButton.addActionListener(e -> {
            System.exit(0);
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
            g.drawImage(backgroundImg, 0, 0, MediumLevelWinner.boardWidth, MediumLevelWinner.boardHeight, null);
        }
        if (birdImg != null) {
            g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
        }
    }

    public void updateBird() {
        birdX += velocityX;
        birdY += velocityY;

        if (birdX <= 0 || birdX + birdWidth >= MediumLevelWinner.boardWidth) {
            velocityX = -velocityX;
            birdX = Math.max(0, Math.min(birdX, MediumLevelWinner.boardWidth - birdWidth));
        }

        if (birdY <= 0 || birdY + birdHeight >= MediumLevelWinner.boardHeight) {
            velocityY = -velocityY;
            birdY = Math.max(0, Math.min(birdY, MediumLevelWinner.boardHeight - birdHeight));
        }
    }
}