import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class FlappyBirdGameNew extends JPanel implements ActionListener, KeyListener {
    private static final int boardWidth = 360;
    private static final int boardHeight = 640;
    private int score = 0;
    private boolean gameOver = false;
    
    private int birdX = boardWidth / 3;
    private int birdY = boardHeight / 2;
    private int birdWidth = 34;
    private int birdHeight = 24;
    private double velocityY = 0;
    private final double gravity = 1.7;
    private final double jumpSpeed = -11 ;
    
    private final int pipeWidth = 60;
    private final int pipeGap = 350;
    private final int pipeSpacing = 900;
    private final int pipeSpeed = 2;
    private ArrayList<PipePair> pipes;
    private Random random;
    
    private Image birdImg;
    private Image backgroundImg;
    private Image topPipeImg;
    private Image bottomPipeImg;
    
    private Timer gameTimer;
    private Timer pipeTimer;

    private int userId;

    public FlappyBirdGameNew(int userId) {
        this.userId = userId;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
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
        gameOver = false;
        score = 0;
        birdY = boardHeight / 2;
        velocityY = 0;
        pipes = new ArrayList<>();
        random = new Random();

        addPipe();

        gameTimer = new Timer(1000 / 60, this);
        gameTimer.start();

        pipeTimer = new Timer(6000, e -> addPipe());
        pipeTimer.start();
    }

    private void addPipe() {
        int minHeight = 50;
        int maxHeight = boardHeight - pipeGap - minHeight;
        if (maxHeight <= minHeight) {
            maxHeight = minHeight + 1;
        }
        int height = minHeight + random.nextInt(maxHeight - minHeight);

        Rectangle2D.Double topPipe = new Rectangle2D.Double(boardWidth, 0, pipeWidth, height);
        Rectangle2D.Double bottomPipe = new Rectangle2D.Double(boardWidth, height + pipeGap, pipeWidth,
                boardHeight - height - pipeGap);
        pipes.add(new PipePair(topPipe, bottomPipe));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            update();
            repaint();
        }
    }

    private void update() {
        velocityY += gravity;
        birdY += velocityY;

        for (int i = pipes.size() - 1; i >= 0; i--) {
            PipePair pair = pipes.get(i);
            pair.topPipe.x -= pipeSpeed;
            pair.bottomPipe.x -= pipeSpeed;

            if (!pair.scored && pair.topPipe.x + pipeWidth < birdX) {
                score++;
                pair.scored = true;
                
                if (score >= 20) {
                    gameOver = true;
                    gameTimer.stop();
                    pipeTimer.stop();
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    currentFrame.dispose();
                    new MediumLevelWinner(userId);
                    return;
                }
            }

            if (pair.topPipe.intersects(birdX, birdY, birdWidth, birdHeight) ||
                    pair.bottomPipe.intersects(birdX, birdY, birdWidth, birdHeight)) {
                handleGameOver();
                return;
            }

            if (pair.topPipe.x + pipeWidth < 0) {
                pipes.remove(i);
            }
        }

        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).topPipe.x < boardWidth - pipeSpacing) {
            addPipe();
        }

        if (birdY + birdHeight >= boardHeight || birdY <= 0) {
            handleGameOver();
        }
    }

    private void handleGameOver() {
        gameOver = true;
        gameTimer.stop();
        pipeTimer.stop();
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.dispose();
        SwingUtilities.invokeLater(() -> {
            new MediumGameOver(score, userId);
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocityY = jumpSpeed;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImg != null) {
            g2d.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        }

        for (PipePair pair : pipes) {
            if (topPipeImg != null) {
                g2d.drawImage(topPipeImg, (int) pair.topPipe.x, (int) pair.topPipe.y, pipeWidth,
                        (int) pair.topPipe.height, null);
            }
            
            if (bottomPipeImg != null) {
                g2d.drawImage(bottomPipeImg, (int) pair.bottomPipe.x, (int) pair.bottomPipe.y, pipeWidth,
                        (int) pair.bottomPipe.height, null);
            }
        }

        if (birdImg != null) {
            g2d.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.drawString("Score: " + score, 20, 40);
    }

    private class PipePair {
        Rectangle2D.Double topPipe;
        Rectangle2D.Double bottomPipe;
        boolean scored = false;

        PipePair(Rectangle2D.Double topPipe, Rectangle2D.Double bottomPipe) {
            this.topPipe = topPipe;
            this.bottomPipe = bottomPipe;
        }
    }

    public static void main(String[] args) {
        int userId = 1;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird - Medium Mode");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            FlappyBirdGameNew game = new FlappyBirdGameNew(userId);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            game.requestFocusInWindow();
        });
    }

    public static void startNewGame() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird - Medium Mode");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            FlappyBirdGameNew game = new FlappyBirdGameNew(1);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            game.requestFocusInWindow();
        });
    }

    public void restart() {
        gameOver = false;
        score = 0;
        birdY = boardHeight / 2;
        velocityY = 0;
        pipes.clear();
        
        if (gameTimer != null) {
            gameTimer.restart();
        }
        if (pipeTimer != null) {
            pipeTimer.restart();
        }
        
        addPipe();
        
        requestFocusInWindow();
    }
}