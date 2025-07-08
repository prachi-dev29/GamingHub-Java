import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LoginPageTicTacToe {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton submitButton;
    private JTextField emailField;
    private JLabel errorLabel;
    private JLabel playerLabel;
    private boolean isPlayerO = true;
    private String playerOEmail = "";
    private String playerOPassword = "";
    private int playerOId = -1;
    
    private static final Color ACCENT_COLOR = new Color(255, 200, 100);
    private static final Color HOVER_COLOR = new Color(255, 220, 150);
    private static final Color ERROR_COLOR = new Color(255, 50, 50);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(gmail|yahoo|hotmail|outlook|icloud|protonmail|aol|mail|yandex|zoho|gmx)\\.(com|net|org|edu|gov|mil|biz|info|mobi|name|aero|jobs|museum)$", Pattern.CASE_INSENSITIVE);
    private JButton loginButton;
    private LoginPageTicTacToeDatabase database;
    private LoginPageTicTacToeDatabaseScore scoreDatabase;
    private JFrame frame;

    public LoginPageTicTacToe() {
        frame = new JFrame("Tic-Tac-Toe Login!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);

        database = new LoginPageTicTacToeDatabase();
        scoreDatabase = new LoginPageTicTacToeDatabaseScore();

        JPanel mainPanel = new JPanel() {
            private Image backgroundImg;

            {
                try {
                    URL imageUrl = getClass().getResource("./backgroundTic-Tac-Toe.png");
                    if (imageUrl != null) {
                        backgroundImg = ImageIO.read(imageUrl);
                    } else {
                        System.err.println("Could not find background image");
                    }
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
        mainPanel.setLayout(new BorderLayout());

        NavBarTicTacToe navBar = new NavBarTicTacToe(frame);
        mainPanel.add(navBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = createStyledLabel("Login!", 28);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(titleLabel, gbc);

        playerLabel = createStyledLabel("Player O", 24);
        playerLabel.setForeground(Color.WHITE);
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        centerPanel.add(playerLabel, gbc);

        JLabel emailLabel = createStyledLabel("Email:", 16);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 10, 8, 10);
        centerPanel.add(emailLabel, gbc);

        emailField = createStyledTextField(50);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(emailField, gbc);

        JLabel passwordLabel = createStyledLabel("Password:", 16);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 10, 8, 10);
        centerPanel.add(passwordLabel, gbc);

        passwordField = createStyledPasswordField(50);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(passwordField, gbc);

        showPasswordCheckBox = createStyledCheckBox("Show Password");
        showPasswordCheckBox.addActionListener(e -> {
            boolean show = showPasswordCheckBox.isSelected();
            passwordField.setEchoChar(show ? (char)0 : '●');
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 10);
        centerPanel.add(showPasswordCheckBox, gbc);

        errorLabel = new JLabel("");
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        centerPanel.add(errorLabel, gbc);

        submitButton = createStyledButton("Submit");
        submitButton.addActionListener(e -> submitButtonActionPerformed(e));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 30, 0);
        centerPanel.add(submitButton, gbc);

        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setOpaque(false);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        paddedPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.add(paddedPanel, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);

        emailField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validateEmail(); }
            public void removeUpdate(DocumentEvent e) { validateEmail(); }
            public void insertUpdate(DocumentEvent e) { validateEmail(); }
        });

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validateForm(); }
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            public void insertUpdate(DocumentEvent e) { validateForm(); }
        });
    }

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(new Color(255, 255, 255));
        field.setForeground(new Color(0, 0, 0));
        field.setCaretColor(new Color(0, 0, 0));
        field.setMinimumSize(new Dimension(200, 35));
        field.setPreferredSize(new Dimension(200, 35));
        field.setMaximumSize(new Dimension(200, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 0, 0)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        field.setOpaque(true);
        return field;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(new Color(255, 255, 255));
        field.setForeground(new Color(0, 0, 0));
        field.setCaretColor(new Color(0, 0, 0));
        field.setMinimumSize(new Dimension(200, 35));
        field.setPreferredSize(new Dimension(200, 35));
        field.setMaximumSize(new Dimension(200, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 0, 0)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        field.setEchoChar('●');
        field.setOpaque(true);
        return field;
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(new Font("Arial", Font.BOLD, 16));
        checkBox.setForeground(Color.WHITE);
        checkBox.setOpaque(false);
        return checkBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setPreferredSize(new Dimension(250, 50));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        return button;
    }

    private void validateEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errorLabel.setText("Email is required");
            submitButton.setEnabled(false);
        } else if (!database.isEmailExists(email)) {
            errorLabel.setText("Email not found. Please sign up first.");
            submitButton.setEnabled(false);
        } else {
            errorLabel.setText("");
            validateForm();
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errors.append("Email is required. ");
        } else if (!database.isEmailExists(email)) {
            errors.append("Email not found. Please sign up first. ");
        }

        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) {
            errors.append("Password is required. ");
        }

        boolean isValid = errors.length() == 0;
        errorLabel.setText(isValid ? "" : errors.toString());
        submitButton.setEnabled(isValid);

        return isValid;
    }

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (validateForm()) {
            if (isPlayerO) {
                try {
                    playerOEmail = emailField.getText().trim();
                    playerOPassword = new String(passwordField.getPassword());
                    
                    if (!database.validateLogin(playerOEmail, playerOPassword)) {
                        errorLabel.setText("Invalid email or password for Player O");
                        return;
                    }

                    playerOId = database.getUserId(playerOEmail);
                    System.out.println("Player O ID: " + playerOId);
                    
                    if (playerOId != -1) {
                        GameTicTacToeDatabase gameDb = new GameTicTacToeDatabase();
                        gameDb.createNewResultEntry(playerOId);
                        gameDb.close();
                        emailField.setText("");
                        passwordField.setText("");
                        errorLabel.setText("");
                        
                        isPlayerO = false;
                        playerLabel.setText("Player X");
                        
                        JOptionPane.showMessageDialog(frame, 
                            "Player O logged in successfully!\nNow enter Player X's information.",
                            "Player O Ready",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        errorLabel.setText("Error retrieving Player O's user ID");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorLabel.setText("Error during Player O login: " + e.getMessage());
                }
            } else {
                try {
                    String playerXEmail = emailField.getText().trim();
                    String playerXPassword = new String(passwordField.getPassword());
                    
                    if (playerXEmail.equals(playerOEmail)) {
                        errorLabel.setText("Player X must use a different email from Player O.");
                        return;
                    }
                    
                    if (!database.validateLogin(playerXEmail, playerXPassword)) {
                        errorLabel.setText("Invalid email or password for Player X");
                        return;
                    }

                    int playerXId = database.getUserId(playerXEmail);
                    System.out.println("Player X ID: " + playerXId);
                    
                    if (playerXId != -1) {
                        GameTicTacToeDatabase gameDb = new GameTicTacToeDatabase();
                        gameDb.createNewResultEntry(playerXId);
                        gameDb.close();
                        JOptionPane.showMessageDialog(frame, 
                            "Both players logged in successfully!\nGame will start now.",
                            "Ready to Play",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        frame.dispose();
                        new TicTacToeMenu(playerOEmail, playerXEmail, playerOId, playerXId);
                    } else {
                        errorLabel.setText("Error retrieving Player X's user ID");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorLabel.setText("Error during Player X login: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginPageTicTacToe();
        });
    }
}