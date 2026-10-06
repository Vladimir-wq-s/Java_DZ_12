package vladimir;

import java.sql.*;

public class Task7 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");

            // 1. Самый молодой клиент
            String sql1 = "SELECT customer_name, birth_date FROM cafe_customers WHERE birth_date = (SELECT MAX(birth_date) FROM cafe_customers)";
            System.out.println("\nYoungest customer(s):");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql1)) {
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("customer_name") + " (Born: " + rs.getDate("birth_date") + ")");
                }
            }

            // 2. Самый возрастной клиент
            String sql2 = "SELECT customer_name, birth_date FROM cafe_customers WHERE birth_date = (SELECT MIN(birth_date) FROM cafe_customers)";
            System.out.println("\nOldest customer(s):");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql2)) {
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("customer_name") + " (Born: " + rs.getDate("birth_date") + ")");
                }
            }

            // 3. Клиенты, у которых день рождения в этот день (сегодня)
            String sql3 = "SELECT customer_name, birth_date FROM cafe_customers WHERE TO_CHAR(birth_date, 'MM-DD') = TO_CHAR(SYSDATE, 'MM-DD')";
            System.out.println("\nCustomers having birthday today:");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql3)) {
                boolean found = false;
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("customer_name") + " (Born: " + rs.getDate("birth_date") + ")");
                    found = true;
                }
                if (!found) System.out.println(" - No customers have a birthday today.");
            }

            // 4. Клиенты, у которых не заполнен контактный почтовый адрес
            String sql4 = "SELECT customer_name FROM cafe_customers WHERE email IS NULL OR TRIM(email) = ''";
            System.out.println("\nCustomers with empty email address:");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql4)) {
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("customer_name"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
