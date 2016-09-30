import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by WesleyLewis on 9/29/16.
 */
public class MainTest {
     public Connection startConnection() throws SQLException{
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
         Main.createTables(conn);
         return conn;
    }
    @Test
    public void testInsertUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Test", "TestAddress", "TestEmail");
        User user = Main.selectUser(conn, 1);
        conn.close();
        assertTrue(user.userName.contains("Test"));
    }
    @Test
    public void testSelectUsers() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Test", "TestAddress", "TestEmail");
        Main.insertUser(conn, "Test2", "TestAddress2", "TestEmail2");
        ArrayList<User> users = Main.selectUsers(conn);
        conn.close();
        assertTrue(users.size() == 2);

    }
    @Test
    public void testUpdateUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Test", "TestAddress", "TestEmail");
        Main.updateUser(conn, "TestPass", "Pass", "PassEmail", 1);
        User user = Main.selectUser(conn, 1);
        assertTrue(user.userName.contains("Pass"));

    }
    @Test
    public void testDeleteUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Test", "TestAddress", "TestEmail");
        User user = Main.selectUser(conn, 1);
        Main.deleteUser(conn, user.id);
        assertTrue(user.userName.contains(""));
    }

}