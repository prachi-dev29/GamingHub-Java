import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class WelcomeTicTacToe extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private Image backgroundImage;

    public WelcomeTicTacToe() {
        setTitle("Welcome to Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            backgroundImage = ImageIO.read(new File("backgroundTic-Tac-Toe.png"));
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            backgroundImage = null;
        }

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(41, 128, 185));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());

        NavBarTicTacToe navBar = new NavBarTicTacToe(this);
        mainPanel.add(navBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(100, 40, 100, 40));

        JLabel titleLabel = new JLabel("Tic-Tac-Toe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 60)));

        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> {
            dispose();
            new LoginPageTicTacToe();
        });
        centerPanel.add(loginButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton signupButton = createStyledButton("Sign Up");
        signupButton.addActionListener(e -> {
            dispose();
            new SignupPageTicTacToe();
        });
        centerPanel.add(signupButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton backButton = createStyledButton("Back to Hub");
        backButton.addActionListener(e -> {
            dispose();
            JOptionPane.showMessageDialog(null, "Back to Hub clicked!");
        });
        centerPanel.add(backButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 50));
        button.setPreferredSize(new Dimension(240, 50));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WelcomeTicTacToe().setVisible(true);
        });
    }
}