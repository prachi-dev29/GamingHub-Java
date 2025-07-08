import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignupPage2048 {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private JFrame frame;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox showPasswordCheckBox;
    private JButton submitButton;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JLabel errorLabel;
    private JRadioButton maleRadio;
    private JRadioButton femaleRadio;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern
        .compile("^(?=.[0-9])(?=.[a-z])(?=.[A-Z])(?=.[@#$%^&+=!])(?=\\S+$).{8,}$");
    private static final String[] SPECIAL_CHARS = {"@", "#", "$", "%", "^", "&", "+", "=", "!"};
    private static final Color ERROR_COLOR = new Color(255, 0, 0);
    private static final String DB_PATH = "database/2048.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;
    private static final String[] ALLOWED_EMAIL_DOMAINS = {"gmail.com", "yahoo.com", "outlook.com"};

    public SignupPage2048() {
        initializeDatabase();
        frame = new JFrame("Signup - 2048");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        NavBar2048 navBar = new NavBar2048();
        mainPanel.add(navBar, BorderLayout.NORTH);

        JPanel panel = new JPanel() {
            private Image bg;
            {
                try {
                    bg = ImageIO.read(new File("Background.png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, 360, 640, this);
            }
        };
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT - navBar.getPreferredSize().height));

        JLabel title = new JLabel("Signup");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(80, 20, 200, 40);
        panel.add(title);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        firstNameLabel.setForeground(Color.WHITE);
        firstNameLabel.setBounds(30, 80, 100, 30);
        panel.add(firstNameLabel);
        
        firstNameField = createStyledTextField();
        firstNameField.putClientProperty("JTextField.placeholderText", "First Name");
        firstNameField.setBounds(130, 80, 200, 30);
        panel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        lastNameLabel.setForeground(Color.WHITE);
        lastNameLabel.setBounds(30, 130, 100, 30);
        panel.add(lastNameLabel);
        
        lastNameField = createStyledTextField();
        lastNameField.putClientProperty("JTextField.placeholderText", "Last Name");
        lastNameField.setBounds(130, 130, 200, 30);
        panel.add(lastNameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(30, 180, 100, 30);
        panel.add(emailLabel);
        
        emailField = createStyledTextField();
        emailField.putClientProperty("JTextField.placeholderText", "Email");
        emailField.setBounds(130, 180, 200, 30);
        panel.add(emailField);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        genderLabel.setForeground(Color.WHITE);
        genderLabel.setBounds(30, 230, 100, 30);
        panel.add(genderLabel);
        
        JPanel genderPanel = new JPanel();
        genderPanel.setOpaque(false);
        genderPanel.setBounds(130, 230, 200, 30);
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        maleRadio.setFont(new Font("Arial", Font.PLAIN, 14));
        femaleRadio.setFont(new Font("Arial", Font.PLAIN, 14));
        maleRadio.setForeground(Color.WHITE);
        femaleRadio.setForeground(Color.WHITE);
        maleRadio.setOpaque(false);
        femaleRadio.setOpaque(false);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        panel.add(genderPanel);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(30, 280, 100, 30);
        panel.add(passwordLabel);
        
        passwordField = createStyledPasswordField();
        passwordField.putClientProperty("JTextField.placeholderText", "Password");
        passwordField.setBounds(130, 280, 200, 30);
        panel.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setBounds(30, 330, 100, 30);
        panel.add(confirmPasswordLabel);
        
        confirmPasswordField = createStyledPasswordField();
        confirmPasswordField.putClientProperty("JTextField.placeholderText", "Confirm Password");
        confirmPasswordField.setBounds(130, 330, 200, 30);
        panel.add(confirmPasswordField);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        showPasswordCheckBox.setForeground(Color.WHITE);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        showPasswordCheckBox.setBounds(130, 380, 200, 30);
        showPasswordCheckBox.addActionListener(e -> {
            boolean show = showPasswordCheckBox.isSelected();
            passwordField.setEchoChar(show ? (char) 0 : '•');
            confirmPasswordField.setEchoChar(show ? (char) 0 : '•');
        });
        panel.add(showPasswordCheckBox);

        errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setBounds(30, 430, 300, 30);
        panel.add(errorLabel);

        submitButton = createStyledButton("Submit");
        submitButton.setBounds(80, 480, 200, 40);
        submitButton.setEnabled(false);
        submitButton.addActionListener(e -> {
            if (validateForm()) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String gender = maleRadio.isSelected() ? "Male" : "Female";

                if (isEmailExists(email)) {
                    errorLabel.setText("Email already exists!");
                    errorLabel.setForeground(ERROR_COLOR);
                    return;
                }

                if (registerUser(firstName, lastName, email, password, gender)) {
                    frame.dispose();
                    int userId = getUserIdByEmail(email);
                    new Menu2048(userId).setVisible(true);
                } else {
                    errorLabel.setText("Registration failed. Please try again.");
                    errorLabel.setForeground(ERROR_COLOR);
                }
            }
        });
        panel.add(submitButton);

        addValidationListener(firstNameField);
        addValidationListener(lastNameField);
        addValidationListener(emailField);
        addValidationListener(passwordField);
        addValidationListener(confirmPasswordField);

        mainPanel.add(panel, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private void initializeDatabase() {
        try {
            System.out.println("Attempting to initialize database at URL: " + DB_URL);
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);

            if (connection != null) {
                System.out.println("Database connection established successfully.");
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                    System.out.println("Foreign key enforcement enabled.");
                } catch (SQLException e) {
                    System.err.println("Error enabling foreign keys: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error enabling foreign keys: " + e.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }

                createUsersTableIfNotExists();
                createScoreTableIfNotExists();

                File dbFile = new File(DB_PATH);
                if (dbFile.exists()) {
                    System.out.println("Database file exists at: " + DB_PATH);
                } else {
                    System.err.println("Database file does NOT exist at: " + DB_PATH);
                    JOptionPane.showMessageDialog(frame, "Database file was not created at: " + DB_PATH + "\nCheck directory permissions.",
                            "Database Creation Failed", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                System.err.println("Failed to establish database connection.");
                 JOptionPane.showMessageDialog(frame, "Failed to establish database connection.",
                            "Database Connection Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
             JOptionPane.showMessageDialog(frame, "SQLite JDBC driver not found.\nPlease ensure the JDBC driver JAR is in your classpath.",
                            "Database Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
             System.err.println("Database access error during connection: " + e.getMessage());
            e.printStackTrace();
             JOptionPane.showMessageDialog(frame, "Database access error during connection: " + e.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during database initialization: " + e.getMessage());
            e.printStackTrace();
             JOptionPane.showMessageDialog(frame, "An unexpected error occurred during database initialization: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isEmailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean registerUser(String firstName, String lastName, String email, String password, String gender) {
        String sql = "INSERT INTO users (first_name, last_name, email, gender, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, gender);
            pstmt.setString(5, password);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                String getIdSql = "SELECT id FROM users WHERE email = ?";
                try (PreparedStatement getIdStmt = connection.prepareStatement(getIdSql)) {
                    getIdStmt.setString(1, email);
                    ResultSet rs = getIdStmt.executeQuery();
                    if (rs.next()) {
                        int userId = rs.getInt("id");
                        insertUserIdIntoScore(userId);
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(0, 0, 0, 200));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        field.setOpaque(true);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(0, 0, 0, 200));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        field.setOpaque(true);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        button.setFocusPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void addValidationListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateForm();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateForm();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateForm();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        String firstName = firstNameField.getText().trim();
        if (firstName.isEmpty()) {
            isValid = false;
            errorMessage.append("First Name cannot be empty. ");
        }

        String lastName = lastNameField.getText().trim();
        if (lastName.isEmpty()) {
            isValid = false;
            errorMessage.append("Last Name cannot be empty. ");
        }

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            isValid = false;
            errorMessage.append("Email cannot be empty. ");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            isValid = false;
            errorMessage.append("Invalid email format (example: user@domain.com). ");
        } else {
            String domain = email.substring(email.lastIndexOf('@') + 1);
            boolean isDomainAllowed = false;
            for (String allowedDomain : ALLOWED_EMAIL_DOMAINS) {
                if (domain.equalsIgnoreCase(allowedDomain)) {
                    isDomainAllowed = true;
                    break;
                }
            }
            if (!isDomainAllowed) {
                isValid = false;
                errorMessage.append("Email domain is not allowed. ");
            }
        }

        String password = new String(passwordField.getPassword());
        System.out.println("Password entered: '" + password + "'");
        System.out.println("Password length: " + password.length());

        if (password.isEmpty()) {
            isValid = false;
            errorMessage.append("Password cannot be empty. ");
        } else {
            boolean isLengthValid = password.length() >= 8;
            System.out.println("Length >= 8 check result: " + isLengthValid);
            if (!isLengthValid) {
                isValid = false;
                errorMessage.append("Password must be at least 8 characters. ");
                System.out.println("Length check failed. Current error: " + errorMessage.toString());
            }

            boolean hasNumber = false;
            boolean hasLowercase = false;
            boolean hasUppercase = false;
            int specialCharCount = 0;

            for (char c : password.toCharArray()) {
                if (Character.isDigit(c)) {
                    hasNumber = true;
                } else if (Character.isLowerCase(c)) {
                    hasLowercase = true;
                } else if (Character.isUpperCase(c)) {
                    hasUppercase = true;
                } else {
                    for (String specialChar : SPECIAL_CHARS) {
                        if (String.valueOf(c).equals(specialChar)) {
                            specialCharCount++;
                            break;
                        }
                    }
                }
            }

            System.out.println("Has number check result: " + hasNumber);
            if (!hasNumber) {
                isValid = false;
                errorMessage.append("Password must contain at least one number. ");
                System.out.println("Number check failed. Current error: " + errorMessage.toString());
            }

            System.out.println("Has lowercase check result: " + hasLowercase);
            if (!hasLowercase) {
                isValid = false;
                errorMessage.append("Password must contain at least one lowercase letter. ");
            }

            System.out.println("Has uppercase check result: " + hasUppercase);
            if (!hasUppercase) {
                isValid = false;
                errorMessage.append("Password must contain at least one uppercase letter. ");
            }

            System.out.println("Special character count: " + specialCharCount);
            boolean isSpecialCharCountValid = specialCharCount == 1;
            System.out.println("Special character count == 1 check result: " + isSpecialCharCountValid);
            if (!isSpecialCharCountValid) {
                isValid = false;
                errorMessage.append("Password must contain exactly one special character (@#$%^&+=!). ");
            }
        }

        String confirmPassword = new String(confirmPasswordField.getPassword());
        System.out.println("Confirm password entered: '" + confirmPassword + "'");
        boolean passwordsMatch = password.equals(confirmPassword);
        System.out.println("Passwords match check result: " + passwordsMatch);
        if (confirmPassword.isEmpty()) {
            isValid = false;
            errorMessage.append("Please confirm your password. ");
        } else if (!passwordsMatch) {
            isValid = false;
            errorMessage.append("Passwords do not match. ");
        }

        boolean isGenderSelected = maleRadio.isSelected() || femaleRadio.isSelected();
        System.out.println("Gender selected check result: " + isGenderSelected);
        if (!isGenderSelected) {
            isValid = false;
            errorMessage.append("Please select your gender. ");
        }

        String errorText = errorMessage.toString().trim();
        errorLabel.setText(errorText);
        submitButton.setEnabled(isValid);

        System.out.println("Final isValid: " + isValid);
        System.out.println("Final error message: '" + errorText + "'");

        return isValid;
    }

    private void createUsersTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "gender TEXT NOT NULL, " +
                "password TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table created successfully (if it didn't exist).");
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
            e.printStackTrace();
             JOptionPane.showMessageDialog(frame, "Error creating users table: " + e.getMessage(),
                            "Database Table Creation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createScoreTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS score (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Score table created successfully (if it didn't exist).");
        } catch (SQLException e) {
            System.err.println("Error creating score table: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error creating score table: " + e.getMessage(),
                            "Database Table Creation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void insertUserIdIntoScore(int userId) {
        String sql = "INSERT INTO score (user_id, score) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignupPage2048 signup = new SignupPage2048();
            Runtime.getRuntime().addShutdownHook(new Thread(signup::close));
        });
    }

    public static class User {
        private final int id;
        private final String firstName, lastName, email, gender, password;

        public User(int id, String firstName, String lastName, String email, String gender, String password) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.gender = gender;
            this.password = password;
        }

        public int getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getGender() { return gender; }
        public String getPassword() { return password; }
    }

    public static class Score {
        private final int id, userId, score;

        public Score(int id, int userId, int score) {
            this.id = id;
            this.userId = userId;
            this.score = score;
        }

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getScore() { return score; }
    }

    private int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}