import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import javax.imageio.ImageIO;
  
public class SimpleFrame extends JFrame{ 
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {  
            new SimpleFrame();
        });
    }

    public SimpleFrame() {
                super("Flappy Bird");

        frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());
        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);
        GamePanel panel = new GamePanel();
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
        frame.setLocationRelativeTo(null);
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
        private JButton loginButton;
        private JButton signupButton;
        private JButton backButton;

        public GamePanel() {
            setLayout(null);

            try {
                birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
                backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
                if (birdImg == null || backgroundImg == null) {
                    System.err.println("Error: Could not load one or more images");
                    JOptionPane.showMessageDialog(this, "Error loading game images. Please check if all image files exist.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (IOException e) {
                System.err.println("Error loading images: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading game images: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            JLabel title = new JLabel("Welcome to Flappy Bird");
            title.setFont(new Font("Arial", Font.BOLD, 28));
            title.setForeground(Color.WHITE);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setBounds(30, 40, 300, 40);
            add(title);

            loginButton = createStyledButton("Login", 50, 120);
            signupButton = createStyledButton("Sign Up", 50, 200);
            backButton = createStyledButton("Exit", 50, 280);


            loginButton.addActionListener(e -> {
                frame.dispose();
                new LoginFrame();
            });
            signupButton.addActionListener(e -> {
                frame.dispose(); 
                new SignupFrame(); 
            });
            backButton.addActionListener(e -> {
                frame.dispose(); 
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
}