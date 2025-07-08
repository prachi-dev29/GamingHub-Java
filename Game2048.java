import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.Random;
import javax.swing.*;

public class Game2048 extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private static final int GRID_SIZE = 4;
    private static final int TILE_SIZE = 65;
    private static final int TILE_MARGIN = 10;
    private int[][] board;
    private int score;
    private Random random;
    private JLabel scoreLabel;
    private boolean gameOver;
    private Color[] tileColors;
    private int userId;
    private GameDatabase2048 gameDatabase;
    private static final String DB_PATH = "C:\\Users\\prach\\OneDrive\\Desktop\\GamingHub2\\GamingHub\\database\\2048.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;

    public Game2048(int userId) {
        this.userId = userId;
        this.gameDatabase = new GameDatabase2048();
        setTitle("2048 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);

        board = new int[GRID_SIZE][GRID_SIZE];
        random = new Random();
        score = 0;
        gameOver = false;

        tileColors = new Color[] {
            new Color(205, 193, 180),
            new Color(238, 228, 218),
            new Color(237, 224, 200),
            new Color(242, 177, 121),
            new Color(245, 149, 99),
            new Color(246, 124, 95),
            new Color(246, 94, 59),
            new Color(237, 207, 114),
            new Color(237, 204, 97),
            new Color(237, 200, 80),
            new Color(237, 197, 63),
            new Color(237, 194, 46)
        };

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.BLACK);

        NavBar2048 navBar = new NavBar2048();
        mainContainer.add(navBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("2048");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(scoreLabel, BorderLayout.CENTER);

        JPanel gamePanelWrapper = new JPanel(new BorderLayout());
        gamePanelWrapper.setOpaque(false);
        gamePanelWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));

                drawBoard(g2d);
            }
        };
        gamePanel.setOpaque(false);
        gamePanel.setPreferredSize(new Dimension(
            GRID_SIZE * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN,
            GRID_SIZE * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN
        ));

        gamePanelWrapper.add(gamePanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanelWrapper, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    boolean moved = false;
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moved = moveLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            moved = moveRight();
                            break;
                        case KeyEvent.VK_UP:
                            moved = moveUp();
                            break;
                        case KeyEvent.VK_DOWN:
                            moved = moveDown();
                            break;
                    }
                    if (moved) {
                        addNewTile();
                        gamePanel.repaint();
                        scoreLabel.setText("Score: " + score);
                        if (isGameOver()) {
                            gameOver = true;
                            SwingUtilities.invokeLater(() -> {
                                dispose();
                                GameOverFrame gameOverFrame = new GameOverFrame(score, Game2048.this, userId);
                                gameOverFrame.setVisible(true);
                            });
                        }
                    }
                }
            }
        });

        mainContainer.add(mainPanel, BorderLayout.CENTER);
        setContentPane(mainContainer);
        setFocusable(true);

        addNewTile();
        addNewTile();
    }

    private void drawBoard(Graphics2D g) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                drawTile(g, row, col);
            }
        }
    }

    private void drawTile(Graphics2D g, int row, int col) {
        int value = board[row][col];
        int x = col * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN;
        int y = row * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN;

        int colorIndex = value == 0 ? 0 : (int)(Math.log(value) / Math.log(2));
        colorIndex = Math.min(colorIndex, tileColors.length - 1);
        g.setColor(tileColors[colorIndex]);

        g.fill(new RoundRectangle2D.Float(x, y, TILE_SIZE, TILE_SIZE, 10, 10));

        if (value != 0) {
            g.setColor(value <= 4 ? new Color(119, 110, 101) : Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, value < 100 ? 24 : value < 1000 ? 20 : 16));
            String s = String.valueOf(value);
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (TILE_SIZE - fm.stringWidth(s)) / 2;
            int textY = y + (fm.getAscent() + (TILE_SIZE - (fm.getAscent() + fm.getDescent())) / 2);
            g.drawString(s, textX, textY);
        }
    }

    private void addNewTile() {
        java.util.List<Point> emptyCells = new java.util.ArrayList<>();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    emptyCells.add(new Point(row, col));
                }
            }
        }
        if (!emptyCells.isEmpty()) {
            Point p = emptyCells.get(random.nextInt(emptyCells.size()));
            board[p.x][p.y] = random.nextFloat() < 0.9 ? 2 : 4;
        }
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int row = 0; row < GRID_SIZE; row++) {
            int[] line = new int[GRID_SIZE];
            int index = 0;
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] != 0) {
                    line[index++] = board[row][col];
                }
            }
            for (int i = 0; i < index - 1; i++) {
                if (line[i] == line[i + 1]) {
                    line[i] *= 2;
                    score += line[i];
                    line[i + 1] = 0;
                }
            }
            int[] newLine = new int[GRID_SIZE];
            index = 0;
            for (int i = 0; i < GRID_SIZE; i++) {
                if (line[i] != 0) {
                    newLine[index++] = line[i];
                }
            }
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] != newLine[col]) {
                    moved = true;
                }
                board[row][col] = newLine[col];
            }
        }
        return moved;
    }

    private boolean moveRight() {
        rotateBoard(2);
        boolean moved = moveLeft();
        rotateBoard(2);
        return moved;
    }

    private boolean moveUp() {
        rotateBoard(3);
        boolean moved = moveLeft();
        rotateBoard(1);
        return moved;
    }

    private boolean moveDown() {
        rotateBoard(1);
        boolean moved = moveLeft();
        rotateBoard(3);
        return moved;
    }

    private void rotateBoard(int times) {
        for (int t = 0; t < times; t++) {
            int[][] rotated = new int[GRID_SIZE][GRID_SIZE];
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    rotated[col][GRID_SIZE - 1 - row] = board[row][col];
                }
            }
            board = rotated;
        }
    }

    private boolean isGameOver() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE - 1; col++) {
                if (board[row][col] == board[row][col + 1]) {
                    return false;
                }
            }
        }
        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = 0; row < GRID_SIZE - 1; row++) {
                if (board[row][col] == board[row + 1][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game2048(1));
    }
}