import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignupPageTicTacToeDatabaseScore {
    private static final String DB_PATH = "database/TicTacToe.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;

    public SignupPageTicTacToeDatabaseScore() {
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
            pstmt.executeUpdate();
            System.out.println("Score entry created for user ID: " + userId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating score entry: " + e.getMessage());
            return false;
        }
    }

    public String getScore(int userId) {
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

    public int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting user ID: " + e.getMessage());
        }
        return -1;
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

    public void updateLastTwoScores(int userId, int result) {
        String sql = "UPDATE scores SET result = ? WHERE user_id = ? AND id IN (SELECT id FROM scores WHERE user_id = ? ORDER BY id DESC LIMIT 2)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, result);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void duplicateLastTwoScoresWithZero(int userId) {
        String selectSql = "SELECT id FROM scores WHERE user_id = ? ORDER BY id DESC LIMIT 2";
        String insertSql = "INSERT INTO scores (user_id, result) VALUES (?, 0)";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();
            while (rs.next()) {
                insertStmt.setInt(1, userId);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void duplicateLastTwoRowsForPlayers(int playerOId, int playerXId) {
        duplicateLastTwoRowsWithZero(playerOId);
        duplicateLastTwoRowsWithZero(playerXId);
    }

    private void duplicateLastTwoRowsWithZero(int userId) {
        String selectSql = "SELECT id FROM scores WHERE user_id = ? ORDER BY id DESC LIMIT 2";
        String insertSql = "INSERT INTO scores (user_id, result) VALUES (?, 0)";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();
            while (rs.next()) {
                insertStmt.setInt(1, userId);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error duplicating last two rows for user ID: " + userId);
        }
    }
}