import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EasyMediumDatabase {
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:C:/Users/prach/OneDrive/Desktop/GamingHub2/GamingHub/database/flappybird.db";

    public EasyMediumDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS score (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "mode TEXT," +
                "score INTEGER)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getUserIdByEmail(String email) {
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

    public static String getEmailByUserId(int userId) {
        String sql = "SELECT email FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            System.err.println("Error getting email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void duplicateLatestEasyRowAndPrintAll(int userId) {
        try {
            String insertSQL = "INSERT INTO score (user_id, mode, score) " +
                    "SELECT user_id, 'medium', score FROM score WHERE user_id = ? AND mode = 'easy' ORDER BY id DESC LIMIT 1";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            String selectSQL = "SELECT * FROM score ORDER BY id";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSQL)) {
                System.out.println("id | user_id | mode   | score");
                while (rs.next()) {
                    System.out.printf("%d | %d | %s | %d\n",
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("mode"),
                            rs.getInt("score"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void duplicateEasyToMediumWithZeroScore(int userId) {
        try {
            String insertSQL = "INSERT INTO score (user_id, mode, score) " +
                    "SELECT user_id, 'medium', 0 FROM score WHERE user_id = ? AND mode = 'easy' ORDER BY id DESC LIMIT 1";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}