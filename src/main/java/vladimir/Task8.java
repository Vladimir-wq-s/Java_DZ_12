package vladimir;

import java.sql.*;
import java.util.Scanner;

public class Task8 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void checkAndCreateTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("ALTER TABLE coffee_orders ADD order_date DATE DEFAULT SYSDATE NOT NULL"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE dessert_orders ADD order_date DATE DEFAULT SYSDATE NOT NULL"); } catch (SQLException ignored) {}

            // Наполнение таблиц заказами на текущую дату, если они пусты
            String countSql = "SELECT COUNT(*) AS cnt FROM coffee_orders";
            try (ResultSet rs = stmt.executeQuery(countSql)) {
                if (rs.next() && rs.getInt("cnt") <= 1) {
                    stmt.execute("INSERT INTO coffee_orders (coffee_name, price, order_date, barista_name, customer_name) VALUES ('Espresso', 3.50, SYSDATE, 'Alex', 'Alice')");
                    stmt.execute("INSERT INTO coffee_orders (coffee_name, price, order_date, barista_name, customer_name) VALUES ('Latte', 4.80, SYSDATE, 'Emma', 'Bob')");
                    stmt.execute("INSERT INTO coffee_orders (coffee_name, price, order_date, barista_name, customer_name) VALUES ('Cappuccino', 4.50, SYSDATE - 2, 'Alex', 'Charlie')");
                    stmt.execute("INSERT INTO dessert_orders (dessert_name, price, order_date) VALUES ('Donut', 2.50, SYSDATE)");
                    stmt.execute("INSERT INTO dessert_orders (dessert_name, price, order_date) VALUES ('Croissant', 3.00, SYSDATE - 1)");
                    System.out.println("Test orders data generated automatically.");
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            checkAndCreateTables(conn);

            System.out.print("Enter specific date (YYYY-MM-DD, e.g. 2026-10-06): ");
            String targetDate = scanner.nextLine();

            // 1. Показать информацию о заказах в конкретную дату
            System.out.println("\n--- Coffee orders on " + targetDate + " ---");
            String sql1 = "SELECT id, coffee_name, price FROM coffee_orders WHERE TO_CHAR(order_date, 'YYYY-MM-DD') = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setString(1, targetDate);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("OrderID: " + rs.getInt("id") + " | Drink: " + rs.getString("coffee_name") + " | Price: $" + rs.getDouble("price"));
                    }
                }
            }

            // 2. Показать информацию о заказах в указанном промежутке дат
            System.out.print("\nEnter start date of range (YYYY-MM-DD): ");
            String startDate = scanner.nextLine();
            System.out.print("Enter end date of range (YYYY-MM-DD): ");
            String endDate = scanner.nextLine();

            System.out.println("\n--- Coffee orders from " + startDate + " to " + endDate + " ---");
            String sql2 = "SELECT id, coffee_name, price, order_date FROM coffee_orders WHERE order_date BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') + 1 ORDER BY order_date";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setString(1, startDate);
                ps.setString(2, endDate);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Date: " + rs.getDate("order_date") + " | OrderID: " + rs.getInt("id") + " | Drink: " + rs.getString("coffee_name") + " | Price: $" + rs.getDouble("price"));
                    }
                }
            }

            // 3. Показать количество заказов десертов в конкретную дату
            String sql3 = "SELECT COUNT(*) AS cnt FROM dessert_orders WHERE TO_CHAR(order_date, 'YYYY-MM-DD') = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql3)) {
                ps.setString(1, targetDate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) System.out.println("\nTotal dessert orders on " + targetDate + ": " + rs.getInt("cnt"));
                }
            }

            // 4. Показать количество заказов напитков в конкретную дату
            String sql4 = "SELECT COUNT(*) AS cnt FROM coffee_orders WHERE TO_CHAR(order_date, 'YYYY-MM-DD') = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql4)) {
                ps.setString(1, targetDate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) System.out.println("Total drink (coffee) orders on " + targetDate + ": " + rs.getInt("cnt"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
