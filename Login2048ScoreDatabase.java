import java.sql.*;

public class Login2048ScoreDatabase {
    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\prach\\OneDrive\\Desktop\\GamingHub2\\GamingHub\\database\\2048.db";

    public int validateUserAndCreateScore(String email, String password) {
        String userCheckSql = "SELECT id FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement userCheckStmt = conn.prepareStatement(userCheckSql)) {
            
            userCheckStmt.setString(1, email);
            userCheckStmt.setString(2, password);
            ResultSet userRs = userCheckStmt.executeQuery();
            
            if (userRs.next()) {
                int userId = userRs.getInt("id");
                String insertSql = "INSERT INTO score (user_id, score) VALUES (?, 0)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.executeUpdate();
                    System.out.println("Created new score entry for user " + userId);
                }
                return userId;
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Error in validateUserAndCreateScore: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
}