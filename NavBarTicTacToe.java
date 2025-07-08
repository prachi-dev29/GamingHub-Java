import java.awt.*;
import javax.swing.*;

public class NavBarTicTacToe extends JPanel {
    private JFrame parentFrame;

    public NavBarTicTacToe(JFrame parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(41, 128, 185));
        setPreferredSize(new Dimension(360, 50));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 0, 0));
        buttonPanel.setOpaque(false);

        JButton homeButton = createNavButton("Home");
        homeButton.addActionListener(e -> {
            parentFrame.dispose();
            new WelcomeTicTacToe().setVisible(true);
        });

        JButton contactButton = createNavButton("Contact");
        contactButton.addActionListener(e -> {
            parentFrame.dispose();
            new ContactTicTacToe();
        });

        JButton instructionsButton = createNavButton("Instructions");
        instructionsButton.addActionListener(e -> {
            parentFrame.dispose();
            new InstructionsTicTacToe().setVisible(true);
        });

        buttonPanel.add(homeButton);
        buttonPanel.add(contactButton);
        buttonPanel.add(instructionsButton);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
                button.setContentAreaFilled(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
                button.setContentAreaFilled(false);
            }
        });

        return button;
    }
}