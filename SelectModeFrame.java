import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectModeFrame {
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;
    private SelectModeDatabase database;
    private int userId;

    public SelectModeFrame(int userId, boolean fromWinnerPanel) {
        this.userId = userId;
        database = new SelectModeDatabase();
        frame = new JFrame("Select Mode");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        SelectModePanel panel = new SelectModePanel(frame, database, userId, fromWinnerPanel);
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

    class SelectModePanel extends JPanel {
        private int birdX = 0;
        private int birdY = 0;
        private int velocityX = 2;
        private int velocityY = 2;
        private final int birdWidth = 68;
        private final int birdHeight = 48;
        private Image birdImg;
        private Image backgroundImg;
        private JLabel titleLabel;
        private JButton easyButton;
        private JButton mediumButton;
        private JButton hardButton;
        private JFrame parentFrame;
        private SelectModeDatabase database;
        private int userId;
        private boolean fromWinnerPanel;

        public SelectModePanel(JFrame parentFrame, SelectModeDatabase database, int userId, boolean fromWinnerPanel) {
            this.parentFrame = parentFrame;
            this.database = database;
            this.userId = userId;
            this.fromWinnerPanel = fromWinnerPanel;
            setLayout(null);
            
            try {
                birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
                backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
                if (birdImg == null || backgroundImg == null) {
                    System.err.println("Error: Could not load one or more images");
                }
            } catch (IOException e) {
                System.err.println("Error loading images: " + e.getMessage());
                e.printStackTrace();
            }

            titleLabel = new JLabel("Select Game Mode");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setBounds(30, 40, 300, 40);
            add(titleLabel);

            easyButton = createStyledButton("Easy Mode", 50, 150);
            mediumButton = createStyledButton("Medium Mode", 50, 250);
            hardButton = createStyledButton("Hard Mode", 50, 350);

            easyButton.addActionListener(e -> {
                if (!fromWinnerPanel) {
                    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db")) {
                        String checkSql = "SELECT 1 FROM score WHERE user_id = ? AND mode = 'easy'";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                            checkStmt.setInt(1, userId);
                            ResultSet rs = checkStmt.executeQuery();
                            if (!rs.next()) {
                                String sql = "INSERT INTO score (user_id, mode, score) VALUES (?, 'easy', 0)";
                                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                    pstmt.setInt(1, userId);
                                    pstmt.executeUpdate();
                                    System.out.println("Inserted new score entry for user_id: " + userId + " in easy mode");
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                database.updateGameMode(userId, "easy");
                parentFrame.dispose();
                new EasyStart(userId);
            });

            mediumButton.addActionListener(e -> {
                if (!fromWinnerPanel) {
                    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db")) {
                        String checkSql = "SELECT 1 FROM score WHERE user_id = ? AND mode = 'medium'";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                            checkStmt.setInt(1, userId);
                            ResultSet rs = checkStmt.executeQuery();
                            if (!rs.next()) {
                                String sql = "INSERT INTO score (user_id, mode, score) VALUES (?, 'medium', 0)";
                                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                    pstmt.setInt(1, userId);
                                    pstmt.executeUpdate();
                                    System.out.println("Inserted new score entry for user_id: " + userId + " in medium mode");
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                database.updateGameMode(userId, "medium");
                parentFrame.dispose();
                new MediumStart(userId);
            });

            hardButton.addActionListener(e -> {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db")) {
                    String checkSql = "SELECT 1 FROM score WHERE user_id = ? AND mode = 'hard'";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, userId);
                        ResultSet rs = checkStmt.executeQuery();
                        if (!rs.next()) {
                            String insertSql = "INSERT INTO score (user_id, mode, score) VALUES (?, 'hard', 0)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setInt(1, userId);
                                insertStmt.executeUpdate();
                                System.out.println("Inserted new score entry for user_id: " + userId + " in hard mode");
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                database.updateGameMode(userId, "hard");
                parentFrame.dispose();
                new HardStart(userId);
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
                g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
            }
            if (birdImg != null) {
                g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
            }
        }

        public void updateBird() {
            birdX += velocityX;
            birdY += velocityY;

            if (birdX <= 0 || birdX + birdWidth >= boardWidth) {
                velocityX = -velocityX;
                birdX = Math.max(0, Math.min(birdX, boardWidth - birdWidth));
            }

            if (birdY <= 0 || birdY + birdHeight >= boardHeight) {
                velocityY = -velocityY;
                birdY = Math.max(0, Math.min(birdY, boardHeight - birdHeight));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SelectModeFrame(1, false));
    }
}