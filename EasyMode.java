import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class EasyMode extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private static final int BIRD_X = WIDTH / 3;
    private static final int BIRD_WIDTH = 34;
    private static final int BIRD_HEIGHT = 24;
    private static final int PIPE_WIDTH = 60;
    private static final int PIPE_GAP = 250;
    private static final int PIPE_SPACING = 400;
    private static final int PIPE_SPEED = 2;
    private static final int GRAVITY = 1;
    private static final int JUMP_SPEED = -8;

    private Timer timer;
    private boolean isRunning;
    private int score;
    private int birdY;
    private int velocity;
    private ArrayList<PipePair> pipes;
    private Random random;
    private Image birdImg, backgroundImg, topPipeImg, bottomPipeImg;
    private boolean gameOver;
    private int userId;

    private class PipePair {
        Rectangle2D.Double topPipe;
        Rectangle2D.Double bottomPipe;
        boolean scored;

        PipePair(Rectangle2D.Double top, Rectangle2D.Double bottom) {
            topPipe = top;
            bottomPipe = bottom;
            scored = false;
        }
    }

    public EasyMode(int userId) {
        this.userId = userId;
        setPreferredSize(new Dimension(WIDTH, HEIGHT - 40)); 
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        try {
            birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
            backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
            topPipeImg = ImageIO.read(getClass().getResource("./toppipe.png"));
            bottomPipeImg = ImageIO.read(getClass().getResource("./bottompipe.png"));

            if (birdImg == null || backgroundImg == null || topPipeImg == null || bottomPipeImg == null) {
                System.err.println("Error: Could not load one or more game images");
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

        initGame();
    }

    private void initGame() {
        isRunning = true;
        score = 0;
        birdY = HEIGHT / 2;
        velocity = 0;
        pipes = new ArrayList<>();
        random = new Random();
        gameOver = false;

        addPipe();

        timer = new Timer(1000 / 60, this);
        timer.start();
    }

    private void addPipe() {
        int minHeight = 50;
        int maxHeight = HEIGHT - PIPE_GAP - minHeight;
        if (maxHeight <= minHeight) {
            maxHeight = minHeight + 1;
        }
        int height = minHeight + random.nextInt(maxHeight - minHeight);

        Rectangle2D.Double topPipe = new Rectangle2D.Double(WIDTH, 0, PIPE_WIDTH, height);
        Rectangle2D.Double bottomPipe = new Rectangle2D.Double(WIDTH, height + PIPE_GAP, PIPE_WIDTH,
                HEIGHT - height - PIPE_GAP);
        pipes.add(new PipePair(topPipe, bottomPipe));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImg != null) {
            g2d.drawImage(backgroundImg, 0, 0, WIDTH, HEIGHT - 40, null);
        }

        for (PipePair pair : pipes) {
            if (topPipeImg != null) {
                g2d.drawImage(topPipeImg, (int) pair.topPipe.x, (int) pair.topPipe.y, PIPE_WIDTH,
                        (int) pair.topPipe.height, null);
            }

            if (bottomPipeImg != null) {
                g2d.drawImage(bottomPipeImg, (int) pair.bottomPipe.x, (int) pair.bottomPipe.y, PIPE_WIDTH,
                        (int) pair.bottomPipe.height, null);
            }
        }

        if (birdImg != null) {
            g2d.drawImage(birdImg, BIRD_X, birdY, BIRD_WIDTH, BIRD_HEIGHT, null);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreText = "Score: " + score;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(scoreText);
        g2d.drawString(scoreText, WIDTH - textWidth - 20, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            update();
            repaint();
        }
    }

    private void handleGameOver() {
        gameOver = true;
        isRunning = false;
        timer.stop();

        EasyModeDatabase db = new EasyModeDatabase();
        db.updateScore(userId, score);

        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.dispose();

        new EasyGameOver(score, userId);
    }

    private void update() {
        velocity += GRAVITY;
        birdY += velocity;

        for (int i = pipes.size() - 1; i >= 0; i--) {
            PipePair pair = pipes.get(i);
            pair.topPipe.x -= PIPE_SPEED;
            pair.bottomPipe.x -= PIPE_SPEED;

            if (!pair.scored && pair.topPipe.x + PIPE_WIDTH < BIRD_X) {
                score++;
                pair.scored = true;

                if (score >= 20) {
                    isRunning = false;
                    timer.stop();
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    currentFrame.dispose();
                    new EasyLevelWinner(userId);
                    return;
                }
            }

            if (pair.topPipe.intersects(BIRD_X, birdY, BIRD_WIDTH, BIRD_HEIGHT) ||
                    pair.bottomPipe.intersects(BIRD_X, birdY, BIRD_WIDTH, BIRD_HEIGHT)) {
                handleGameOver();
                return;
            }

            if (pair.topPipe.x + PIPE_WIDTH < 0) {
                pipes.remove(i);
            }
        }

        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).topPipe.x < WIDTH - PIPE_SPACING) {
            addPipe();
        }

        if (birdY + BIRD_HEIGHT >= HEIGHT - 40 || birdY <= 0) {
            handleGameOver();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocity = JUMP_SPEED;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird - Easy Mode");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            EasyMode game = new EasyMode(1);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}