package vladimir;

import java.sql.*;

public class Task12 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void initTriggersAndArchives(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("CREATE TABLE archive_drinks (id NUMBER, name VARCHAR2(100), deleted_at DATE)"); } catch (SQLException ignored) {}
            try { stmt.execute("CREATE TABLE archive_desserts (id NUMBER, name VARCHAR2(100), deleted_at DATE)"); } catch (SQLException ignored) {}
            try { stmt.execute("CREATE TABLE staff_transfers (employee_name VARCHAR2(100), old_shop NUMBER, new_shop NUMBER, transfer_date DATE)"); } catch (SQLException ignored) {}

            stmt.execute("CREATE OR REPLACE TRIGGER trg_del_drink BEFORE DELETE ON coffee_types FOR EACH ROW BEGIN INSERT INTO archive_drinks VALUES (:OLD.id, :OLD.type_name, SYSDATE); END;");
            stmt.execute("CREATE OR REPLACE TRIGGER trg_del_dessert BEFORE DELETE ON dessert_orders FOR EACH ROW BEGIN INSERT INTO archive_desserts VALUES (:OLD.id, :OLD.dessert_name, SYSDATE); END;");
            stmt.execute("CREATE OR REPLACE TRIGGER trg_transfer_staff BEFORE UPDATE OF shop_id ON work_schedule FOR EACH ROW WHEN (OLD.shop_id <> NEW.shop_id) BEGIN INSERT INTO staff_transfers VALUES (:OLD.employee_name, :OLD.shop_id, :NEW.shop_id, SYSDATE); END;");
            System.out.println("Database triggers and archive logs registered successfully.");
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            initTriggersAndArchives(conn);
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
