import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class NavBar2048 extends JPanel {
    private JButton homeButton;
    private JButton aboutButton;
    private JButton contactButton;
    private Color buttonDefaultColor = new Color(20, 20, 20);
    private Color buttonHoverColor = new Color(40, 40, 40);

    public NavBar2048() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(360, 40));

        homeButton = createStyledButton("Home");
        aboutButton = createStyledButton("About");
        contactButton = createStyledButton("Contact");

        homeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                window.dispose();
                new StartFrame2048().setVisible(true);
            }
        });

        aboutButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                window.dispose();
                new InstructionsFrame2048().setVisible(true);
            }
        });

        contactButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                window.dispose();
                new Contact2048().setVisible(true);
            }
        });

        add(homeButton);
        add(Box.createHorizontalStrut(2));
        add(aboutButton);
        add(Box.createHorizontalStrut(2));
        add(contactButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(90, 30));
        button.setBackground(buttonDefaultColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        button.setFocusPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(buttonHoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(buttonDefaultColor);
            }
        });

        return button;
    }

    public void updateScore(int score) {
    }
} 