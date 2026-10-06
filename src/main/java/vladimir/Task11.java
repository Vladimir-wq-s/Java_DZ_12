package vladimir;

import java.sql.*;

public class Task11 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void checkAndCreateTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("CREATE TABLE coffeeshops (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, shop_name VARCHAR2(100) NOT NULL, address VARCHAR2(200))"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE coffee_types ADD shop_id NUMBER"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE coffee_types ADD price NUMBER(6,2)"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE dessert_orders ADD shop_id NUMBER"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE work_schedule ADD shop_id NUMBER"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE work_schedule ADD role VARCHAR2(50)"); } catch (SQLException ignored) {}

            String countSql = "SELECT COUNT(*) AS cnt FROM coffeeshops";
            try (ResultSet rs = stmt.executeQuery(countSql)) {
                if (rs.next() && rs.getInt("cnt") == 0) {
                    stmt.execute("INSERT INTO coffeeshops (shop_name, address) VALUES ('Central Station', 'Downtown 10')");
                    stmt.execute("INSERT INTO coffeeshops (shop_name, address) VALUES ('Uptown Corner', 'North Ave 45')");
                    stmt.execute("INSERT INTO coffee_types (type_name, shop_id, price) VALUES ('Espresso', 1, 3.00)");
                    stmt.execute("INSERT INTO coffee_types (type_name, shop_id, price) VALUES ('Espresso', 2, 3.50)");
                    stmt.execute("INSERT INTO coffee_types (type_name, shop_id, price) VALUES ('Latte', 1, 4.50)");
                    stmt.execute("INSERT INTO coffee_types (type_name, shop_id, price) VALUES ('Latte', 2, 5.00)");
                    System.out.println("Network coffeeshops data generated.");
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            checkAndCreateTables(conn);

            String sql = "SELECT s.shop_name, t.type_name, t.price FROM coffee_types t JOIN coffeeshops s ON t.shop_id = s.id ORDER BY t.type_name, s.shop_name";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("\n--- Coffee network assortment and prices ---");
                while (rs.next()) {
                    System.out.println("Shop: " + rs.getString("shop_name") + " | Drink: " + rs.getString("type_name") + " | Price: $" + rs.getDouble("price"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
