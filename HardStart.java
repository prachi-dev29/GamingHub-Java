import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;

public class HardStart {
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;
    private int userId;

    public HardStart(int userId) {
        this.userId = userId;
        frame = new JFrame("Hard Mode");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        HardStartPanel panel = new HardStartPanel(frame, userId);
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

class HardStartPanel extends JPanel {
    private int birdX = 0;
    private int birdY = 0;
    private int velocityX = 4;
    private int velocityY = 4;
    private final int birdWidth = 68;
    private final int birdHeight = 48;
    private Image birdImg;
    private Image backgroundImg;
    private JLabel modeLabel;
    private JButton startButton;
    private JButton quitButton;
    private JButton backButton;
    private JFrame parentFrame;
    private int userId;

    public HardStartPanel(JFrame parentFrame, int userId) {
        this.parentFrame = parentFrame;
        this.userId = userId;
        setLayout(null);
        
        try {
            backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
            birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
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

        modeLabel = new JLabel("Hard Mode");
        modeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        modeLabel.setForeground(Color.WHITE);
        modeLabel.setBounds(30, 40, 300, 40);
        modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(modeLabel);

        startButton = createStyledButton("Start", 50, 200);
        quitButton = createStyledButton("Quit", 50, 280);
        backButton = createStyledButton("Back", 50, 360);

        startButton.addActionListener(e -> {
            System.out.println("Hard Mode started");
            JFrame frame = new JFrame("Flappy Bird - Hard Mode");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            HardModeNew game = new HardModeNew(userId, frame);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            parentFrame.dispose();
            frame.setVisible(true);
        });

        quitButton.addActionListener(e -> {
            System.exit(0);
        });

        backButton.addActionListener(e -> {
            parentFrame.dispose();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, HardStart.boardWidth, HardStart.boardHeight, null);
        }
        if (birdImg != null) {
            g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
        }
    }

    public void updateBird() {
        birdX += velocityX;
        birdY += velocityY;

        if (birdX <= 0 || birdX + birdWidth >= HardStart.boardWidth) {
            velocityX = -velocityX;
            birdX = Math.max(0, Math.min(birdX, HardStart.boardWidth - birdWidth));
        }

        if (birdY <= 0 || birdY + birdHeight >= HardStart.boardHeight) {
            velocityY = -velocityY;
            birdY = Math.max(0, Math.min(birdY, HardStart.boardHeight - birdHeight));
        }
    }
}