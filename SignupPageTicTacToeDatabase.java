import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;

public class SignupPageTicTacToeDatabase {
    private static final String DB_PATH = "database/TicTacToe.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;
    private JComboBox<String> genderComboBox;

    public SignupPageTicTacToeDatabase() {
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
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    gender TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS scores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    result TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public boolean registerUser(String firstName, String lastName, String email, String password, String gender) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, gender) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, gender);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error checking email: " + e.getMessage());
        }
        return false;
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
}