import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HardLevelWinner {
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;
    private int userId;
    private static final String DB_URL = "jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HardLevelWinner(1);
        });
    }

    public HardLevelWinner(int userId) {
        this.userId = userId;
        updateUserScoreToTwenty(userId);
        frame = new JFrame("Hard Level Winner");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        HardLevelWinnerPanel panel = new HardLevelWinnerPanel(userId);
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
}

class HardLevelWinnerPanel extends JPanel {
    private Image backgroundImg;
    private Image birdImg;
    private int birdX = 0;
    private int birdY = 0;
    private int velocityX = 4;
    private int velocityY = 4;
    private final int birdWidth = 68;
    private final int birdHeight = 48;
    private JLabel completedLabel;
    private JLabel allLevelsLabel;
    private JLabel congratsLabel;
    private JButton quitButton;
    private int userId;

    private static final int MARGIN_LEFT = 20;
    private static final int MARGIN_RIGHT = 20;
    private static final int LABEL_WIDTH = HardLevelWinner.boardWidth - (MARGIN_LEFT + MARGIN_RIGHT);

    public HardLevelWinnerPanel(int userId) {
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

        completedLabel = new JLabel("You have completed");
        completedLabel.setFont(new Font("Arial", Font.BOLD, 28));
        completedLabel.setForeground(Color.WHITE);
        completedLabel.setBounds(MARGIN_LEFT, 180, LABEL_WIDTH, 50);
        completedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(completedLabel);

        allLevelsLabel = new JLabel("all levels!");
        allLevelsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        allLevelsLabel.setForeground(Color.WHITE);
        allLevelsLabel.setBounds(MARGIN_LEFT, 240, LABEL_WIDTH, 50);
        allLevelsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(allLevelsLabel);

        congratsLabel = new JLabel("CONGRATULATIONS!");
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        congratsLabel.setForeground(Color.WHITE);
        congratsLabel.setBounds(MARGIN_LEFT, 320, LABEL_WIDTH, 60);
        congratsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(congratsLabel);

        quitButton = createStyledButton("Exit Game", MARGIN_LEFT + 10, 480);
        quitButton.addActionListener(e -> {
            System.exit(0);
        });
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        int buttonWidth = LABEL_WIDTH - 20;
        button.setBounds(x, y, buttonWidth, 60);
        add(button);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, HardLevelWinner.boardWidth, HardLevelWinner.boardHeight, null);
        }
        if (birdImg != null) {
            g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
        }
    }

    public void updateBird() {
        birdX += velocityX;
        birdY += velocityY;

        if (birdX <= 0 || birdX + birdWidth >= HardLevelWinner.boardWidth) {
            velocityX = -velocityX;
            birdX = Math.max(0, Math.min(birdX, HardLevelWinner.boardWidth - birdWidth));
        }

        if (birdY <= 0 || birdY + birdHeight >= HardLevelWinner.boardHeight) {
            velocityY = -velocityY;
            birdY = Math.max(0, Math.min(birdY, HardLevelWinner.boardHeight - birdHeight));
        }
    }
}