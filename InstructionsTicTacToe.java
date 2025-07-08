import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class InstructionsTicTacToe extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 700;
    private BufferedImage backgroundImage;
    private JButton backButton;
    private static final Color ACCENT_COLOR = new Color(255, 200, 100);
    private static final Color HOVER_COLOR = new Color(255, 220, 150);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 100);

    public InstructionsTicTacToe() {
        setTitle("Tic Tac Toe - Instructions");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());

        NavBarTicTacToe navBar = new NavBarTicTacToe(this);
        mainPanel.add(navBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 180), 
                                             0, getHeight(), new Color(0, 0, 0, 150)));
                int arc = 30;
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, arc, arc));
            }
        };
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel topDecor = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fill(new Ellipse2D.Float(20, 10, 20, 20));
                g2d.fill(new Ellipse2D.Float(60, 10, 20, 20));
                g2d.fill(new Ellipse2D.Float(100, 10, 20, 20));
            }
        };
        topDecor.setPreferredSize(new Dimension(WIDTH, 40));
        topDecor.setOpaque(false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        centerPanel.add(topDecor, gbc);

        JLabel titleLabel = createStyledLabel("How to Play", 32);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 40, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = createStyledLabel("Tic Tac Toe", 24);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        subtitleLabel.setForeground(ACCENT_COLOR);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 50, 0);
        centerPanel.add(subtitleLabel, gbc);

        JPanel instructionsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(20, 0, getWidth() - 20, 0);
            }
        };
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setOpaque(false);

        addInstruction(instructionsPanel, "1. The game is played on a 3x3 grid.");
        addInstruction(instructionsPanel, "2. There are two players: Player X and Player O.");
        addInstruction(instructionsPanel, "3. Players take turns marking a space in the grid.");
        addInstruction(instructionsPanel, "4. Player X goes first, followed by Player O.");
        addInstruction(instructionsPanel, "5. The first player to get 3 of their marks in a row (horizontally, vertically, or diagonally) wins.");
        addInstruction(instructionsPanel, "6. If all 9 squares are filled and no player has 3 in a row, the game ends in a draw.");

        JScrollPane scrollPane = new JScrollPane(instructionsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(320, 450));
        scrollPane.setMinimumSize(new Dimension(320, 200));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 30, 20, 30);
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(scrollPane, gbc);

        backButton = createStyledButton("Back to Menu");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 30, 30, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(backButton, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        try {
            File imageFile = new File("backgroundTic-Tac-Toe.png");
            System.out.println("Attempting to load image from: " + imageFile.getAbsolutePath());
            if (imageFile.exists()) {
                System.out.println("Image file exists");
                backgroundImage = ImageIO.read(imageFile);
                System.out.println("Image loaded successfully");
            } else {
                System.err.println("Image file does not exist");
            }
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }

        backButton.addActionListener(e -> {
            dispose();
            new WelcomeTicTacToe().setVisible(true);
        });

        add(mainPanel);
    }

    private void addInstruction(JPanel panel, String text) {
        int wrapWidth = 260;
        JLabel label = new JLabel("<html><div style='width:" + wrapWidth + "px'>" + text + "</div></html>");
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(320, 60));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InstructionsTicTacToe().setVisible(true);
        });
    }
}