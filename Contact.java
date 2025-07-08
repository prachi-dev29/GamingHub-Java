import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Contact extends JFrame {
    private Image backgroundImg;
    private Image birdImg;
    private int birdX = 0;
    private int birdY = 0;
    private int velocityX = 2;
    private int velocityY = 2;
    private final int birdWidth = 68;
    private final int birdHeight = 48;

    public Contact() {
        setTitle("Contact");
        setSize(360, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());

        NavBar navBar = new NavBar(this);
        mainContainer.add(navBar, BorderLayout.NORTH);

        ContactPanel contactPanel = new ContactPanel();
        contactPanel.setPreferredSize(new Dimension(360, 600));
        mainContainer.add(contactPanel, BorderLayout.CENTER);

        Timer timer = new Timer(1000/60, e -> {
            contactPanel.updateBird();
            contactPanel.repaint();
        });
        timer.start();

        add(mainContainer);
        setVisible(true);
    }

    class ContactPanel extends JPanel {
        private JLabel titleLabel;
        private JLabel emailsLabel;
        private JPanel emailBoxPanel;
        private JLabel email1Label;
        private JLabel email2Label;
        private JButton backButton;

        public ContactPanel() {
            setLayout(null);
            setOpaque(false);

            try {
                backgroundImg = ImageIO.read(getClass().getResource("./flappybirdbg.png"));
                birdImg = ImageIO.read(getClass().getResource("./flappybird.png"));
            } catch (IOException e) {
                System.err.println("Error loading images: " + e.getMessage());
            }

            titleLabel = new JLabel("DEVELOPERS");
            titleLabel.setForeground(new Color(255, 215, 0));
            titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
            titleLabel.setBounds(30, 30, 300, 50);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(titleLabel);

            emailsLabel = new JLabel("EMAILS");
            emailsLabel.setForeground(new Color(255, 215, 0));
            emailsLabel.setFont(new Font("Arial", Font.BOLD, 28));
            emailsLabel.setBounds(30, 90, 300, 40);
            emailsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(emailsLabel);

            emailBoxPanel = new JPanel();
            emailBoxPanel.setLayout(new GridLayout(2, 1, 0, 10));
            emailBoxPanel.setOpaque(false);
            emailBoxPanel.setBounds(40, 150, 280, 90);

            email1Label = new JLabel("prachiarora781@gmail.com", SwingConstants.CENTER);
            email1Label.setForeground(new Color(76, 175, 80));
            email1Label.setFont(new Font("Arial", Font.BOLD, 17));
            emailBoxPanel.add(email1Label);

            email2Label = new JLabel("tushararo62@gmail.com", SwingConstants.CENTER);
            email2Label.setForeground(new Color(76, 175, 80));
            email2Label.setFont(new Font("Arial", Font.BOLD, 17));
            emailBoxPanel.add(email2Label);

            JPanel bgPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(new Color(0, 0, 0, 120));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    super.paintComponent(g);
                }
            };
            bgPanel.setOpaque(false);
            bgPanel.setLayout(null);
            bgPanel.setBounds(35, 145, 290, 100);
            emailBoxPanel.setBounds(0, 0, 290, 100);
            bgPanel.add(emailBoxPanel);
            add(bgPanel);
            bgPanel.setLocation(35, 145);

            backButton = new JButton("BACK TO MENU") {
                @Override
                protected void paintComponent(Graphics g) {
                    if (getModel().isPressed()) {
                        g.setColor(new Color(31, 97, 141));
                    } else if (getModel().isRollover()) {
                        g.setColor(new Color(52, 152, 219));
                    } else {
                        g.setColor(new Color(41, 128, 185));
                    }
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    super.paintComponent(g);
                }
            };
            backButton.setBounds(50, 300, 260, 50);
            backButton.setForeground(Color.WHITE);
            backButton.setFont(new Font("Arial", Font.BOLD, 20));
            backButton.setFocusPainted(false);
            backButton.setBorderPainted(false);
            backButton.setContentAreaFilled(false);
            backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            backButton.addActionListener(e -> {
                dispose();
                new SimpleFrame();
            });
            add(backButton);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundImg != null) {
                g2d.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
            }
            if (birdImg != null) {
                g2d.drawImage(birdImg, getWidth() - birdWidth - 10, 10, birdWidth, birdHeight, null);
            }
        }

        public void updateBird() {
            birdX = getWidth() - birdWidth - 10;
            birdY = 10;
        }
    }
}