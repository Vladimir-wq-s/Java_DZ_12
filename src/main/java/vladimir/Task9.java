package vladimir;

import java.sql.*;
import java.util.Scanner;

public class Task9 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void checkAndCreateTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("ALTER TABLE coffee_orders ADD barista_name VARCHAR2(100)"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE coffee_orders ADD customer_name VARCHAR2(100)"); } catch (SQLException ignored) {}
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            checkAndCreateTables(conn);

            // 1. Показать информацию о клиентах, которые заказывали напитки сегодня + инфо о бариста
            System.out.println("\n--- Todays orders with Customer & Barista details ---");
            String sql1 = "SELECT customer_name, coffee_name, price, barista_name FROM coffee_orders WHERE TO_CHAR(order_date, 'YYYY-MM-DD') = TO_CHAR(SYSDATE, 'YYYY-MM-DD')";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql1)) {
                boolean found = false;
                while (rs.next()) {
                    System.out.println("Customer: " + rs.getString("customer_name") + " | Ordered: " + rs.getString("coffee_name") + " ($" + rs.getDouble("price") + ") | Handled by Barista: " + rs.getString("barista_name"));
                    found = true;
                }
                if (!found) System.out.println("No drink orders made today yet.");
            }

            System.out.print("\nEnter target date (YYYY-MM-DD, e.g. 2026-10-06) for finance metrics: ");
            String targetDate = scanner.nextLine();

            // 2. Показать среднюю сумму заказа в конкретную дату
            // 3. Показать максимальную сумму заказа в конкретную дату
            String sql2 = "SELECT AVG(price) AS avg_p, MAX(price) AS max_p FROM coffee_orders WHERE TO_CHAR(order_date, 'YYYY-MM-DD') = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setString(1, targetDate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\nMetrics for " + targetDate + ":");
                        System.out.println(" - Average drink order sum: $" + (Math.round(rs.getDouble("avg_p") * 100.0) / 100.0));
                        System.out.println(" - Maximum single order sum: $" + rs.getDouble("max_p"));
                    }
                }
            }

            // 4. Показать клиента, который совершил максимальную сумму заказа в конкретную дату
            String sql3 = "SELECT customer_name, price FROM coffee_orders WHERE TO_CHAR(order_date, 'YYYY-MM-DD') = ? AND price = (SELECT MAX(price) FROM coffee_orders WHERE TO_CHAR(order_date, 'YYYY-MM-DD') = ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql3)) {
                ps.setString(1, targetDate);
                ps.setString(2, targetDate);
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("\nTop-spending customer(s) on " + targetDate + ":");
                    while (rs.next()) {
                        System.out.println(" - " + rs.getString("customer_name") + " (Spent: $" + rs.getDouble("price") + ")");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
