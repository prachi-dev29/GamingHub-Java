import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginPageTicTacToeDatabase {
    private static final String DB_PATH = "database/TicTacToe.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;

    public LoginPageTicTacToeDatabase() {
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
            createUserTable();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    private void createUserTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
            """);
            System.out.println("Users table created or already exists");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating users table: " + e.getMessage());
        }
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println("Email " + email + " exists: " + exists);
                return exists;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking email: " + e.getMessage());
        }
        return false;
    }

    public boolean validateLogin(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            boolean isValid = rs.next();
            System.out.println("Login validation for " + email + ": " + isValid);
            return isValid;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error validating login: " + e.getMessage());
            return false;
        }
    }

    public int getUserId(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                System.out.println("User ID for " + email + ": " + userId);
                return userId;
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
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}