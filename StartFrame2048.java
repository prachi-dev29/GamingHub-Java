import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class StartFrame2048 extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;

    public StartFrame2048() {
        setTitle("2048");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.BLACK);

        NavBar2048 navBar = new NavBar2048();
        mainContainer.add(navBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel() {
            private Image backgroundImg;
            {
                try {
                    File imageFile = new File("Background.png");
                    if (imageFile.exists()) {
                        backgroundImg = ImageIO.read(imageFile);
                    }
                } catch (IOException e) {
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
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("2048");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, gbc);

        gbc.gridy++;
        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> {
            dispose();
            new Login2048();
        });
        mainPanel.add(loginButton, gbc);

        gbc.gridy++;
        JButton signupButton = createStyledButton("Signup");
        signupButton.addActionListener(e -> {
            dispose();
            new SignupPage2048();
        });
        mainPanel.add(signupButton, gbc);

        gbc.gridy++;
        JButton backButton = createStyledButton("Back to Hub");
        backButton.addActionListener(e -> {
            dispose();
            JOptionPane.showMessageDialog(this, "Back to Hub clicked!");
        });
        mainPanel.add(backButton, gbc);

        mainContainer.add(mainPanel, BorderLayout.CENTER);
        setContentPane(mainContainer);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 30, 30));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StartFrame2048());
    }
}

