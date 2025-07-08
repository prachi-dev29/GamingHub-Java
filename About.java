import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class About extends JFrame {
    private Image backgroundImg;
    private Image birdImg;
    private int birdX = 0;
    private int birdY = 0;
    private int velocityX = 2;
    private int velocityY = 2;
    private final int birdWidth = 68;
    private final int birdHeight = 48;

    public About() {
        setTitle("About");
        setSize(360, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(this);
        mainContainer.add(navBar, BorderLayout.NORTH);

        AboutPanel aboutPanel = new AboutPanel();
        aboutPanel.setPreferredSize(new Dimension(360, 600));
        mainContainer.add(aboutPanel, BorderLayout.CENTER);

        Timer timer = new Timer(1000/60, e -> {
            aboutPanel.updateBird();
            aboutPanel.repaint();
        });
        timer.start();

        add(mainContainer);
        setVisible(true);
    }

    class AboutPanel extends JPanel {
        private JLabel titleLabel;
        private JLabel instructionsLabel;
        private JButton backButton;

        public AboutPanel() {
            setLayout(null);
            setOpaque(false);

            try {
                backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
                birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
            } catch (IOException e) {
                System.err.println("Error loading images: " + e.getMessage());
            }

            titleLabel = new JLabel("HOW TO PLAY") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    super.paintComponent(g2d);
                }
            };
            titleLabel.setForeground(new Color(255, 215, 0));
            titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
            titleLabel.setBounds(30, 30, 300, 50);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(titleLabel);

            String instructions = "<html><div style='text-align: center; background: rgba(0, 0, 0, 0.5); padding: 20px; border-radius: 15px;'>" +
                "<div style='margin-bottom: 20px;'>" +
                "<span style='color: #FFD700; font-size: 20px;'>1. Controls</span><br>" +
                "Press <span style='color: #FFD700; font-weight: bold;'>SPACE</span> to make the bird fly<br><br>" +
                "<span style='color: #FFD700; font-size: 20px;'>2. Objective</span><br>" +
                "Avoid the pipes and don't hit the ground<br><br>" +
                "<span style='color: #FFD700; font-size: 20px;'>3. Game Modes</span><br>" +
                "<span style='color: #4CAF50; font-weight: bold;'>EASY</span>: Slow bird speed<br>" +
                "<span style='color: #FFA500; font-weight: bold;'>MEDIUM</span>: Medium bird speed<br>" +
                "<span style='color: #FF0000; font-weight: bold;'>HARD</span>: Fast bird speed<br><br>" +
                "<span style='color: #FFD700; font-size: 20px;'>4. Scoring</span><br>" +
                "Each pipe passed gives you 1 point</div></html>";

            instructionsLabel = new JLabel(instructions);
            instructionsLabel.setForeground(Color.WHITE);
            instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            instructionsLabel.setBounds(20, 100, 320, 400);
            add(instructionsLabel);

            backButton = new JButton("BACK TO MENU") {
                @Override
                protected void paintComponent(Graphics g) {
                    if (getModel().isPressed()) {
                        g.setColor(new Color(31, 97, 141));
                    } else if (getModel().isRollover()) {
                        g.setColor(new Color(52, 152, 219));
                    } else {
                        g.setColor(new Color(41, 128, 185));
                    }
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    super.paintComponent(g);
                }
            };
            backButton.setBounds(50, 520, 260, 50);
            backButton.setForeground(Color.WHITE);
            backButton.setFont(new Font("Arial", Font.BOLD, 18));
            backButton.setFocusPainted(false);
            backButton.setBorderPainted(false);
            backButton.setContentAreaFilled(false);
            backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            backButton.addActionListener(e -> {
                dispose();
                new SimpleFrame();
            });
            add(backButton);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (backgroundImg != null) {
                g2d.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
            }
            if (birdImg != null) {
                g2d.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
            }
        }

        public void updateBird() {
            birdX += velocityX;
            birdY += velocityY;

            if (birdX <= 0 || birdX + birdWidth >= getWidth()) {
                velocityX = -velocityX;
                birdX = Math.max(0, Math.min(birdX, getWidth() - birdWidth));
            }

            if (birdY <= 0 || birdY + birdHeight >= getHeight()) {
                velocityY = -velocityY;
                birdY = Math.max(0, Math.min(birdY, getHeight() - birdHeight));
            }
        }
    }
}