import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Menu2048 extends JFrame {
    private int userId;

    public Menu2048(int userId) {
        this.userId = userId;
        setTitle("Menu - 2048");
        setSize(360, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.BLACK);

        NavBar2048 navBar = new NavBar2048();
        mainContainer.add(navBar, BorderLayout.NORTH);

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
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton startButton = createStyledButton("Start Game");
        startButton.addActionListener(e -> {
            dispose();
            new Game2048(userId).setVisible(true);
        });
        gbc.gridy = 0;
        panel.add(startButton, gbc);

        JButton hubButton = createStyledButton("Back to Hub");
        hubButton.addActionListener(e -> {
            dispose();
            new StartFrame2048().setVisible(true);
        });
        gbc.gridy = 1;
        panel.add(hubButton, gbc);

        JButton quitButton = createStyledButton("Quit");
        quitButton.addActionListener(e -> dispose());
        gbc.gridy = 2;
        panel.add(quitButton, gbc);

        mainContainer.add(panel, BorderLayout.CENTER);
        setContentPane(mainContainer);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(200, 40));
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
        SwingUtilities.invokeLater(() -> new Menu2048(0));
    }
}