import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionTest {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/student_db";
        String username = "root";

        // Replace this with your MySQL root password
        String password = "Srejal@2008";

        try {
            Connection con = DriverManager.getConnection(
                url, username, password
            );

            System.out.println("MySQL connected successfully!");
            System.out.println("Database: " +
                con.getCatalog());

            con.close();

        } catch (SQLException e) {
            System.out.println("Connection failed!");
            System.out.println(e.getMessage());
        }
    }
}