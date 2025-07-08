import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HardModeDatabase {
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db";

    public HardModeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateScore(int userId, int score) {
        String sql = "UPDATE score SET score = ? WHERE id = (SELECT id FROM score WHERE user_id = ? AND mode = 'hard' ORDER BY id DESC LIMIT 1)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, score);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}