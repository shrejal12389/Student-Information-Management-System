
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {

    // Add a student to the database
    public boolean addStudent(
            String name,
            int age,
            String gender,
            String department,
            String email,
            String phone) {

        String sql = "INSERT INTO students "
                + "(name, age, gender, department, email, phone) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, department);
            ps.setString(5, email);
            ps.setString(6, phone);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding student: "
                    + e.getMessage());
            return false;
        }
    }

    // Display all students
    public void viewStudents() {

        String sql = "SELECT * FROM students";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("student_id")
                        + " | Name: " + rs.getString("name")
                        + " | Age: " + rs.getInt("age")
                        + " | Gender: " + rs.getString("gender")
                        + " | Department: " + rs.getString("department")
                        + " | Email: " + rs.getString("email")
                        + " | Phone: " + rs.getString("phone")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error viewing students: "
                    + e.getMessage());
        }
    }
    
    // Search for a student using their ID
    public void searchStudent(int id) {

        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Student Found!");
                    System.out.println("ID: " + rs.getInt("student_id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Age: " + rs.getInt("age"));
                    System.out.println("Gender: " + rs.getString("gender"));
                    System.out.println("Department: " + rs.getString("department"));
                    System.out.println("Email: " + rs.getString("email"));
                    System.out.println("Phone: " + rs.getString("phone"));
                } else {
                    System.out.println("Student not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching student: " + e.getMessage());
        }
    }

    // Update a student's details
    public boolean updateStudent(
            int id,
            String name,
            int age,
            String gender,
            String department,
            String email,
            String phone) {

        String sql = "UPDATE students SET name=?, age=?, gender=?, "
                + "department=?, email=?, phone=? WHERE student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, department);
            ps.setString(5, email);
            ps.setString(6, phone);
            ps.setInt(7, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    // Delete a student using their ID
    public boolean deleteStudent(int id) {

        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

}
