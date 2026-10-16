
public class StudentTest {

    public static void main(String[] args) {

        StudentDAO dao = new StudentDAO();

        // Display all existing students
        System.out.println("ALL STUDENTS:");
        dao.viewStudents();

        // Search for student with ID 1
        System.out.println("\nSEARCH RESULT:");
        dao.searchStudent(1);

        // Update student with ID 1
        // Uncomment these lines only when you want to test updating.
        // boolean updated = dao.updateStudent(
        //     1, "Updated Student", 20, "Male",
        //     "CSE", "updated@example.com", "9876543210"
        // );
        // System.out.println(updated ? "Updated!" : "Not updated.");

        // Delete student with ID 1
        // Uncomment these lines only when you want to test deleting.
        // boolean deleted = dao.deleteStudent(1);
        // System.out.println(deleted ? "Deleted!" : "Not deleted.");
    }
}
