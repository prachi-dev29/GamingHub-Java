import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ContactTicTacToe {
    public static final int boardWidth = 360;
    public static final int boardHeight = 640;
    private JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ContactTicTacToe();
        });
    }

    public ContactTicTacToe() {
        frame = new JFrame("Contact Us");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBarTicTacToe navBar = new NavBarTicTacToe(frame);
        mainContainer.add(navBar, BorderLayout.NORTH);

        ContactPanelTicTacToe panel = new ContactPanelTicTacToe();
        panel.setPreferredSize(new Dimension(boardWidth, boardHeight - 40)); 
        mainContainer.add(panel, BorderLayout.CENTER);

        frame.add(mainContainer);
        frame.pack();
        frame.setVisible(true);
    }
}

class ContactPanelTicTacToe extends JPanel {
    private Image backgroundImg;
    private final int MARGIN_LEFT = 20;
    private final int MARGIN_RIGHT = 20;
    private final int LABEL_WIDTH = ContactTicTacToe.boardWidth - (MARGIN_LEFT + MARGIN_RIGHT);

    public ContactPanelTicTacToe() {
        setLayout(null);
        
        try {
            backgroundImg = ImageIO.read(new File("backgroundTic-Tac-Toe.png"));
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            e.printStackTrace();
        }

        JPanel textBgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        textBgPanel.setOpaque(false);
        textBgPanel.setBounds(MARGIN_LEFT - 10, 30, LABEL_WIDTH + 20, 550);
        textBgPanel.setLayout(null);
        add(textBgPanel);

        JLabel titleLabel = createShadowLabel("Contact Us", 32);
        titleLabel.setBounds(10, 10, LABEL_WIDTH, 50);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textBgPanel.add(titleLabel);

        addSeparator(textBgPanel, 70);

        JLabel developersLabel = createShadowLabel("Developers:", 26);
        developersLabel.setBounds(10, 90, LABEL_WIDTH, 35);
        textBgPanel.add(developersLabel);

        createEmailLink("Prachi Arora", "prachiarora781@gmail.com", 150, textBgPanel);
        createEmailLink("Tushar Arora", "tushararo62@gmail.com", 250, textBgPanel);

        addSeparator(textBgPanel, 450);

        JButton backButton = createStyledButton("Back to Menu", MARGIN_LEFT + 10, 520);
        backButton.addActionListener(e -> {
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            currentFrame.dispose();
            new WelcomeTicTacToe().setVisible(true);;
        });
    }

    private void createEmailLink(String name, String email, int y, JPanel panel) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(20, y, LABEL_WIDTH - 20, 30);
        panel.add(nameLabel);

        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        emailLabel.setForeground(new Color(135, 206, 250)); // Light blue color
        emailLabel.setBounds(40, y + 30, LABEL_WIDTH - 40, 30);
        emailLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        emailLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                emailLabel.setForeground(new Color(100, 149, 237)); // Darker blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                emailLabel.setForeground(new Color(135, 206, 250));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().mail(new URI("mailto:" + email));
                } catch (Exception ex) {
                    System.err.println("Could not open email client: " + ex.getMessage());
                }
            }
        });
        panel.add(emailLabel);
    }

    private JLabel createShadowLabel(String text, int size) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 128));
                g2d.drawString(getText(), 2, getHeight() - 4);
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), 0, getHeight() - 6);
            }
        };
        label.setFont(new Font("Arial", Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void addSeparator(JPanel panel, int y) {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(255, 255, 255, 150));
        separator.setBounds(20, y, LABEL_WIDTH - 20, 2);
        panel.add(separator);
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
        });
        
        int buttonWidth = LABEL_WIDTH - 20;
        button.setBounds(x, y, buttonWidth, 55);
        add(button);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, ContactTicTacToe.boardWidth, ContactTicTacToe.boardHeight, null);
        }
    }
}