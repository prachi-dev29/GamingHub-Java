import java.sql.*;
import java.util.regex.Pattern;

public class MediumModeDatabase {
    private static final String DB_URL = "jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db";
    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("^(?=.[0-9])(?=.[a-z])(?=.[A-Z])(?=.[@#$%^&+=!])(?=\\S+$).{8,}$");

    public void updateScore(int userId, int finalScore) {
        String userCheckSql = "SELECT 1 FROM users WHERE id = ?";
        String selectSql = "SELECT id FROM score WHERE user_id = ? AND mode = 'medium'";
        String updateSql = "UPDATE score SET score = ? WHERE id = (SELECT id FROM score WHERE user_id = ? AND mode = 'medium' ORDER BY id DESC LIMIT 1)";
        String insertSql = "INSERT INTO score (user_id, mode, score) VALUES (?, 'medium', ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement userCheckStmt = conn.prepareStatement(userCheckSql)) {
            userCheckStmt.setInt(1, userId);
            ResultSet userRs = userCheckStmt.executeQuery();
            if (!userRs.next()) {
                System.err.println("Error: User with ID " + userId + " does not exist");
                return;
            }
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userId);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, finalScore);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                        System.out.println("Updated medium score for user " + userId + " to " + finalScore);
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, finalScore);
                        insertStmt.executeUpdate();
                        System.out.println("Inserted new medium score for user " + userId + ": " + finalScore);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in updateScore: " + e.getMessage());
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
        String insertSql = "INSERT INTO score (user_id, mode, score) " +
                "SELECT user_id, mode, 0 FROM score WHERE user_id = ? AND mode = 'medium' ORDER BY id DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("Duplicated latest medium score row for user " + userId + " with score set to 0");
        } catch (SQLException e) {
            System.err.println("Error duplicating latest medium score row: " + e.getMessage());
            e.printStackTrace();
        }
    }
}