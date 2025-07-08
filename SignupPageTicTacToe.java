import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SignupPageTicTacToe extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox showPasswordCheckBox;
    private JButton submitButton;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JLabel errorLabel;
    private JLabel playerLabel;
    private boolean isPlayerO = true;
    
    private String playerOFirstName = "";
    private String playerOLastName = "";
    private String playerOEmail = "";
    private String playerOPassword = "";
    private String playerOGender = "";
    private static final Color ACCENT_COLOR = new Color(255, 200, 100);
    private static final Color HOVER_COLOR = new Color(255, 220, 150);
    private static final Color ERROR_COLOR = new Color(255, 50, 50);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(gmail|yahoo|hotmail|outlook|icloud|protonmail|aol|mail|yandex|zoho|gmx)\\.(com|net|org|edu|gov|mil|biz|info|mobi|name|aero|jobs|museum)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.[0-9])(?=.[!@#$%^&])[A-Za-z0-9!@#$%^&]{8,}$");
    private BufferedImage backgroundImage;
    private JButton loginButton;
    private JButton signupButton;
    private JButton backButton;
    private JLabel welcomeLabel;
    private JRadioButton maleRadio;
    private JRadioButton femaleRadio;
    private SignupPageTicTacToeDatabase database;
    private SignupPageTicTacToeDatabaseScore scoreDatabase;
    private int playerOId = -1;
    private int playerXId = -1;

    public SignupPageTicTacToe() {
        setTitle("Tic-Tac-Toe Signup!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        database = new SignupPageTicTacToeDatabase();
        scoreDatabase = new SignupPageTicTacToeDatabaseScore();

        try {
            backgroundImage = ImageIO.read(new File("backgroundTic-Tac-Toe.png"));
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }

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

        NavBarTicTacToe navBar = new NavBarTicTacToe(this);
        mainPanel.add(navBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = createStyledLabel("Sign Up!", 28);
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

        JLabel firstNameLabel = createStyledLabel("First Name:", 16);
        firstNameLabel.setForeground(Color.WHITE);
        firstNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 10, 8, 10);
        centerPanel.add(firstNameLabel, gbc);

        firstNameField = createStyledTextField(50);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(firstNameField, gbc);

        JLabel lastNameLabel = createStyledLabel("Last Name:", 16);
        lastNameLabel.setForeground(Color.WHITE);
        lastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 10, 8, 10);
        centerPanel.add(lastNameLabel, gbc);

        lastNameField = createStyledTextField(50);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(lastNameField, gbc);

        JLabel emailLabel = createStyledLabel("Email:", 16);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 10, 8, 10);
        centerPanel.add(emailLabel, gbc);

        emailField = createStyledTextField(50);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(emailField, gbc);

        JLabel genderLabel = createStyledLabel("Gender:", 16);
        genderLabel.setForeground(Color.WHITE);
        genderLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 10, 8, 10);
        centerPanel.add(genderLabel, gbc);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setOpaque(false);
        ButtonGroup genderGroup = new ButtonGroup();
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        
        maleRadio.setForeground(Color.WHITE);
        femaleRadio.setForeground(Color.WHITE);
        maleRadio.setOpaque(false);
        femaleRadio.setOpaque(false);
        
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(genderPanel, gbc);

        JLabel passwordLabel = createStyledLabel("Password:", 16);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 6;
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

        JLabel confirmPasswordLabel = createStyledLabel("Confirm:", 16);
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 10, 8, 10);
        centerPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = createStyledPasswordField(50);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 10, 0);
        centerPanel.add(confirmPasswordField, gbc);

        showPasswordCheckBox = createStyledCheckBox("Show Password");
        showPasswordCheckBox.addActionListener(e -> {
            boolean show = showPasswordCheckBox.isSelected();
            passwordField.setEchoChar(show ? (char)0 : '●');
            confirmPasswordField.setEchoChar(show ? (char)0 : '●');
        });
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 15, 10);
        centerPanel.add(showPasswordCheckBox, gbc);

        errorLabel = new JLabel("");
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        centerPanel.add(errorLabel, gbc);

        submitButton = createStyledButton("Submit");
        submitButton.addActionListener(e -> {
            if (validateForm()) {
                if (isPlayerO) {
                    playerOFirstName = firstNameField.getText().trim();
                    playerOLastName = lastNameField.getText().trim();
                    playerOEmail = emailField.getText().trim();
                    playerOPassword = new String(passwordField.getPassword());
                    playerOGender = maleRadio.isSelected() ? "Male" : "Female";
                    
                    if (!database.registerUser(playerOFirstName, playerOLastName, playerOEmail, playerOPassword, playerOGender)) {
                        errorLabel.setText("Error registering Player O. Please try again.");
                        return;
                    }
                    playerOId = scoreDatabase.getUserIdByEmail(playerOEmail);
                    GameTicTacToeDatabase gameDbO = new GameTicTacToeDatabase();
                    gameDbO.createNewResultEntry(playerOId);
                    gameDbO.close();
                    
                    firstNameField.setText("");
                    lastNameField.setText("");
                    emailField.setText("");
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                    showPasswordCheckBox.setSelected(false);
                    maleRadio.setSelected(false);
                    femaleRadio.setSelected(false);
                    errorLabel.setText("");
                    
                    isPlayerO = false;
                    playerLabel.setText("Player X");
                    
                    JOptionPane.showMessageDialog(this, 
                        "Player O registered successfully!\nNow enter Player X's information.",
                        "Player O Ready",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String playerXFirstName = firstNameField.getText().trim();
                    String playerXLastName = lastNameField.getText().trim();
                    String playerXEmail = emailField.getText().trim();
                    String playerXPassword = new String(passwordField.getPassword());
                    String playerXGender = maleRadio.isSelected() ? "Male" : "Female";
                    
                    if (playerXEmail.equals(playerOEmail)) {
                        errorLabel.setText("Player X must use a different email from Player O.");
                        return;
                    }
                    
                    if (!database.registerUser(playerXFirstName, playerXLastName, playerXEmail, playerXPassword, playerXGender)) {
                        errorLabel.setText("Error registering Player X. Please try again.");
                        return;
                    }
                    playerXId = scoreDatabase.getUserIdByEmail(playerXEmail);
                    GameTicTacToeDatabase gameDbX = new GameTicTacToeDatabase();
                    gameDbX.createNewResultEntry(playerXId);
                    gameDbX.close();

                    JOptionPane.showMessageDialog(this, 
                        "Both players registered successfully!\nGame will start now.",
                        "Ready to Play",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    new TicTacToeMenu(playerOFirstName, playerXFirstName, playerOId, playerXId);
                    dispose();
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 30, 0);
        centerPanel.add(submitButton, gbc);

        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setOpaque(false);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        paddedPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.add(paddedPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
        setVisible(true);
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
        addValidationListener(field);
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
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validateForm(); }
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            public void insertUpdate(DocumentEvent e) { validateForm(); }
        });
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

    private void addValidationListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { validateForm(); }
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            public void insertUpdate(DocumentEvent e) { validateForm(); }
        });
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        String firstName = firstNameField.getText().trim();
        if (firstName.isEmpty()) {
            errors.append("First name is required. ");
        }

        String lastName = lastNameField.getText().trim();
        if (lastName.isEmpty()) {
            errors.append("Last name is required. ");
        }

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errors.append("Email is required. ");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("Please enter a valid email address. ");
        } else if (database.isEmailExists(email)) {
            errors.append("This email is already registered. ");
        }

        if (!maleRadio.isSelected() && !femaleRadio.isSelected()) {
            errors.append("Please select a gender. ");
        }

        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (password.isEmpty()) {
            errors.append("Password is required. ");
        } else if (password.length() < 8) {
            errors.append("Password must be at least 8 characters long. ");
        } else if (!password.equals(confirmPassword)) {
            errors.append("Passwords do not match. ");
        }

        boolean isValid = errors.length() == 0;
        errorLabel.setText(isValid ? "" : errors.toString());
        submitButton.setEnabled(isValid);

        return isValid;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SignupPageTicTacToe();
        });
    }
}