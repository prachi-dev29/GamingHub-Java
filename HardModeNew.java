import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class HardModeNew extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;
    private boolean gameOver = false;
    private double score = 0;
    private int userId;
    private JFrame frame;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;
    int velocityY = 0;
    int gravity = 1;

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    int pipeGap = 250;
    int pipeSpacing = 1400;
    int pipeSpeed = -5;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer pipeTimer;

    public HardModeNew(int userId, JFrame frame) {
        this.userId = userId;
        this.frame = frame;
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

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        pipeTimer = new Timer(pipeSpacing, e -> {
            if (!gameOver) {
                addPipe();
            }
        });
        pipeTimer.start();
    }

    private void addPipe() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = pipeGap;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImg != null) {
            g2d.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        }

        for (Pipe pipe : pipes) {
            g2d.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        if (birdImg != null) {
            g2d.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreText = "Score: " + String.valueOf((int) score);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(scoreText);
        g2d.drawString(scoreText, boardWidth - textWidth - 20, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            update();
            repaint();
        }
    }

    private void handleGameOver() {
        gameOver = true;
        gameLoop.stop();
        pipeTimer.stop();

        HardModeDatabase db = new HardModeDatabase();
        db.updateScore(userId, (int) score);
        db.closeConnection();

        if (frame != null) {
            frame.dispose();
        }

        if (score >= 20) {
            SwingUtilities.invokeLater(() -> {
                new HardLevelWinner(userId);
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                new HardGameOver((int)score, userId);
            });
        }
    }

    private void update() {
        velocityY += gravity;
        birdY += velocityY;
        birdY = Math.max(birdY, 0);

        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);
            pipe.x += pipeSpeed;

            if (i % 2 == 0 && !pipe.passed && birdX > pipe.x + pipe.width) {
                score += 1;
                pipe.passed = true;
                
                if (score >= 20) {
                    gameOver = true;
                    gameLoop.stop();
                    pipeTimer.stop();
                    
                    HardModeDatabase db = new HardModeDatabase();
                    db.updateScore(userId, (int) score);
                    db.closeConnection();
                    
                    if (frame != null) {
                        frame.dispose();
                    }
                    
                    SwingUtilities.invokeLater(() -> {
                        new HardLevelWinner(userId);
                    });
                    return;
                }
            }

            if (birdX < pipe.x + pipe.width &&
                birdX + birdWidth > pipe.x &&
                birdY < pipe.y + pipe.height &&
                birdY + birdHeight > pipe.y) {
                if (!gameOver) {
                    handleGameOver();
                }
                return;
            }
        }

        pipes.removeIf(pipe -> pipe.x + pipe.width < 0);

        if (pipes.isEmpty() || pipes.get(pipes.size() - 1).x < boardWidth - pipeSpacing) {
            addPipe();
        }

        if (birdY + birdHeight >= boardHeight || birdY <= 0) {
            if (!gameOver) {
                handleGameOver();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocityY = -9;
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
            JFrame frame = new JFrame("Flappy Bird - Hard Mode");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            HardModeNew game = new HardModeNew(1, frame);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}