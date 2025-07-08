import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignupDatabaseScore {
    private static final String DB_PATH = "C:\\Users\\prach\\OneDrive\\Desktop\\GamingHub2\\GamingHub\\database\\flappybird.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;

    public SignupDatabaseScore() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
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
            } else {
                System.out.println("User with ID " + userId + " exists.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        String sql = "INSERT INTO score (user_id, mode, score) VALUES (?, 'default', 0)";
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
        SignupDatabaseScore db = new SignupDatabaseScore();
        System.out.println("Using DB file: " + DB_PATH);
        db.printAllUsers();
        int testUserId = 1;
        db.insertUserIdIntoScore(testUserId);
        db.close();
    }
}