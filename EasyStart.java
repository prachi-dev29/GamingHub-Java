import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class EasyStart {
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;
    private JButton startButton;
    private JButton quitButton;
    private JButton backButton;
    private int userId;

    public EasyStart(int userId) {
        this.userId = userId;
        frame = new JFrame("Easy Mode");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        GamePanel panel = new GamePanel(frame, userId);
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

    class GamePanel extends JPanel {
        private int birdX = 0;
        private int birdY = 0;
        private int velocityX = 2;
        private int velocityY = 2;
        private final int birdWidth = 68;
        private final int birdHeight = 48;
        private Image birdImg;
        private Image backgroundImg;
        private int userId;

        public GamePanel(JFrame frame, int userId) {
            this.userId = userId;
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

            JLabel title = new JLabel("Easy Mode");
            title.setFont(new Font("Arial", Font.BOLD, 28));
            title.setForeground(Color.WHITE);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setBounds(30, 40, 300, 40);
            add(title);

            startButton = createStyledButton("Start Game");
            startButton.setBounds(50, 200, 260, 50);
            startButton.addActionListener(e -> {
                frame.dispose();
                SwingUtilities.invokeLater(() -> {
                    JFrame gameFrame = new JFrame("Flappy Bird - Easy Mode");
                    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameFrame.setResizable(false);
                    
                    EasyMode game = new EasyMode(userId);
                    gameFrame.add(game);
                    gameFrame.pack();
                    gameFrame.setLocationRelativeTo(null);
                    gameFrame.setVisible(true);
                });
            });
            add(startButton);

            quitButton = createStyledButton("Quit");
            quitButton.setBounds(50, 280, 260, 50);
            quitButton.addActionListener(e -> {
                frame.dispose();
                new SelectModeFrame(userId, false);
            });
            add(quitButton);

            backButton = createStyledButton("Back");
            backButton.setBounds(50, 360, 260, 50);
            backButton.addActionListener(e -> {
                frame.dispose();
                new SelectModeFrame(userId, false);
            });
            add(backButton);
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

        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(41, 128, 185));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            return button;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EasyStart(1)); // Test with user ID 1
    }
}