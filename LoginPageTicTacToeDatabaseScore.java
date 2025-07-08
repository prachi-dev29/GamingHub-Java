import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginPageTicTacToeDatabaseScore {
    private static final String DB_PATH = "database/TicTacToe.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;

    public LoginPageTicTacToeDatabaseScore() {
        try {
            File dbDir = new File("database");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createScoreTable();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    private void createScoreTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS scores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    result INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
            System.out.println("Scores table created or verified successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating scores table: " + e.getMessage());
        }
    }

    public boolean isScoreTableExists() {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='scores'";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking if scores table exists: " + e.getMessage());
            return false;
        }
    }

    public boolean createScoreEntry(int userId) {
        if (!isScoreTableExists()) {
            createScoreTable();
        }

        String sql = "INSERT INTO scores (user_id, result) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Score entry created for user ID: " + userId);
                return true;
            } else {
                System.err.println("No rows affected when creating score entry");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating score entry: " + e.getMessage());
            return false;
        }
    }

    public String getScore(int userId) {
        if (!isScoreTableExists()) {
            createScoreTable();
            return "0";
        }

        String sql = "SELECT result FROM scores WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("result");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting score: " + e.getMessage());
        }
        return "0";
    }

    public void updateScore(int userId, String result) {
        if (!isScoreTableExists()) {
            createScoreTable();
        }

        String sql = "UPDATE scores SET result = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, result);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Score updated for user ID: " + userId + " with result: " + result);
            } else {
                System.err.println("No rows affected when updating score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating score: " + e.getMessage());
        }
    }

    public void updateLatestScore(int userId, int result) {
        String sql = "UPDATE scores SET result = ? WHERE id = (SELECT id FROM scores WHERE user_id = ? ORDER BY id DESC LIMIT 1)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, result);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}