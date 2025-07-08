import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login2048 extends JFrame {
    private JFrame frame;
    private JButton loginBtn;
    private JTextField emailField;
    private JPasswordField passwordField;
    private static final String DB_PATH = "database/2048.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;
    private int userId;

    public Login2048() {
        initializeDatabase();
        frame = new JFrame("Login - 2048");
        frame.setSize(360, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.BLACK);

        NavBar2048 navBar = new NavBar2048();
        mainContainer.add(navBar, BorderLayout.NORTH);

        JPanel panel = createBackgroundPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 5, 30, 5);
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 10);
        emailField = createStyledTextField();
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 10, 5, 5);
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passLabel.setForeground(Color.WHITE);
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 10);
        passwordField = createStyledPasswordField();
        panel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        showPasswordCheckBox.setForeground(Color.WHITE);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.addActionListener(e -> {
            boolean show = showPasswordCheckBox.isSelected();
            passwordField.setEchoChar(show ? (char) 0 : '•');
        });
        panel.add(showPasswordCheckBox, gbc);

        loginBtn = createStyledButton("Submit");
        loginBtn.setEnabled(false);
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            int authenticatedUserId = authenticateUser(email, password);
            if (authenticatedUserId != -1) {
                frame.dispose();
                insertUserIdIntoScore(authenticatedUserId);
                new Menu2048(authenticatedUserId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 5, 10, 5);
        panel.add(loginBtn, gbc);

        addValidationListener(emailField);
        addValidationListener(passwordField);

        mainContainer.add(panel, BorderLayout.CENTER);
        frame.setContentPane(mainContainer);
        frame.setVisible(true);
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database connection failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int authenticateUser(String email, String password) {
        String sql = "SELECT id FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void insertUserIdIntoScore(int userId) {
        String sql = "INSERT INTO score (user_id, score) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addValidationListener(JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateFields();
            }
        });
    }

    private void validateFields() {
        boolean isValid = !emailField.getText().trim().isEmpty() && 
                         passwordField.getPassword().length > 0;
        loginBtn.setEnabled(isValid);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(0, 0, 0, 150));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setMinimumSize(new Dimension(200, 30));
        field.setPreferredSize(new Dimension(200, 30));
        field.setMaximumSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setOpaque(true);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(0, 0, 0, 150));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setMinimumSize(new Dimension(200, 30));
        field.setPreferredSize(new Dimension(200, 30));
        field.setMaximumSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setOpaque(true);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 35));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createBackgroundPanel() {
        return new JPanel() {
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
                if (bg != null)
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
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
            Login2048 login = new Login2048();
            login.setVisible(true);
            Runtime.getRuntime().addShutdownHook(new Thread(login::close));
        });
    }
}