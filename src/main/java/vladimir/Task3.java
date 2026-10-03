package vladimir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Task3 {
    // Настройки подключения к локальной базе данных Oracle Express Edition (XE)
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void checkAndCreateTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("CREATE TABLE coffee_types (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, type_name VARCHAR2(100) NOT NULL)"); } catch (SQLException ignored) {}
            try { stmt.execute("CREATE TABLE coffee_orders (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, coffee_name VARCHAR2(100) NOT NULL, price NUMBER(6,2) NOT NULL, waiter_name VARCHAR2(100), customer_name VARCHAR2(100))"); } catch (SQLException ignored) {}
            try { stmt.execute("CREATE TABLE dessert_orders (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, dessert_name VARCHAR2(100) NOT NULL, price NUMBER(6,2) NOT NULL)"); } catch (SQLException ignored) {}
            try { stmt.execute("CREATE TABLE work_schedule (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, work_date DATE NOT NULL, employee_name VARCHAR2(100) NOT NULL)"); } catch (SQLException ignored) {}
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(java.util.Locale.US);

        // Установка соединения с базой данных Oracle с использованием логина и пароля
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            checkAndCreateTables(conn);

            System.out.print("Enter order ID to delete: ");
            int orderId = scanner.nextInt();
            scanner.nextLine();
            String sql1 = "DELETE FROM coffee_orders WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
                System.out.println("Specific coffee order deleted.");
            }

            System.out.print("Enter dessert order ID to delete: ");
            int dessertId = scanner.nextInt();
            scanner.nextLine();
            String sql2 = "DELETE FROM dessert_orders WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, dessertId);
                ps.executeUpdate();
                System.out.println("Specific dessert order deleted.");
            }

            System.out.print("Enter date to delete schedule (YYYY-MM-DD): ");
            String targetDate = scanner.nextLine();
            String sql3 = "DELETE FROM work_schedule WHERE work_date = TO_DATE(?, 'YYYY-MM-DD')";
            try (PreparedStatement ps = conn.prepareStatement(sql3)) {
                ps.setString(1, targetDate);
                ps.executeUpdate();
                System.out.println("Schedule for specified day deleted.");
            }

            System.out.print("Enter start date of range (YYYY-MM-DD): ");
            String startDate = scanner.nextLine();
            System.out.print("Enter end date of range (YYYY-MM-DD): ");
            String endDate = scanner.nextLine();
            String sql4 = "DELETE FROM work_schedule WHERE work_date BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD')";
            try (PreparedStatement ps = conn.prepareStatement(sql4)) {
                ps.setString(1, startDate);
                ps.setString(2, endDate);
                ps.executeUpdate();
                System.out.println("Schedule range deleted.");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
