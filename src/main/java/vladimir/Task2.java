package vladimir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Task2 {
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

            System.out.print("Enter new employee name for next Tuesday: ");
            String employeeName = scanner.nextLine();
            String sql1 = "UPDATE work_schedule SET employee_name = ? WHERE work_date = TRUNC(SYSDATE) + (9 - TO_CHAR(SYSDATE, 'D'))";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setString(1, employeeName);
                ps.executeUpdate();
                System.out.println("Tuesday schedule updated.");
            }

            System.out.print("Enter old coffee type name: ");
            String oldCoffee = scanner.nextLine();
            System.out.print("Enter new coffee type name: ");
            String newCoffee = scanner.nextLine();
            String sql2 = "UPDATE coffee_types SET type_name = ? WHERE type_name = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setString(1, newCoffee);
                ps.setString(2, oldCoffee);
                ps.executeUpdate();
                System.out.println("Coffee type name updated.");
            }

            System.out.print("Enter order ID to update: ");
            int orderId = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter new coffee name for this order: ");
            String updatedCoffee = scanner.nextLine();
            String sql3 = "UPDATE coffee_orders SET coffee_name = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql3)) {
                ps.setString(1, updatedCoffee);
                ps.setInt(2, orderId);
                ps.executeUpdate();
                System.out.println("Order data updated.");
            }

            System.out.print("Enter old dessert name: ");
            String oldDessert = scanner.nextLine();
            System.out.print("Enter new dessert name: ");
            String newDessert = scanner.nextLine();
            String sql4 = "UPDATE dessert_orders SET dessert_name = ? WHERE dessert_name = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql4)) {
                ps.setString(1, newDessert);
                ps.setString(2, oldDessert);
                ps.executeUpdate();
                System.out.println("Dessert name updated.");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
