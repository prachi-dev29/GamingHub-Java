import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class InstructionsFrame2048 extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;

    public InstructionsFrame2048() {
        setTitle("2048 Instructions");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        NavBar2048 navBar = new NavBar2048();
        mainPanel.add(navBar, BorderLayout.NORTH);

        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImg;
            {
                try {
                    backgroundImg = ImageIO.read(new File("Background.png"));
                } catch (IOException e) {
                    System.err.println("Error loading images: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImg != null) {
                    g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 20, 20));
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("How to Play 2048");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        String[][] instructions = {
            {"1. Use arrow keys", "to move tiles"},
            {"2. Tiles with", "same numbers merge"},
            {"3. Try to reach", "the 2048 tile!"},
            {"4. Game ends", "when board is full"},
            {"5. No more moves", "possible = Game Over"}
        };

        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setOpaque(false);
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        for (String[] instruction : instructions) {
            JPanel linePanel = new JPanel();
            linePanel.setOpaque(false);
            linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.Y_AXIS));
            JLabel label1 = new JLabel(instruction[0]);
            JLabel label2 = new JLabel(instruction[1]);
            label1.setFont(new Font("Arial", Font.BOLD, 18));
            label2.setFont(new Font("Arial", Font.BOLD, 18));
            label1.setForeground(new Color(255, 255, 255));
            label2.setForeground(new Color(255, 255, 255));
            label1.setAlignmentX(Component.LEFT_ALIGNMENT);
            label2.setAlignmentX(Component.LEFT_ALIGNMENT);
            linePanel.add(label1);
            linePanel.add(label2);
            linePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            instructionsPanel.add(linePanel);
        }

        JButton backButton = createStyledButton("Back to Menu");
        backButton.addActionListener(e -> {
            dispose();
            new StartFrame2048().setVisible(true);
        });
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(300, 60));

        contentPanel.add(titleLabel);
        contentPanel.add(instructionsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(backButton);

        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setOpaque(false);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddedPanel.add(contentPanel, BorderLayout.CENTER);

        backgroundPanel.add(paddedPanel, BorderLayout.CENTER);
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 100));
        button.setPreferredSize(new Dimension(300, 60));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 3),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new InstructionsFrame2048().setVisible(true);
        });
    }
}