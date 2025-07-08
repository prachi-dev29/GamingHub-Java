import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameDatabase2048 {
    private static final String DB_PATH = "jdbc:sqlite:C:\\Users\\prach\\OneDrive\\Desktop\\GamingHub2\\GamingHub\\database\\2048.db";
    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\prach\\OneDrive\\Desktop\\GamingHub2\\GamingHub\\database\\2048.db";

    private Connection connection;

    public GameDatabase2048() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createScoreTableIfNotExists();
            System.out.println("Using database file: " + DB_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting to database in GameDatabase2048: " + e.getMessage());
        }
    }

    private void createScoreTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS score (" +
                              "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                              "user_id INTEGER NOT NULL," +
                              "score INTEGER NOT NULL DEFAULT 0)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(createTableSQL)) {
            pstmt.executeUpdate();
            System.out.println("Score table created or already exists");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating score table: " + e.getMessage());
        }
    }

    public void updateScore(int userId, int score) {
        String findMostRecentSql = "SELECT id FROM score WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        String updateScoreSql = "UPDATE score SET score = ? WHERE id = ?";

        try (PreparedStatement findStmt = connection.prepareStatement(findMostRecentSql)) {
            findStmt.setInt(1, userId);
            ResultSet rs = findStmt.executeQuery();

            if (rs.next()) {
                int scoreId = rs.getInt("id");
                try (PreparedStatement updateStmt = connection.prepareStatement(updateScoreSql)) {
                    updateStmt.setInt(1, score);
                    updateStmt.setInt(2, scoreId);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Score updated successfully for user ID: " + userId + ", Score ID: " + scoreId);
                    } else {
                         System.out.println("No score entry found or updated for user ID: " + userId + ", Score ID: " + scoreId);
                    }
                }
            } else {
                 System.out.println("No recent score entry found for user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating score: " + e.getMessage());
        }
    }

     public void createNewScoreEntry(int userId) {
        String sql = "INSERT INTO score (user_id, score) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("createNewScoreEntry: userId=" + userId + ", rowsAffected=" + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error creating new score entry: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void duplicateLastScoreEntry(int userId) {
        String duplicateSql = "INSERT INTO score (user_id, score) " +
                            "SELECT user_id, 0 FROM score " +
                            "WHERE user_id = ? " +
                            "ORDER BY id DESC LIMIT 1";
        
        try (PreparedStatement pstmt = connection.prepareStatement(duplicateSql)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Duplicated last score entry for user ID: " + userId);
            } else {
                System.out.println("No previous score entry found to duplicate for user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error duplicating score entry: " + e.getMessage());
        }
    }

    public void updateLatestScoreForUser(int userId, int newScore) {
        String selectSql = "SELECT id FROM score WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        String updateSql = "UPDATE score SET score = ? WHERE id = ?";
        String insertSql = "INSERT INTO score (user_id, score) VALUES (?, ?)";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int scoreId = rs.getInt("id");
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, newScore);
                    updateStmt.setInt(2, scoreId);
                    int rowsAffected = updateStmt.executeUpdate();
                    System.out.println("updateLatestScoreForUser: userId=" + userId + ", scoreId=" + scoreId + ", newScore=" + newScore + ", rowsAffected=" + rowsAffected);
                }
            } else {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, newScore);
                    int rowsAffected = insertStmt.executeUpdate();
                    System.out.println("updateLatestScoreForUser (insert): userId=" + userId + ", newScore=" + newScore + ", rowsAffected=" + rowsAffected);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating latest score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void duplicateLatestScoreWithZero(int userId) {
        String sql = "INSERT INTO score (user_id, score) SELECT user_id, 0 FROM score WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("duplicateLatestScoreWithZero: userId=" + userId + ", rowsAffected=" + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error duplicating score entry: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GameDatabase2048 db = new GameDatabase2048();
        int testUserId = 7;
        db.duplicateLatestScoreWithZero(testUserId);
        db.updateLatestScoreForUser(testUserId, 9999);
        db.close();
    }
}