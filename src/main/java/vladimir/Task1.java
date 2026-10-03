package vladimir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Task1 {
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

            System.out.print("Enter coffee name for new order: ");
            String coffeeName = scanner.nextLine();
            System.out.print("Enter order price: ");
            double orderPrice = scanner.nextDouble();
            scanner.nextLine();
            String sql1 = "INSERT INTO coffee_orders (coffee_name, price) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setString(1, coffeeName);
                ps.setDouble(2, orderPrice);
                ps.executeUpdate();
                System.out.println("Coffee order added.");
            }

            System.out.print("Enter dessert name for new order: ");
            String dessertName = scanner.nextLine();
            System.out.print("Enter dessert price: ");
            double dessertPrice = scanner.nextDouble();
            scanner.nextLine();
            String sql2 = "INSERT INTO dessert_orders (dessert_name, price) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setString(1, dessertName);
                ps.setDouble(2, dessertPrice);
                ps.executeUpdate();
                System.out.println("Dessert order added.");
            }

            System.out.print("Enter employee name for next Monday schedule: ");
            String employeeName = scanner.nextLine();
            String sql3 = "INSERT INTO work_schedule (work_date, employee_name) VALUES (TRUNC(SYSDATE) + (8 - TO_CHAR(SYSDATE, 'D')), ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql3)) {
                ps.setString(1, employeeName);
                ps.executeUpdate();
                System.out.println("Monday schedule added.");
            }

            System.out.print("Enter new coffee type name: ");
            String newCoffeeType = scanner.nextLine();
            String sql4 = "INSERT INTO coffee_types (type_name) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(sql4)) {
                ps.setString(1, newCoffeeType);
                ps.executeUpdate();
                System.out.println("New coffee type added.");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
