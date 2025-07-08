import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SignupDatabase {
    private static final String DB_DIR = "C:\\Users\\prach\\OneDrive\\Desktop\\GamingHub2\\GamingHub\\database";
    private static final String DB_NAME = "flappybird.db";
    private static final String DB_PATH = DB_DIR + "/" + DB_NAME;
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;

    public SignupDatabase() {
        try {
            File dbDir = new File(DB_DIR);
            if (!dbDir.exists() && !dbDir.mkdirs()) {
                throw new RuntimeException("Failed to create database directory: " + DB_DIR);
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            if (connection == null) throw new RuntimeException("Failed to create database connection");

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            createTable();
            createScoreTable();
            System.out.println("Database initialized successfully at: " + DB_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'");
            if (rs.next()) {
                rs = stmt.executeQuery("PRAGMA table_info(users)");
                boolean hasScoreColumn = false;
                while (rs.next()) {
                    if ("score".equalsIgnoreCase(rs.getString("name"))) {
                        hasScoreColumn = true;
                        break;
                    }
                }

                if (hasScoreColumn) {
                    stmt.execute("ALTER TABLE users RENAME TO users_old");
                    stmt.execute("CREATE TABLE users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "first_name TEXT NOT NULL, " +
                            "last_name TEXT NOT NULL, " +
                            "email TEXT UNIQUE NOT NULL, " +
                            "gender TEXT NOT NULL, " +
                            "password TEXT NOT NULL)");
                    stmt.execute("INSERT INTO users (id, first_name, last_name, email, gender, password) " +
                            "SELECT id, first_name, last_name, email, gender, password FROM users_old");
                    stmt.execute("DROP TABLE users_old");
                }
            } else {
                stmt.execute("CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "first_name TEXT NOT NULL, " +
                        "last_name TEXT NOT NULL, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "gender TEXT NOT NULL, " +
                        "password TEXT NOT NULL)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create users table", e);
        }
    }

    private void createScoreTable() {
        String sql = "CREATE TABLE IF NOT EXISTS score (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "mode TEXT NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create score table", e);
        }
    }

    public boolean registerUser(String firstName, String lastName, String email, String gender, String password) {
        String sql = "INSERT INTO users (first_name, last_name, email, gender, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, gender);
            pstmt.setString(5, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                System.err.println("Email already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean loginUser(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void upsertScore(int userId, String mode, int score) {
        try {
            String checkSql = "SELECT 1 FROM users WHERE id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                if (!checkStmt.executeQuery().next()) {
                    System.err.println("User not found.");
                    return;
                }
            }

            String selectSql = "SELECT id FROM score WHERE user_id = ? AND mode = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userId);
                selectStmt.setString(2, mode);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    String updateSql = "UPDATE score SET score = ? WHERE user_id = ? AND mode = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, score);
                        updateStmt.setInt(2, userId);
                        updateStmt.setString(3, mode);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertSql = "INSERT INTO score (user_id, mode, score) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setString(2, mode);
                        insertStmt.setInt(3, score);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean scoreExists(int userId, String mode) {
        String sql = "SELECT 1 FROM score WHERE user_id = ? AND mode = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, mode);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void printAllUsers() {
        List<User> users = getAllUsers();
        System.out.println("\n=== All Users ===");
        for (User user : users) {
            System.out.printf("%d | %s | %s | %s | %s%n",
                    user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getGender());
        }
    }

    public List<Score> getUserScores(int userId) {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT * FROM score WHERE user_id = ? ORDER BY mode";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                scores.add(new Score(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("mode"),
                        rs.getInt("score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public void displayUserScores(int userId) {
        List<Score> scores = getUserScores(userId);
        System.out.println("\n=== Scores for User ID: " + userId + " ===");
        System.out.println("Mode | Score");
        for (Score score : scores) {
            System.out.printf("%s | %d%n", score.getMode(), score.getScore());
        }
    }

    public void insertUserIdIntoScore(int userId) {
        String sql = "INSERT INTO score (user_id, mode, score) VALUES (?, 'default', 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
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

    public static class User {
        private final int id;
        private final String firstName, lastName, email, gender, password;

        public User(int id, String firstName, String lastName, String email, String gender, String password) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.gender = gender;
            this.password = password;
        }

        public int getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
        public String getGender() { return gender; }
        public String getPassword() { return password; }
    }

    public static class Score {
        private final int id, userId, score;
        private final String mode;

        public Score(int id, int userId, String mode, int score) {
            this.id = id;
            this.userId = userId;
            this.mode = mode;
            this.score = score;
        }

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public String getMode() { return mode; }
        public int getScore() { return score; }
    }
}