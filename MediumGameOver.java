import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class MediumGameOver {
    private final int score;
    private final int userId;
    private static final int boardWidth = 360;
    private static final int boardHeight = 640;
    private JFrame frame;

    public MediumGameOver(int score, int userId) {
        this.score = score;
        this.userId = userId;
        frame = new JFrame("Game Over - Medium Mode");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        MediumGameOverPanel panel = new MediumGameOverPanel(score, userId);
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

        MediumModeDatabase db = new MediumModeDatabase();
        db.updateScore(userId, score);
    }

    public static void main(String[] args) {
        int userId = 1;
        new MediumGameOver(10, userId);
    }
}

class MediumGameOverPanel extends JPanel {
    private final int score;
    private final int userId;
    private Image backgroundImg;
    private Image birdImg;
    private JLabel gameLabel;
    private JLabel overLabel;
    private JLabel scoreLabel;
    private JButton retryButton;
    private JButton quitButton;
    
    private int birdX = 0;
    private int birdY = 0;
    private int velocityX = 3;
    private int velocityY = 3;
    private final int birdWidth = 68;
    private final int birdHeight = 48;

    public MediumGameOverPanel(int score, int userId) {
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
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (currentFrame != null) {
                currentFrame.dispose();
            }
            MediumModeDatabase db = new MediumModeDatabase();
            db.duplicateLatestScoreWithZero(userId);
            new MediumStart(userId);
        });

        quitButton.addActionListener(e -> {
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (currentFrame != null) {
                currentFrame.dispose();
            }
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