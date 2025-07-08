import java.sql.*;

public class LoginDatabaseScore {
    private static final String DB_URL = "jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db";

    public void insertScoreForUser(int userId) {
        String userCheckSql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement userCheckStmt = conn.prepareStatement(userCheckSql)) {
            
            userCheckStmt.setInt(1, userId);
            ResultSet userRs = userCheckStmt.executeQuery();
            
            if (!userRs.next()) {
                System.err.println("Error: User with ID " + userId + " does not exist");
                return;
            }

            String insertSql = "INSERT INTO score (user_id, mode, score) VALUES (?, 'default', 0)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.executeUpdate();
                System.out.println("Created new score entry for user " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error in insertScoreForUser: " + e.getMessage());
            e.printStackTrace();
        }
    }
}