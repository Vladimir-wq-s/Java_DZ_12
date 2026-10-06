package vladimir;

import java.sql.*;

public class Task13 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void initViews(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE OR REPLACE VIEW view_all_shops AS SELECT * FROM coffeeshops");
            stmt.execute("CREATE OR REPLACE VIEW view_common_drinks AS SELECT type_name FROM coffee_types GROUP BY type_name HAVING COUNT(DISTINCT shop_id) = (SELECT COUNT(*) FROM coffeeshops)");
            stmt.execute("CREATE OR REPLACE VIEW view_common_desserts AS SELECT dessert_name FROM dessert_orders GROUP BY dessert_name HAVING COUNT(DISTINCT shop_id) = (SELECT COUNT(*) FROM coffeeshops)");
            stmt.execute("CREATE OR REPLACE VIEW view_all_baristas AS SELECT DISTINCT employee_name, shop_id FROM work_schedule WHERE role = 'Barista'");
            stmt.execute("CREATE OR REPLACE VIEW view_all_waiters AS SELECT DISTINCT employee_name, shop_id FROM work_schedule WHERE role = 'Waiter'");
            System.out.println("Database analytical views created successfully.");
        } catch (SQLException e) {
            System.err.println("View creation error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            initViews(conn);
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
