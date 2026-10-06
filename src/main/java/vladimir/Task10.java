package vladimir;

import java.sql.*;
import java.util.Scanner;

public class Task10 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void checkAndCreateTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("ALTER TABLE work_schedule ADD role VARCHAR2(50) DEFAULT 'Staff' NOT NULL"); } catch (SQLException ignored) {}

            // Наполнение расписания на текущую неделю (понедельник - воскресенье), если таблица очищена
            String countSql = "SELECT COUNT(*) AS cnt FROM work_schedule";
            try (ResultSet rs = stmt.executeQuery(countSql)) {
                if (rs.next() && rs.getInt("cnt") == 0) {
                    stmt.execute("INSERT INTO work_schedule (work_date, employee_name, role) VALUES (TRUNC(SYSDATE, 'IW'), 'Alex', 'Barista')");
                    stmt.execute("INSERT INTO work_schedule (work_date, employee_name, role) VALUES (TRUNC(SYSDATE, 'IW') + 1, 'Emma', 'Barista')");
                    stmt.execute("INSERT INTO work_schedule (work_date, employee_name, role) VALUES (TRUNC(SYSDATE, 'IW') + 2, 'Alex', 'Barista')");
                    stmt.execute("INSERT INTO work_schedule (work_date, employee_name, role) VALUES (TRUNC(SYSDATE, 'IW') + 3, 'Emma', 'Barista')");
                    stmt.execute("INSERT INTO work_schedule (work_date, employee_name, role) VALUES (TRUNC(SYSDATE, 'IW') + 4, 'Clara', 'Cleaner')");
                    stmt.execute("INSERT INTO work_schedule (work_date, employee_name, role) VALUES (TRUNC(SYSDATE, 'IW') + 5, 'Manager David', 'Manager')");
                    System.out.println("Test weekly schedule generated automatically.");
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            checkAndCreateTables(conn);

            System.out.print("\nEnter barista name to view weekly schedule (e.g. Alex): ");
            String baristaName = scanner.nextLine();

            // 1. Показать расписание работы конкретного бариста на неделю
            System.out.println("\n--- Weekly schedule for barista: " + baristaName + " ---");
            String sql1 = "SELECT work_date FROM work_schedule WHERE employee_name = ? AND role = 'Barista' AND work_date BETWEEN TRUNC(SYSDATE, 'IW') AND TRUNC(SYSDATE, 'IW') + 6 ORDER BY work_date";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setString(1, baristaName);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        System.out.println(" - Shift Date: " + rs.getDate("work_date"));
                        found = true;
                    }
                    if (!found) System.out.println(" No shifts found for this barista on the current week.");
                }
            }

            // 2. Показать расписание работы всех бариста на неделю
            System.out.println("\n--- Weekly schedule for ALL Baristas ---");
            String sql2 = "SELECT employee_name, work_date FROM work_schedule WHERE role = 'Barista' AND work_date BETWEEN TRUNC(SYSDATE, 'IW') AND TRUNC(SYSDATE, 'IW') + 6 ORDER BY work_date";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql2)) {
                while (rs.next()) {
                    System.out.println("Date: " + rs.getDate("work_date") + " | Barista: " + rs.getString("employee_name"));
                }
            }

            // 3. Показать расписание работы для всех работников кафе на неделю
            System.out.println("\n--- Weekly schedule for ALL Cafe Employees ---");
            String sql3 = "SELECT employee_name, role, work_date FROM work_schedule WHERE work_date BETWEEN TRUNC(SYSDATE, 'IW') AND TRUNC(SYSDATE, 'IW') + 6 ORDER BY work_date";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql3)) {
                while (rs.next()) {
                    System.out.println("Date: " + rs.getDate("work_date") + " | Post: " + rs.getString("role") + " | Name: " + rs.getString("employee_name"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
