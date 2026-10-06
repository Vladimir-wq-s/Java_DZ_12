package vladimir;

import java.sql.*;

public class Task14 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void initProceduresAndBlacklist(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("CREATE TABLE blacklist_staff (employee_name VARCHAR2(100) PRIMARY KEY, reason VARCHAR2(200), banned_at DATE)"); } catch (SQLException ignored) {}

            stmt.execute("CREATE OR REPLACE PROCEDURE get_popular_drink(out_name OUT VARCHAR2) AS BEGIN SELECT coffee_name INTO out_name FROM (SELECT coffee_name, COUNT(*) FROM coffee_orders GROUP BY coffee_name ORDER BY COUNT(*) DESC) WHERE ROWNUM = 1; END;");
            stmt.execute("CREATE OR REPLACE PROCEDURE get_popular_dessert(out_name OUT VARCHAR2) AS BEGIN SELECT dessert_name INTO out_name FROM (SELECT dessert_name, COUNT(*) FROM dessert_orders GROUP BY dessert_name ORDER BY COUNT(*) DESC) WHERE ROWNUM = 1; END;");
            stmt.execute("CREATE OR REPLACE PROCEDURE add_to_blacklist(emp_name IN VARCHAR2, ban_reason IN VARCHAR2) AS BEGIN INSERT INTO blacklist_staff VALUES (emp_name, ban_reason, SYSDATE); DELETE FROM work_schedule WHERE employee_name = emp_name; END;");
            System.out.println("Stored procedures and Blacklist sub-system compiled.");
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            initProceduresAndBlacklist(conn);
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
