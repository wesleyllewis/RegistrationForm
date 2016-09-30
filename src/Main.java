import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by WesleyLewis on 9/29/16.
 */
public class Main {
    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, address VARCHAR, email VARCHAR )");
    }
    public static ArrayList<User> selectUsers(Connection conn) throws SQLException{
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet results = stmt.executeQuery();
        while (results.next()){
            int id = results.getInt("id");
            String username = results.getString("userName");
            String address = results.getString("address");
            String email = results.getString("email");
            users.add(new User(id, username, address, email));
        }
        return users;
    }
    public static void insertUser(Connection conn, String userName, String address, String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(NULL, ?, ?, ?)");
        stmt.setString(1, userName);
        stmt.setString(2, address);
        stmt.setString(3, email);
        stmt.execute();
    }
    public static void updateUser(Connection conn, String userName, String address, String email, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE users SET username=?, address=?, email=? WHERE id=?");
        stmt.setString(1, userName);
        stmt.setString(2, address);
        stmt.setString(3, email);
        stmt.setInt(4, id);
        stmt.execute();
    }
    public static void deleteUser(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id=?");
        stmt.setInt(1, id);
        stmt.execute();
    }
    public static User selectUser(Connection conn, int id) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id=?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()){
            String userName = results.getString("userName");
            String address = results.getString("address");
            String email = results.getString("email");
            return new User(id, userName, address, email);
        }
        return null;
    }
    public static void main(String[] args) throws SQLException {
        Server.createWebServer();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.externalStaticFileLocation("public");
        Spark.init();

        Spark.get("/user", (request, response) -> {
                ArrayList<User> users = selectUsers(conn);
            JsonSerializer s = new JsonSerializer();
            return s.serialize(users);
        });
        Spark.post("/user", (request, response) -> {
            String body = request.body();
            JsonParser p = new JsonParser();
            User user = p.parse(body, User.class);
            insertUser(conn, user.userName, user.address, user.email);
            return "";
        });
        Spark.put("/user", (request, response) -> {
            String body = request.body();
            JsonParser p = new JsonParser();
            User user = p.parse(body, User.class);
            updateUser(conn, user.userName, user.address, user.email, user.id);
            return "";
        });
        Spark.delete("/user/:id", (request, response) -> {
            String params = request.params(":id");
            int userId = Integer.parseInt(params);
            deleteUser(conn, userId);
            return "";

        });
    }
}
