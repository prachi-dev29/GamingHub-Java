import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Contact2048 extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;

    public Contact2048() {
        setTitle("2048 Contact");
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
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 20, 20));
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        contentPanel.add(Box.createVerticalStrut(20));

        JLabel titleLabel1 = new JLabel("Contact");
        titleLabel1.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel1.setForeground(Color.WHITE);
        titleLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel1);

        JLabel titleLabel2 = new JLabel("Us");
        titleLabel2.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel2.setForeground(Color.WHITE);
        titleLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel2);
        contentPanel.add(Box.createVerticalStrut(30));

        JLabel developersLabel = new JLabel("Developers:");
        developersLabel.setFont(new Font("Arial", Font.BOLD, 26));
        developersLabel.setForeground(Color.WHITE);
        developersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(developersLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel dev1Name = new JLabel("Prachi Arora");
        dev1Name.setFont(new Font("Arial", Font.BOLD, 22));
        dev1Name.setForeground(Color.WHITE);
        dev1Name.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(dev1Name);

        JLabel dev1Email = new JLabel("prachiarora781@gmail.com");
        dev1Email.setFont(new Font("Arial", Font.PLAIN, 20));
        dev1Email.setForeground(new Color(135, 206, 250));
        dev1Email.setAlignmentX(Component.CENTER_ALIGNMENT);
        dev1Email.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contentPanel.add(dev1Email);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel dev2Name = new JLabel("Tushar Arora");
        dev2Name.setFont(new Font("Arial", Font.BOLD, 22));
        dev2Name.setForeground(Color.WHITE);
        dev2Name.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(dev2Name);

        JLabel dev2Email = new JLabel("tushararo62@gmail.com");
        dev2Email.setFont(new Font("Arial", Font.PLAIN, 20));
        dev2Email.setForeground(new Color(135, 206, 250));
        dev2Email.setAlignmentX(Component.CENTER_ALIGNMENT);
        dev2Email.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contentPanel.add(dev2Email);
        contentPanel.add(Box.createVerticalStrut(30));

        addEmailListener(dev1Email, "prachiarora781@gmail.com");
        addEmailListener(dev2Email, "tushararo62@gmail.com");

        JButton backButton = createStyledButton("Back to Menu");
        backButton.addActionListener(e -> {
            dispose();
            new StartFrame2048().setVisible(true);
        });
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(300, 60));
        contentPanel.add(backButton);

        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setOpaque(false);
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddedPanel.add(contentPanel, BorderLayout.CENTER);

        backgroundPanel.add(paddedPanel, BorderLayout.CENTER);
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
        pack();
        setSize(WIDTH, HEIGHT);
        setVisible(true);
    }

    private void addEmailListener(JLabel emailLabel, String email) {
        emailLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                emailLabel.setForeground(new Color(100, 149, 237));
            }

            public void mouseExited(MouseEvent evt) {
                emailLabel.setForeground(new Color(135, 206, 250));
            }

            public void mouseClicked(MouseEvent evt) {
                try {
                    Desktop.getDesktop().mail(new URI("mailto:" + email));
                } catch (Exception ex) {
                    System.err.println("Could not open email client: " + ex.getMessage());
                }
            }
        });
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
            new Contact2048().setVisible(true);
        });
    }
}