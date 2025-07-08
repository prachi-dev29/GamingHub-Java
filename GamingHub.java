import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class GamingHub extends JFrame {
    private static final int WIDTH = 360;
    private static final int HEIGHT = 640;
    private Image backgroundImage;

    public GamingHub() {
        setTitle("Gaming Hub");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        try {
            backgroundImage = ImageIO.read(new java.io.File("GamingInterface.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(null);

        int buttonWidth = 150;
        int buttonHeight = 40;
        int spacing = 20;
        int totalHeight = 3 * buttonHeight + 2 * spacing;
        int startY = (HEIGHT - totalHeight) / 2;

        JButton flappyBirdBtn = new JButton("Flappy Bird");
        flappyBirdBtn.setBounds((WIDTH - buttonWidth) / 2, startY, buttonWidth, buttonHeight);
        flappyBirdBtn.addActionListener(e -> {
            dispose();
            new SimpleFrame();
        });
        panel.add(flappyBirdBtn);

        JButton game2048Btn = new JButton("2048");
        game2048Btn.setBounds((WIDTH - buttonWidth) / 2, startY + buttonHeight + spacing, buttonWidth, buttonHeight);
        game2048Btn.addActionListener(e -> {
            dispose();
            new StartFrame2048();
        });
        panel.add(game2048Btn);

        JButton ticTacToeBtn = new JButton("TicTacToe");
        ticTacToeBtn.setBounds((WIDTH - buttonWidth) / 2, startY + 2 * (buttonHeight + spacing), buttonWidth,
                buttonHeight);
        ticTacToeBtn.addActionListener(e -> {
            dispose();
            new WelcomeTicTacToe().setVisible(true);;
        });
        panel.add(ticTacToeBtn);

        setContentPane(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GamingHub().setVisible(true);
        });
    }
}