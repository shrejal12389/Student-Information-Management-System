
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class StudentManagementGUI extends JFrame {

    private static final Color NAVY = Color.decode("#172554");
private static final Color BLUE = Color.decode("#2563EB");
private static final Color BACKGROUND = Color.decode("#F1F5F9");
private static final Color TEXT = Color.BLACK;
private static final Color MUTED = Color.decode("#64748B");
private static final Color BORDER = new Color(226, 231, 240);
private static final Color WHITE = Color.decode("#FFFFFF");
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private JTable dashboardTable;
    private DefaultTableModel dashboardModel;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalValue;
    private JLabel departmentValue;
    private JLabel latestValue;
    private JLabel pageTitle;

    private final String[] columns = {
        "ID", "Name", "Age", "Gender",
        "Department", "Email", "Phone"
    };

    public StudentManagementGUI() {
        setTitle("Student Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 680));
        setSize(1250, 780);
        setLocationRelativeTo(null);

        buildInterface();
        loadStudents();
        loadStatistics();
    }

    private void buildInterface() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        add(createSidebar(), BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createTopBar(), BorderLayout.NORTH);

        contentPanel.setBackground(BACKGROUND);
        contentPanel.add(createDashboard(), "Dashboard");
        contentPanel.add(createDirectory(), "Students");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBorder(new EmptyBorder(25, 15, 20, 15));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("  STUDENT HUB");
        logo.setForeground(Color.BLACK);
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("  INFORMATION SYSTEM");
        subtitle.setForeground(Color.BLACK);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitle.setBorder(new EmptyBorder(7, 0, 35, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(subtitle);

        sidebar.add(createNavButton("▦   Dashboard", "Dashboard"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton("☷   Student Directory", "Students"));

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel(
            "<html><div style='text-align:center'>"
            + "STUDENT MANAGEMENT<br>System Version 2.0"
            + "</div></html>"
        );
        footer.setForeground(new Color(165, 180, 210));
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(footer);

        return sidebar;
    }

    private JButton createNavButton(String text, String page) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(new Color(225, 232, 247));
        button.setBackground(NAVY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(14, 12, 14, 8));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(e -> {
            pageTitle.setText(page.equals("Dashboard")
                ? "Dashboard" : "Student Directory");

            cardLayout.show(contentPanel, page);

            if (page.equals("Dashboard")) {
                loadStatistics();
            } else {
                loadStudents();
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(43, 57, 91));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(NAVY);
            }
        });

        return button;
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(WHITE);
        top.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(18, 28, 18, 28)
        ));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 23));
        pageTitle.setForeground(TEXT);

        JLabel right = new JLabel("Student Administration");
        right.setForeground(MUTED);
        right.setFont(new Font("SansSerif", Font.PLAIN, 13));

        top.add(pageTitle, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(28, 30, 30, 30));

        JPanel heading = new JPanel();
        heading.setBackground(BACKGROUND);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome back!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 25));
        welcome.setForeground(TEXT);

        JLabel description = new JLabel(
            "Here's an overview of your student records."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(MUTED);
        description.setBorder(new EmptyBorder(7, 0, 0, 0));

        heading.add(welcome);
        heading.add(description);

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 0));
        cards.setBackground(BACKGROUND);
        cards.setPreferredSize(new Dimension(0, 135));

        totalValue = new JLabel("0");
        departmentValue = new JLabel("0");
        latestValue = new JLabel("0");

        cards.add(createStatCard("TOTAL STUDENTS", totalValue,
            "Registered records", new Color(55, 105, 220)));
        cards.add(createStatCard("DEPARTMENTS", departmentValue,
            "Departments represented", new Color(27, 151, 111)));
        cards.add(createStatCard("LATEST STUDENT ID", latestValue,
            "Most recent record ID", new Color(218, 137, 42)));

        JPanel middle = new JPanel(new BorderLayout(0, 15));
        middle.setBackground(BACKGROUND);

        JLabel recentTitle = new JLabel("Student Records");
        recentTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        recentTitle.setForeground(TEXT);

        JButton viewAll = createButton("View Student Directory", BLUE);
        viewAll.addActionListener(e -> {
            pageTitle.setText("Student Directory");
            cardLayout.show(contentPanel, "Students");
            loadStudents();
        });

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setBackground(BACKGROUND);
        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(viewAll, BorderLayout.EAST);

        middle.add(recentHeader, BorderLayout.NORTH);
        middle.add(createRecentTablePanel(), BorderLayout.CENTER);

        JPanel topContent = new JPanel(new BorderLayout(0, 23));
        topContent.setBackground(BACKGROUND);
        topContent.add(heading, BorderLayout.NORTH);
        topContent.add(cards, BorderLayout.CENTER);

        panel.add(topContent, BorderLayout.NORTH);
        panel.add(middle, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(
        String title, JLabel value, String description, Color accent
    ) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));

        value.setForeground(accent);
        value.setFont(new Font("SansSerif", Font.BOLD, 30));

        JLabel desc = new JLabel(description);
        desc.setForeground(MUTED);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(WHITE);
        center.add(value, BorderLayout.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(desc, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDirectory() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(25, 28, 28, 28));

        JPanel heading = new JPanel(new BorderLayout(0, 8));
        heading.setBackground(BACKGROUND);

        JLabel title = new JLabel("Manage Students");
        title.setFont(new Font("SansSerif", Font.BOLD, 23));
        title.setForeground(TEXT);

        JLabel desc = new JLabel(
            "Search, add, update, and remove student records."
        );
        desc.setForeground(MUTED);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel titleBox = new JPanel();
        titleBox.setBackground(BACKGROUND);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(6));
        titleBox.add(desc);

        heading.add(titleBox, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(WHITE);
        toolbar.setBorder(new EmptyBorder(13, 13, 13, 13));

        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(220, 38));
        searchField.setToolTipText("Search by student ID or name");

        JButton search = createButton("Search", BLUE);
        JButton reset = createButton("Show All", new Color(91, 105, 130));

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchButtons.setBackground(BLUE);
        searchButtons.add(search);
        searchButtons.add(reset);

        JPanel searchBox = new JPanel(new BorderLayout(8, 0));
        searchBox.setBackground(WHITE);
        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(searchButtons, BorderLayout.EAST);

        JButton add = createButton("+ Add Student", new Color(27, 151, 111));
        JButton edit = createButton("Edit Selected", BLUE);
        JButton delete = createButton("Delete Selected", new Color(195, 65, 75));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(BLUE);
        actions.add(add);
        actions.add(edit);
        actions.add(delete);

        toolbar.add(searchBox, BorderLayout.CENTER);
        toolbar.add(actions, BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setBackground(BACKGROUND);
        top.add(heading, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.CENTER);

        JPanel tableArea = createTablePanel();

        search.addActionListener(e -> searchStudents());
        searchField.addActionListener(e -> searchStudents());
        reset.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });
        add.addActionListener(e -> showStudentForm(null));
        edit.addActionListener(e -> editSelectedStudent());
        delete.addActionListener(e -> deleteSelectedStudent());

        panel.add(top, BorderLayout.NORTH);
        panel.add(tableArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(34);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentTable.setForeground(TEXT);
        studentTable.setBackground(WHITE);
        studentTable.setSelectionBackground(new Color(221, 232, 255));
        studentTable.setSelectionForeground(TEXT);
        studentTable.setShowVerticalLines(false);
        studentTable.setGridColor(BORDER);
        studentTable.setFillsViewportHeight(true);
        studentTable.setAutoCreateRowSorter(true);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setForeground(TEXT);
        header.setBackground(new Color(246, 248, 252));
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(studentTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadStudents() {
        

        String sql = "SELECT student_id, name, age, gender, department, "
                   + "email, phone FROM students ORDER BY student_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("department"),
                    rs.getString("email"),
                    rs.getString("phone")
                });
                dashboardModel.addRow(new Object[]{
                rs.getInt("student_id"),
                rs.getString("name"),
                rs.getString("department"),
                rs.getString("email")
            });
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void loadStatistics() {
        String sql = "SELECT COUNT(*) AS total, "
                   + "COUNT(DISTINCT department) AS departments, "
                   + "COALESCE(MAX(student_id), 0) AS latest "
                   + "FROM students";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalValue.setText(String.valueOf(rs.getInt("total")));
                departmentValue.setText(
                    String.valueOf(rs.getInt("departments"))
                );
                latestValue.setText(String.valueOf(rs.getInt("latest")));
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void searchStudents() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);

        if (query.isEmpty()) {
            loadStudents();
            return;
        }

        String sql;
        boolean isId = query.matches("\\d+");

        if (isId) {
            sql = "SELECT student_id, name, age, gender, department, "
                + "email, phone FROM students "
                + "WHERE student_id = ? OR name LIKE ? "
                + "ORDER BY student_id DESC";
        } else {
            sql = "SELECT student_id, name, age, gender, department, "
                + "email, phone FROM students "
                + "WHERE name LIKE ? OR department LIKE ? OR email LIKE ? "
                + "ORDER BY student_id DESC";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (isId) {
                ps.setInt(1, Integer.parseInt(query));
                ps.setString(2, "%" + query + "%");
            } else {
                String pattern = "%" + query + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("department"),
                        rs.getString("email"),
                        rs.getString("phone")
                    });
                }
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                    this, "No matching students found.",
                    "Search Results", JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void showStudentForm(Integer studentId) {
        boolean editing = studentId != null;

        JTextField nameField = new JTextField(22);
        JTextField ageField = new JTextField(22);
        JComboBox<String> genderBox = new JComboBox<>(
            new String[] {"Select gender", "Male", "Female", "Other"}
        );
        JComboBox<String> departmentBox = new JComboBox<>(
            new String[] {
                "Select department", "CSE", "IT", "ECE", "EEE",
                "Mechanical", "Civil", "AI & ML", "Data Science",
                "Other"
            }
        );
        JTextField emailField = new JTextField(22);
        JTextField phoneField = new JTextField(22);

        if (editing) {
            int modelRow = getSelectedModelRow();
            if (modelRow < 0) {
                return;
            }

            nameField.setText(String.valueOf(tableModel.getValueAt(modelRow, 1)));
            ageField.setText(String.valueOf(tableModel.getValueAt(modelRow, 2)));
            setComboValue(genderBox, tableModel.getValueAt(modelRow, 3));
            setComboValue(departmentBox, tableModel.getValueAt(modelRow, 4));
            emailField.setText(String.valueOf(tableModel.getValueAt(modelRow, 5)));
            phoneField.setText(String.valueOf(tableModel.getValueAt(modelRow, 6)));
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addFormRow(form, gbc, 0, "Full Name", nameField);
        addFormRow(form, gbc, 1, "Age", ageField);
        addFormRow(form, gbc, 2, "Gender", genderBox);
        addFormRow(form, gbc, 3, "Department", departmentBox);
        addFormRow(form, gbc, 4, "Email", emailField);
        addFormRow(form, gbc, 5, "Phone (10 digits)", phoneField);

        int result = JOptionPane.showConfirmDialog(
            this, form,
            editing ? "Edit Student" : "Add New Student",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String department = (String) departmentBox.getSelectedItem();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            showValidationError("Name is required.");
            return;
        }

        if (!ageText.matches("\\d+")) {
            showValidationError("Age must be a whole number.");
            return;
        }

        int age = Integer.parseInt(ageText);
        if (age < 1 || age > 120) {
            showValidationError("Age must be between 1 and 120.");
            return;
        }

        if (gender == null || gender.startsWith("Select")) {
            showValidationError("Please select a gender.");
            return;
        }

        if (department == null || department.startsWith("Select")) {
            showValidationError("Please select a department.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showValidationError("Enter a valid email address.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            showValidationError("Phone number must contain exactly 10 digits.");
            return;
        }

        String sql;

        if (editing) {
            sql = "UPDATE students SET name=?, age=?, gender=?, "
                + "department=?, email=?, phone=? WHERE student_id=?";
        } else {
            sql = "INSERT INTO students "
                + "(name, age, gender, department, email, phone) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, department);
            ps.setString(5, email);
            ps.setString(6, phone);

            if (editing) {
                ps.setInt(7, studentId);
            }

            int affected = ps.executeUpdate();

            if (affected > 0) {
                JOptionPane.showMessageDialog(
                    this,
                    editing ? "Student updated successfully."
                            : "Student added successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE
                );
                loadStudents();
                loadStatistics();
            } else {
                JOptionPane.showMessageDialog(
                    this, "No record was changed.",
                    "Information", JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void addFormRow(
        JPanel panel, GridBagConstraints gbc, int row,
        String labelText, JComponent input
    ) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXT);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;

        input.setFont(new Font("SansSerif", Font.PLAIN, 13));
        input.setPreferredSize(new Dimension(220, 32));
        panel.add(input, gbc);
    }

    private void setComboValue(JComboBox<String> combo, Object value) {
        if (value != null) {
            combo.setSelectedItem(String.valueOf(value));
        }
    }

    private int getSelectedModelRow() {
        int viewRow = studentTable.getSelectedRow();

        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                this, "Please select a student from the table first.",
                "No Student Selected", JOptionPane.WARNING_MESSAGE
            );
            return -1;
        }

        return studentTable.convertRowIndexToModel(viewRow);
    }

    private void editSelectedStudent() {
        int row = getSelectedModelRow();
        if (row < 0) {
            return;
        }

        int id = Integer.parseInt(
            String.valueOf(tableModel.getValueAt(row, 0))
        );

        showStudentForm(id);
    }

    private void deleteSelectedStudent() {
        int row = getSelectedModelRow();
        if (row < 0) {
            return;
        }

        int id = Integer.parseInt(
            String.valueOf(tableModel.getValueAt(row, 0))
        );
        String name = String.valueOf(tableModel.getValueAt(row, 1));

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Delete student \"" + name + "\" (ID: " + id + ")?\n"
            + "This action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                JOptionPane.showMessageDialog(
                    this, "Student deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE
                );
                loadStudents();
                loadStatistics();
            } else {
                JOptionPane.showMessageDialog(
                    this, "Student record was not found.",
                    "Information", JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(
            this, message, "Check Input", JOptionPane.WARNING_MESSAGE
        );
    }

    private void showDatabaseError(SQLException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
            this,
            "Could not complete the database operation.\n\n"
            + "Check that MySQL is running and the database connection "
            + "is configured correctly.\n\n"
            + "Details: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
                // Use the default Swing look and feel if unavailable.
            }

            new StudentManagementGUI().setVisible(true);
        });
    }
    private JPanel createRecentTablePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(WHITE);
    panel.setBorder(BorderFactory.createLineBorder(BORDER));

    dashboardModel = new DefaultTableModel(
        new String[]{"ID", "Name", "Department", "Email"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    dashboardTable = new JTable(dashboardModel);
    dashboardTable.setRowHeight(32);
    dashboardTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
    dashboardTable.setAutoCreateRowSorter(true);
    dashboardTable.setSelectionMode(
        ListSelectionModel.SINGLE_SELECTION
    );

    JTableHeader header = dashboardTable.getTableHeader();
    header.setFont(new Font("SansSerif", Font.BOLD, 12));
    header.setBackground(new Color(246, 248, 252));
    header.setPreferredSize(new Dimension(0, 38));

    panel.add(new JScrollPane(dashboardTable), BorderLayout.CENTER);
    return panel;
}
}
