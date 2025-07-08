import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignupDatabase2048Score {
    private static final String DB_PATH = "C:\\Users\\prach\\OneDrive\\Desktop\\GamingHub2\\GamingHub\\database\\2048.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;

    public SignupDatabase2048Score() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createScoreTableIfNotExists();
        } catch (Exception e) {
            e.printStackTrace();
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

    public void insertUserIdIntoScore(int userId) {
        String userCheckSql = "SELECT 1 FROM users WHERE id = ?";
        try (PreparedStatement userCheckStmt = connection.prepareStatement(userCheckSql)) {
            userCheckStmt.setInt(1, userId);
            ResultSet rs = userCheckStmt.executeQuery();
            if (!rs.next()) {
                System.err.println("User with ID " + userId + " does not exist in users table.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String sql = "INSERT INTO score (user_id, score) VALUES (?, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Inserted user_id " + userId + " into score table.");
            } else {
                System.err.println("Insert did not affect any rows.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user_id into score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void printAllUsers() {
        String sql = "SELECT id, first_name, last_name, email FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("User: id=" + rs.getInt("id") + ", email=" + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SignupDatabase2048Score db = new SignupDatabase2048Score();
        System.out.println("Using DB file: " + DB_PATH);
        db.printAllUsers();
        int testUserId = 1;
        db.insertUserIdIntoScore(testUserId);
        db.close();
    }
}