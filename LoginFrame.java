import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame{
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;
    private LoginDatabase database;

    public LoginFrame() {
        database = new LoginDatabase();
        frame = new JFrame("Login");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        LoginPanel panel = new LoginPanel(database);
        panel.setPreferredSize(new Dimension(boardWidth, boardHeight - 40)); // Adjust for navbar height
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

    class LoginPanel extends JPanel {
        private int birdX = 0;
        private int birdY = 0;
        private int velocityX = 2;
        private int velocityY = 2;
        private final int birdWidth = 68;
        private final int birdHeight = 48;
        private Image birdImg;
        private Image backgroundImg;
        private JLabel loginLabel;
        private JLabel emailLabel;
        private JLabel passwordLabel;
        private JTextField emailField;
        private JPasswordField passwordField;
        private JButton submitButton;
        private JCheckBox showPasswordCheckBox;
        private JLabel errorLabel;
        private LoginDatabase database;

        public LoginPanel(LoginDatabase database) {
            this.database = database;
            setLayout(null);
            
            try {
                birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
                backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
                if (birdImg == null || backgroundImg == null) {
                    System.err.println("Error: Could not load one or more images");
                }
            } catch (IOException e) {
                System.err.println("Error loading images: " + e.getMessage());
                e.printStackTrace();
            }

            loginLabel = new JLabel("Login!");
            loginLabel.setFont(new Font("Arial", Font.BOLD, 28));
            loginLabel.setForeground(Color.WHITE);
            loginLabel.setBounds(30, 40, 300, 40);
            loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(loginLabel);

            emailLabel = new JLabel("Email:");
            emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
            emailLabel.setForeground(Color.WHITE);
            emailLabel.setBounds(30, 120, 100, 30);
            add(emailLabel);

            emailField = new JTextField();
            emailField.setFont(new Font("Arial", Font.PLAIN, 14));
            emailField.setBounds(140, 120, 180, 35);
            add(emailField);

            passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            passwordLabel.setForeground(Color.WHITE);
            passwordLabel.setBounds(30, 170, 100, 30);
            add(passwordLabel);

            passwordField = new JPasswordField();
            passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
            passwordField.setBounds(140, 170, 180, 35);
            add(passwordField);

            showPasswordCheckBox = new JCheckBox("Show Password");
            showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
            showPasswordCheckBox.setForeground(Color.WHITE);
            showPasswordCheckBox.setBackground(new Color(0, 0, 0, 0));
            showPasswordCheckBox.setBounds(140, 215, 150, 20);
            showPasswordCheckBox.addActionListener(e -> {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('•');
                }
            });
            add(showPasswordCheckBox);

            submitButton = new JButton("Login");
            submitButton.setFont(new Font("Arial", Font.BOLD, 14));
            submitButton.setForeground(Color.WHITE);
            submitButton.setBackground(new Color(41, 128, 185));
            submitButton.setFocusPainted(false);
            submitButton.setBorderPainted(false);
            submitButton.setBounds(50, 260, 260, 50);
            submitButton.addActionListener(e -> {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                try {
                    if (database.loginUser(email, password)) {
                        frame.dispose();
                        new SelectModeFrame(database.getUserIdByEmail(email), false);
                    } else {
                        errorLabel.setText("Invalid email or password.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    errorLabel.setText("Database error.");
                }
            });
            add(submitButton);

            errorLabel = new JLabel();
            errorLabel.setFont(new Font("Arial", Font.BOLD, 12));
            errorLabel.setForeground(Color.RED);
            errorLabel.setBounds(30, 320, 300, 30);
            add(errorLabel);
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