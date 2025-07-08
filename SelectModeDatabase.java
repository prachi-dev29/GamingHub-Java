import java.sql.*;

public class SelectModeDatabase {
    private static final String DB_URL = "jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db";

    public void updateGameMode(int userId, String mode) {
        String userCheckSql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement userCheckStmt = conn.prepareStatement(userCheckSql)) {
            
            userCheckStmt.setInt(1, userId);
            ResultSet userRs = userCheckStmt.executeQuery();
            
            if (!userRs.next()) {
                System.err.println("Error: User with ID " + userId + " does not exist");
                return;
            }

            String getRecentScoreSql = "SELECT id FROM score WHERE user_id = ? ORDER BY id DESC LIMIT 1";
            try (PreparedStatement getRecentStmt = conn.prepareStatement(getRecentScoreSql)) {
                getRecentStmt.setInt(1, userId);
                ResultSet recentRs = getRecentStmt.executeQuery();
                
                if (recentRs.next()) {
                    int scoreId = recentRs.getInt("id");
                    
                    String updateSql = "UPDATE score SET mode = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, mode);
                        updateStmt.setInt(2, scoreId);
                        updateStmt.executeUpdate();
                        System.out.println("Updated game mode to " + mode + " for user " + userId);
                    }
                } else {
                    System.err.println("No score entry found for user " + userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in updateGameMode: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void duplicateLatestScoreWithZero(int userId) {
    }
}