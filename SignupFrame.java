import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SignupFrame {
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
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
    private SignupDatabase database;
    private SignupDatabaseScore signupDatabaseScore;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(".*");
    private static final String[] SPECIAL_CHARS = { "@", "#", "$", "%", "^", "&", "+", "=", "!" };
    private static final Color ERROR_COLOR = new Color(255, 0, 0);
    private static final String[] COMMON_DOMAINS = {
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "icloud.com",
            "aol.com", "protonmail.com", "mail.com", "zoho.com", "yandex.com"
    };
    private static final String[] COMMON_TYPOS = {
            "gmmail.com", "gmaail.com", "gmial.com", "gmai.com", "gamil.com",
            "gmaiil.com", "gmaill.com", "gmai.com", "gmal.com", "gmil.com"
    };

    public SignupFrame() {
        database = new SignupDatabase();
        signupDatabaseScore = new SignupDatabaseScore();

        frame = new JFrame("Signup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        SignupPanel panel = new SignupPanel(database);
        panel.setPreferredSize(new Dimension(boardWidth, boardHeight - 40));
        mainContainer.add(panel, BorderLayout.CENTER);

        Timer timer = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.updateBird();
                panel.repaint();
            }
        });
        timer.start();

        frame.add(mainContainer);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    class SignupPanel extends JPanel {
        private int birdX = 0;
        private int birdY = 0;
        private int velocityX = 2;
        private int velocityY = 2;
        private final int birdWidth = 68;
        private final int birdHeight = 48;
        private Image birdImg;
        private Image backgroundImg;
        private SignupDatabase database;

        public SignupPanel(SignupDatabase database) {
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

            JLabel title = new JLabel("Signup");
            title.setFont(new Font("Arial", Font.BOLD, 28));
            title.setForeground(Color.WHITE);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setBounds(30, 40, 300, 40);
            add(title);

            JLabel firstNameLabel = new JLabel("First Name:");
            firstNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            firstNameLabel.setForeground(Color.WHITE);
            firstNameLabel.setBounds(30, 100, 100, 30);
            add(firstNameLabel);

            firstNameField = createStyledTextField();
            firstNameField.setBounds(140, 100, 180, 35);
            add(firstNameField);

            JLabel lastNameLabel = new JLabel("Last Name:");
            lastNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            lastNameLabel.setForeground(Color.WHITE);
            lastNameLabel.setBounds(30, 150, 100, 30);
            add(lastNameLabel);

            lastNameField = createStyledTextField();
            lastNameField.setBounds(140, 150, 180, 35);
            add(lastNameField);

            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
            emailLabel.setForeground(Color.WHITE);
            emailLabel.setBounds(30, 200, 100, 30);
            add(emailLabel);

            emailField = createStyledTextField();
            emailField.setBounds(140, 200, 180, 35);
            add(emailField);

            JLabel genderLabel = new JLabel("Gender:");
            genderLabel.setFont(new Font("Arial", Font.BOLD, 14));
            genderLabel.setForeground(Color.WHITE);
            genderLabel.setBounds(30, 250, 100, 30);
            add(genderLabel);

            JPanel genderPanel = new JPanel();
            genderPanel.setOpaque(false);
            genderPanel.setBounds(140, 250, 180, 35);
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
            add(genderPanel);

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            passwordLabel.setForeground(Color.WHITE);
            passwordLabel.setBounds(30, 300, 100, 30);
            add(passwordLabel);

            passwordField = createStyledPasswordField();
            passwordField.setBounds(140, 300, 180, 35);
            add(passwordField);

            JLabel confirmPasswordLabel = new JLabel("Confirm:");
            confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            confirmPasswordLabel.setForeground(Color.WHITE);
            confirmPasswordLabel.setBounds(30, 350, 100, 30);
            add(confirmPasswordLabel);

            confirmPasswordField = createStyledPasswordField();
            confirmPasswordField.setBounds(140, 350, 180, 35);
            add(confirmPasswordField);

            showPasswordCheckBox = new JCheckBox("Show Password");
            showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
            showPasswordCheckBox.setForeground(Color.WHITE);
            showPasswordCheckBox.setBackground(new Color(0, 0, 0, 0));
            showPasswordCheckBox.setBounds(140, 395, 150, 20);
            showPasswordCheckBox.addActionListener(e -> {
                boolean show = showPasswordCheckBox.isSelected();
                passwordField.setEchoChar(show ? (char) 0 : '•');
                confirmPasswordField.setEchoChar(show ? (char) 0 : '•');
            });
            add(showPasswordCheckBox);

            errorLabel = new JLabel("");
            errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            errorLabel.setForeground(ERROR_COLOR);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setBounds(10, 425, 380, 30);
            add(errorLabel);

            submitButton = createStyledButton("Submit");
            submitButton.setBounds(50, 460, 260, 50);
            submitButton.setEnabled(false);
            submitButton.addActionListener(e -> {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : "");

                try {
                    boolean success = database.registerUser(firstName, lastName, email, gender, password);
                    if (success) {
                        frame.dispose();
                        new SelectModeFrame(database.getUserIdByEmail(email), false);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Registration failed. Email may already exist.",
                                "Signup Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            add(submitButton);

            addValidationListener(firstNameField);
            addValidationListener(lastNameField);
            addValidationListener(emailField);
            addValidationListener(passwordField);
            addValidationListener(confirmPasswordField);
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
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(41, 128, 185));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
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
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : "");

            boolean isValid = true;
            String errorMessage = "";

            if (firstName.isEmpty()) {
                errorMessage = "First name is required";
                isValid = false;
            } else if (lastName.isEmpty()) {
                errorMessage = "Last name is required";
                isValid = false;
            } else if (email.isEmpty()) {
                errorMessage = "Email is required";
                isValid = false;
            } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                errorMessage = "Invalid email format";
                isValid = false;
            } else if (gender.isEmpty()) {
                errorMessage = "Please select a gender";
                isValid = false;
            } else if (password.isEmpty()) {
                errorMessage = "Password is required";
                isValid = false;
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                errorMessage = "Password must be at least 8 characters long and contain uppercase, lowercase, number, and special character";
                isValid = false;
            } else if (!password.equals(confirmPassword)) {
                errorMessage = "Passwords do not match";
                isValid = false;
            }

            System.out.println("Password (hex): " + password.chars().mapToObj(Integer::toHexString).reduce((a, b) -> a + " " + b).orElse(""));

            errorLabel.setText("<html>" + errorMessage + "</html>");
            submitButton.setEnabled(isValid);
            return isValid;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SignupFrame();
        });
    }

}