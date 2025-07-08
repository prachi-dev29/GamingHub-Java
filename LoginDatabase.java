import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDatabase {
    private static final String DB_DIR = "C:\\Users\\prach\\OneDrive\\Desktop\\GamingHub2\\GamingHub\\database";
    private static final String DB_NAME = "flappybird.db";
    private static final String DB_PATH = DB_DIR + "/" + DB_NAME;
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private Connection connection;
    private String userEmail;
    private String mode;

    public LoginDatabase() {
        try {
            File dbDir = new File(DB_DIR);
            if (!dbDir.exists()) {
                if (!dbDir.mkdirs()) {
                    throw new RuntimeException("Failed to create database directory: " + DB_DIR);
                }
            }

            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                System.err.println("SQLite JDBC driver not found. Please add sqlite-jdbc-3.42.0.0.jar to your classpath.");
                throw new RuntimeException("SQLite JDBC driver not found", e);
            }

            connection = DriverManager.getConnection(DB_URL);
            if (connection == null) {
                throw new RuntimeException("Failed to create database connection");
            }

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            createTable();
            createScoreTable();
            System.out.println("Database initialized successfully at: " + DB_PATH);
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "first_name TEXT NOT NULL, " +
                    "last_name TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "gender TEXT NOT NULL, " +
                    "password TEXT NOT NULL)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created successfully in " + DB_PATH);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create table", e);
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
            System.out.println("Score table created successfully in " + DB_PATH);
        } catch (SQLException e) {
            System.err.println("Error creating score table: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create score table", e);
        }
    }

    public boolean registerUser(String firstName, String lastName, String email, String gender, String password) {
        if (connection == null) {
            throw new RuntimeException("Database connection is not initialized");
        }

        String sql = "INSERT INTO users (first_name, last_name, email, gender, password) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, gender);
            pstmt.setString(5, password);
            pstmt.executeUpdate();
            System.out.println("User registered successfully");
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                System.err.println("Email already exists");
                return false;
            }
            System.err.println("Error registering user: " + e.getMessage());
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
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                String password = rs.getString("password");
                
                users.add(new User(id, firstName, lastName, email, gender, password));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }

    public void printAllUsers() {
        List<User> users = getAllUsers();
        System.out.println("\n=== All Users in Database ===");
        System.out.println("ID | First Name | Last Name | Email | Gender");
        System.out.println("----------------------------------------");
        
        for (User user : users) {
            System.out.printf("%d | %s | %s | %s | %s%n",
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender());
        }
        System.out.println("----------------------------------------\n");
    }

    public void displayAllUsers() {
        String sql = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n=== All Users in Database ===");
            System.out.println("ID | First Name | Last Name | Email | Gender");
            System.out.println("----------------------------------------");
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                
                System.out.printf("%d | %s | %s | %s | %s%n",
                    id, firstName, lastName, email, gender);
            }
            System.out.println("----------------------------------------\n");
            
        } catch (SQLException e) {
            System.err.println("Error displaying users: " + e.getMessage());
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
            System.err.println("Error closing database: " + e.getMessage());
            e.printStackTrace();
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
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void upsertScore(int userId, String mode, int score) {
        String selectSql = "SELECT id FROM score WHERE user_id = ? AND mode = ?";
        String updateSql = "UPDATE score SET score = ? WHERE user_id = ? AND mode = ?";
        String insertSql = "INSERT INTO score (user_id, mode, score) VALUES (?, ?, ?)";
        
        try {
            String userCheckSql = "SELECT 1 FROM users WHERE id = ?";
            try (PreparedStatement userCheckStmt = connection.prepareStatement(userCheckSql)) {
                userCheckStmt.setInt(1, userId);
                ResultSet userRs = userCheckStmt.executeQuery();
                if (!userRs.next()) {
                    System.err.println("Error: User with ID " + userId + " does not exist");
                    return;
                }
            }

            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setInt(1, userId);
                selectStmt.setString(2, mode);
                ResultSet rs = selectStmt.executeQuery();
                
                if (rs.next()) {
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, score);
                        updateStmt.setInt(2, userId);
                        updateStmt.setString(3, mode);
                        updateStmt.executeUpdate();
                        System.out.println("Updated score for user " + userId + " in mode " + mode + " to " + score);
                    }
                } else {
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setString(2, mode);
                        insertStmt.setInt(3, score);
                        insertStmt.executeUpdate();
                        System.out.println("Inserted new score for user " + userId + " in mode " + mode + ": " + score);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in upsertScore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean scoreExists(int userId, String mode) {
        String sql = "SELECT 1 FROM score WHERE user_id = ? AND mode = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, mode);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking score existence: " + e.getMessage());
            e.printStackTrace();
            return false;
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
            System.err.println("Error getting user scores: " + e.getMessage());
            e.printStackTrace();
        }
        return scores;
    }

    public void displayUserScores(int userId) {
        List<Score> scores = getUserScores(userId);
        if (scores.isEmpty()) {
            System.out.println("No scores found for user ID: " + userId);
            return;
        }
        
        System.out.println("\n=== Scores for User ID: " + userId + " ===");
        System.out.println("Mode | Score");
        System.out.println("------------");
        for (Score score : scores) {
            System.out.printf("%s | %d%n", score.getMode(), score.getScore());
        }
        System.out.println("------------\n");
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
        }
        return -1;
    }

    public static class User {
        private int id;
        private String firstName;
        private String lastName;
        private String email;
        private String gender;
        private String password;

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
        private int id;
        private int userId;
        private String mode;
        private int score;

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