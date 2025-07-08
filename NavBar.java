import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NavBar extends JPanel {
    private JFrame currentFrame;

    public NavBar(JFrame frame) {
        this.currentFrame = frame;
        setBackground(new Color(41, 128, 185));
        setPreferredSize(new Dimension(frame.getWidth(), 50));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton homeButton = createMenuButton("HOME");
        homeButton.addActionListener(e -> {
            currentFrame.dispose();
            new SimpleFrame();
        });
        add(homeButton);

        JButton aboutButton = createMenuButton("ABOUT");
        aboutButton.addActionListener(e -> {
            currentFrame.dispose();
            new About();
        });
        add(aboutButton);

        JButton contactButton = createMenuButton("CONTACT");
        contactButton.addActionListener(e -> {
            currentFrame.dispose();
            new Contact();
        });
        add(contactButton);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(31, 97, 141));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(52, 152, 219));
                } else {
                    g.setColor(new Color(41, 128, 185));
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(100, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, 15));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, 14));
            }
        });
        
        return button;
    }
}