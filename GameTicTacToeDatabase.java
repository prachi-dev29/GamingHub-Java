import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameTicTacToeDatabase {
    private static final String DB_DIR = "database";
    private static final String DB_NAME = "TicTacToe.db";
    private static final String DB_PATH = DB_DIR + File.separator + DB_NAME;
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;

    public GameTicTacToeDatabase() {
        try {
            File dbDir = new File(DB_DIR);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            
            createScoresTable();
            System.out.println("Database connected successfully at: " + DB_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    private void createScoresTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS scores (" +
                              "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                              "user_id INTEGER NOT NULL," +
                              "result INTEGER NOT NULL)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(createTableSQL)) {
            pstmt.executeUpdate();
            System.out.println("Scores table created/verified successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating scores table: " + e.getMessage());
        }
    }

    public void updateResult(int userId, int result) {
        String sql = "INSERT INTO scores (user_id, result) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, result);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Result inserted successfully for user ID: " + userId + " with result: " + result);
            } else {
                System.out.println("Failed to insert result for user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error inserting result: " + e.getMessage());
        }
    }

    public void createNewResultEntry(int userId) {
        String sql = "INSERT INTO scores (user_id, result) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("New result entry created for user ID: " + userId);
            } else {
                System.out.println("Failed to create new result entry for user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating new result entry: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printAllResults() {
        String sql = "SELECT * FROM scores ORDER BY id DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            System.out.println("\nCurrent results in database:");
            System.out.println("ID | User ID | Result");
            System.out.println("----------------------");
            
            while (rs.next()) {
                System.out.printf("%d | %d | %d%n",
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("result"));
            }
            System.out.println("----------------------\n");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error printing results: " + e.getMessage());
        }
    }

    public void updateLastTwoFields(int userId, int result) {
        String sql = "UPDATE scores SET result = ? WHERE user_id = ? AND id IN (SELECT id FROM scores WHERE user_id = ? ORDER BY id DESC LIMIT 2)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, result);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Last two fields updated successfully for user ID: " + userId);
            } else {
                System.out.println("No fields were updated for user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating last two fields: " + e.getMessage());
        }
    }

    public void duplicateLatestRowsForPlayers(int playerOId, int playerXId) {
        try {
            duplicateLatestRowWithZero(playerOId);
            duplicateLatestRowWithZero(playerXId);
            System.out.println("Duplication completed for both players");
        } catch (Exception e) {
            System.err.println("Error during duplication: " + e.getMessage());
        }
    }

    private void duplicateLatestRowWithZero(int userId) {
        String selectSql = "SELECT id FROM scores WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        String insertSql = "INSERT INTO scores (user_id, result) VALUES (?, 0)";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                insertStmt.setInt(1, userId);
                insertStmt.executeUpdate();
                System.out.println("Duplicated latest row for user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error duplicating latest row for user ID: " + userId);
        }
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
            System.err.println("Error fetching user ID for email: " + email);
        }
        return -1;
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
}